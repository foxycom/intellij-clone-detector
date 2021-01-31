package com.github.foxycom.clone_detector;

import com.github.foxycom.clone_detector.parser.MethodParser;
import com.github.foxycom.clone_detector.parser.MethodVector;
import com.github.foxycom.clone_detector.parser.VectorSimilarity;
import com.github.foxycom.clone_detector.util.MethodComparisonContainer;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.JavaElementVisitor;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class MethodInspectionVisitor extends JavaElementVisitor {

  private final CloneFix cloneFix = new CloneFix();
  private final ProblemsHolder holder;
  private final Backend backend;

  public MethodInspectionVisitor(ProblemsHolder holder, Backend backend) {
    this.holder = holder;
    this.backend = backend;
  }

  @Override
  public void visitClass(PsiClass aClass) {
    PsiMethod[] methods = aClass.getMethods();
    MethodParser parser = new MethodParser();
    VectorSimilarity vs = new VectorSimilarity();

    List<MethodComparisonContainer> containerList = new LinkedList<>();

    for (int i = 0; i < methods.length; i++) {
      PsiMethod methodA = methods[i];
      if (skip(methodA)) continue;

      MethodVector mvA = parser.parse(aClass.getText(), methodA.getName());
      for (int j = i + 1; j < methods.length; j++) {
        PsiMethod methodB = methods[j];
        if (skip(methodB)) continue;

        MethodVector mvB = parser.parse(aClass.getText(), methodB.getName());
        double[] simVector = vs.methodVectorSim(mvA, mvB);
        containerList.add(new MethodComparisonContainer(simVector, i, j));
        Notifier.notify("Vector", String.format("%s - %s: %s", methodA.getName(), methodB.getName(), Arrays.toString(simVector)));
      }
    }

    double[][] vectors = new double[containerList.size()][8];
    for (int i = 0; i < containerList.size(); i++) {
      vectors[i] = containerList.get(i).getSimVector();
    }

    int pairsAnalyzed = containerList.size();
    int trueClones = 0;
    try {
      double[] result = backend.getResult(vectors);
      int i = 0;
      while (i < result.length) {
        double v = result[i];
        if (v > 0.98) {
          MethodComparisonContainer container = containerList.get(i);
          PsiMethod methodA = methods[container.getMethodA()];

          PsiMethod methodB = methods[container.getMethodB()];

          holder.registerProblem(methodB.getBody(), getDesc(methodA), cloneFix);
          holder.registerProblem(methodA.getBody(), getDesc(methodB), cloneFix);
          trueClones++;
        }
        i++;
      }

    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    Notifier.notify("Clone detection", String.format("Analyzed %d method pairs.\nClones found: %d", pairsAnalyzed, trueClones));
  }


  private boolean skip(PsiMethod method) {
    return method.getBody() == null || method.getBody().getText().split("\n").length <= 6;
  }

  private String getDesc(PsiMethod method) {
    return String.format("This code block looks like a duplicate of method %s.", method.getName());
  }
}
