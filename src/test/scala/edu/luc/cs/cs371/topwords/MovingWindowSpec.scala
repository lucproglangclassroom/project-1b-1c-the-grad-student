package edu.luc.cs.cs371.topwords

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class MovingWindowSpec extends AnyWordSpec with Matchers:

  "A MovingWindow" when {
    "newly created" should {
      "be empty" in {
        val cloud = WordCloud(5)
        val window = MovingWindow(3, cloud)
        window.isEmpty shouldBe true
        window.size shouldBe 0
        window.isFull shouldBe false
      }
    }

    "filling up" should {
      "add words to cloud" in {
        val cloud = WordCloud(5)
        val window = MovingWindow(3, cloud)

        window.addWord("hello")
        window.size shouldBe 1
        cloud.getTopWords() shouldBe "hello: 1"

        window.addWord("world")
        window.size shouldBe 2
        cloud.getTopWords() shouldBe "hello: 1 world: 1"
      }

      "detect when full" in {
        val cloud = WordCloud(5)
        val window = MovingWindow(3, cloud)

        window.addWord("one")
        window.isFull shouldBe false
        window.addWord("two")
        window.isFull shouldBe false
        window.addWord("three")
        window.isFull shouldBe true
      }

      "track word frequencies correctly" in {
        val cloud = WordCloud(5)
        val window = MovingWindow(3, cloud)

        window.addWord("hello")
        window.addWord("hello")
        window.addWord("world")

        cloud.getTopWords() shouldBe "hello: 2 world: 1"
      }
    }

    "exceeding window size" should {
      "remove oldest word from cloud" in {
        val cloud = WordCloud(5)
        val window = MovingWindow(3, cloud)

        window.addWord("first")
        window.addWord("second")
        window.addWord("third")
        // Window: [first, second, third]
        cloud.getTopWords() shouldBe "first: 1 second: 1 third: 1"

        window.addWord("fourth")
        // Window: [second, third, fourth] - "first" removed
        window.size shouldBe 3
        cloud.getTopWords() shouldBe "fourth: 1 second: 1 third: 1"
      }

      "handle repeated words sliding out" in {
        val cloud = WordCloud(5)
        val window = MovingWindow(3, cloud)

        window.addWord("hello")
        window.addWord("hello")
        window.addWord("world")
        // Window: [hello, hello, world] - hello:2, world:1
        cloud.getTopWords() shouldBe "hello: 2 world: 1"

        window.addWord("foo")
        // Window: [hello, world, foo] - first "hello" removed
        cloud.getTopWords() shouldBe "foo: 1 hello: 1 world: 1"
      }

      "maintain correct frequencies during sliding" in {
        val cloud = WordCloud(5)
        val window = MovingWindow(3, cloud)

        window.addWord("a")
        window.addWord("a")
        window.addWord("a")
        // Window: [a, a, a] - a:3
        cloud.getTopWords() shouldBe "a: 3"

        window.addWord("b")
        // Window: [a, a, b] - a:2, b:1
        cloud.getTopWords() shouldBe "a: 2 b: 1"

        window.addWord("c")
        // Window: [a, b, c] - a:1, b:1, c:1
        cloud.getTopWords() shouldBe "a: 1 b: 1 c: 1"

        window.addWord("d")
        // Window: [b, c, d] - a removed
        cloud.getTopWords() shouldBe "b: 1 c: 1 d: 1"
      }

      "stay at window size" in {
        val cloud = WordCloud(5)
        val window = MovingWindow(3, cloud)

        for i <- 1 to 10 do
          window.addWord(s"word$i")

        window.size shouldBe 3
        window.isFull shouldBe true
      }
    }

    "with window size 1" should {
      "only track most recent word" in {
        val cloud = WordCloud(5)
        val window = MovingWindow(1, cloud)

        window.addWord("first")
        cloud.getTopWords() shouldBe "first: 1"

        window.addWord("second")
        cloud.getTopWords() shouldBe "second: 1"

        window.addWord("third")
        cloud.getTopWords() shouldBe "third: 1"
      }
    }

    "with large window" should {
      "not remove words until full" in {
        val cloud = WordCloud(10)
        val window = MovingWindow(1000, cloud)

        for i <- 1 to 100 do
          window.addWord(s"word$i")

        window.size shouldBe 100
        cloud.size shouldBe 100
      }
    }
  }
