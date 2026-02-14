[![Open in Codespaces](https://classroom.github.com/assets/launch-codespace-2972f46106e565e64193e422d61a12cf1da4916b45550586e14ef0a7c637dd04.svg)](https://classroom.github.com/open-in-codespaces?assignment_repo_id=22450184)

# TopWords - Streaming Word Cloud Analyzer

A Scala application that processes streaming text input and generates real-time word clouds showing the most frequent words in a sliding window.

## Features

- **Streaming processing**: Handles arbitrarily large input with constant memory usage
- **Sliding window**: Maintains statistics for the most recent N words
- **Configurable parameters**: Cloud size, minimum word length, window size
- **Case-insensitive matching**: Treats "Hello" and "hello" as the same word
- **SIGPIPE handling**: Gracefully terminates when output pipe is closed
- **Observer pattern**: Separates computation from I/O for testability

### Extra Credit Features

- ✅ **Case-insensitive word matching** (0.5 points)
- ✅ **Output every k steps** (`-k` flag): Reduces output frequency (0.5 points)
- ✅ **Minimum frequency filter** (`-f` flag): Only show words appearing at least N times (1.0 points)

**Total extra credit: 2.0 points**

## Usage

```bash
./topwords [OPTIONS]
```

### Command-line Options

- `-c, --cloud-size <N>` - Number of top words to display (default: 10)
- `-l, --length-at-least <N>` - Minimum word length to consider (default: 6)
- `-w, --window-size <N>` - Size of moving window (default: 1000)
- `-k, --every-k-steps <N>` - Output word cloud every k steps (default: 1)
- `-f, --min-frequency <N>` - Minimum frequency to include word (default: 1)

### Examples

Basic usage with defaults:
```bash
cat textfile.txt | ./topwords
```

Show top 5 words, minimum length 4, window of 500:
```bash
cat textfile.txt | ./topwords -c 5 -l 4 -w 500
```

Show only words appearing at least 3 times:
```bash
cat textfile.txt | ./topwords -f 3
```

Output every 10 steps to reduce output volume:
```bash
yes helloworld | ./topwords -k 10 > /dev/null
```

## Building and Running

### Build the standalone application

```bash
sbt stage
```

This creates an executable at `target/universal/stage/bin/topwords`.

### Run with sbt

```bash
sbt "run -c 5 -l 4 -w 100"
```

### Run the staged application

```bash
./target/universal/stage/bin/topwords < input.txt
```

Or with streaming input:
```bash
echo "hello world hello scala programming" | ./target/universal/stage/bin/topwords -c 3 -l 4 -w 5
```

## Testing

### Run all tests

```bash
sbt test
```

### Test coverage

```bash
sbt clean coverage test coverageReport
```

View coverage report at: `target/scala-3.7.4/scoverage-report/index.html`

**Coverage Results:**
- **Overall: 72%**
- **Business Logic: 100%** (WordCloud, MovingWindow, TopWordsProcessor, TestObserver)
- ConsoleObserver: 67% (uncovered: SIGPIPE error exit path)
- Main: 19% (uncovered: entry point methods requiring stdin)

All testable business logic has 100% test coverage. The gaps are:
- Error handling paths that are difficult to test (SIGPIPE)
- Entry point methods that require stdin mocking

**Test Suite:** 64 unit tests covering:
- Word frequency tracking
- Sliding window behavior
- Case-insensitive matching
- Edge cases and boundary conditions
- Scalability (up to 5 million words)

## Scalability

The program uses a sliding window to maintain **constant space** complexity, regardless of input size.

**Verified with:**
- Scalability tests processing 5 million words
- Memory profiling with htop showing constant memory usage
- Infinite stream test: `yes helloworld | ./topwords > /dev/null`

The sliding window ensures that only the most recent `windowSize` words are tracked, with older words removed as new ones arrive.

## Architecture

### Key Components

- **WordCloud**: Tracks word frequencies and generates top-N word lists
- **MovingWindow**: Maintains sliding window of recent words, updating WordCloud
- **TopWordsProcessor**: Orchestrates word processing pipeline
- **OutputObserver**: Abstraction for output (enables testability)
  - ConsoleObserver: Writes to stdout with SIGPIPE handling
  - TestObserver: Collects outputs for unit testing

### Design Patterns

- **Observer Pattern**: Separates computation from I/O
- **Iterator Pattern**: Processes streaming data efficiently
- **Pipes and Filters**: Modular processing pipeline

## Development

### Project Structure

```
src/
├── main/scala/edu/luc/cs/cs371/topwords/
│   ├── Main.scala                 # Entry point and CLI
│   ├── TopWordsProcessor.scala    # Main processing logic
│   ├── WordCloud.scala            # Frequency tracking
│   ├── MovingWindow.scala         # Sliding window
│   └── OutputObserver.scala       # Output abstraction
└── test/scala/edu/luc/cs/cs371/topwords/
    ├── MainSpec.scala
    ├── TopWordsProcessorSpec.scala
    ├── WordCloudSpec.scala
    ├── MovingWindowSpec.scala
    ├── OutputObserverSpec.scala
    └── ScalabilitySpec.scala
```

### Technologies

- **Language**: Scala 3 (with significant indentation)
- **Build Tool**: SBT
- **Testing**: ScalaTest
- **Libraries**:
  - mainargs: Command-line argument parsing
  - SLF4J + Logback: Logging
  - ScalaTest + ScalaCheck: Testing

## LLM Assistance

This project was developed with assistance from Claude (Anthropic). All interactions are documented in `doc/llm-interactions.md`.

## Team

- The Grad Student

## Assignment

CS 371 - Programming Language Design and Implementation
Project 1b - Imperative/Object-Oriented Implementation
Loyola University Chicago
