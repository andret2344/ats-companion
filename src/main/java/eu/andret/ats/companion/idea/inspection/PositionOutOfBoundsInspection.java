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
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiAnnotationParameterList;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiMethod;
import com.intellij.util.IncorrectOperationException;
import eu.andret.ats.companion.idea.utilities.Constants;
import eu.andret.ats.companion.idea.utilities.Verifier;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Optional;

public class PositionOutOfBoundsInspection extends AbstractBaseJavaLocalInspectionTool {
	@NonNls
	public static final String DESCRIPTION = "Position must be positive number not greater than method's parameters count";

	@NotNull
	@Override
	public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, final boolean isOnTheFly) {
		return new JavaElementVisitor() {
			@Override
			public void visitMethod(@NotNull final PsiMethod psiMethod) {
				if (!Verifier.verifyArgumentMethod(psiMethod)) {
					return;
				}
				final int args = psiMethod.getParameterList().getParametersCount();
				Optional.of(psiMethod)
						.map(method -> method.getAnnotation(Constants.ANNOTATION_ARGUMENT))
						.map(PsiAnnotation::getParameterList)
						.map(PsiAnnotationParameterList::getAttributes)
						.stream()
						.flatMap(Arrays::stream)
						.filter(nameValuePair -> "position".equals(nameValuePair.getName()))
						.findFirst()
						.filter(nameValuePair -> nameValuePair.getValue() != null)
						.filter(PsiElement::isValid)
						.ifPresent(nameValuePair -> {
							try {
								final int intPosition = Integer.parseInt(nameValuePair.getValue().getText());
								if (intPosition < 0 || intPosition > args) {
									holder.registerProblem(nameValuePair.getValue(), DESCRIPTION,
											ProblemHighlightType.ERROR, new RemoveParameterQuickFix());
								}
							} catch (final NumberFormatException e) {
								// Do nothing
							}
						});
			}
		};
	}

	@Slf4j
	public static class RemoveParameterQuickFix implements LocalQuickFix {
		public static final String NAME = "Remove parameter";

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
					.ifPresent(PsiElement::delete);
		}

		@Override
		@NotNull
		public String getFamilyName() {
			return getName();
		}
	}
}
