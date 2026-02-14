package edu.luc.cs.cs371.topwords

import scala.collection.mutable

/**
 * Observer trait for receiving word cloud updates.
 * Allows separation of processing logic from output concerns.
 */
trait OutputObserver:
  /**
   * Called when a new word cloud is available.
   * @param wordCloud the formatted word cloud string
   */
  def notify(wordCloud: String): Unit

/**
 * Console-based observer that prints word clouds to stdout.
 * Handles SIGPIPE (broken pipe) by checking for output errors and exiting gracefully.
 */
class ConsoleObserver extends OutputObserver:
  def notify(wordCloud: String): Unit =
    println(wordCloud)
    // Check for output errors (e.g., broken pipe/SIGPIPE)
    // Java converts SIGPIPE to IOException, which PrintStream converts to an error flag
    if System.out.checkError() then
      sys.exit(0)

/**
 * Test observer that collects word clouds in memory for verification.
 * Useful for unit testing without parsing console output.
 */
class TestObserver extends OutputObserver:
  private val outputs = mutable.ListBuffer.empty[String]

  def notify(wordCloud: String): Unit =
    outputs += wordCloud

  /**
   * Returns all word clouds that have been notified.
   */
  def getOutputs: Seq[String] = outputs.toSeq

  /**
   * Returns the number of outputs received.
   */
  def size: Int = outputs.size

  /**
   * Returns the most recent output, or None if no outputs yet.
   */
  def lastOutput: Option[String] =
    if outputs.isEmpty then None else Some(outputs.last)

  /**
   * Clears all collected outputs.
   */
  def clear(): Unit = outputs.clear()
