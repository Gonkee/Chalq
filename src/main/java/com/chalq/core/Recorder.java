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

import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glReadPixels;

class Recorder {

    private final int width, height;
    private final BufferedImage image;
    private final byte[] imageData;
    private final ByteBuffer buffer;

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
        image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        imageData = ( (DataBufferByte) image.getRaster().getDataBuffer() ).getData();
        buffer = ByteBuffer.allocateDirect(width * height * 3);

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
        Codec codec = Codec.findEncodingCodec(format.getDefaultVideoCodecId());

        System.out.println("codec: " + codec);

        encoder = Encoder.make(codec);
        encoder.setWidth(1920);
        encoder.setHeight(1080);
        // We are going to use 420P as the format because that's what most video formats these days use
        PixelFormat.Type pixelformat = PixelFormat.Type.PIX_FMT_YUV420P;
        encoder.setPixelFormat(pixelformat);
        encoder.setTimeBase(timeBase);

        if (format.getFlag(MuxerFormat.Flag.GLOBAL_HEADER))
            encoder.setFlag(Encoder.Flag.FLAG_GLOBAL_HEADER, true);

        encoder.open(null, null);
        muxer.addNewStream(encoder);
        try {
            muxer.open(null, null);
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }

        picture = MediaPicture.make(encoder.getWidth(), encoder.getHeight(), pixelformat);
        picture.setTimeBase(timeBase);

        picture.setQuality(20);
        System.out.println("quality: " + picture.getQuality());

        packet = MediaPacket.make();
    }

    protected void recordFrame() {
        if (readSync != 0) {
        /*
            wait for pixel data to be done moving to PBO from previous tick (wait unlimited time to not
            lose any frames) - takes less time as that action was performed on the GPU, at the same time as
            the rendering processes above
         */
            GL30.glBindBuffer(GL30.GL_PIXEL_PACK_BUFFER, pbo2);
            GL30.glGetBufferSubData(GL30.GL_PIXEL_PACK_BUFFER, 0, buffer); // could load into either array or buffer
//                    pixelBuffer.get(pixelBytes);
            for (int i = 0; i < buffer.remaining(); i++) {
                imageData[i] = buffer.get(i + buffer.position());
            }
//                        for (int i = 0; i < pixelArray.length; i++) {
//                            // int in ARGB form
//                            pixelArray[i] = 0xFF000000                          // A = 255
//                                    | ( pixelBuffer.get(i * 3    ) << 16 )  // R
//                                    | ( pixelBuffer.get(i * 3 + 1) <<  8 )  // G
//                                    | ( pixelBuffer.get(i * 3 + 2) <<  0 ); // B
//                        }

            int syncStatus = ARBSync.glClientWaitSync(readSync, 0, Long.MAX_VALUE);
            if (syncStatus == GL32.GL_ALREADY_SIGNALED || syncStatus == GL32.GL_CONDITION_SATISFIED) {
                // swap
                int temp = pbo1;
                pbo1 = pbo2;
                pbo2 = temp;
                readSync = 0;
            }
        }
        if (readSync == 0) {
                    /*
                        the pixel data is being moved to the Pixel Buffer Object (PBO), which is on the GPU,
                        as opposed to directly to system memory, which will block the thread due to synchronisation
                        between the CPU and GPU
                     */
            GL30.glBindBuffer(GL30.GL_PIXEL_PACK_BUFFER, pbo1);
            glReadPixels(0, 0, width, height, GL30.GL_BGR, GL_UNSIGNED_BYTE, 0); // was prev GL_RGB
            readSync = ARBSync.glFenceSync(ARBSync.GL_SYNC_GPU_COMMANDS_COMPLETE, 0);
        }

//                System.out.println("colour: " + String.format("0x%08X", pixelArray[1000]));
//                System.out.println(String.format("0x%08X", image.getRGB(100, 100)));
//                System.out.println("sync time: " + String.format("%.4f", syncTime) + "read pixels: " + String.format("%.4f", readPixelTime));// + ", fill array: " + String.format("%.4f", fillArrayTime) + ", fill image:" + String.format("%.4f", fillImageTime) );
        GL30.glBindBuffer(GL30.GL_PIXEL_PACK_BUFFER, 0); // unbind

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
    }

    protected void stopRecording() {
        do {
            encoder.encode(packet, null);
            if (packet.isComplete())
                muxer.write(packet,  false);
        } while (packet.isComplete());

        muxer.close();
    }

}
