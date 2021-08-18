package eu.andret.ats.idea.inspection;

import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.openapi.diagnostic.Logger;
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
import eu.andret.arguments.api.annotation.Argument;
import eu.andret.arguments.api.entity.ExecutorType;
import eu.andret.ats.idea.utilities.Verifier;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Optional;

public class InstanceCheckInspection extends AbstractBaseJavaLocalInspectionTool {
	@NotNull
	@Override
	public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, final boolean isOnTheFly) {
		return new JavaElementVisitor() {
			@NonNls
			private static final String DESCRIPTION_TEMPLATE_UNUSED = "Executor type is already defined in an annotation";

			@NonNls
			private static final String DESCRIPTION_TEMPLATE_PROBLEM = "Executor type in an annotation is contradictory";

			@Override
			public void visitInstanceOfExpression(final PsiInstanceOfExpression expression) {
				final PsiElement context = expression.getContext();
				if (context == null) {
					return;
				}
				final PsiMethod method = (PsiMethod) context.getParent().getParent();
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
				Optional.of(method)
						.map(x -> x.getAnnotation(Argument.class.getName()))
						.map(x -> x.findAttributeValue("executorType"))
						.map(PsiElement::getReference)
						.map(PsiReference::resolve)
						.map(PsiElement::getText)
						.map(ExecutorType::valueOf)
						.ifPresent(executorType -> {
							final PsiElement resolve = ((PsiReference) expression.getOperand()).resolve();
							if (resolve instanceof PsiField && ((PsiField) resolve).getName().equals("sender")) {
								if (executorType.equals(ExecutorType.PLAYER)) {
									if (type.getCanonicalText().equals("org.bukkit.entity.Player")) {
										holder.registerProblem(expression, DESCRIPTION_TEMPLATE_UNUSED, ProblemHighlightType.LIKE_UNUSED_SYMBOL, new RemoveExpressionQuickFix());
									}
									if (type.getCanonicalText().equals("org.bukkit.command.ConsoleCommandSender")) {
										holder.registerProblem(expression, DESCRIPTION_TEMPLATE_PROBLEM, ProblemHighlightType.WARNING, new RemoveExpressionQuickFix());
									}
								}

								if (executorType.equals(ExecutorType.CONSOLE)) {
									if (type.getCanonicalText().equals("org.bukkit.entity.Player")) {
										holder.registerProblem(expression, DESCRIPTION_TEMPLATE_PROBLEM, ProblemHighlightType.WARNING, new RemoveExpressionQuickFix());
									}
									if (type.getCanonicalText().equals("org.bukkit.command.ConsoleCommandSender")) {
										holder.registerProblem(expression, DESCRIPTION_TEMPLATE_UNUSED, ProblemHighlightType.LIKE_UNUSED_SYMBOL, new RemoveExpressionQuickFix());
									}
								}
							}
						});
			}
		};
	}

	public static class RemoveExpressionQuickFix implements LocalQuickFix {
		private static final Logger LOG = Logger.getInstance("#eu.andret.ats.idea.annotation.ObsoleteInstanceCheckInspection.RemoveExpressionQuickFix");

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
				final PsiElement[] children = context.getLastChild().getLastChild().getChildren();
				final PsiElement[] psiElements = Arrays.copyOfRange(children, 2, children.length - 2);
				Arrays.stream(psiElements).forEach(x -> parent.addAfter(x, context));
				context.delete();
			} catch (final IncorrectOperationException e) {
				LOG.error(e);
			}
		}

		@Override
		@NotNull
		public String getFamilyName() {
			return getName();
		}
	}
}
