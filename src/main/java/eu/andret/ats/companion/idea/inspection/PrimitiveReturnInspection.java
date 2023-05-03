/*
 * Copyright (c) 2018 Andret Tools System. Copying and modifying allowed only keeping git link reference.
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
import eu.andret.ats.companion.idea.utilities.Util;
import eu.andret.ats.companion.idea.utilities.Verifier;
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
				Optional.of(method)
						.filter(psiMethod -> Verifier.isBaseCommandClass(method.getContainingClass()))
						.filter(psiMethod -> psiMethod.hasAnnotation(Constants.ANNOTATION_ARGUMENT))
						.map(PsiMethod::getReturnTypeElement)
						.ifPresent(psiTypeElement -> Optional.of(psiTypeElement)
								.map(PsiTypeElement::getType)
								.filter(PsiType::isValid)
								.filter(psiType -> !psiType.equalsToText("void"))
								.filter(PsiPrimitiveType.class::isInstance)
								.ifPresent(ignored -> holder.registerProblem(psiTypeElement, DESCRIPTION,
										ProblemHighlightType.WARNING, new ChangeToStringQuickFix())));

			}
		};
	}

	public static class ChangeToStringQuickFix implements LocalQuickFix {
		public static final String NAME = "Change to String";

		@Override
		public void applyFix(@NotNull final Project project, @NotNull final ProblemDescriptor descriptor) {
			Optional.of(descriptor)
					.map(ProblemDescriptor::getPsiElement)
					.map(PsiElement::getParent)
					.map(PsiMethod.class::cast)
					.map(PsiMethod::getReturnTypeElement)
					.ifPresent(typeElement -> {
						final PsiElementFactory factory = JavaPsiFacade.getInstance(project).getElementFactory();
						final PsiClassType classType = Util.createStringType(project);
						typeElement.replace(factory.createTypeElement(classType));
						JavaCodeStyleManager.getInstance(project).shortenClassReferences(typeElement);
					});
		}

		@NotNull
		@Override
		public String getFamilyName() {
			return NAME;
		}
	}
}
