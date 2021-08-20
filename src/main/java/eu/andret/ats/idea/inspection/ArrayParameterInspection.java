package eu.andret.ats.idea.inspection;

import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaElementVisitor;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiArrayType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import com.intellij.util.IncorrectOperationException;
import eu.andret.ats.idea.utilities.Verifier;
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
						.filter(psiType -> psiType.getType().isValid())
						.filter(obj -> obj.getType() instanceof PsiArrayType)
						.filter(x -> !x.isVarArgs())
						.map(PsiParameter::getTypeElement)
						.filter(Objects::nonNull)
						.forEach(psiTypeElement -> holder.registerProblem(psiTypeElement, DESCRIPTION_TEMPLATE, ProblemHighlightType.ERROR, getFixes()));
			}

			private LocalQuickFix[] getFixes() {
				return new LocalQuickFix[]{
						new ChangeToVarargQuickFix(),
						new ConvertToSimpleVariableQuickFix()
				};
			}
		};
	}

	public static class ChangeToVarargQuickFix implements LocalQuickFix {
		private static final Logger LOG = Logger.getInstance("#eu.andret.ats.idea.annotation.ArrayParameterInspection.ChangeToVarargQuickFix");

		@NotNull
		@Override
		public String getName() {
			return "Change to vararg";
		}

		@Override
		public void applyFix(@NotNull final Project project, @NotNull final ProblemDescriptor descriptor) {
			try {
				Optional.of(descriptor)
						.map(ProblemDescriptor::getPsiElement)
						.map(PsiElement::getParent)
						.map(PsiParameter.class::cast)
						.ifPresent(psiParameter -> {
							final PsiElementFactory factory = JavaPsiFacade.getInstance(project).getElementFactory();
							final PsiArrayType type = (PsiArrayType) psiParameter.getType();
							final String newType = type.getComponentType().getCanonicalText() + "...";
							psiParameter.replace(factory.createParameter(psiParameter.getName(), factory.createTypeByFQClassName(newType)));
						});
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

	public static class ConvertToSimpleVariableQuickFix implements LocalQuickFix {
		private static final Logger LOG = Logger.getInstance("#eu.andret.ats.idea.annotation.ArrayParameterInspection.ChangeToSimpleVariableQuickFix");

		@NotNull
		@Override
		public String getName() {
			return "Convert to simple variable";
		}

		@Override
		public void applyFix(@NotNull final Project project, @NotNull final ProblemDescriptor descriptor) {
			try {
				Optional.of(descriptor)
						.map(ProblemDescriptor::getPsiElement)
						.map(PsiElement::getParent)
						.map(PsiParameter.class::cast)
						.ifPresent(psiParameter -> {
							final PsiElementFactory factory = JavaPsiFacade.getInstance(project).getElementFactory();
							final PsiArrayType type = (PsiArrayType) psiParameter.getType();
							final String newType = type.getComponentType().getCanonicalText();
							psiParameter.replace(factory.createParameter(psiParameter.getName(), factory.createTypeByFQClassName(newType)));
						});
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
