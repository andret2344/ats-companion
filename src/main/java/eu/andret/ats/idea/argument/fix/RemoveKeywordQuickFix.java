package eu.andret.ats.idea.argument.fix;

import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class RemoveKeywordQuickFix implements LocalQuickFix {
	private static final Logger LOG = Logger.getInstance("#eu.andret.ats.idea.annotation.fix.RemoveKeywordQuickFix");

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
