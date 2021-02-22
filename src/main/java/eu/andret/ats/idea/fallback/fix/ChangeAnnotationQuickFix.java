package eu.andret.ats.idea.fallback.fix;

import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifierList;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import com.intellij.util.IncorrectOperationException;
import eu.andret.ats.idea.utilities.Constants;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class ChangeAnnotationQuickFix implements LocalQuickFix {
	private static final Logger LOG = Logger.getInstance("#eu.andret.ats.idea.fallback.fix.ChangeAnnotationQuickFix");

	@NotNull
	@Override
	public String getName() {
		return "Change to @Argument";
	}

	@Override
	public void applyFix(@NotNull final Project project, @NotNull final ProblemDescriptor descriptor) {
		try {
			Optional.of(descriptor)
					.map(ProblemDescriptor::getPsiElement)
					.map(PsiElement::getParent)
					.map(PsiMethod.class::cast)
					.ifPresent(psiMethod -> {
						final PsiAnnotation annotation = psiMethod.getAnnotation(Constants.API_ANNOTATION_FALLBACK);
						if (annotation != null) {
							annotation.delete();
							final PsiElementFactory factory = JavaPsiFacade.getInstance(project).getElementFactory();
							final PsiModifierList psiModifierList = psiMethod.getModifierList();
							final PsiAnnotation psiAnnotation = factory.createAnnotationFromText("@" + Constants.API_ANNOTATION_ARGUMENT, psiMethod);
							final PsiElement inserted = psiModifierList.addBefore(psiAnnotation, psiModifierList.getFirstChild());
							JavaCodeStyleManager.getInstance(project).shortenClassReferences(inserted);
						}
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
