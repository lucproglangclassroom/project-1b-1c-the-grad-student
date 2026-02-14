package edu.luc.cs.cs371.topwords

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class TopWordsProcessorSpec extends AnyWordSpec with Matchers:

  "A TopWordsProcessor" when {
    "processing words below minimum length" should {
      "ignore them" in {
        val observer = TestObserver()
        val processor = TopWordsProcessor(
          minLength = 3,
          windowSize = 2,
          cloudSize = 5,
          observer = observer
        )

        processor.processWord("a")
        processor.processWord("b")
        processor.processWord("hi")

        // Nothing should be output because all words are too short
        observer.size shouldBe 0
      }
    }

    "processing words meeting minimum length" should {
      "add them to the window" in {
        val observer = TestObserver()
        val processor = TopWordsProcessor(
          minLength = 2,
          windowSize = 2,
          cloudSize = 5,
          observer = observer
        )

        processor.processWord("aa")
        // Window not full yet - no output
        observer.size shouldBe 0

        processor.processWord("bb")
        // Window is now full - should output
        observer.size shouldBe 1
        observer.lastOutput shouldBe Some("aa: 1 bb: 1")
      }

      "handle case-insensitive matching" in {
        val observer = TestObserver()
        val processor = TopWordsProcessor(
          minLength = 2,
          windowSize = 3,
          cloudSize = 5,
          observer = observer
        )

        processor.processWord("Hello")
        processor.processWord("HELLO")
        processor.processWord("hello")

        // All three "hello" variants should be counted together
        observer.size shouldBe 1
        observer.lastOutput shouldBe Some("hello: 3")
      }
    }

    "waiting for window to fill" should {
      "not output before window is full" in {
        val observer = TestObserver()
        val processor = TopWordsProcessor(
          minLength = 2,
          windowSize = 5,
          cloudSize = 3,
          observer = observer
        )

        processor.processWord("one")
        processor.processWord("two")
        processor.processWord("three")
        processor.processWord("four")

        // Only 4 words, window size is 5 - no output yet
        observer.size shouldBe 0
      }

      "output once window is full" in {
        val observer = TestObserver()
        val processor = TopWordsProcessor(
          minLength = 2,
          windowSize = 3,
          cloudSize = 3,
          observer = observer
        )

        processor.processWord("one")
        processor.processWord("two")
        observer.size shouldBe 0

        processor.processWord("three")
        // Window is now full
        observer.size shouldBe 1
      }

      "output after every word once window is full" in {
        val observer = TestObserver()
        val processor = TopWordsProcessor(
          minLength = 2,
          windowSize = 3,
          cloudSize = 3,
          observer = observer
        )

        processor.processWord("one")
        processor.processWord("two")
        processor.processWord("three")
        observer.size shouldBe 1

        processor.processWord("four")
        observer.size shouldBe 2

        processor.processWord("five")
        observer.size shouldBe 3
      }
    }

    "processing word streams" should {
      "match example from assignment" in {
        val observer = TestObserver()
        val processor = TopWordsProcessor(
          minLength = 2,
          windowSize = 5,
          cloudSize = 3,
          observer = observer
        )

        // Process: a b c aa bb cc aa bb aa bb
        val words = Iterator("a", "b", "c", "aa", "bb", "cc", "aa", "bb", "aa", "bb")
        processor.processWords(words)

        // First output after 5th word (cc)
        val outputs = observer.getOutputs
        outputs.size should be >= 5

        // After processing "aa bb cc aa bb": bb:2, aa:2, cc:1
        outputs(0) shouldBe "aa: 2 bb: 2 cc: 1"
      }

      "handle empty iterator" in {
        val observer = TestObserver()
        val processor = TopWordsProcessor(
          minLength = 2,
          windowSize = 3,
          cloudSize = 3,
          observer = observer
        )

        processor.processWords(Iterator.empty)
        observer.size shouldBe 0
      }

      "filter short words correctly" in {
        val observer = TestObserver()
        val processor = TopWordsProcessor(
          minLength = 3,
          windowSize = 2,
          cloudSize = 2,
          observer = observer
        )

        // Mix of short and long words: "a" (skip), "bb" (skip), "ccc" (keep), "dddd" (keep)
        val words = Iterator("a", "bb", "ccc", "dddd")
        processor.processWords(words)

        // Should only process "ccc" and "dddd"
        observer.size shouldBe 1
        observer.lastOutput shouldBe Some("ccc: 1 dddd: 1")
      }

      "handle repeated words in stream" in {
        val observer = TestObserver()
        val processor = TopWordsProcessor(
          minLength = 2,
          windowSize = 3,
          cloudSize = 2,
          observer = observer
        )

        val words = Iterator("hello", "hello", "world", "hello")
        processor.processWords(words)

        val outputs = observer.getOutputs
        // After "hello hello world": hello:2, world:1
        outputs(0) shouldBe "hello: 2 world: 1"
        // After "hello world hello" (first hello dropped): hello:2, world:1
        outputs(1) shouldBe "hello: 2 world: 1"
      }
    }

    "with small window size" should {
      "handle window size of 1" in {
        val observer = TestObserver()
        val processor = TopWordsProcessor(
          minLength = 2,
          windowSize = 1,
          cloudSize = 2,
          observer = observer
        )

        processor.processWord("first")
        processor.processWord("second")
        processor.processWord("third")

        val outputs = observer.getOutputs
        outputs.size shouldBe 3
        outputs(0) shouldBe "first: 1"
        outputs(1) shouldBe "second: 1"
        outputs(2) shouldBe "third: 1"
      }
    }
  }
