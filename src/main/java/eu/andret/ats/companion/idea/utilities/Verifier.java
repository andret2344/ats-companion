package eu.andret.ats.companion.idea.utilities;

import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiIdentifier;
import com.intellij.psi.PsiMethod;

import java.util.Optional;

public class Verifier {
	public static boolean verifyClass(final PsiClass psiClass) {
		return Optional.of(psiClass)
				.filter(aClass -> aClass.hasAnnotation(Constants.ANNOTATION_BASE_COMMAND))
				.map(PsiClass::getSuperClass)
				.map(PsiClass::getQualifiedName)
				.filter(x -> x.equals(Constants.CLASS_ANNOTATED_COMMAND_EXECUTOR))
				.isPresent();
	}

	public static boolean verifyArgumentMethod(final PsiMethod psiMethod) {
		if (!verifyClass(psiMethod.getContainingClass())) {
			return false;
		}
		return psiMethod.hasAnnotation(Constants.ANNOTATION_ARGUMENT);
	}

	public static boolean verifyArgumentMethodIdentifier(final PsiIdentifier psiIdentifier) {
		final PsiElement parent = psiIdentifier.getParent();
		if (!(parent instanceof PsiMethod)) {
			return false;
		}
		return verifyArgumentMethod(((PsiMethod) parent));
	}

	public static boolean verifyArgumentAnnotation(final PsiAnnotation psiAnnotation) {
		return Optional.of(psiAnnotation)
				.map(PsiAnnotation::getQualifiedName)
				.filter(x -> x.equals(Constants.ANNOTATION_ARGUMENT))
				.isPresent();
	}

	public static boolean verifyArgumentElement(final PsiElement psiElement) {
		if (psiElement == null) {
			return false;
		}
		if (psiElement instanceof PsiAnnotation) {
			return Verifier.verifyArgumentAnnotation((PsiAnnotation) psiElement);
		}
		if (psiElement instanceof PsiIdentifier) {
			return Verifier.verifyArgumentMethodIdentifier((PsiIdentifier) psiElement);
		}
		if (psiElement instanceof PsiMethod) {
			return Verifier.verifyArgumentMethod((PsiMethod) psiElement);
		}
		return false;
	}

	private Verifier() {
	}
}
