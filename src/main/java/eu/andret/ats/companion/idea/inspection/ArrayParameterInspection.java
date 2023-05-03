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
import com.intellij.psi.PsiArrayType;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import eu.andret.ats.companion.idea.utilities.Verifier;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

public class ArrayParameterInspection extends AbstractBaseJavaLocalInspectionTool {
	@NonNls
	public static final String DESCRIPTION = "Array is not a valid type, use vararg instead";

	@NotNull
	@Override
	public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, final boolean isOnTheFly) {
		return new JavaElementVisitor() {
			@Override
			public void visitMethod(@NotNull final PsiMethod method) {
				if (!Verifier.isArgumentMethod(method)) {
					return;
				}
				Arrays.stream(method.getParameterList().getParameters())
						.filter(parameter -> parameter.getType().isValid())
						.filter(parameter -> parameter.getType() instanceof PsiArrayType)
						.filter(parameter -> !parameter.isVarArgs())
						.map(PsiParameter::getTypeElement)
						.filter(Objects::nonNull)
						.forEach(typeElement -> holder.registerProblem(typeElement, DESCRIPTION,
								ProblemHighlightType.ERROR, getFixes()));
			}

			@NotNull
			private LocalQuickFix[] getFixes() {
				return new LocalQuickFix[]{
						new ChangeToVarargQuickFix(),
						new ConvertToSimpleVariableQuickFix()
				};
			}
		};
	}

	public static class ChangeToVarargQuickFix implements LocalQuickFix {
		public static final String NAME = "Change to vararg";

		@Override
		public void applyFix(@NotNull final Project project, @NotNull final ProblemDescriptor descriptor) {
			Optional.of(descriptor)
					.map(ProblemDescriptor::getPsiElement)
					.map(PsiElement::getParent)
					.map(PsiParameter.class::cast)
					.ifPresent(parameter -> {
						final PsiElementFactory factory = JavaPsiFacade.getInstance(project).getElementFactory();
						final PsiArrayType type = (PsiArrayType) parameter.getType();
						final String newType = type.getComponentType().getCanonicalText() + "...";
						final PsiClassType classType = factory.createTypeByFQClassName(newType);
						parameter.replace(factory.createParameter(parameter.getName(), classType));
					});
		}

		@NotNull
		@Override
		public String getFamilyName() {
			return NAME;
		}
	}

	public static class ConvertToSimpleVariableQuickFix implements LocalQuickFix {
		public static final String NAME = "Convert to simple variable";

		@Override
		public void applyFix(@NotNull final Project project, @NotNull final ProblemDescriptor descriptor) {
			Optional.of(descriptor)
					.map(ProblemDescriptor::getPsiElement)
					.map(PsiElement::getParent)
					.map(PsiParameter.class::cast)
					.ifPresent(psiParameter -> {
						final PsiElementFactory factory = JavaPsiFacade.getInstance(project).getElementFactory();
						final PsiArrayType type = (PsiArrayType) psiParameter.getType();
						final String newType = type.getComponentType().getCanonicalText();
						final PsiClassType classType = factory.createTypeByFQClassName(newType);
						psiParameter.replace(factory.createParameter(psiParameter.getName(), classType));
					});
		}

		@NotNull
		@Override
		public String getFamilyName() {
			return NAME;
		}
	}
}
