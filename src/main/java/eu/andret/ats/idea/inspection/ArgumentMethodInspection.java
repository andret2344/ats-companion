package eu.andret.ats.idea.inspection;

import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaElementVisitor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiJvmMember;
import com.intellij.psi.PsiKeyword;
import com.intellij.psi.PsiMethod;
import com.intellij.util.IncorrectOperationException;
import eu.andret.arguments.api.annotation.Argument;
import eu.andret.ats.idea.utilities.Verifier;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Optional;

public class ArgumentMethodInspection extends AbstractBaseJavaLocalInspectionTool {
	@NotNull
	@Override
	public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, final boolean isOnTheFly) {
		return new JavaElementVisitor() {

			@NonNls
			private static final String DESCRIPTION_TEMPLATE = "Static method cannot be annotated with @Argument annotation";

			@Override
			public void visitMethod(@NotNull final PsiMethod method) {
				// FIXME temporary restriction
				if (Optional.of(method)
						.map(PsiJvmMember::getContainingClass)
						.map(NavigationItem::getName)
						.filter(x -> x.equals("TestCommand"))
						.isEmpty()) {
					return;
				}
				if (!Verifier.verifyClass(method.getContainingClass())) {
					return;
				}
				if (!method.hasAnnotation(Argument.class.getName())) {
					return;
				}
				Arrays.stream(method.getModifierList().getChildren())
						.filter(x -> x instanceof PsiKeyword)
						.map(PsiKeyword.class::cast)
						.filter(x -> x.textMatches(PsiKeyword.STATIC))
						.findAny()
						.ifPresent(keyword -> holder.registerProblem(keyword, DESCRIPTION_TEMPLATE, ProblemHighlightType.GENERIC_ERROR, new RemoveKeywordQuickFix()));
			}
		};
	}

	public static class RemoveKeywordQuickFix implements LocalQuickFix {
		private static final Logger LOG = Logger.getInstance("#eu.andret.ats.idea.annotation.ArgumentMethodInspection.RemoveKeywordQuickFix");

		@NotNull
		@Override
		public String getName() {
			return "Remove keyword";
		}

		@Override
		public void applyFix(@NotNull final Project project, @NotNull final ProblemDescriptor descriptor) {
			try {
				Optional.of(descriptor)
						.map(ProblemDescriptor::getPsiElement)
						.ifPresent(PsiElement::delete);
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
