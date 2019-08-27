# Change log

## MS4 - 2019-08-18

Development was resumed after a brief hiatus of three years, and we're back with expressions! All classic PLanG operators and functions have been implemented, complete with function resolution and implicit type coercion.

As for restarting the development: the CI has been repaired, and now we're using JDK11.

## Features

- Pratt parser for exceptions
- typechecker object
- resolution for overloaded functions. Only parameter overloading is supported yet
- all PLanG operators and functions (except I/O)
- loops

## MS3 - 2016-12-16

The highlights of this iteration was fleshing out the memory to handle
composite values (arrays and strings, called `AddressableValue`s in the code),
and refactoring PlangGrammar to be more robust.

I'm happy with how the memory turned out: arrays are little addressable pieces
of memories themselvess, making the whole thing a nice treelike structure. I'm
less happy with the PlangGrammar changes: while it's better than it was, it
really starts feeling overengineered. Also, while a separate typechecker object
was expected to be created in this sprint, it wasn't: it will start to really
matter only when function resolution comes in.

### Features

Lots of types: strings, characters, booleans, floats, and also fixed-size
arrays, including multidimensional arrays. Conditional statements.

### Design

- Better memory model, composite values.
- Conditional statements.
- Lexical elements of PLanG now come from a property file, making language
variants possible.

### Refactor

- Exploded PlangGrammar: the recursive descent parser is implemented through a
collection of parses classes with a single Parse static function. Not very
pretty design, but definitely nicer than the huge PlangGrammar.java was.
- AST nodes keep their location information.
- The names of PlangGrammar's symbols are now in an enum.
- Things (types, variables) are registered in Context by a Name object
(instead of by plain string keys).


## [MS2](https://github.com/Botffy/XPLanG/releases/tag/MS2) - 2016-12-09

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
