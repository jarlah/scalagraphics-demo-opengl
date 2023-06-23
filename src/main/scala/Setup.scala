package com.github.jarlah.scalagraphics

trait Setup(val windowWidth: Int, val windowHeight: Int, val windowTitle: String) {
  def init(): Unit
  def setupDisplay(): Unit
  def cleanup(): Unit
  def isCloseRequested: Boolean
  def updateDisplay(): Unit
  def sync(): Unit

  def clear(): Unit

  val font: Font
}
