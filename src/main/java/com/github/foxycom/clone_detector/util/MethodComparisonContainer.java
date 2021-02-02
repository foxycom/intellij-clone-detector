package com.github.foxycom.clone_detector.util;

import com.intellij.psi.PsiMethod;

public class MethodComparisonContainer {
  private final double[] simVector;
  private final PsiMethod methodA;
  private final PsiMethod methodB;

  public MethodComparisonContainer(double[] simVector, PsiMethod methodA, PsiMethod methodB) {
    this.simVector = simVector;
    this.methodA = methodA;
    this.methodB = methodB;
  }

  public double[] getSimVector() {
    return simVector;
  }

  public PsiMethod getMethodA() {
    return methodA;
  }

  public PsiMethod getMethodB() {
    return methodB;
  }
}
