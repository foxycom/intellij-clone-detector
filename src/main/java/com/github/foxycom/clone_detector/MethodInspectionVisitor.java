package com.github.foxycom.clone_detector;

import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.JavaElementVisitor;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiType;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MethodInspectionVisitor extends JavaElementVisitor {

  private final CloneFix cloneFix = new CloneFix();
  private final List<PsiClass> psiClasses;
  private final Common common = new Common();
  private final ProblemsHolder holder;

  public MethodInspectionVisitor(List<PsiClass> psiClasses, ProblemsHolder holder) {
    this.psiClasses = psiClasses;
    this.holder = holder;
  }


  @Override
  public void visitClass(PsiClass aClass) {
    psiClasses.add(aClass);
  }

  @Override
  public void visitJavaFile(PsiJavaFile file) {
    if (holder != null) {
      psiClasses.clear();
    }
    Arrays.stream(file.getClasses()).forEach(this::visitClass);

    if (holder != null) {
      for (var pair : common.checkClasses(psiClasses)) {
        holder.registerProblem(pair.getLeft(), getDesc(pair.getRight()), ProblemHighlightType.WARNING);
        holder.registerProblem(pair.getRight(), getDesc(pair.getLeft()), ProblemHighlightType.WARNING);
      }
    }
  }

  private String getDesc(PsiMethod method) {
    return String.format("This looks like a duplicate of method %s(%s).",
        method.getName(), getMethodParamString(method));
  }

  private String getMethodParamString(PsiMethod method) {
    PsiParameter[] params = method.getParameterList().getParameters();
    return Arrays.stream(params)
        .map(PsiParameter::getType)
        .map(PsiType::getCanonicalText)
        .collect(Collectors.joining(", "));
  }
}
