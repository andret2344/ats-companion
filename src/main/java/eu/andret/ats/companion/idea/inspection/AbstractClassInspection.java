package eu.andret.ats.companion.idea.inspection;

import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaElementVisitor;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiKeyword;
import com.intellij.psi.PsiModifierListOwner;
import eu.andret.ats.companion.idea.utilities.Constants;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Optional;

public class AbstractClassInspection extends AbstractBaseJavaLocalInspectionTool {
	@NonNls
	public static final String DESCRIPTION = "@BaseCommand class cannot be abstract";

	@NotNull
	@Override
	public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, final boolean isOnTheFly) {
		return new JavaElementVisitor() {
			@Override
			public void visitClass(@NotNull final PsiClass aClass) {
				Optional.of(aClass)
						.filter(psiClass -> psiClass.hasAnnotation(Constants.ANNOTATION_BASE_COMMAND))
						.map(PsiModifierListOwner::getModifierList)
						.map(PsiElement::getChildren)
						.stream()
						.flatMap(Arrays::stream)
						.filter(PsiKeyword.class::isInstance)
						.map(PsiKeyword.class::cast)
						.filter(keyword -> keyword.getText().equals(PsiKeyword.ABSTRACT))
						.findAny()
						.ifPresent(identifier -> holder.registerProblem(identifier, DESCRIPTION,
								ProblemHighlightType.GENERIC_ERROR, new RemoveModifierQuickFix()));
			}
		};
	}

	public static class RemoveModifierQuickFix implements LocalQuickFix {
		public static final String NAME = "Remove modifier";

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
