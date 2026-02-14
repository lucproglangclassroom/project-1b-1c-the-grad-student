package edu.luc.cs.cs371.topwords

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class OutputObserverSpec extends AnyWordSpec with Matchers:

  "A TestObserver" when {
    "newly created" should {
      "be empty" in {
        val observer = TestObserver()
        observer.size shouldBe 0
        observer.getOutputs shouldBe empty
        observer.lastOutput shouldBe None
      }
    }

    "receiving notifications" should {
      "collect single output" in {
        val observer = TestObserver()
        observer.notify("hello: 1")
        observer.size shouldBe 1
        observer.getOutputs shouldBe Seq("hello: 1")
        observer.lastOutput shouldBe Some("hello: 1")
      }

      "collect multiple outputs in order" in {
        val observer = TestObserver()
        observer.notify("first: 1")
        observer.notify("second: 2")
        observer.notify("third: 3")

        observer.size shouldBe 3
        observer.getOutputs shouldBe Seq("first: 1", "second: 2", "third: 3")
        observer.lastOutput shouldBe Some("third: 3")
      }

      "handle empty string notifications" in {
        val observer = TestObserver()
        observer.notify("")
        observer.size shouldBe 1
        observer.getOutputs shouldBe Seq("")
      }
    }

    "clearing outputs" should {
      "remove all collected outputs" in {
        val observer = TestObserver()
        observer.notify("first: 1")
        observer.notify("second: 2")
        observer.clear()

        observer.size shouldBe 0
        observer.getOutputs shouldBe empty
        observer.lastOutput shouldBe None
      }

      "allow new notifications after clear" in {
        val observer = TestObserver()
        observer.notify("before: 1")
        observer.clear()
        observer.notify("after: 2")

        observer.size shouldBe 1
        observer.getOutputs shouldBe Seq("after: 2")
      }
    }
  }

  "A ConsoleObserver" should {
    "write to stdout without errors" in {
      val observer = ConsoleObserver()
      noException should be thrownBy {
        observer.notify("test: 1")
        observer.notify("hello: 5 world: 3")
        observer.notify("")
      }
    }

    "handle multiple notifications" in {
      val observer = ConsoleObserver()
      noException should be thrownBy {
        for i <- 1 to 10 do
          observer.notify(s"word: $i")
      }
    }
  }
