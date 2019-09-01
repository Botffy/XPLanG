[![Gittens](http://gittens.r15.railsrumble.com/badge/Botffy/XPLanG)](http://gittens.r15.railsrumble.com/gitten/Botffy/XPLanG)

[![Code Climate](https://codeclimate.com/github/Botffy/XPLanG/badges/gpa.svg)](https://codeclimate.com/github/Botffy/XPLanG)
[![Test Coverage](https://codeclimate.com/github/Botffy/XPLanG/badges/coverage.svg)](https://codeclimate.com/github/Botffy/XPLanG/coverage)
[![Codeship](https://app.codeship.com/projects/d19f6fd0-9464-0137-af5e-22e0f80832fb/status?branch=master)](https://app.codeship.com/projects/356917)


The PLanG programming language is an educational programming language used at the Faculty of Information Technology of the Péter Pázmány Catholic University, Hungary, for teaching the very basics of programming in the first semester. The language and the small IDE/interpreter accompanying it was designed and implemented by László Lövei in the summer of 2005, based on a pseudocode language used earlier to describe algorithms on paper. A Pascal-like language, its main distinguishing feature is that its keywords are in Hungarian.

It's a small language: while it's Turing-complete (of course), it lacks subroutines, records, user-defined types, and it doesn't even have elsif.

I hated that language with a passion.

But I'm kinda weird, you know, and while everybody else just wanted to forget the language, I wanted to do something about it. Having taken a course in compilers at ELTE, I chose extending PLanG as the topic for my [self-directed laboratory project](https://github.com/Botffy/onlab), then I chose to continue the project for my thesis. This is that project. It's taking a while to do it, but oh well, what the hell.

The dream lives on.

# Running

## Prerequisites

You will need Java 11 Runtime Environment (or later). Just install the latest JRE from [Oracle's download page](http://www.oracle.com/technetwork/java/javase/downloads/index.html).

## Download

Download the latest build from our [meager homepage](http://users.itk.ppke.hu/~sciar/XPLanG/downloads/).

## Command line

Start XPLanG by running `bin/XPLanG` (Unix-like systems) or `bin/XPLanG.bat` (windows).

## Usage

`XPLanG [options] SOURCE_FILE`

`SOURCE_FIE` should be a PLanG program.

Valid options:

- `--dry-run` Perform a dry run: parse and analyse the source, displaying any errors, but do not interpret it.
- `--print-ast` Print the Abstract Syntax Tree after parsing the program
- `--dump-memory` Dump the contents of the memory to the StdOut after running the program
- `--source-encoding` the encoding of the source code (UTF8 or LATIN1)
- `--output-encoding` The encoding of the standard output (UTF8 or LATIN1). When not set, the system default will be used.
- `--help` display usage information and exit
- `--version` display version information and exit


# Building

## Prerequisites

You will need

- [Git](https://git-scm.com/downloads),
- [Java 11 Development Kit](http://www.oracle.com/technetwork/java/javase/downloads/index.html)
- [Gradle (version 5.5.1)](https://gradle.org).

Make sure to have these in your PATH, then restart your terminal.

## Getting the source code

To get the source code, type `git clone https://github.com/Botffy/XPLanG.git` in your terminal, in the directory where you'd like your copy of the repo to be created.

## Building in the command line

- `gradle classes` downloads all dependencies and compiles the whole thing.
- `gradle assemble` creates the actual product. The distribution packages are put in the `build/distributions`.
- `gradle test` runs the JUnit tests.
- `gradle javadoc` generates the Javadoc documentation, putting it in `docs/javadoc`.
- `gradle check` invokes [Checkstyle](./config/checkstyle/README.md) in addition to running the tests.
- `gradle jacoco` invokes the [JaCoCo](http://www.eclemma.org/jacoco/) code coverage tool.
- `gradle run` runs the interpreter. Pass arguements to XPLanG via Gradle through the `-Pappargs` command line argument as a list of strings. Try `gradle run -PappArgs="['example.prog']"`.

You can find the build reports in `build/reports`.
