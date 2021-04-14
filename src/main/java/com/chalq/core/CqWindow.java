package com.chalq.core;

import com.chalq.math.Mat3;
import io.humble.video.*;
import io.humble.video.awt.MediaPictureConverter;
import io.humble.video.awt.MediaPictureConverterFactory;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.nanovg.NVGColor;
import org.lwjgl.nanovg.NanoVG;
import org.lwjgl.nanovg.NanoVGGL3;
import org.lwjgl.opengl.*;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.IOException;
import java.nio.ByteBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_STENCIL_BUFFER_BIT;
import static org.lwjgl.system.MemoryUtil.NULL;

public class CqWindow {

    public final int width;
    public final int height;
    public final int fps;

    public CqWindow(CqConfig config, CqScene scene) {
        width = config.width;
        height = config.height;
        fps = config.fps;

        if (scene == null) throw new NullPointerException("Graphics scene is null");
        if (!glfwInit()) throw new IllegalStateException("Unable to initialize GLFW");

        long window = glfwCreateWindow(width, height, "Chalq", NULL, NULL);
        if (window == NULL) {
            glfwTerminate();
            throw new RuntimeException("Failed to create the GLFW window");
        }

        glfwSetKeyCallback(window, new GLFWKeyCallback() {
            @Override
            public void invoke(long window, int key, int scancode, int action, int mods) {
                if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
                    glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
            }
        });

        GLFWVidMode vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        glfwSetWindowPos(window, (vidMode.width() - width) / 2, (vidMode.height() - height) / 2 );
        glfwShowWindow(window);
        glfwMakeContextCurrent(window);
        GL.createCapabilities();

        final long nvg = NanoVGGL3.nvgCreate(config.antialiasing ? NanoVGGL3.NVG_ANTIALIAS : 0);
        Cq.init(nvg, config.backgroundColor);
        Cq.width = config.width;
        Cq.height = config.height;
        scene.init();

        Mat3 identity = new Mat3();

        //  VID RECORDING
        final Rational framerate = Rational.make(1, 60);
        final Muxer muxer = Muxer.make("./lessgeddit.avi", null, "avi");
        final MuxerFormat format = muxer.getFormat();
        final Codec codec;
        codec = Codec.findEncodingCodec(format.getDefaultVideoCodecId());

        System.out.println("codec: " + codec);

        Encoder encoder = Encoder.make(codec);
        encoder.setWidth(1920);
        encoder.setHeight(1080);
        // We are going to use 420P as the format because that's what most video formats these days use
        final PixelFormat.Type pixelformat = PixelFormat.Type.PIX_FMT_YUV420P;
        encoder.setPixelFormat(pixelformat);
        encoder.setTimeBase(framerate);

        if (format.getFlag(MuxerFormat.Flag.GLOBAL_HEADER))
            encoder.setFlag(Encoder.Flag.FLAG_GLOBAL_HEADER, true);

        encoder.open(null, null);
        muxer.addNewStream(encoder);
        try {
            muxer.open(null, null);
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }

        MediaPictureConverter converter = null;
        final MediaPicture picture = MediaPicture.make(
                encoder.getWidth(),
                encoder.getHeight(),
                pixelformat);
        picture.setTimeBase(framerate);
        long timestamp = 0;

        System.out.println("quality: " + picture.getQuality());

        final MediaPacket packet = MediaPacket.make();

        int framesToEncode = 400;

        int[] pixelArray = new int[width * height];
        ByteBuffer pixelBuffer = ByteBuffer.allocateDirect(width * height * 3);
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        byte[] pixelBytes = ( (DataBufferByte) image.getRaster().getDataBuffer() ).getData();

        // while one is receiving data from GPU, other is sending data to CPU (simultaneous = faster)
        int pbo1 = GL30.glGenBuffers();
        int pbo2 = GL30.glGenBuffers();
        GL30.glBindBuffer(GL30.GL_PIXEL_PACK_BUFFER, pbo1);
        GL30.glBufferData(GL30.GL_PIXEL_PACK_BUFFER, width * height * 4, GL30.GL_STREAM_READ);
        GL30.glBindBuffer(GL30.GL_PIXEL_PACK_BUFFER, pbo2);
        GL30.glBufferData(GL30.GL_PIXEL_PACK_BUFFER, width * height * 4, GL30.GL_STREAM_READ);
        long readSync = 0;

        long lastTick = System.nanoTime();
        final long interval = 1000000000 / fps;
        while (!glfwWindowShouldClose(window)) {
            if (System.nanoTime() - lastTick >= interval) {

                Cq.delta = (System.nanoTime() - lastTick) / 1000000000f;
                Cq.time += Cq.delta;
                lastTick = System.nanoTime();

                NanoVG.nvgBeginFrame(nvg, width, height, 1);
                Cq.clearFrame();
                scene.processInterpolations();
                scene.drawRecursive(nvg, identity, false);
                scene.updateRecursive(); // update later, as custom drawing should be on top of drawn objects
                NanoVG.nvgEndFrame(nvg);

                Cq.frameTime = (System.nanoTime() - lastTick) / 1000000000f;

                glfwSwapBuffers(window);
                glfwPollEvents();

                float syncTime = 0, readPixelTime, fillArrayTime, fillImageTime;
                long start = System.nanoTime();

                if (readSync != 0) {
                    /*
                        wait for pixel data to be done moving to PBO from previous tick (wait unlimited time to not
                        lose any frames) - takes less time as that action was performed on the GPU, at the same time as
                        the rendering processes above
                     */
                    GL30.glBindBuffer(GL30.GL_PIXEL_PACK_BUFFER, pbo2);
                    GL30.glGetBufferSubData(GL30.GL_PIXEL_PACK_BUFFER, 0, pixelBuffer); // could load into either array or buffer
//                    pixelBuffer.get(pixelBytes);
                    for (int i = 0; i < pixelBuffer.remaining(); i++) {
                        pixelBytes[i] = pixelBuffer.get(i +  pixelBuffer.position());

                    }
//                        for (int i = 0; i < pixelArray.length; i++) {
//                            // int in ARGB form
//                            pixelArray[i] = 0xFF000000                          // A = 255
//                                    | ( pixelBuffer.get(i * 3    ) << 16 )  // R
//                                    | ( pixelBuffer.get(i * 3 + 1) <<  8 )  // G
//                                    | ( pixelBuffer.get(i * 3 + 2) <<  0 ); // B
//                        }

                    int syncStatus = ARBSync.glClientWaitSync(readSync, 0, Long.MAX_VALUE);
                    syncTime = (System.nanoTime() - start) / 1000000000f;
//                    start = System.nanoTime();
                    if (syncStatus == GL32.GL_ALREADY_SIGNALED || syncStatus == GL32.GL_CONDITION_SATISFIED) {

//                        GL30.glBindBuffer(GL30.GL_PIXEL_PACK_BUFFER, pbo1);
//                        GL30.glGetBufferSubData(GL30.GL_PIXEL_PACK_BUFFER, 0, pixelArray); // could load into either array or buffer
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

                readPixelTime = (System.nanoTime() - start) / 1000000000f;

//                start = System.nanoTime();
//                fillArrayTime = (System.nanoTime() - start) / 1000000000f;
//
//                start = System.nanoTime();
//                image.setRGB(0, 0, width, height, pixelArray, 0, width);
//                fillImageTime = (System.nanoTime() - start) / 1000000000f;

//                System.out.println("colour: " + String.format("0x%08X", pixelArray[1000]));
                System.out.println(String.format("0x%08X", image.getRGB(100, 100)));
                System.out.println("sync time: " + String.format("%.4f", syncTime) + "read pixels: " + String.format("%.4f", readPixelTime));// + ", fill array: " + String.format("%.4f", fillArrayTime) + ", fill image:" + String.format("%.4f", fillImageTime) );
                GL30.glBindBuffer(GL30.GL_PIXEL_PACK_BUFFER, 0); // unbind


                /** This is LIKELY not in YUV420P format, so we're going to convert it using some handy utilities. */
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
        }

        do {
            encoder.encode(packet, null);
            if (packet.isComplete())
                muxer.write(packet,  false);
        } while (packet.isComplete());

        muxer.close();

        glfwDestroyWindow(window);
        glfwTerminate();
    }


}
