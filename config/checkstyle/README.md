[Checkstyle](http://checkstyle.sourceforge.net/index.html) is a development tool to help programmers write Java code that adheres to a [coding standard](https://en.wikipedia.org/wiki/Coding_conventions). When run, it cries when you did something wrong.

It features syntactic checks, enforcing naming conventions, whitespace rules (for example that operators must be surrounded with spaces), telling you where the braces should go, things like that. There are also checks to ensure common programming pitfalls are avoided. And it also features [complexity checks](https://en.wikipedia.org/wiki/Programming_complexity), to ensure a class or method doesn't cause brain damage when looked at.

Checkstyle is highly configurable. XPLanG features a fairly lenient configuration, which should be worked on, but it's good enough for now.

Run Checkstyle with `gradle check`.
