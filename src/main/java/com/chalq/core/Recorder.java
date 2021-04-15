package com.chalq.core;

import io.humble.video.*;
import io.humble.video.awt.MediaPictureConverter;
import io.humble.video.awt.MediaPictureConverterFactory;
import org.lwjgl.opengl.ARBSync;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL32;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.Semaphore;

import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glReadPixels;

class Recorder{

    private boolean running = true;
//    private boolean toEncode = false;
    private final Semaphore mainSignal = new Semaphore(0);
    private final Semaphore encoderSignal = new Semaphore(0);
    private final Semaphore pixelSignal = new Semaphore(0);
    private final EncoderThread encoderThread = new EncoderThread(this);
    private final PixelThread pixelThread = new PixelThread(this);

    private final int width, height, w3;
    private final ByteBuffer buffer;
    private byte[] iData1, iData2;        // iData1 is for the buffer to write into
    private BufferedImage image1, image2; // image2 is to send to the encoder

    private int pbo1, pbo2;
    private long readSync;

    private final Muxer muxer;
    private final Encoder encoder;
    private final MediaPacket packet;
    private final MediaPicture picture;
    private MediaPictureConverter converter = null;
    private long timestamp = 0;

    protected Recorder(int width, int height, String outputFile) {
        this.width = width;
        this.height = height;
        w3 = 3 * width;
        buffer = ByteBuffer.allocateDirect(width * height * 3);

        // while one is receiving data from buffer, other is sending data to encoder (simultaneous = faster)
        image1 = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        iData1 = ( (DataBufferByte) image1.getRaster().getDataBuffer() ).getData();
        image2 = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        iData2 = ( (DataBufferByte) image2.getRaster().getDataBuffer() ).getData();

        // while one is receiving data from GPU, other is sending data to CPU (simultaneous = faster)
        pbo1 = GL30.glGenBuffers();
        pbo2 = GL30.glGenBuffers();
        GL30.glBindBuffer(GL30.GL_PIXEL_PACK_BUFFER, pbo1);
        GL30.glBufferData(GL30.GL_PIXEL_PACK_BUFFER, width * height * 3, GL30.GL_STREAM_READ);
        GL30.glBindBuffer(GL30.GL_PIXEL_PACK_BUFFER, pbo2);
        GL30.glBufferData(GL30.GL_PIXEL_PACK_BUFFER, width * height * 3, GL30.GL_STREAM_READ);
        readSync = 0;

        //  VID RECORDING
        muxer = Muxer.make(outputFile, null, "mp4");
        Rational timeBase = Rational.make(1, 60);
        MuxerFormat format = muxer.getFormat();
        Codec codec = Codec.findEncodingCodecByName("libx264");

        System.out.println("codec: " + codec);

        KeyValueBag encoderOptions = KeyValueBag.make();
        encoderOptions.setValue("crf", "18");

        encoder = Encoder.make(codec);
        encoder.setWidth(width);
        encoder.setHeight(height);
        // We are going to use 420P as the format because that's what most video formats these days use
        PixelFormat.Type pixelformat = PixelFormat.Type.PIX_FMT_YUV420P;
        encoder.setPixelFormat(pixelformat);
        encoder.setTimeBase(timeBase);

        if (format.getFlag(MuxerFormat.Flag.GLOBAL_HEADER))
            encoder.setFlag(Encoder.Flag.FLAG_GLOBAL_HEADER, true);

        encoder.open(encoderOptions, null);
        muxer.addNewStream(encoder);
        try {
            muxer.open(null, null);
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }

        picture = MediaPicture.make(encoder.getWidth(), encoder.getHeight(), pixelformat);
        picture.setTimeBase(timeBase);


        packet = MediaPacket.make();

        pixelThread.start();
        encoderThread.start();
        // kickstart first frame
        encoderSignal.release();
        pixelSignal.release();
    }

