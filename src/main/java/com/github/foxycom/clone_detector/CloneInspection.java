package com.github.foxycom.clone_detector;

import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElementVisitor;
import java.io.IOException;
import org.jetbrains.annotations.NotNull;

public class CloneInspection extends LocalInspectionTool {

  private final Backend backend;

  public CloneInspection() throws IOException {
    backend = new Backend("http://localhost:8080/");
  }

  @Override
  public @NotNull PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder,
      boolean isOnTheFly) {
    return new MethodInspectionVisitor(holder, backend);
  }

}
