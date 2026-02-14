package edu.luc.cs.cs371.topwords

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class WordCloudSpec extends AnyWordSpec with Matchers:

  "A WordCloud" when {
    "newly created" should {
      "be empty" in {
        val cloud = WordCloud(5)
        cloud.isEmpty shouldBe true
        cloud.size shouldBe 0
        cloud.getTopWords() shouldBe ""
      }
    }

    "adding words" should {
      "track single word" in {
        val cloud = WordCloud(5)
        cloud.addWord("hello")
        cloud.size shouldBe 1
        cloud.getTopWords() shouldBe "hello: 1"
      }

      "track multiple different words" in {
        val cloud = WordCloud(5)
        cloud.addWord("hello")
        cloud.addWord("world")
        cloud.size shouldBe 2
        // Alphabetically: hello, world
        cloud.getTopWords() shouldBe "hello: 1 world: 1"
      }

      "increment frequency for repeated words" in {
        val cloud = WordCloud(5)
        cloud.addWord("hello")
        cloud.addWord("hello")
        cloud.addWord("hello")
        cloud.size shouldBe 1
        cloud.getTopWords() shouldBe "hello: 3"
      }

      "sort by frequency descending" in {
        val cloud = WordCloud(5)
        cloud.addWord("rare")
        cloud.addWord("common")
        cloud.addWord("common")
        cloud.addWord("common")
        cloud.getTopWords() shouldBe "common: 3 rare: 1"
      }

      "sort alphabetically for same frequency" in {
        val cloud = WordCloud(5)
        cloud.addWord("zebra")
        cloud.addWord("apple")
        cloud.addWord("banana")
        // All have freq 1, should be alphabetical
        cloud.getTopWords() shouldBe "apple: 1 banana: 1 zebra: 1"
      }

      "limit output to cloudSize" in {
        val cloud = WordCloud(3)
        cloud.addWord("first")
        cloud.addWord("first")
        cloud.addWord("first")
        cloud.addWord("second")
        cloud.addWord("second")
        cloud.addWord("third")
        cloud.addWord("fourth")
        // Should only show top 3: first(3), second(2), then alphabetically fourth before third
        cloud.getTopWords() shouldBe "first: 3 second: 2 fourth: 1"
      }
    }

    "removing words" should {
      "decrement frequency" in {
        val cloud = WordCloud(5)
        cloud.addWord("hello")
        cloud.addWord("hello")
        cloud.addWord("hello")
        cloud.removeWord("hello")
        cloud.getTopWords() shouldBe "hello: 2"
      }

      "remove word when frequency reaches 0" in {
        val cloud = WordCloud(5)
        cloud.addWord("hello")
        cloud.removeWord("hello")
        cloud.isEmpty shouldBe true
        cloud.getTopWords() shouldBe ""
      }

      "handle removing non-existent word" in {
        val cloud = WordCloud(5)
        cloud.addWord("hello")
        cloud.removeWord("world") // Should not crash
        cloud.size shouldBe 1
        cloud.getTopWords() shouldBe "hello: 1"
      }

      "handle removing from empty cloud" in {
        val cloud = WordCloud(5)
        cloud.removeWord("anything") // Should not crash
        cloud.isEmpty shouldBe true
      }
    }

    "complex scenarios" should {
      "handle add and remove mixed operations" in {
        val cloud = WordCloud(3)
        cloud.addWord("apple")
        cloud.addWord("banana")
        cloud.addWord("apple")
        cloud.addWord("cherry")
        cloud.removeWord("apple")
        // apple:1, banana:1, cherry:1 - alphabetical
        cloud.getTopWords() shouldBe "apple: 1 banana: 1 cherry: 1"
      }

      "maintain correct order after removals" in {
        val cloud = WordCloud(3)
        cloud.addWord("high")
        cloud.addWord("high")
        cloud.addWord("high")
        cloud.addWord("medium")
        cloud.addWord("medium")
        cloud.addWord("low")
        cloud.removeWord("high")
        cloud.removeWord("high")
        // Now: high:1, medium:2, low:1
        cloud.getTopWords() shouldBe "medium: 2 high: 1 low: 1"
      }
    }

    "validation" should {
      "reject negative cloudSize" in {
        an[IllegalArgumentException] should be thrownBy {
          WordCloud(-1)
        }
      }

      "reject zero cloudSize" in {
        an[IllegalArgumentException] should be thrownBy {
          WordCloud(0)
        }
      }
    }
  }
