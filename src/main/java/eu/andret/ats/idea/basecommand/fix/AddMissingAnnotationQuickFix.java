package eu.andret.ats.idea.basecommand.fix;

import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiModifierListOwner;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import com.intellij.util.IncorrectOperationException;
import eu.andret.ats.idea.utilities.Constants;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class AddMissingAnnotationQuickFix implements LocalQuickFix {
	private static final Logger LOG = Logger.getInstance("#eu.andret.ats.idea.fallback.fix.RemoveAnnotationQuickFix");

	@NotNull
	@Override
	public String getName() {
		return "Add @BaseCommand annotation";
	}

	@Override
	public void applyFix(@NotNull final Project project, @NotNull final ProblemDescriptor descriptor) {
		try {
			Optional.of(descriptor)
					.map(ProblemDescriptor::getPsiElement)
					.map(PsiElement::getParent)
					.map(PsiClass.class::cast)
					.map(PsiModifierListOwner::getModifierList)
					.ifPresent(psiModifierList -> {
						final PsiElementFactory factory = JavaPsiFacade.getInstance(project).getElementFactory();
						final PsiAnnotation psiAnnotation = factory.createAnnotationFromText("@" + Constants.API_ANNOTATION_BASE_COMMAND + "(\"\")", psiModifierList.getParent());
						final PsiElement inserted = psiModifierList.addBefore(psiAnnotation, psiModifierList.getFirstChild());
						JavaCodeStyleManager.getInstance(project).shortenClassReferences(inserted);
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
