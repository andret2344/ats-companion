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
import com.intellij.psi.PsiPrimitiveType;
import com.intellij.psi.PsiType;
import com.intellij.util.IncorrectOperationException;
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
						.filter(x -> !(x.getType() instanceof PsiPrimitiveType))
						.forEach(x -> {
							final boolean rNull = isNull(expression.getROperand());
							final boolean lNull = isNull(expression.getLOperand());
							final boolean rMatches = is(expression.getROperand(), x);
							final boolean lMatches = is(expression.getLOperand(), x);
							if ((!lMatches || !rNull) && (!rMatches || !lNull)) {
								return;
							}
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

			public boolean isNull(final PsiExpression expression) {
				return Optional.of(expression)
						.map(PsiExpression::getType)
						.map(PsiType.NULL::equals)
						.orElse(false);
			}

			public boolean is(final PsiExpression expression, final PsiElement element) {
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
			try {
				Optional.of(descriptor)
						.map(ProblemDescriptor::getPsiElement)
						.map(PsiElement::getParent)
						.map(PsiIfStatement.class::cast)
						.ifPresent(x -> {
							if (x.getThenBranch() != null) {
								x.replace(x.getThenBranch());
							} else {
								x.delete();
							}
						});
			} catch (final IncorrectOperationException e) {
				log.error(getClass().getName(), e);
			}
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
			try {
				Optional.of(descriptor)
						.map(ProblemDescriptor::getPsiElement)
						.map(PsiElement::getParent)
						.map(PsiIfStatement.class::cast)
						.ifPresent(x -> {
							if (x.getElseBranch() != null) {
								x.replace(x.getElseBranch());
							} else {
								x.delete();
							}
						});
			} catch (final IncorrectOperationException e) {
				log.error(getClass().getName(), e);
			}
		}

		@Override
		@NotNull
		public String getFamilyName() {
			return getName();
		}
	}
}
