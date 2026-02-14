package edu.luc.cs.cs371.topwords

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class ScalabilitySpec extends AnyWordSpec with Matchers:

  "TopWords scalability" should {
    "handle large input without excessive memory" in {
      val observer = TestObserver()
      val processor = TopWordsProcessor(
        minLength = 4,
        windowSize = 1000,
        cloudSize = 10,
        observer = observer
      )

      // Generate 100,000 words
      val words = Iterator.continually(Seq("hello", "world", "scala", "programming", "test")).flatten.take(100000)

      // This should complete without running out of memory
      processor.processWords(words)

      // Should have produced many outputs after window filled
      observer.size should be > 90000
    }

    "maintain constant space with sliding window" in {
      val observer = TestObserver()
      val processor = TopWordsProcessor(
        minLength = 4,
        windowSize = 1000,
        cloudSize = 10,
        observer = observer
      )

      // Generate 50,000 words - memory should stay constant due to sliding window
      for i <- 1 to 50000 do
        processor.processWord(s"word${i % 100}") // Cycle through 100 different words

      // Should have produced outputs after window filled
      observer.size should be > 49000

      // Verify the window is working (frequency should be reasonable, not cumulative)
      val lastOutput = observer.lastOutput.getOrElse("")
      // With window size 1000 and 100 unique words cycling, max frequency should be around 10-20
      lastOutput should not include("word0: 500") // Would indicate cumulative counting
    }

    "handle repeated words efficiently" in {
      val observer = TestObserver()
      val processor = TopWordsProcessor(
        minLength = 4,
        windowSize = 1000,
        cloudSize = 5,
        observer = observer
      )

      // Generate 10,000 instances of the same word
      val words = Iterator.fill(10000)("testing")
      processor.processWords(words)

      // Should complete quickly and produce correct output
      observer.size shouldBe 9001 // 10000 - 999 (window size)
      observer.lastOutput shouldBe Some("testing: 1000")
    }

    "process diverse vocabulary at scale" in {
      val observer = TestObserver()
      val processor = TopWordsProcessor(
        minLength = 4,
        windowSize = 1000,
        cloudSize = 10,
        observer = observer
      )

      // Generate 10,000 unique words
      val words = (1 to 10000).iterator.map(i => s"word$i")
      processor.processWords(words)

      // Should handle large vocabulary
      observer.size shouldBe 9001
      // Last output should show words from the most recent window
      val lastOutput = observer.lastOutput.getOrElse("")
      lastOutput should not be empty
    }
  }
