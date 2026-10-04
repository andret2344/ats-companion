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
import com.intellij.psi.PsiNameValuePair;
import eu.andret.ats.companion.idea.utilities.Constants;
import eu.andret.ats.companion.idea.utilities.Verifier;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Optional;
import java.util.regex.Pattern;

public class PositionOutOfBoundsInspection extends AbstractBaseJavaLocalInspectionTool {
	// I'm sure pattern is correct
	private static final Pattern INTEGER_REGEX = Pattern.compile("-?\\d+");

	@NonNls
	public static final String DESCRIPTION = "Position must be a positive number not greater than method's parameters count";

	@NotNull
	@Override
	public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, final boolean isOnTheFly) {
		return new JavaElementVisitor() {
			@Override
			public void visitMethod(@NotNull final PsiMethod method) {
				if (!Verifier.isArgumentMethod(method)) {
					return;
				}
				final int args = method.getParameterList().getParametersCount();
				Optional.of(method)
						.map(psiMethod -> psiMethod.getAnnotation(Constants.ANNOTATION_ARGUMENT))
						.map(PsiAnnotation::getParameterList)
						.map(PsiAnnotationParameterList::getAttributes)
						.stream()
						.flatMap(Arrays::stream)
						.filter(nameValuePair -> "position".equals(nameValuePair.getName()))
						.findFirst()
						.filter(nameValuePair -> nameValuePair.getValue() != null)
						.filter(PsiElement::isValid)
						.map(PsiNameValuePair::getValue)
						.filter(value -> value.getText().matches(INTEGER_REGEX.pattern()))
						.ifPresent(value -> {
							final int intPosition = Integer.parseInt(value.getText());
							if (intPosition < 0 || intPosition > args) {
								holder.registerProblem(value, DESCRIPTION,
										ProblemHighlightType.ERROR, new RemoveParameterQuickFix());
							}
						});
			}
		};
	}

	public static class RemoveParameterQuickFix implements LocalQuickFix {
		public static final String NAME = "Remove parameter";

		@Override
		public void applyFix(@NotNull final Project project, @NotNull final ProblemDescriptor descriptor) {
			Optional.of(descriptor)
					.map(ProblemDescriptor::getPsiElement)
					.map(PsiElement::getParent)
					.ifPresent(PsiElement::delete);
		}

		@NotNull
		@Override
		public String getFamilyName() {
			return NAME;
		}
	}
}
