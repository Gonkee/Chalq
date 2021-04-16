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

class Recorder1{

    private boolean running = true;

    private final int width, height, w3;
    private final ByteBuffer buffer;
    private final BufferedImage image;
    private final byte[] imageData;

    private int pbo1, pbo2;
    private long readSync;

    private final Muxer muxer;
    private final Encoder encoder;
    private final MediaPacket packet;
    private final MediaPicture picture;
    private MediaPictureConverter converter = null;
    private long timestamp = 0;

    float pixelTime, encoderTime, recordingTime;

    protected Recorder1(int width, int height, String outputFile) {
        this.width = width;
        this.height = height;
        w3 = 3 * width;
        buffer = ByteBuffer.allocateDirect(width * height * 3);
        image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        imageData = ( (DataBufferByte) image.getRaster().getDataBuffer() ).getData();

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
    }

    protected void recordFrame() {

        long start = System.nanoTime();
//        System.out.println("acquire time: " + ((System.nanoTime() - start) / 1000000000f));

        if (readSync != 0) {
            GL30.glBindBuffer(GL30.GL_PIXEL_PACK_BUFFER, pbo2);
            GL30.glGetBufferSubData(GL30.GL_PIXEL_PACK_BUFFER, 0, buffer);

            int syncStatus = ARBSync.glClientWaitSync(readSync, 0, Long.MAX_VALUE);
            if (syncStatus == GL32.GL_ALREADY_SIGNALED || syncStatus == GL32.GL_CONDITION_SATISFIED) {
                // swap PBO references
                int temp = pbo1;
                pbo1 = pbo2;
                pbo2 = temp;
                readSync = 0;
            }
        }
        if (readSync == 0) {
            GL30.glBindBuffer(GL30.GL_PIXEL_PACK_BUFFER, pbo1);
            glReadPixels(0, 0, width, height, GL30.GL_BGR, GL_UNSIGNED_BYTE, 0);
            readSync = ARBSync.glFenceSync(ARBSync.GL_SYNC_GPU_COMMANDS_COMPLETE, 0);
        }

        GL30.glBindBuffer(GL30.GL_PIXEL_PACK_BUFFER, 0); // unbind

        // glReadPixels goes row by row, from bottom to top, but
        // BufferedImage goes row by row, from top to bottom.
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < w3; x++) {
                imageData[ y * w3 + x ] = buffer.get( (height - y - 1) * w3 + x );
            }
        }
        pixelTime = (System.nanoTime() - start) / 1000000000f;

        start = System.nanoTime();
        /* This is LIKELY not in YUV420P format, so we're going to convert it using some handy utilities. */
        if (converter == null)
            converter = MediaPictureConverterFactory.createConverter(image, picture);
        converter.toPicture(picture, image, timestamp);
        timestamp++;

        do {
            encoder.encode(packet, picture);
            if (packet.isComplete())
                muxer.write(packet, false);
        } while (packet.isComplete());

        recordingTime = (System.nanoTime() - start) / 1000000000f;
//        System.out.println("rec: " + String.format("%.4f", recordingTime) + ", pix: " + String.format("%.4f", pixelTime) + ", enc: " + String.format("%.4f", encoderTime));
    }

    protected void stopRecording() {
        // done encoding the video, finish up
        do {
            encoder.encode(packet, null);
            if (packet.isComplete())
                muxer.write(packet,  false);
        } while (packet.isComplete());

        muxer.close();
    }



}
