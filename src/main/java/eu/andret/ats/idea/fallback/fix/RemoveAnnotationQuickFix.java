package eu.andret.ats.idea.fallback.fix;

import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.util.IncorrectOperationException;
import eu.andret.ats.idea.fallback.FallbackMethodInspection;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class RemoveAnnotationQuickFix implements LocalQuickFix {
	private static final Logger LOG = Logger.getInstance("#eu.andret.ats.idea.fallback.fix.RemoveAnnotationQuickFix");

	@NotNull
	@Override
	public String getName() {
		return "Remove @Fallback annotation";
	}

	@Override
	public void applyFix(@NotNull final Project project, @NotNull final ProblemDescriptor descriptor) {
		try {
			Optional.of(descriptor)
					.map(ProblemDescriptor::getPsiElement)
					.map(PsiElement::getParent)
					.map(psiElement -> (PsiMethod) psiElement)
					.map(psiMethod -> psiMethod.getAnnotation(FallbackMethodInspection.API_ANNOTATION_FALLBACK))
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
