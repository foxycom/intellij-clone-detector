package com.github.foxycom.clone_detector;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;

public class CloneIntention extends PsiElementBaseIntentionAction implements IntentionAction {

  @Override
  public void invoke(@NotNull Project project, Editor editor, @NotNull PsiElement element)
      throws IncorrectOperationException {
  }

  @Override
  public boolean isAvailable(@NotNull Project project, Editor editor, @NotNull PsiElement element) {
    return false;
  }

  @Override
  public @NotNull @IntentionFamilyName String getFamilyName() {
    return "Clone detection";
  }
}
