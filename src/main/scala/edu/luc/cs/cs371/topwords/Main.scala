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

    // TODO: Implement main processing logic in future steps
    println("TopWords initialized with:")
    println(s"  Cloud size: $cloudSize")
    println(s"  Min length: $lengthAtLeast")
    println(s"  Window size: $windowSize")
    println("Waiting for input... (processing logic to be implemented)")

  def main(args: Array[String]): Unit =
    ParserForMethods(this).runOrExit(args): Unit
