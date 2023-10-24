#!/bin/bash
mvn clean install
java -cp ~/Projects/java/snake/target/snake-1.0-SNAPSHOT.jar my.project.Main
