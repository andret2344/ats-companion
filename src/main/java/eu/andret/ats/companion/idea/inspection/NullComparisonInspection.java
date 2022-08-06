/*
 * Copyright Andret Tools System (c) 2018-2022. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.ats.companion.idea.inspection;

import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaElementVisitor;
import com.intellij.psi.JavaTokenType;
import com.intellij.psi.PsiBinaryExpression;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiIfStatement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiPrimitiveType;
import com.intellij.psi.PsiType;
import eu.andret.ats.companion.idea.utilities.Util;
import eu.andret.ats.companion.idea.utilities.Verifier;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Optional;

public class NullComparisonInspection extends AbstractBaseJavaLocalInspectionTool {
	@NotNull
	@Override
	public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, final boolean isOnTheFly) {
		return new JavaElementVisitor() {
			@NonNls
			private static final String DESCRIPTION_TEMPLATE = "The parameter is never null";

			@Override
			public void visitBinaryExpression(final PsiBinaryExpression expression) {
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
								holder.registerProblem(expression, DESCRIPTION_TEMPLATE,
										ProblemHighlightType.LIKE_UNUSED_SYMBOL, new UnwrapQuickFix());
							}
							if (expression.getOperationTokenType().equals(JavaTokenType.NE)) {
								holder.registerProblem(expression, DESCRIPTION_TEMPLATE,
										ProblemHighlightType.LIKE_UNUSED_SYMBOL, new RemoveQuickFix());
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
						.map(PsiType.NULL::equals)
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

	@Slf4j
	public static class UnwrapQuickFix implements LocalQuickFix {
		@NotNull
		@Override
		public String getName() {
			return "Unwrap";
		}

		@Override
		public void applyFix(@NotNull final Project project, @NotNull final ProblemDescriptor descriptor) {
			Optional.of(descriptor)
					.map(ProblemDescriptor::getPsiElement)
					.map(PsiElement::getParent)
					.map(PsiIfStatement.class::cast)
					.ifPresent(psiIfStatement -> {
						if (psiIfStatement.getThenBranch() != null) {
							psiIfStatement.replace(psiIfStatement.getThenBranch());
						} else {
							psiIfStatement.delete();
						}
					});
		}

		@Override
		@NotNull
		public String getFamilyName() {
			return getName();
		}
	}

	@Slf4j
	public static class RemoveQuickFix implements LocalQuickFix {
		@NotNull
		@Override
		public String getName() {
			return "Remove unreachable code";
		}

		@Override
		public void applyFix(@NotNull final Project project, @NotNull final ProblemDescriptor descriptor) {
			Optional.of(descriptor)
					.map(ProblemDescriptor::getPsiElement)
					.map(PsiElement::getParent)
					.map(PsiIfStatement.class::cast)
					.ifPresent(psiIfStatement -> {
						if (psiIfStatement.getElseBranch() != null) {
							psiIfStatement.replace(psiIfStatement.getElseBranch());
						} else {
							psiIfStatement.delete();
						}
					});
		}

		@Override
		@NotNull
		public String getFamilyName() {
			return getName();
		}
	}
}
