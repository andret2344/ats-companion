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
import com.intellij.psi.PsiArrayType;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import eu.andret.ats.companion.idea.utilities.Verifier;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

public class ArrayParameterInspection extends AbstractBaseJavaLocalInspectionTool {
	@NotNull
	@Override
	public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, final boolean isOnTheFly) {
		return new JavaElementVisitor() {
			@NonNls
			private static final String DESCRIPTION_TEMPLATE = "Array is not a valid type, use vararg instead";

			@Override
			public void visitMethod(@NotNull final PsiMethod method) {
				if (!Verifier.verifyArgumentMethod(method)) {
					return;
				}
				Arrays.stream(method.getParameterList().getParameters())
						.filter(parameter -> parameter.getType().isValid())
						.filter(parameter -> parameter.getType() instanceof PsiArrayType)
						.filter(parameter -> !parameter.isVarArgs())
						.map(PsiParameter::getTypeElement)
						.filter(Objects::nonNull)
						.forEach(typeElement -> holder.registerProblem(typeElement, DESCRIPTION_TEMPLATE,
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

	@Slf4j
	public static class ChangeToVarargQuickFix implements LocalQuickFix {
		@NotNull
		@Override
		public String getName() {
			return "Change to vararg";
		}

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

		@Override
		@NotNull
		public String getFamilyName() {
			return getName();
		}
	}

	@Slf4j
	public static class ConvertToSimpleVariableQuickFix implements LocalQuickFix {
		@NotNull
		@Override
		public String getName() {
			return "Convert to simple variable";
		}

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

		@Override
		@NotNull
		public String getFamilyName() {
			return getName();
		}
	}
}
