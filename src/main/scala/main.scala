package com.github.jarlah.scalagraphics

import scala.language.postfixOps

@main
def main(): Unit = {
  val graphics = new OpenGLGraphics()
  graphics.setWindowSize(800, 600)

  val setup = new OpenGLSetup(graphics.setWindowSize, graphics.setNanoVgPointer)
    with Setup(800, 600, "Snake")
    with OpenGLKeyManager
  setup.init()
  setup.setupDisplay()
  setup.initKeyCallback

  graphics.setupShaderProgram()
  graphics.setupRectangle()

  val snakeGame = new SnakeGame()
  snakeGame.init(setup)

  while (!setup.isCloseRequested) {
    setup.clear()

    snakeGame.update(setup)

    graphics.run(snakeGame.render(setup))

    setup.updateDisplay()
    setup.sync()
  }

  setup.cleanup()
  System.exit(0)
}