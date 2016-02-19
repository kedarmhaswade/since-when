# The Great Java @Since Generator ...

## Introduction
Generates a report for all the methods for the JDK src.zip classes so that you know which method is since when.
In a way, this is just regex parsing with @since, but I wanted to try out the javaparser library :-).

## Instructions

Java 8 is needed. Other than that ...

1. mvn install
2. copy src.zip to some folder, all remember a path to it.
3. java -jar target/java-jar-source-since-final.jar path-to-jar some-kind-of-class-name