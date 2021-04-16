package com.chalq.core;

import com.chalq.math.Mat3;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.nanovg.NanoVG;
import org.lwjgl.nanovg.NanoVGGL3;
import org.lwjgl.opengl.*;

import static org.lwjgl.glfw.GLFW.*;
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

        VideoRecorder recorder = null;
        if (config.outputMP4Path != null) recorder = new VideoRecorder(width, height, config.outputMP4Path);

        Mat3 identity = new Mat3();
        long lastTick = System.nanoTime();
        final long interval = 1000000000 / fps;
        while (!glfwWindowShouldClose(window)) {
            if (System.nanoTime() - lastTick >= interval) {

                if (config.constantTimeStep || recorder != null) {
                    Cq.delta = 1f / config.fps;
                } else {
                    Cq.delta = (System.nanoTime() - lastTick) / 1000000000f;
                }
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

                if (recorder != null) recorder.recordFrame();
            }
        }
        if (recorder != null) recorder.stopRecording();

        glfwDestroyWindow(window);
        glfwTerminate();
    }


}
