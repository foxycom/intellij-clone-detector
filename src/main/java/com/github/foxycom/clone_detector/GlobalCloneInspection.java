package com.github.foxycom.clone_detector;

import com.intellij.analysis.AnalysisScope;
import com.intellij.codeInspection.CommonProblemDescriptorImpl;
import com.intellij.codeInspection.GlobalInspectionContext;
import com.intellij.codeInspection.GlobalInspectionTool;
import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemDescriptionsProcessor;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiType;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GlobalCloneInspection extends /*LocalInspectionTool,*/ GlobalInspectionTool {

  private final List<PsiClass> psiClasses;
  private final LocalCloneInspection localCloneInspection;
  private final Common common;

  public GlobalCloneInspection() throws IOException {
    psiClasses = new ArrayList<>();
    localCloneInspection = new LocalCloneInspection();
    common = new Common();
  }

  @Override
  public void runInspection(@NotNull AnalysisScope scope, @NotNull InspectionManager manager,
      @NotNull GlobalInspectionContext globalContext,
      @NotNull ProblemDescriptionsProcessor problemDescriptionsProcessor) {
    psiClasses.clear();
    scope.accept(new MethodInspectionVisitor(psiClasses, null));

    for (Pair<PsiMethod, PsiMethod> pair : common.checkClasses(psiClasses)) {
      problemDescriptionsProcessor
          .addProblemElement(globalContext.getRefManager().getReference(pair.getLeft()),
              new CommonProblemDescriptorImpl(null, getDesc(pair.getRight())));
      problemDescriptionsProcessor
          .addProblemElement(globalContext.getRefManager().getReference(pair.getRight()),
              new CommonProblemDescriptorImpl(null, getDesc(pair.getLeft())));
    }

    super.runInspection(scope, manager, globalContext, problemDescriptionsProcessor);
  }



  private String getDesc(PsiMethod method) {
    return String.format("This code block looks like a duplicate of method %s(%s).",
        method.getName(), getMethodParamString(method));
  }

  private boolean skip(PsiMethod method) {
    return method.getBody() == null || method.getBody().getText().split("\n").length <= 6;
  }

  private String getMethodParamString(PsiMethod method) {
    PsiParameter[] params = method.getParameterList().getParameters();
    return Arrays.stream(params)
        .map(PsiParameter::getType)
        .map(PsiType::getPresentableText)
        .collect(Collectors.joining(", "));
  }

  @Override
  public @Nullable LocalInspectionTool getSharedLocalInspectionTool() {
    // Assume there is only one project open
    return localCloneInspection;
  }
}
