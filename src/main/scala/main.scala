package com.github.jarlah.scalagraphics

import scala.language.postfixOps

@main
def main(): Unit = {
  val graphics = new OpenGLGraphics()

  val setup = new OpenGLSetup(400, 300, "Snake", graphics.setWindowSize)
    with Setup
    with OpenGLKeyManager

  setup.init()
  setup.setupDisplay()
  setup.initKeyCallback

  graphics.setWindowSize(setup.windowWidth, setup.windowHeight)
  graphics.setupShaderProgram()
  graphics.setupRectangle()
  graphics.setFont(Some(setup.font))
  graphics.setupNanoVg()

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