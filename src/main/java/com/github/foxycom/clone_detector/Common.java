package com.github.foxycom.clone_detector;

import com.github.foxycom.clone_detector.parser.MethodParser;
import com.github.foxycom.clone_detector.parser.MethodVector;
import com.github.foxycom.clone_detector.parser.VectorSimilarity;
import com.github.foxycom.clone_detector.util.MethodComparisonContainer;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.lang3.tuple.Pair;

public class Common {
  private final MethodParser parser;
  private final VectorSimilarity vectorSimilarity;
  private final Backend backend;

  public Common() {
    this.parser = new MethodParser();
    this.vectorSimilarity = new VectorSimilarity();
    backend = new Backend("http://192.168.2.109:8080/");
  }

  public List<Pair<PsiMethod, PsiMethod>> checkClasses(List<PsiClass> classes) {
    List<Pair<PsiMethod, PsiMethod>> results = new LinkedList<>();
    List<MethodComparisonContainer> containerList = new LinkedList<>();

    for (var psiClass : classes) {
      var psiMethods = psiClass.getMethods();
      List<MethodVector> methodVectors = new ArrayList<>();
      for (var psiMethod : psiMethods) {
        if (skip(psiMethod)) continue;
        String classBody = wrapIntoClass(psiMethod.getBody().getText());
        var methodVector = parser.parse(classBody);
        methodVectors.add(methodVector);
      }

      for (int i = 0; i < methodVectors.size(); i++) {
        MethodVector mvA = methodVectors.get(i);
        for (int j = i + 1; j < methodVectors.size(); j++) {
          var mvB = methodVectors.get(j);
          var container = new MethodComparisonContainer(vectorSimilarity.methodVectorSim(mvA, mvB),
              psiMethods[i], psiMethods[j]);
          containerList.add(container);
        }
      }
    }

    double[][] vectors = new double[containerList.size()][8];
    for (int i = 0; i < containerList.size(); i++) {
      vectors[i] = containerList.get(i).getSimVector();
    }

    try {
      var result = backend.getResult(vectors);
      int i = 0;
      while (i < result.length) {
        double v = result[i];
        if (v > 0.98) {
          var container = containerList.get(i);
          var methodA = container.getMethodA();
          var methodB = container.getMethodB();

          Pair<PsiMethod, PsiMethod> pair = Pair.of(methodA, methodB);
          results.add(pair);
        }
        i++;
      }

    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    return results;
  }

  public static String wrapIntoClass(String method) {
    return String.format("public class Temp {%n%s%n}", method);
  }

  private boolean skip(PsiMethod psiMethod) {
    return psiMethod.getBody() == null || psiMethod.getText().split("\n").length <= 5;
  }
}
