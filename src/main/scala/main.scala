package com.github.jarlah.scalagraphics

import org.lwjgl.glfw.Callbacks.glfwFreeCallbacks
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL11.*
import org.lwjgl.system.MemoryUtil.*
import GraphicsIO.Color.*

import org.lwjgl.glfw.GLFWKeyCallbackI

import scala.language.postfixOps
import org.lwjgl.nanovg.NanoVG.*
import org.lwjgl.nanovg.NanoVGGL3.*
import org.lwjgl.system.MemoryStack.*
import org.lwjgl.system.MemoryUtil.*
import org.lwjgl.nanovg.{NVGColor, NanoVG}
import org.lwjgl.system.MemoryStack

case class Point(x: Int, y: Int)

case class Snake(body: List[Point], var direction: Point) {

  def move(screenWidth: Int, screenHeight: Int): Snake = {
    val newHead = Point((body.head.x + direction.x + screenWidth) % screenWidth, (body.head.y + direction.y + screenHeight) % screenHeight)
    copy(body = newHead :: body.dropRight(1))
  }

  def eat(apple: Apple): Boolean = body.head == apple.position

  def grow: Snake = copy(body = body.head :: body)

  def render: GraphicsOp[Unit] =
    GraphicsOp.setColor(Green) >> body.foldLeft(GraphicsOp.pure(())) { (acc, point) =>
      acc >> GraphicsOp.fillRect(point.x, point.y, 10, 10)
    }
}

case class Apple(position: Point) {
  def render: GraphicsOp[Unit] =
    GraphicsOp.setColor(Red) >> GraphicsOp.fillRect(position.x, position.y, 10, 10)
}


@main
def main(): Unit = {
  if (!glfwInit())
    throw new IllegalStateException("Unable to initialize GLFW")

  glfwDefaultWindowHints()
  glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE)
  glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE)

  val window = glfwCreateWindow(800, 600, "Hello World!", NULL, NULL)
  if (window == NULL)
    throw new RuntimeException("Failed to create the GLFW window")

  glfwSetKeyCallback(window, (window, key, scancode, action, mods) => {
    if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
      glfwSetWindowShouldClose(window, true)
  })

  val vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor())
  glfwSetWindowPos(window, (vidmode.width() - 800) / 2, (vidmode.height() - 600) / 2)

  glfwMakeContextCurrent(window)
  glfwSwapInterval(1)
  glfwShowWindow(window)

  GL.createCapabilities()

  val currentFont = GraphicsIO.Font("Arialn", 14, 0)

  // Create the NanoVG context and font outside of the game loop
  val vg = nvgCreate(NVG_ANTIALIAS | NVG_STENCIL_STROKES)
  if (vg == NULL) {
    throw new RuntimeException("Could not init nanovg.")
  }

  val font = nvgCreateFont(vg, currentFont.name, s"fonts/${currentFont.name}.ttf")
  if (font == -1) {
    throw new RuntimeException("Could not add font.")
  }

  val graphics = new OpenGLGraphicsIO()
  graphics.setupShaderProgram()
  graphics.setupRectangle()
  graphics.setWindowSize(800, 600)
  graphics.setNanoVgPointer(vg)

  glfwSetWindowSizeCallback(window, (window, width, height) => {
    GL11.glViewport(0, 0, width, height)
    graphics.setWindowSize(width, height)
  })

  glfwSwapInterval(1)

  var moveUp = false
  var moveDown = false
  var moveLeft = false
  var moveRight = false

  val keyCallback = new GLFWKeyCallbackI {
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

  glfwSetKeyCallback(window, keyCallback)

  val CELL_SIZE = 10
  val SNAKE_SPEED: Int = CELL_SIZE
  var snake = Snake(List(Point((200 / CELL_SIZE) * CELL_SIZE, (200 / CELL_SIZE) * CELL_SIZE)), Point(SNAKE_SPEED, 0))
  var apple = Apple(Point((Math.random() * (graphics.getWindowWidth / CELL_SIZE)).toInt * CELL_SIZE, (Math.random() * (graphics.getWindowHeight / CELL_SIZE)).toInt * CELL_SIZE))
  var timer = 0
  var updateInterval = 10.0f
  val fontColor = GraphicsIO.Color.Black
  val backgroundColor = GraphicsIO.Color.White

  glClearColor(backgroundColor.r, backgroundColor.g, backgroundColor.b, backgroundColor.a)

  while (!glfwWindowShouldClose(window)) {
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT)

    if (moveUp) snake = snake.copy(direction = Point(0, -SNAKE_SPEED))
    if (moveDown) snake = snake.copy(direction = Point(0, SNAKE_SPEED))
    if (moveLeft)  snake = snake.copy(direction = Point(-SNAKE_SPEED, 0))
    if (moveRight)  snake = snake.copy(direction = Point(SNAKE_SPEED, 0))

    timer += 1
    if (timer >= updateInterval) {
      timer = 0
      snake = snake.move(graphics.getWindowWidth, graphics.getWindowHeight)
      if (snake.eat(apple)) {
        updateInterval = updateInterval - 0.1f
        snake = snake.grow
        apple = apple.copy(position = Point((Math.random() * (graphics.getWindowWidth / CELL_SIZE)).toInt * CELL_SIZE, (Math.random() * (graphics.getWindowHeight / CELL_SIZE)).toInt * CELL_SIZE))
      }
    }

    val render = for {
      _ <- GraphicsOp.setColor(Black)
      _ <- GraphicsOp.setFont(currentFont)
      _ <- GraphicsOp.drawString("Score: " + (snake.body.length - 1), 10, graphics.getWindowHeight - 40)
      _ <- GraphicsOp.drawString("Interval: " + updateInterval, 10, graphics.getWindowHeight - 20)
      _ <- snake.render
      _ <- apple.render
    } yield ()

    render.run(graphics) match {
      case Left(error) =>
        // exit the game loop if an error occurs
        throw error // TODO: handle error
      case Right(_) =>
    }

    glfwSwapBuffers(window)
    glfwPollEvents()
  }

  glfwFreeCallbacks(window)
  glfwDestroyWindow(window)
  glfwTerminate()
}
