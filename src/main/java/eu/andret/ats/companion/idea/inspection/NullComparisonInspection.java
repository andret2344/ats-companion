/*
 * Copyright (c) 2018 Andret Tools System. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.ats.companion.idea.inspection;

import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.JavaElementVisitor;
import com.intellij.psi.JavaTokenType;
import com.intellij.psi.PsiBinaryExpression;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameterList;
import com.intellij.psi.PsiPrimitiveType;
import com.intellij.psi.PsiTypes;
import com.intellij.psi.util.PsiTreeUtil;
import eu.andret.ats.companion.idea.utilities.Verifier;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Optional;

public class NullComparisonInspection extends AbstractBaseJavaLocalInspectionTool {
	@NonNls
	public static final String DESCRIPTION = "The parameter is never null";

	@NotNull
	@Override
	public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, final boolean isOnTheFly) {
		return new JavaElementVisitor() {
			@Override
			public void visitBinaryExpression(@NotNull final PsiBinaryExpression expression) {
				Optional.of(expression)
						.map(PsiElement::getContext)
						.map(element -> PsiTreeUtil.getParentOfType(element, PsiMethod.class))
						.filter(Verifier::isArgumentMethod)
						.map(PsiMethod::getParameterList)
						.map(PsiParameterList::getParameters)
						.stream()
						.flatMap(Arrays::stream)
						.filter(psiParameter -> !(psiParameter.getType() instanceof PsiPrimitiveType))
						.filter(psiParameter -> validateParameter(psiParameter, expression))
						.forEach(psiParameter -> {
							if (expression.getOperationTokenType().equals(JavaTokenType.EQEQ)) {
								holder.registerProblem(expression, DESCRIPTION,
										ProblemHighlightType.LIKE_UNUSED_SYMBOL, new InstanceCheckInspection.UnWrapIfStatementQuickFix());
							}
							if (expression.getOperationTokenType().equals(JavaTokenType.NE)) {
								holder.registerProblem(expression, DESCRIPTION,
										ProblemHighlightType.LIKE_UNUSED_SYMBOL, new InstanceCheckInspection.UnWrapElseStatementQuickFix());
							}
						});
			}

			private boolean validateParameter(final PsiElement psiElement, final PsiBinaryExpression expression) {
				final boolean rNull = isNull(expression.getROperand());
				final boolean lNull = isNull(expression.getLOperand());
				final boolean rMatches = is(expression.getROperand(), psiElement);
				final boolean lMatches = is(expression.getLOperand(), psiElement);
				return lMatches && rNull || rMatches && lNull;
			}

			private boolean isNull(final PsiExpression expression) {
				return Optional.ofNullable(expression)
						.map(PsiExpression::getType)
						.map(PsiTypes.nullType()::equals)
						.orElse(false);
			}

			private boolean is(final PsiExpression expression, final PsiElement element) {
				return Optional.ofNullable(expression)
						.map(PsiExpression::getReference)
						.map(reference -> reference.isReferenceTo(element))
						.orElse(false);
			}
		};
	}
}
