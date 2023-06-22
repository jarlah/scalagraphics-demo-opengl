package com.github.jarlah.scalagraphics

import scala.language.postfixOps

@main
def main(): Unit = {
  val graphics = new OpenGLGraphics()
  graphics.setWindowSize(800, 600)

  val setup = new OpenGLSetup(graphics.setWindowSize, graphics.setNanoVgPointer) with Setup(800, 600, "Snake")
  setup.init()
  setup.setupDisplay()

  graphics.setupShaderProgram()
  graphics.setupRectangle()

  val snakeGame = new SnakeGame()
  snakeGame.init(setup)

  val keyManager = new OpenGLKeyManager(setup.getWindow)

  while (!setup.isCloseRequested) {
    setup.clear()

    snakeGame.update(keyManager, setup)

    graphics.run(snakeGame.render(setup)).getOrElse(throw new RuntimeException("Failed to render"))

    setup.updateDisplay()
    setup.sync()
  }

  setup.cleanup()
  System.exit(0)
}