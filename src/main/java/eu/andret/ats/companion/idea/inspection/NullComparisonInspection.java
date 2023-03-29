/*
 * Copyright Andret Tools System (c) 2018-2023. Copying and modifying allowed only keeping git link reference.
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
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiPrimitiveType;
import com.intellij.psi.PsiTypes;
import eu.andret.ats.companion.idea.utilities.Util;
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
				final PsiElement context = expression.getContext();
				if (context == null) {
					return;
				}
				final PsiMethod method = Util.ancestorOf(context, PsiMethod.class);
				if (!Verifier.verifyArgumentMethod(method)) {
					return;
				}
				Arrays.stream(method.getParameterList().getParameters())
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

			private boolean validateParameter(final PsiParameter psiParameter, final PsiBinaryExpression expression) {
				final boolean rNull = isNull(expression.getROperand());
				final boolean lNull = isNull(expression.getLOperand());
				final boolean rMatches = is(expression.getROperand(), psiParameter);
				final boolean lMatches = is(expression.getLOperand(), psiParameter);
				return (lMatches && rNull) || (rMatches && lNull);
			}

			private boolean isNull(final PsiExpression expression) {
				return Optional.of(expression)
						.map(PsiExpression::getType)
						.map(PsiTypes.nullType()::equals)
						.orElse(false);
			}

			private boolean is(final PsiExpression expression, final PsiElement element) {
				return Optional.of(expression)
						.map(PsiExpression::getReference)
						.map(reference -> reference.isReferenceTo(element))
						.orElse(false);
			}
		};
	}
}
