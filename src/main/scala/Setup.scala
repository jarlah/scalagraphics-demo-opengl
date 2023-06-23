package com.github.jarlah.scalagraphics

trait Setup {
  val windowWidth: Int
  val windowHeight: Int
  val windowTitle: String
  val font: Font
  val background: Image = loadImageFromDisk("assets/welcome.jpeg")
  def init(): Unit
  def setupDisplay(): Unit
  def cleanup(): Unit
  def isCloseRequested: Boolean
  def updateDisplay(): Unit
  def sync(): Unit
  def clear(): Unit

  import javax.imageio.ImageIO
  import java.io.File
  import java.awt.image.BufferedImage

  def loadImageFromDisk(filename: String): Image = {
    val img: BufferedImage = ImageIO.read(new File(filename))
    val width: Int = img.getWidth
    val height: Int = img.getHeight
    val pixels: Array[Int] = new Array[Int](width * height)
    img.getRGB(0, 0, width, height, pixels, 0, width)
    Image(width, height, pixels)
  }

}