    protected void recordFrame() {

        long start = System.nanoTime();
        try {
            pixelSignal.acquire(); // pixel thread must be done with 1 frame ago
            encoderSignal.acquire(); // encoder thread must be done with 2 frames ago
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        System.out.println("acquire time: " + ((System.nanoTime() - start) / 1000000000f));

        // swap BufferedImage's and their byte arrays
        byte[] td = iData1;
        iData1 = iData2;
        iData2 = td;
        BufferedImage ti = image1;
        image1 = image2;
        image2 = ti;

        if (readSync != 0) {
            GL30.glBindBuffer(GL30.GL_PIXEL_PACK_BUFFER, pbo2);
            GL30.glGetBufferSubData(GL30.GL_PIXEL_PACK_BUFFER, 0, buffer);
            mainSignal.release(2); // immediately after loading data from GPU, start encoding current frame on encoder thread

            int syncStatus = ARBSync.glClientWaitSync(readSync, 0, Long.MAX_VALUE);
            if (syncStatus == GL32.GL_ALREADY_SIGNALED || syncStatus == GL32.GL_CONDITION_SATISFIED) {
                // swap PBO references
                int temp = pbo1;
                pbo1 = pbo2;
                pbo2 = temp;
                readSync = 0;
            }
        } else {
            mainSignal.release(2);
        }
        if (readSync == 0) {
            GL30.glBindBuffer(GL30.GL_PIXEL_PACK_BUFFER, pbo1);
            glReadPixels(0, 0, width, height, GL30.GL_BGR, GL_UNSIGNED_BYTE, 0);
            readSync = ARBSync.glFenceSync(ARBSync.GL_SYNC_GPU_COMMANDS_COMPLETE, 0);
        }

        GL30.glBindBuffer(GL30.GL_PIXEL_PACK_BUFFER, 0); // unbind

    }

    protected void stopRecording() {
        running = false;
        mainSignal.release(2);
    }

//    @Override
//    public void run() {
//        while (running) {
//
//            try {
//                mainSignal.acquire(); // wait for next frame to be available to encode from main thread
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//
//            long start = System.nanoTime();
//            if (!running) break; // running may be set to false during the acquire phase, when the program is ended
//
//            // glReadPixels goes row by row, from bottom to top, but
//            // BufferedImage goes row by row, from top to bottom.
//            for (int y = 0; y < height; y++) {
//                for (int x = 0; x < w3; x++) {
//                    iData1[ y * w3 + x ] = buffer.get( (height - y - 1) * w3 + x );
//                }
//            }
//            /* This is LIKELY not in YUV420P format, so we're going to convert it using some handy utilities. */
//            if (converter == null)
//                converter = MediaPictureConverterFactory.createConverter(image1, picture);
//            converter.toPicture(picture, image1, timestamp);
//            timestamp++;
//
//            do {
//                encoder.encode(packet, picture);
//                if (packet.isComplete())
//                    muxer.write(packet, false);
//            } while (packet.isComplete());
//
////            toEncode = false;
//            encoderSignal.release(); // done encoding this frame, allow main thread to proceed with next frame
////            System.out.println("encoding time: " + ((System.nanoTime() - start) / 1000000000f));
//        }
//
//        // done encoding the video, finish up
//        do {
//            encoder.encode(packet, null);
//            if (packet.isComplete())
//                muxer.write(packet,  false);
//        } while (packet.isComplete());
//
//        muxer.close();
//    }

    private static class EncoderThread extends Thread {

        private final Recorder r;

        public EncoderThread(Recorder recorder) {
            this.r = recorder;
        }

        @Override
        public void run() {
            while (r.running) {
                try {
                    r.mainSignal.acquire();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                long start = System.nanoTime();
                if (!r.running) break; // running may be set to false during the acquire phase, when the program is ended

                /* This is LIKELY not in YUV420P format, so we're going to convert it using some handy utilities. */
                if (r.converter == null)
                    r.converter = MediaPictureConverterFactory.createConverter(r.image2, r.picture);
                r.converter.toPicture(r.picture, r.image2, r.timestamp);
                r.timestamp++;

                do {
                    r.encoder.encode(r.packet, r.picture);
                    if (r.packet.isComplete())
                        r.muxer.write(r.packet, false);
                } while (r.packet.isComplete());

                r.encoderSignal.release();
                System.out.println("encoder time: " + ((System.nanoTime() - start) / 1000000000f));
            }

            // done encoding the video, finish up
            do {
                r.encoder.encode(r.packet, null);
                if (r.packet.isComplete())
                    r.muxer.write(r.packet,  false);
            } while (r.packet.isComplete());

            r.muxer.close();
        }
    }


    private static class PixelThread extends Thread {

        private final Recorder r;

        public PixelThread(Recorder recorder) {
            this.r = recorder;
        }

        @Override
        public void run() {
            while (r.running) {
                try {
                    r.mainSignal.acquire();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                long start = System.nanoTime();
                // glReadPixels goes row by row, from bottom to top, but
                // BufferedImage goes row by row, from top to bottom.
                for (int y = 0; y < r.height; y++) {
                    for (int x = 0; x < r.w3; x++) {
                        r.iData1[ y * r.w3 + x ] = r.buffer.get( (r.height - y - 1) * r.w3 + x );
                    }
                }
                r.pixelSignal.release();
                System.out.println("pixel time: " + ((System.nanoTime() - start) / 1000000000f));
            }
        }
    }

}
