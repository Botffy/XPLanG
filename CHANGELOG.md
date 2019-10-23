# Change log

## MS7 - 2019-10-23

New language features.

### Features

- Elsif
- Basic assertions
- Functions (with overloading, recursion and forward declarations.

### Refactor

- ErrorCodes in Interpreter, errors handled more gracefully in CLI (#3)
- Better null checks in Interpreter 
- ErrorCodes in Parser (#5)
- Somewhat better parsing error recovery (#6)
- Refactor Memory to use a frame-based system
- Replace the old handwritten lexer with a JFlex generated one
- Refactor and clean up Context to conform the new view on scopes

## MS6 - 2019-09-17

The birth of the XPLanG IDE. The small IDE comes with an editor, the edited files can be compiled. If there were no errors, the program can be run. Unlike in the original PLanG, the programs may be interactive: input can be submitted through the built-in console window. The IDE can be started with a separate startup script called 'XPLanG-editor'.

## MS5 - 2019-09-01

We have ourselves a PLanG :)

Tested against all the PLanG programming homeworks turned in in the past three years, we can now pretty safely say PLanG handles correct programs much like the reference implementation. A whole lot of errors turned up, these were mostly fixed. A couple of edge cases remain, cases where the reference implementation does not really conform the specification. Also, our error handling is not very good just yet.

### Features

- IO: read input from the standard input or files, write output to the standard output or files
- Conditional connectives (short-circuiting OR and AND)
- Command line options to specify source and output encoding
- The AST is not always printed (can be turned on with a command line option)
- The memory is not always dumped at the end of the execution (can be turned on with a command line option)
- The interpreter now logs to a file instead of littering the stdOut
- Numerous fixes after the The Great Big System Test.

### Tooling

- Downgraded back to Java 8.

## MS4 - 2019-08-18

Development was resumed after a brief hiatus of three years, and we're back with expressions! All classic PLanG operators and functions have been implemented, complete with function resolution and implicit type coercion.

As for restarting the development: the CI has been repaired, and now we're using JDK11.

### Features

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
