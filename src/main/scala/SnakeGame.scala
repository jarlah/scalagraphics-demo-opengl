package com.github.jarlah.scalagraphics

import cats.implicits.catsSyntaxFlatMapOps
import GraphicsIO.*

class SnakeGame {
  import SnakeGame._

  val CELL_SIZE = 10
  val SNAKE_SPEED: Int = CELL_SIZE
  var timer: Int = 0
  var updateInterval: Float = 0f

  var snake: Snake = _
  var apple: Apple = _

  def init(setup: Setup): Unit = {
    snake = Snake(List(Point((200 / CELL_SIZE) * CELL_SIZE, (200 / CELL_SIZE) * CELL_SIZE)), Point(SNAKE_SPEED, 0))
    apple = Apple(Point((Math.random() * (setup.windowWidth / CELL_SIZE)).toInt * CELL_SIZE, (Math.random() * (setup.windowHeight / CELL_SIZE)).toInt * CELL_SIZE))
    timer = 0
    updateInterval = 10.0f
  }

  def update(keyManager: KeyManager, setup: Setup): Unit = {
    if (keyManager.moveUp) snake = snake.copy(direction = Point(0, -SNAKE_SPEED))
    if (keyManager.moveDown) snake = snake.copy(direction = Point(0, SNAKE_SPEED))
    if (keyManager.moveLeft) snake = snake.copy(direction = Point(-SNAKE_SPEED, 0))
    if (keyManager.moveRight) snake = snake.copy(direction = Point(SNAKE_SPEED, 0))

    timer += 1
    if (timer >= updateInterval) {
      timer = 0
      snake = snake.move(setup.windowWidth, setup.windowHeight)
      if (snake.eat(apple)) {
        updateInterval = updateInterval - 0.1f
        snake = snake.grow
        apple = apple.copy(position = Point((Math.random() * (setup.windowWidth / CELL_SIZE)).toInt * CELL_SIZE, (Math.random() * (setup.windowHeight / CELL_SIZE)).toInt * CELL_SIZE))
      }
    }
  }

  def render(setup: Setup): GraphicsIO[Unit] =
    for {
      _ <- setColor(Black)
      _ <- setFont(setup.font)
      _ <- drawString("Score: " + (snake.body.length - 1), 10, setup.windowHeight - 40)
      _ <- drawString("Interval: " + updateInterval, 10, setup.windowHeight - 20)
      _ <- snake.render
      _ <- apple.render
    } yield ()
}

object SnakeGame {
  case class Point(x: Int, y: Int)

  case class Snake(body: List[Point], var direction: Point) {

    def move(screenWidth: Int, screenHeight: Int): Snake = {
      val newHead = Point((body.head.x + direction.x + screenWidth) % screenWidth, (body.head.y + direction.y + screenHeight) % screenHeight)
      copy(body = newHead :: body.dropRight(1))
    }

    def eat(apple: Apple): Boolean = body.head == apple.position

    def grow: Snake = copy(body = body.head :: body)

    def render: GraphicsIO[Unit] =
      setColor(Green) >> body.foldLeft(pure(())) { (acc, point) =>
        acc >> fillRect(point.x, point.y, 10, 10)
      }
  }

  case class Apple(position: Point) {
    def render: GraphicsIO[Unit] =
      setColor(Red) >> fillRect(position.x, position.y, 10, 10)
  }

}