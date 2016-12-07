[![Code Climate](https://codeclimate.com/github/Botffy/XPLanG/badges/gpa.svg)](https://codeclimate.com/github/Botffy/XPLanG)
[![Codeship](https://codeship.com/projects/af791830-9e3d-0134-355d-328bd15b6072/status?branch=master)](https://app.codeship.com/projects/188927)

The PLanG programming language is an educational programming language used at the Faculty of Information Technology of the Péter Pázmány Catholic University, Hungary, for teaching the very basics of programming in the first semester. The language and the small IDE/interpreter accompanying it was designed and implemented by László Lövei in the summer of 2005, based on a pseudocode language used earlier to describe algorithms on paper. A Pascal-like language, its main distinguishing feature is that its keywords are in Hungarian.

It's a small language: while it's Turing-complete (of course), it lacks subroutines, records, user-defined types, and it doesn't even have elsif.

I hated that language with a passion.

But I'm kinda weird, you know, and while everybody else just wanted to forget the language, I wanted to do something about it. Having taken a course in compilers at ELTE, I chose extending PLanG as the topic for my [self-directed laboratory project](https://github.com/Botffy/onlab), then I chose to continue the project for my thesis. This is that project. It's taking a while to do it, but oh well, what the hell.

The dream lives on.

# Building

## Prerequisites

You will need

- [Git](https://git-scm.com/downloads),
- [Java 8](http://www.oracle.com/technetwork/java/javase/downloads/index.html)
- [Gradle (version 3.2.1)](https://gradle.org).

Make sure to have these in your PATH, then restart your terminal.

## Getting the source code

To get the source code, type `git clone https://github.com/Botffy/XPLanG.git` in your terminal, in the directory where you'd like your copy of the repo to be created.

## Building in the command line

Then enter the newly created `XPLanG` directory, and type `gradle classes` in your terminal: it downloads all dependencies and compiles the whole thing.

### Further build commands

- `gradle test` runs the JUnit tests.
- `gradle javadoc` generates the Javadoc documentation, putting it in `docs/javadoc`.
- `gradle check` invokes [Checkstyle](./config/checkstyle/README.md) in addition to running the tests.
- `gradle jacoco` invokes the [JaCoCo](http://www.eclemma.org/jacoco/) code coverage tool.

You can find the build reports in `build/reports`.

## Running the thing

Type `gradle run` in your terminal. It will start XPLanG, but it will complain about getting too few arguments: it expects the path to a source file to interpret. Pass arguements to XPLanG via Gradle through the `-Pappargs` command line argument, which expects a list of strings. It's a bit hard to explain: just try `gradle run -PappArgs="['example.prog']"`.
