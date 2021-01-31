package com.github.foxycom.clone_detector;

import com.github.foxycom.clone_detector.classifier.CloneClassifier;
import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElementVisitor;
import java.io.IOException;
import org.jetbrains.annotations.NotNull;

public class CloneInspection extends LocalInspectionTool {

  private final CloneClassifier classifier;

  public CloneInspection() throws IOException {
    this.classifier = new CloneClassifier("./src/resources/model.mdl");
  }

  @Override
  public @NotNull PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder,
      boolean isOnTheFly) {
    return new MethodInspectionVisitor(holder, classifier);
  }

}
