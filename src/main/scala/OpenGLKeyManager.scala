package com.github.jarlah.scalagraphics

import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.{GLFWKeyCallback, GLFWKeyCallbackI}

trait OpenGLKeyManager extends KeyManager { self: OpenGLSetup =>
  var moveUp = false
  var moveDown = false
  var moveLeft = false
  var moveRight = false

  private val keyCallback: GLFWKeyCallbackI = new GLFWKeyCallbackI {
    override def invoke(window: Long, key: Int, scancode: Int, action: Int, mods: Int): Unit = {
      if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
        glfwSetWindowShouldClose(window, true)
      } else {
        val isPressed = action == GLFW_PRESS || action == GLFW_REPEAT
        key match {
          case GLFW_KEY_UP => moveUp = isPressed
          case GLFW_KEY_DOWN => moveDown = isPressed
          case GLFW_KEY_LEFT => moveLeft = isPressed
          case GLFW_KEY_RIGHT => moveRight = isPressed
          case _ =>
        }
      }
    }
  }

  def initKeyCallback: GLFWKeyCallback = glfwSetKeyCallback(getWindow, keyCallback)
}
