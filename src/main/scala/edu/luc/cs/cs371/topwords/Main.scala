package edu.luc.cs.cs371.topwords

import mainargs.{main, arg, ParserForMethods, Flag}
import org.slf4j.LoggerFactory

object Main:
  private val logger = LoggerFactory.getLogger(getClass)

  /**
   * Processes lines of text into words and generates word clouds.
   * Extracted for testability.
   */
  def processInputStream(
    lines: Iterator[String],
    cloudSize: Int,
    lengthAtLeast: Int,
    windowSize: Int,
    observer: OutputObserver,
    everyKSteps: Int = 1,
    minFrequency: Int = 1
  ): Unit =
    // Split lines into words by non-alphanumeric characters
    val words =
      import scala.language.unsafeNulls
      lines.flatMap(l => l.split("(?U)[^\\p{Alpha}0-9']+"))

    // Create processor and process the word stream
    val processor = TopWordsProcessor(lengthAtLeast, windowSize, cloudSize, observer, everyKSteps, minFrequency)
    processor.processWords(words)

  @main
  def run(
    @arg(short = 'c', doc = "Size of word cloud (number of top words to display)")
    cloudSize: Int = 10,
    @arg(short = 'l', doc = "Minimum word length to consider")
    lengthAtLeast: Int = 6,
    @arg(short = 'w', doc = "Size of moving window (number of recent words to track)")
    windowSize: Int = 1000,
    @arg(short = 'k', doc = "Output word cloud every k steps (default: every step)")
    everyKSteps: Int = 1,
    @arg(short = 'f', doc = "Minimum frequency to include word in cloud")
    minFrequency: Int = 1
  ): Unit =
    // Log the configuration parameters
    logger.debug(s"cloudSize=$cloudSize lengthAtLeast=$lengthAtLeast windowSize=$windowSize everyKSteps=$everyKSteps minFrequency=$minFrequency")

    // Validate arguments
    require(cloudSize > 0, "cloudSize must be positive")
    require(lengthAtLeast > 0, "lengthAtLeast must be positive")
    require(windowSize > 0, "windowSize must be positive")
    require(everyKSteps > 0, "everyKSteps must be positive")
    require(minFrequency > 0, "minFrequency must be positive")

    // Read from stdin and process
    val lines = scala.io.Source.stdin.getLines
    val observer = ConsoleObserver()
    processInputStream(lines, cloudSize, lengthAtLeast, windowSize, observer, everyKSteps, minFrequency)

  def main(args: Array[String]): Unit =
    ParserForMethods(this).runOrExit(args): Unit
