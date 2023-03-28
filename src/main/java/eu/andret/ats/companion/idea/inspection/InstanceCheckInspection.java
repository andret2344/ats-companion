/*
 * Copyright Andret Tools System (c) 2018-2022. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.ats.companion.idea.inspection;

import com.intellij.codeInsight.unwrap.JavaElseUnwrapper;
import com.intellij.codeInsight.unwrap.JavaIfUnwrapper;
import com.intellij.codeInsight.unwrap.JavaUnwrapper;
import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaElementVisitor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiIfStatement;
import com.intellij.psi.PsiInstanceOfExpression;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiType;
import com.intellij.psi.PsiTypeElement;
import eu.andret.ats.companion.idea.utilities.Constants;
import eu.andret.ats.companion.idea.utilities.Util;
import eu.andret.ats.companion.idea.utilities.Verifier;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class InstanceCheckInspection extends AbstractBaseJavaLocalInspectionTool {
	@NonNls
	public static final String DESCRIPTION_UNUSED = "Executor type is already defined in the annotation";

	@NonNls
	public static final String DESCRIPTION_PROBLEM = "Executor type defined in the annotation is contradictory";

	@NotNull
	@Override
	public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, final boolean isOnTheFly) {
		return new JavaElementVisitor() {
			@Override
			public void visitInstanceOfExpression(@NotNull final PsiInstanceOfExpression expression) {
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
						.map(PsiElement::getLastChild)
						.map(PsiElement::getText)
						.ifPresent(field -> {
							final PsiElement resolve = ((PsiReference) expression.getOperand()).resolve();
							if (!(resolve instanceof final PsiField psiField) || !psiField.getName().equals("sender")) {
								return;
							}
							analyzeAndReport(holder, expression, type, field);
						});
			}
		};
	}

	private void analyzeAndReport(final ProblemsHolder holder, final PsiInstanceOfExpression expression,
								  final PsiType type, @NotNull final String executorType) {
		if (executorType.equals("PLAYER")) {
			if (type.getCanonicalText().equals(Constants.BUKKIT_PLAYER)) {
				holder.registerProblem(expression, DESCRIPTION_UNUSED,
						ProblemHighlightType.LIKE_UNUSED_SYMBOL, new UnWrapIfStatementQuickFix());
			} else if (type.getCanonicalText().equals(Constants.BUKKIT_CONSOLE_COMMAND_SENDER)) {
				holder.registerProblem(expression, DESCRIPTION_PROBLEM,
						ProblemHighlightType.WARNING, new UnWrapElseStatementQuickFix());
			}
		} else if (executorType.equals("CONSOLE")) {
			if (type.getCanonicalText().equals(Constants.BUKKIT_PLAYER)) {
				holder.registerProblem(expression, DESCRIPTION_PROBLEM,
						ProblemHighlightType.WARNING, new UnWrapElseStatementQuickFix());
			} else if (type.getCanonicalText().equals(Constants.BUKKIT_CONSOLE_COMMAND_SENDER)) {
				holder.registerProblem(expression, DESCRIPTION_UNUSED,
						ProblemHighlightType.LIKE_UNUSED_SYMBOL, new UnWrapIfStatementQuickFix());
			}
		}
	}

	public abstract static class UnWrapStatementQuickFix implements LocalQuickFix {
		@NotNull
		public abstract JavaUnwrapper getUnWrapper();

		@Override
		public final void applyFix(@NotNull final Project project, @NotNull final ProblemDescriptor descriptor) {
			final PsiElement element = getElement(descriptor);
			final Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
			if (editor == null || element == null) {
				return;
			}
			getUnWrapper().unwrap(editor, element);
		}

		public abstract PsiElement getElement(@NotNull final ProblemDescriptor problemDescriptor);
	}

	public static class UnWrapIfStatementQuickFix extends UnWrapStatementQuickFix {
		public static final String NAME = "Unwrap if statement";

		@NotNull
		@Override
		public JavaUnwrapper getUnWrapper() {
			return new JavaIfUnwrapper();
		}

		@Nullable
		@Override
		public PsiElement getElement(@NotNull final ProblemDescriptor problemDescriptor) {
			return problemDescriptor.getPsiElement().getParent();
		}

		@NotNull
		@Override
		public String getFamilyName() {
			return NAME;
		}
	}

	public static class UnWrapElseStatementQuickFix extends UnWrapStatementQuickFix {
		public static final String NAME = "Unwrap else statement";

		@NotNull
		@Override
		public JavaUnwrapper getUnWrapper() {
			return new JavaElseUnwrapper();
		}

		@Nullable
		@Override
		public PsiElement getElement(@NotNull final ProblemDescriptor problemDescriptor) {
			return ((PsiIfStatement) problemDescriptor.getPsiElement().getParent()).getElseElement();
		}

		@NotNull
		@Override
		public String getFamilyName() {
			return NAME;
		}
	}
}
