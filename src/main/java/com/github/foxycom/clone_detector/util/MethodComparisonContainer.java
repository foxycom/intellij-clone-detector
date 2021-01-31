package com.github.foxycom.clone_detector.util;

public class MethodComparisonContainer {
  private final double[] simVector;
  private final int methodA;
  private final int methodB;

  public MethodComparisonContainer(double[] simVector, int methodA, int methodB) {
    this.simVector = simVector;
    this.methodA = methodA;
    this.methodB = methodB;
  }

  public double[] getSimVector() {
    return simVector;
  }

  public int getMethodA() {
    return methodA;
  }

  public int getMethodB() {
    return methodB;
  }
}
