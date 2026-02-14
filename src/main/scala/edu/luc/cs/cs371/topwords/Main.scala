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
    observer: OutputObserver
  ): Unit =
    // Split lines into words by non-alphanumeric characters
    val words =
      import scala.language.unsafeNulls
      lines.flatMap(l => l.split("(?U)[^\\p{Alpha}0-9']+"))

    // Create processor and process the word stream
    val processor = TopWordsProcessor(lengthAtLeast, windowSize, cloudSize, observer)
    processor.processWords(words)

  @main
  def run(
    @arg(short = 'c', doc = "Size of word cloud (number of top words to display)")
    cloudSize: Int = 10,
    @arg(short = 'l', doc = "Minimum word length to consider")
    lengthAtLeast: Int = 6,
    @arg(short = 'w', doc = "Size of moving window (number of recent words to track)")
    windowSize: Int = 1000
  ): Unit =
    // Log the configuration parameters
    logger.debug(s"cloudSize=$cloudSize lengthAtLeast=$lengthAtLeast windowSize=$windowSize")

    // Validate arguments
    require(cloudSize > 0, "cloudSize must be positive")
    require(lengthAtLeast > 0, "lengthAtLeast must be positive")
    require(windowSize > 0, "windowSize must be positive")

    // Read from stdin and process
    val lines = scala.io.Source.stdin.getLines
    val observer = ConsoleObserver()
    processInputStream(lines, cloudSize, lengthAtLeast, windowSize, observer)

  def main(args: Array[String]): Unit =
    ParserForMethods(this).runOrExit(args): Unit
