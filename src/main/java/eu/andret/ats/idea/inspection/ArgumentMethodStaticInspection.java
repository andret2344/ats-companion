package eu.andret.ats.idea.inspection;

import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaElementVisitor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiKeyword;
import com.intellij.psi.PsiMethod;
import com.intellij.util.IncorrectOperationException;
import eu.andret.ats.idea.utilities.Verifier;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Optional;

public class ArgumentMethodStaticInspection extends AbstractBaseJavaLocalInspectionTool {
	@NotNull
	@Override
	public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, final boolean isOnTheFly) {
		return new JavaElementVisitor() {

			@NonNls
			private static final String DESCRIPTION_TEMPLATE = "Method annotated with @Argument annotation cannot use static qualifier";

			@Override
			public void visitMethod(@NotNull final PsiMethod method) {
				if (!Verifier.verifyArgumentMethod(method)) {
					return;
				}
				Arrays.stream(method.getModifierList().getChildren())
						.filter(PsiKeyword.class::isInstance)
						.map(PsiKeyword.class::cast)
						.filter(x -> x.textMatches(PsiKeyword.STATIC))
						.findAny()
						.ifPresent(keyword -> holder.registerProblem(keyword, DESCRIPTION_TEMPLATE, ProblemHighlightType.GENERIC_ERROR, new RemoveQualifierQuickFix()));
			}
		};
	}

	@Slf4j
	public static class RemoveQualifierQuickFix implements LocalQuickFix {
		@NotNull
		@Override
		public String getName() {
			return "Remove qualifier";
		}

		@Override
		public void applyFix(@NotNull final Project project, @NotNull final ProblemDescriptor descriptor) {
			try {
				Optional.of(descriptor)
						.map(ProblemDescriptor::getPsiElement)
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
