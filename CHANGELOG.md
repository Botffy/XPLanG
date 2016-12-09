# Change log

## MS2 - 2016-12-09

The goal of the milestone was to have variables and assignments: done
surprisingly quickly, the rest of the week was spent on setting up continuous
integration, getting functional tests, getting more fancy tools, working on
the process. A byproduct was the
[jacoco-codeclimate-reporter](https://github.com/Botffy/jacoco-codeclimate-reporter)
script.

### Features

Integer variables, integer literals, assignments.

### Design

- Basic memory model for the interpreter.
- Localisation

### Refactor

- Rework Lexer to use a proper buffer (instead of lines)

### Tooling

- [Continuous integration](config/ci/README.md) is live.
- Packaged binaries are built and stored at
- [users.itk.ppke.hu](http://users.itk.ppke.hu/~sciar/XPLanG/).
- Test coverage is measured and recorded.
- Build instructions.
- Language integration testing.

## [MS1](https://github.com/Botffy/XPLanG/releases/tag/MS1) - 2016-12-04

First milestone, featuring the basic project structure & design, and a
not very useful language.
