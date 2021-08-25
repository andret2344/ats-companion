package eu.andret.ats.idea.inspection;

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
import eu.andret.arguments.api.annotation.Argument;
import eu.andret.ats.idea.utilities.Verifier;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Optional;

public class PositionOutOfBoundsInspection extends AbstractBaseJavaLocalInspectionTool {
	@NotNull
	@Override
	public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, final boolean isOnTheFly) {
		return new JavaElementVisitor() {

			@NonNls
			private static final String DESCRIPTION_TEMPLATE
					= "Position must be positive number not greater than method's parameters count";

			@Override
			public void visitMethod(@NotNull final PsiMethod method) {
				if (!Verifier.verifyArgumentMethod(method)) {
					return;
				}
				final int args = method.getParameterList().getParametersCount();
				Optional.of(method)
						.map(x -> x.getAnnotation(Argument.class.getName()))
						.map(PsiAnnotation::getParameterList)
						.map(PsiAnnotationParameterList::getAttributes)
						.stream()
						.flatMap(Arrays::stream)
						.filter(x -> "position".equals(x.getName()))
						.findFirst()
						.filter(x -> x.getValue() != null)
						.filter(PsiElement::isValid)
						.ifPresent(x -> {
							try {
								final int intPosition = Integer.parseInt(x.getValue().getText());
								if (intPosition < 0 || intPosition > args) {
									holder.registerProblem(x.getValue(), DESCRIPTION_TEMPLATE,
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
		@NotNull
		@Override
		public String getName() {
			return "Remove parameter";
		}

		@Override
		public void applyFix(@NotNull final Project project, @NotNull final ProblemDescriptor descriptor) {
			try {
				Optional.of(descriptor)
						.map(ProblemDescriptor::getPsiElement)
						.map(PsiElement::getParent)
						.ifPresent(PsiElement::delete);
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
