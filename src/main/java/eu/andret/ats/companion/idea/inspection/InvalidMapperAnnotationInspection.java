package eu.andret.ats.companion.idea.inspection;

import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaElementVisitor;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.util.PsiTreeUtil;
import eu.andret.ats.companion.idea.utilities.Constants;
import eu.andret.ats.companion.idea.utilities.Verifier;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class InvalidMapperAnnotationInspection extends AbstractBaseJavaLocalInspectionTool {
	@NonNls
	public static final String DESCRIPTION = "Cannot use @Mapper inside non-@Argument method.";

	@NotNull
	@Override
	public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, final boolean isOnTheFly) {
		return new JavaElementVisitor() {
			@Override
			public void visitParameter(@NotNull final PsiParameter parameter) {
				final PsiAnnotation annotation = parameter.getAnnotation(Constants.ANNOTATION_MAPPER);
				if (annotation == null) {
					return;
				}
				final PsiMethod psiMethod = PsiTreeUtil.getParentOfType(parameter, PsiMethod.class);
				if (!Verifier.isArgumentMethod(psiMethod)) {
					holder.registerProblem(annotation, DESCRIPTION, ProblemHighlightType.GENERIC_ERROR,
							new RemoveAnnotationQuickFix());
				}
			}
		};
	}

	public static class RemoveAnnotationQuickFix implements LocalQuickFix {
		public static final String NAME = "Remove annotation";

		@Override
		public void applyFix(@NotNull final Project project, @NotNull final ProblemDescriptor descriptor) {
			Optional.of(descriptor)
					.map(ProblemDescriptor::getPsiElement)
					.ifPresent(PsiElement::delete);
		}

		@NotNull
		@Override
		public String getFamilyName() {
			return NAME;
		}
	}
}
