package edu.luc.cs.cs371.topwords

import mainargs.{main, arg, ParserForMethods, Flag}
import org.slf4j.LoggerFactory

object Main:
  private val logger = LoggerFactory.getLogger(getClass)

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

    // Read words from stdin and split by non-alphanumeric characters
    val lines = scala.io.Source.stdin.getLines
    val words =
      import scala.language.unsafeNulls
      lines.flatMap(l => l.split("(?U)[^\\p{Alpha}0-9']+"))

    // Create processor with console observer
    val observer = ConsoleObserver()
    val processor = TopWordsProcessor(lengthAtLeast, windowSize, cloudSize, observer)

    // Process the word stream
    processor.processWords(words)

  def main(args: Array[String]): Unit =
    ParserForMethods(this).runOrExit(args): Unit
