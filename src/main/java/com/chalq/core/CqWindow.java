package com.chalq.core;

import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.nanovg.NVGColor;
import org.lwjgl.nanovg.NanoVG;
import org.lwjgl.nanovg.NanoVGGL3;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL30;

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

        GLFWVidMode vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        glfwSetWindowPos(window, (vidMode.width() - width) / 2, (vidMode.height() - height) / 2 );
        glfwShowWindow(window);
        glfwMakeContextCurrent(window);
        GL.createCapabilities();

        final long nvg = NanoVGGL3.nvgCreate(config.antialiasing ? NanoVGGL3.NVG_ANTIALIAS : 0);
        Cq.init(nvg, config.backgroundColor);

        long lastTick = System.nanoTime();
        final long interval = 1000000000 / fps;
        while (!glfwWindowShouldClose(window)) {
            if (System.nanoTime() - lastTick >= interval) {

                Cq.delta = (System.nanoTime() - lastTick) / 1000000000f;
                Cq.time += Cq.delta;
                lastTick = System.nanoTime();

                NanoVG.nvgBeginFrame(nvg, width, height, 1);
                scene.update();
                NanoVG.nvgEndFrame(nvg);

                glfwSwapBuffers(window);
                glfwPollEvents();
            }
        }

        glfwDestroyWindow(window);
        glfwTerminate();
    }


}
