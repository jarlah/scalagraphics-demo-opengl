package com.github.jarlah.scalagraphics

import org.lwjgl.glfw.Callbacks.glfwFreeCallbacks
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL11.*
import org.lwjgl.system.MemoryUtil.*

import org.lwjgl.glfw.GLFWKeyCallbackI

import org.lwjgl.nanovg.NanoVG.*
import org.lwjgl.nanovg.NanoVGGL3.*
import org.lwjgl.system.MemoryStack.*
import org.lwjgl.system.MemoryUtil.*
import org.lwjgl.nanovg.{NVGColor, NanoVG}
import org.lwjgl.system.MemoryStack

import scala.language.postfixOps

import GraphicsIO.Color.*
import GraphicsIO.FontStyle

@main
def main(): Unit = {
  val setup = new OpenGLSetup with Setup(800, 600, "Snake")
  setup.init()
  setup.setupDisplay()

  val gameLogic = new SnakeGame()
  gameLogic.init(setup)

  val keyManager = new OpenGLKeyManager(setup.getWindow)

  while (!setup.isCloseRequested) {
    setup.clear()

    gameLogic.update(keyManager, setup)

    gameLogic.render(setup).run(setup.graphicsIO) match {
      case Left(error) =>
        // exit the game loop if an error occurs
        throw error // TODO: handle error
      case Right(_) =>
    }

    setup.updateDisplay()
    setup.sync()
  }

  setup.cleanup()
  System.exit(0)
}