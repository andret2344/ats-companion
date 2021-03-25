package eu.andret.ats.idea.utilities;

import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiIdentifier;
import com.intellij.psi.PsiMethod;
import eu.andret.arguments.AnnotatedCommandExecutor;
import eu.andret.arguments.api.annotation.Argument;
import eu.andret.arguments.api.annotation.BaseCommand;

import java.util.Optional;

public class Verifier {
	public static boolean verifyClass(final PsiClass psiClass) {
		return Optional.of(psiClass)
				.filter(aClass -> aClass.hasAnnotation(BaseCommand.class.getName()))
				.map(PsiClass::getSuperClass)
				.map(PsiClass::getQualifiedName)
				.filter(x -> x.equals(AnnotatedCommandExecutor.class.getName()))
				.isPresent();
	}

	public static boolean verifyArgumentMethod(final PsiMethod psiMethod) {
		if (!verifyClass(psiMethod.getContainingClass())) {
			return false;
		}
		return psiMethod.hasAnnotation(Argument.class.getName());
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
				.filter(x -> x.equals(Argument.class.getName()))
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
