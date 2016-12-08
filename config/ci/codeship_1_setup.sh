#!/usr/bin/env bash

# download our preferred Gradle
PATH=$GRADLE_HOME/bin:$PATH
wget -N $GRADLE_URL
mkdir -p ~/gradle
unzip $GRADLE.zip -d ~

# get the code climate jacoco reporter and its dependency
git clone https://github.com/Botffy/jacoco-codeclimate-reporter.git
pip install requests
