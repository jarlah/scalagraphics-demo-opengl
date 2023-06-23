package com.github.jarlah.scalagraphics

import org.lwjgl.glfw.GLFW.*
import org.lwjgl.nanovg.NanoVG.*
import org.lwjgl.nanovg.NanoVGGL3.*
import org.lwjgl.opengl.{GL, GL11}
import org.lwjgl.opengl.GL11.*
import org.lwjgl.system.MemoryUtil.NULL

trait OpenGLSetup(override val windowWidth: Int, override val windowHeight: Int, override val windowTitle: String, setWindowSize: (Int, Int) => Unit, setNanoVgPointer: Long => Unit) extends Setup with KeyManager {
  private var window: Long = _

  val font: Font = Font("Arialn", 14, Plain)

  def init(): Unit = {
    if (!glfwInit()) {
      throw new IllegalStateException("Unable to initialize GLFW")
    }
  }

  def setupDisplay(): Unit = {
    window = glfwCreateWindow(800, 600, windowTitle, NULL, NULL)
    if (window == NULL) {
      throw new RuntimeException("Failed to create the GLFW window")
    }

    glfwMakeContextCurrent(window)
    GL.createCapabilities()

    // Create the NanoVG context and font outside of the game loop
    val vg = nvgCreate(NVG_ANTIALIAS | NVG_STENCIL_STROKES)
    if (vg == NULL) {
      throw new RuntimeException("Could not init nanovg.")
    }

    val nanoVgFont = nvgCreateFont(vg, font.name, s"fonts/${font.name}.ttf")
    if (nanoVgFont == -1) {
      throw new RuntimeException("Could not add font.")
    }

    setNanoVgPointer(vg)

    glfwSetWindowSizeCallback(window, (window, width, height) => {
      GL11.glViewport(0, 0, width, height)
      setWindowSize(width, height)
    })

    glClearColor(1.0f, 1.0f, 1.0f, 1.0f)
  }

  def getWindow: Long = {
    window
  }

  def cleanup(): Unit = {
    glfwTerminate()
  }

  def isCloseRequested: Boolean = {
    glfwWindowShouldClose(window)
  }

  def updateDisplay(): Unit = {
    glfwSwapBuffers(window)
    glfwPollEvents()
  }

  def sync(): Unit = {
    glfwSwapInterval(1)
  }

  def clear(): Unit = {
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT)
  }
}
