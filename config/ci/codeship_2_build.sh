#!/usr/bin/env bash

# build and test everything
gradle javadoc test assemble jacocoTestReport

# send JaCoCo coverage report to codeship
cd jacoco-codeclimate-reporter
python3 report-jacoco.py ../build/reports/jacoco/test/jacocoTestReport.xml ../src/main/java ../
