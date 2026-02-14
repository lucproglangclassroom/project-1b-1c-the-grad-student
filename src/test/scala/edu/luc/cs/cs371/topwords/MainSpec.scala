package edu.luc.cs.cs371.topwords

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class MainSpec extends AnyWordSpec with Matchers:

  "Main.processInputStream" should {
    "process simple input" in {
      val lines = Iterator("hello world", "hello scala")
      val observer = TestObserver()

      Main.processInputStream(
        lines = lines,
        cloudSize = 3,
        lengthAtLeast = 4,
        windowSize = 3,
        observer = observer
      )

      observer.size should be >= 1
      observer.getOutputs.last should include("hello")
    }

    "filter by minimum length" in {
      val lines = Iterator("a bb ccc dddd")
      val observer = TestObserver()

      Main.processInputStream(
        lines = lines,
        cloudSize = 5,
        lengthAtLeast = 3,
        windowSize = 2,
        observer = observer
      )

      observer.size shouldBe 1
      observer.lastOutput shouldBe Some("ccc: 1 dddd: 1")
    }

    "handle empty input" in {
      val lines = Iterator.empty[String]
      val observer = TestObserver()

      Main.processInputStream(
        lines = lines,
        cloudSize = 5,
        lengthAtLeast = 2,
        windowSize = 3,
        observer = observer
      )

      observer.size shouldBe 0
    }

    "handle punctuation and special characters" in {
      val lines = Iterator("hello, world!", "it's working")
      val observer = TestObserver()

      Main.processInputStream(
        lines = lines,
        cloudSize = 5,
        lengthAtLeast = 2,
        windowSize = 4,
        observer = observer
      )

      observer.size should be >= 1
    }

    "match assignment example" in {
      val lines = Iterator("a b c", "aa bb cc", "aa bb aa bb")
      val observer = TestObserver()

      Main.processInputStream(
        lines = lines,
        cloudSize = 3,
        lengthAtLeast = 2,
        windowSize = 5,
        observer = observer
      )

      val outputs = observer.getOutputs
      outputs.size should be >= 1
      outputs(0) shouldBe "aa: 2 bb: 2 cc: 1"
    }

    "respect cloud size limit" in {
      val lines = Iterator("one two three four five six")
      val observer = TestObserver()

      Main.processInputStream(
        lines = lines,
        cloudSize = 2,
        lengthAtLeast = 3,
        windowSize = 5,
        observer = observer
      )

      val output = observer.lastOutput.getOrElse("")
      output.split(" ").count(_.contains(":")) shouldBe 2
    }

    "handle repeated words" in {
      val lines = Iterator("test test test other")
      val observer = TestObserver()

      Main.processInputStream(
        lines = lines,
        cloudSize = 5,
        lengthAtLeast = 4,
        windowSize = 4,
        observer = observer
      )

      observer.size should be >= 1
      observer.getOutputs.last should include("test: 3")
    }
  }
