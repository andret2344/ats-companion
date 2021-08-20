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
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiPrimitiveType;
import com.intellij.psi.PsiType;
import com.intellij.psi.PsiTypeElement;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import com.intellij.util.IncorrectOperationException;
import eu.andret.arguments.api.annotation.Argument;
import eu.andret.ats.idea.utilities.Verifier;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class PrimitiveReturnInspection extends AbstractBaseJavaLocalInspectionTool {
	@NotNull
	@Override
	public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, final boolean isOnTheFly) {
		return new JavaElementVisitor() {
			@NonNls
			private static final String DESCRIPTION_TEMPLATE = "Method probably shouldn't return a primitive";

			@Override
			public void visitMethod(@NotNull final PsiMethod method) {
				if (!Verifier.verifyClass(method.getContainingClass())) {
					return;
				}
				if (!method.hasAnnotation(Argument.class.getName())) {
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
					holder.registerProblem(returnTypeElement, DESCRIPTION_TEMPLATE, ProblemHighlightType.WARNING, new ChangeToStringQuickFix());
				}
			}
		};
	}

	public static class ChangeToStringQuickFix implements LocalQuickFix {
		private static final Logger LOG = Logger.getInstance("#eu.andret.ats.idea.annotation.PrimitiveReturnInspection.ChangeToStringQuickFix");

		@NotNull
		@Override
		public String getName() {
			return "Change to String";
		}

		@Override
		public void applyFix(@NotNull final Project project, @NotNull final ProblemDescriptor descriptor) {
			try {
				Optional.of(descriptor)
						.map(ProblemDescriptor::getPsiElement)
						.map(PsiElement::getParent)
						.map(PsiMethod.class::cast)
						.map(PsiMethod::getReturnTypeElement)
						.ifPresent(typeElement -> {
							final PsiElementFactory factory = JavaPsiFacade.getInstance(project).getElementFactory();
							typeElement.replace(factory.createTypeElement(factory.createTypeByFQClassName("String")));
							JavaCodeStyleManager.getInstance(project).shortenClassReferences(typeElement);
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
