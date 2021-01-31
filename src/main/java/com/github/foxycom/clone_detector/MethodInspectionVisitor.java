package com.github.foxycom.clone_detector;

import com.github.foxycom.clone_detector.classifier.CloneClassifier;
import com.github.foxycom.clone_detector.parser.MethodParser;
import com.github.foxycom.clone_detector.parser.MethodVector;
import com.github.foxycom.clone_detector.parser.VectorSimilarity;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.JavaElementVisitor;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import java.util.Arrays;

public class MethodInspectionVisitor extends JavaElementVisitor {

  private final CloneFix cloneFix = new CloneFix();
  private final ProblemsHolder holder;
  private final CloneClassifier classifier;

  public MethodInspectionVisitor(ProblemsHolder holder, CloneClassifier classifier) {
    this.holder = holder;
    this.classifier = classifier;
  }

  @Override
  public void visitClass(PsiClass aClass) {
    PsiMethod[] methods = aClass.getMethods();
    MethodParser parser = new MethodParser();
    VectorSimilarity vs = new VectorSimilarity();
    for (int i = 0; i < methods.length; i++) {
      PsiMethod methodA = methods[i];
      if (methodA.getBody() == null) continue;

      MethodVector mvA = parser.parse(aClass.getText(), methodA.getName());
      for (int j = i + 1; j < methods.length; j++) {
        PsiMethod methodB = methods[j];
        if (methodB.getBody() == null) continue;

        MethodVector mvB = parser.parse(aClass.getText(), methodB.getName());
        double[] simVector = vs.methodVectorSim(mvA, mvB);
        double p = classifier.run(simVector);
        if (p > 0.8) {
          holder.registerProblem(methodA.getBody(), getDesc(methodB), cloneFix);
          holder.registerProblem(methodB.getBody(), getDesc(methodA), cloneFix);
        }
      }
    }
  }

  private String getDesc(PsiMethod method) {
    return String.format("This code block looks like a duplicate of method %s.", method.getName());
  }
}
