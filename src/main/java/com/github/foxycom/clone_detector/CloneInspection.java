package com.github.foxycom.clone_detector;

import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElementVisitor;
import org.jetbrains.annotations.NotNull;

public class CloneInspection extends LocalInspectionTool {


  @Override
  public @NotNull PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder,
      boolean isOnTheFly) {
    return new MethodInspectionVisitor(holder);
  }

}
