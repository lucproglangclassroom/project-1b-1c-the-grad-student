package edu.luc.cs.cs371.topwords

import scala.collection.mutable

/**
 * Tracks word frequencies and produces a word cloud of the top N words.
 *
 * @param cloudSize the number of top words to include in the cloud
 */
class WordCloud(cloudSize: Int):
  require(cloudSize > 0, "cloudSize must be positive")

  private val frequencies = mutable.HashMap.empty[String, Int]

  /**
   * Adds a word to the cloud, incrementing its frequency.
   */
  def addWord(word: String): Unit =
    val currentCount = frequencies.getOrElse(word, 0)
    frequencies(word) = currentCount + 1

  /**
   * Removes a word from the cloud, decrementing its frequency.
   * If the frequency reaches 0, the word is removed from tracking.
   */
  def removeWord(word: String): Unit =
    frequencies.get(word) match
      case Some(count) if count > 1 =>
        frequencies(word) = count - 1
      case Some(_) =>
        frequencies.remove(word)
      case None =>
        // Word not in map, do nothing

  /**
   * Returns the current word cloud as a formatted string.
   * Format: "word1: freq1 word2: freq2 ..."
   * Words are sorted by frequency (descending), then alphabetically.
   * Only includes the top cloudSize words.
   */
  def getTopWords(): String =
    frequencies
      .toSeq
      .sortBy { case (word, freq) => (-freq, word) } // Sort by freq desc, then word asc
      .take(cloudSize)
      .map { case (word, freq) => s"$word: $freq" }
      .mkString(" ")

  /**
   * Returns the number of unique words currently tracked.
   */
  def size: Int = frequencies.size

  /**
   * Returns true if no words are being tracked.
   */
  def isEmpty: Boolean = frequencies.isEmpty
