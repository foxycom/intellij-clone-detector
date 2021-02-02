package com.github.foxycom.clone_detector;

import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.LocalInspectionToolSession;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElementVisitor;
import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public class LocalCloneInspection extends LocalInspectionTool {

  private final Backend backend = new Backend("http://192.168.2.109:8080/");
  private final Common common = new Common();

  @Override
  public @NotNull PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly,
      @NotNull LocalInspectionToolSession session) {
    return new MethodInspectionVisitor(new ArrayList<>(), holder);
  }

}
