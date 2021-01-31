package com.github.foxycom.clone_detector.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MethodParserTest {

  private MethodParser methodParser;

  @BeforeEach
  void setUp() {
    methodParser = new MethodParser();
  }

  @Test
  void parse() throws FileNotFoundException {

    File testClass = new File("src/test/resources/ConnectionPool.java");
    BufferedReader in = new BufferedReader(new FileReader(testClass));
    String clazz = in.lines().collect(Collectors.joining("\n"));
    MethodVector res = methodParser
        .parse(clazz, "closeDBConnections");
    System.out.println();
  }
}