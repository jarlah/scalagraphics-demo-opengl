package com.github.jarlah.scalagraphics

import org.lwjgl.glfw.Callbacks.glfwFreeCallbacks
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL11.*
import org.lwjgl.system.MemoryUtil.*
import GraphicsIO.Color.*

@main
def main(): Unit = {
  if (!glfwInit())
    throw new IllegalStateException("Unable to initialize GLFW")

  glfwDefaultWindowHints()
  glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE)
  glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE)

  val window = glfwCreateWindow(300, 300, "Hello World!", NULL, NULL)
  if (window == NULL)
    throw new RuntimeException("Failed to create the GLFW window")

  glfwSetKeyCallback(window, (window, key, scancode, action, mods) => {
    if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
      glfwSetWindowShouldClose(window, true)
  })

  val vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor())
  glfwSetWindowPos(window, (vidmode.width() - 300) / 2, (vidmode.height() - 300) / 2)

  glfwMakeContextCurrent(window)
  glfwSwapInterval(1)
  glfwShowWindow(window)

  GL.createCapabilities()

  val graphics = new OpenGLGraphicsIO()
  graphics.setupShaderProgram()
  graphics.setupRectangle()
  graphics.setWindowSize(300, 300)

  glfwSetWindowSizeCallback(window, (window, width, height) => {
    GL11.glViewport(0, 0, width, height)
    graphics.setWindowSize(width, height)
  })

  while (!glfwWindowShouldClose(window)) {
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT)

    drawGreenRectangle(30, 100, 100, 100).run(graphics)
      .getOrElse(throw new RuntimeException("Failed to set color"))

    glfwSwapBuffers(window)
    glfwPollEvents()
  }

  glfwFreeCallbacks(window)
  glfwDestroyWindow(window)
  glfwTerminate()
}

def drawGreenRectangle(x: Int, y: Int, width: Int, height: Int): GraphicsOp[Unit] =
  GraphicsOp.setColor(Green)
    >> GraphicsOp.drawRect(x, y, width, height)
