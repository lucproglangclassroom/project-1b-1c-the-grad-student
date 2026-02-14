package edu.luc.cs.cs371.topwords

/**
 * Processes a stream of words and generates word cloud updates.
 *
 * @param minLength minimum length for a word to be considered
 * @param windowSize size of the moving window
 * @param cloudSize number of top words to include in the cloud
 * @param observer observer to notify with word cloud updates
 * @param everyKSteps output word cloud every k steps (default: 1 = every step)
 * @param minFrequency minimum frequency to include word in cloud (default: 1)
 */
class TopWordsProcessor(
  minLength: Int,
  windowSize: Int,
  cloudSize: Int,
  observer: OutputObserver,
  everyKSteps: Int = 1,
  minFrequency: Int = 1
):
  require(minLength > 0, "minLength must be positive")
  require(windowSize > 0, "windowSize must be positive")
  require(cloudSize > 0, "cloudSize must be positive")
  require(everyKSteps > 0, "everyKSteps must be positive")
  require(minFrequency > 0, "minFrequency must be positive")

  private val cloud = WordCloud(cloudSize, minFrequency)
  private val window = MovingWindow(windowSize, cloud)
  private var stepCounter = 0

  /**
   * Processes a single word.
   * If the word meets length requirements, adds it to the window and
   * notifies the observer if the window is full.
   */
  def processWord(word: String): Unit =
    // Filter by minimum length and convert to lowercase for case-insensitive matching
    if word.length >= minLength then
      window.addWord(word.toLowerCase)

      // Only output when window is full (after first windowSize words)
      if window.isFull then
        stepCounter += 1
        if stepCounter % everyKSteps == 0 then
          observer.notify(cloud.getTopWords())

  /**
   * Processes an iterator of words.
   * Convenience method that calls processWord for each word.
   */
  def processWords(words: Iterator[String]): Unit =
    words.foreach(processWord)
