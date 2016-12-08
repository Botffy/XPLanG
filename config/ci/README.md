# Continuous Integration

XPLanG uses continuous integration. Whenever a commit is pushed to the master branch on [Github](https://github.com), [Codeship](https://codeship.com) is notified of the chage. Codeship then uses the scripts in this library to:

1. run all tests
1. run [Checkstyle](../checkstyle/README.md) and the code coverage tester JaCoCo
1. upload the test coverage results to [Code Climate](https://codeclimate.com)
1. create the Javadoc documentation
1. build the compiler
1. upload documentation, the build reports and the compiler to the [distribution directory](http://users.itk.ppke.hu/~sciar/XPLanG/)

If any of the above steps fail, the entire build fails, and sirens start blaring! Well, okay, I get an email.
