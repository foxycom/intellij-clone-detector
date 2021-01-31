package com.github.foxycom.clone_detector;

import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.JavaTokenType;
import com.intellij.psi.PsiBinaryExpression;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.PsiPrefixExpression;
import com.intellij.psi.tree.IElementType;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;

public class CloneFix implements LocalQuickFix {

  /**
   * Returns a partially localized string for the quick fix intention. Used by the test code for
   * this plugin.
   *
   * @return Quick fix short name.
   */
  @NotNull
  @Override
  public String getName() {
    return "Code clone detection";
  }

  @Override
  public @IntentionFamilyName @NotNull String getFamilyName() {
    return getName();
  }

  /**
   * @param project    The project that contains the file being edited.
   * @param descriptor A problem found by this inspection.
   */
  public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
    try {
      PsiBinaryExpression binaryExpression = (PsiBinaryExpression) descriptor.getPsiElement();
      IElementType opSign = binaryExpression.getOperationTokenType();
      PsiExpression lExpr = binaryExpression.getLOperand();
      PsiExpression rExpr = binaryExpression.getROperand();
      if (rExpr == null) {
        return;
      }

      PsiElementFactory factory = JavaPsiFacade.getInstance(project).getElementFactory();
      PsiMethodCallExpression equalsCall =
          (PsiMethodCallExpression) factory.createExpressionFromText("a.equals(b)", null);

      equalsCall.getMethodExpression().getQualifierExpression().replace(lExpr);
      equalsCall.getArgumentList().getExpressions()[0].replace(rExpr);

      PsiExpression result = (PsiExpression) binaryExpression.replace(equalsCall);

      if (opSign == JavaTokenType.NE) {
        PsiPrefixExpression negation = (PsiPrefixExpression) factory
            .createExpressionFromText("!a", null);
        negation.getOperand().replace(result);
        result.replace(negation);
      }
    } catch (IncorrectOperationException e) {
      throw new RuntimeException(e);
    }
  }
}
