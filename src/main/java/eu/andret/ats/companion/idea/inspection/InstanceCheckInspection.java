package eu.andret.ats.companion.idea.inspection;

import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaElementVisitor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiInstanceOfExpression;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiType;
import com.intellij.psi.PsiTypeElement;
import com.intellij.util.IncorrectOperationException;
import eu.andret.ats.companion.idea.utilities.Constants;
import eu.andret.ats.companion.idea.utilities.Util;
import eu.andret.ats.companion.idea.utilities.Verifier;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Optional;

public class InstanceCheckInspection extends AbstractBaseJavaLocalInspectionTool {
	@NonNls
	private static final String DESCRIPTION_TEMPLATE_UNUSED = "Executor type is already defined in an annotation";

	@NonNls
	private static final String DESCRIPTION_TEMPLATE_PROBLEM = "Executor type in an annotation is contradictory";

	@NotNull
	@Override
	public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, final boolean isOnTheFly) {
		return new JavaElementVisitor() {
			@Override
			public void visitInstanceOfExpression(final PsiInstanceOfExpression expression) {
				final PsiElement context = expression.getContext();
				if (context == null) {
					return;
				}
				final PsiMethod method = Util.ancestorOf(context, PsiMethod.class);
				if (!Verifier.verifyArgumentMethod(method)) {
					return;
				}
				final PsiTypeElement checkType = expression.getCheckType();
				if (checkType == null) {
					return;
				}
				final PsiType type = checkType.getType();
				if (!type.isValid()) {
					return;
				}
				findElementAndValidate(expression, method, type);
			}

			private void findElementAndValidate(final PsiInstanceOfExpression expression, final PsiMethod psiMethod,
												final PsiType type) {
				Optional.of(psiMethod)
						.map(method -> method.getAnnotation(Constants.ANNOTATION_ARGUMENT))
						.map(annotation -> annotation.findAttributeValue("executorType"))
						.map(PsiElement::getReference)
						.map(PsiReference::resolve)
						.map(PsiField.class::cast)
						.ifPresent(field -> {
							final PsiElement resolve = ((PsiReference) expression.getOperand()).resolve();
							if (!(resolve instanceof PsiField) || !((PsiField) resolve).getName().equals("sender")) {
								return;
							}
							analyzeAndReport(holder, expression, type, field.getName());
						});
			}
		};
	}

	private void analyzeAndReport(final ProblemsHolder holder, final PsiInstanceOfExpression expression,
								  final PsiType type, @NotNull final String executorType) {
		if (executorType.equals("PLAYER")) {
			if (type.getCanonicalText().equals(Constants.BUKKIT_PLAYER)) {
				holder.registerProblem(expression, DESCRIPTION_TEMPLATE_UNUSED,
						ProblemHighlightType.LIKE_UNUSED_SYMBOL, new RemoveExpressionQuickFix());
			} else if (type.getCanonicalText().equals(Constants.BUKKIT_CONSOLE_COMMAND_SENDER)) {
				holder.registerProblem(expression, DESCRIPTION_TEMPLATE_PROBLEM,
						ProblemHighlightType.WARNING, new RemoveExpressionQuickFix());
			}
		} else if (executorType.equals("CONSOLE")) {
			if (type.getCanonicalText().equals(Constants.BUKKIT_PLAYER)) {
				holder.registerProblem(expression, DESCRIPTION_TEMPLATE_PROBLEM,
						ProblemHighlightType.WARNING, new RemoveExpressionQuickFix());
			} else if (type.getCanonicalText().equals(Constants.BUKKIT_CONSOLE_COMMAND_SENDER)) {
				holder.registerProblem(expression, DESCRIPTION_TEMPLATE_UNUSED,
						ProblemHighlightType.LIKE_UNUSED_SYMBOL, new RemoveExpressionQuickFix());
			}
		}
	}

	@Slf4j
	public static class RemoveExpressionQuickFix implements LocalQuickFix {
		@NotNull
		@Override
		public String getName() {
			return "Remove statement";
		}

		@Override
		public void applyFix(@NotNull final Project project, @NotNull final ProblemDescriptor descriptor) {
			try {
				final PsiElement context = descriptor.getPsiElement().getContext();
				if (context == null) {
					return;
				}
				final PsiElement parent = context.getParent();
				final PsiElement[] children = Util.repeat(context, 2, PsiElement::getLastChild).getChildren();
				final PsiElement[] psiElements = Arrays.copyOfRange(children, 2, children.length - 2);
				Arrays.stream(psiElements).forEach(element -> parent.addAfter(element, context));
				context.delete();
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
