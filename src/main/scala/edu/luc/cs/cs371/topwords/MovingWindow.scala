package edu.luc.cs.cs371.topwords

import scala.collection.mutable

/**
 * Maintains a moving window of the most recent words, updating a WordCloud
 * as words enter and leave the window.
 *
 * @param windowSize the maximum number of words to keep in the window
 * @param wordCloud the WordCloud to update as words enter/leave
 */
class MovingWindow(windowSize: Int, wordCloud: WordCloud):
  require(windowSize > 0, "windowSize must be positive")

  private val buffer = mutable.Queue.empty[String]

  /**
   * Adds a word to the window. If the window is full, removes the oldest word.
   * Updates the WordCloud accordingly.
   */
  def addWord(word: String): Unit =
    // Add new word to cloud
    wordCloud.addWord(word)
    buffer.enqueue(word)

    // If window is full, remove oldest word
    if buffer.size > windowSize then
      val oldest = buffer.dequeue()
      wordCloud.removeWord(oldest)

  /**
   * Returns the current size of the window (number of words currently stored).
   */
  def size: Int = buffer.size

  /**
   * Returns true if the window is at full capacity.
   */
  def isFull: Boolean = buffer.size >= windowSize

  /**
   * Returns true if the window is empty.
   */
  def isEmpty: Boolean = buffer.isEmpty
