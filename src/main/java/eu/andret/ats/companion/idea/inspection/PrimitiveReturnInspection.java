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
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiPrimitiveType;
import com.intellij.psi.PsiType;
import com.intellij.psi.PsiTypeElement;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import eu.andret.ats.companion.idea.utilities.Constants;
import eu.andret.ats.companion.idea.utilities.Verifier;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class PrimitiveReturnInspection extends AbstractBaseJavaLocalInspectionTool {
	@NonNls
	public static final String DESCRIPTION = "Method probably shouldn't return a primitive";

	@NotNull
	@Override
	public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, final boolean isOnTheFly) {
		return new JavaElementVisitor() {
			@Override
			public void visitMethod(@NotNull final PsiMethod method) {
				if (!Verifier.verifyClass(method.getContainingClass())) {
					return;
				}
				if (!method.hasAnnotation(Constants.ANNOTATION_ARGUMENT)) {
					return;
				}
				final PsiTypeElement returnTypeElement = method.getReturnTypeElement();
				if (returnTypeElement == null) {
					return;
				}
				final PsiType returnType = returnTypeElement.getType();
				if (!returnType.isValid()) {
					return;
				}
				if (returnType.equalsToText("void")) {
					return;
				}
				if (returnType instanceof PsiPrimitiveType) {
					holder.registerProblem(returnTypeElement, DESCRIPTION, ProblemHighlightType.WARNING, new ChangeToStringQuickFix());
				}
			}
		};
	}

	@Slf4j
	public static class ChangeToStringQuickFix implements LocalQuickFix {
		public static final String NAME = "Change to String";

		@NotNull
		@Override
		public String getName() {
			return NAME;
		}

		@Override
		public void applyFix(@NotNull final Project project, @NotNull final ProblemDescriptor descriptor) {
			Optional.of(descriptor)
					.map(ProblemDescriptor::getPsiElement)
					.map(PsiElement::getParent)
					.map(PsiMethod.class::cast)
					.map(PsiMethod::getReturnTypeElement)
					.ifPresent(typeElement -> {
						final PsiElementFactory factory = JavaPsiFacade.getInstance(project).getElementFactory();
						final PsiClassType classType = factory.createTypeByFQClassName("String");
						typeElement.replace(factory.createTypeElement(classType));
						JavaCodeStyleManager.getInstance(project).shortenClassReferences(typeElement);
					});
		}

		@Override
		@NotNull
		public String getFamilyName() {
			return getName();
		}
	}
}
