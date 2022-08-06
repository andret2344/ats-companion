package eu.andret.ats.companion.idea.utilities;

import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiIdentifier;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class Verifier {
	public static boolean verifyClass(final PsiClass psiClass) {
		return Optional.of(psiClass)
				.filter(aClass -> aClass.hasAnnotation(Constants.ANNOTATION_BASE_COMMAND))
				.map(PsiClass::getSuperClass)
				.map(PsiClass::getQualifiedName)
				.filter(name -> name.equals(Constants.CLASS_ANNOTATED_COMMAND_EXECUTOR))
				.isPresent();
	}

	public static boolean verifyArgumentMethod(@Nullable final PsiMethod psiMethod) {
		if (psiMethod == null) {
			return false;
		}
		if (!verifyClass(psiMethod.getContainingClass())) {
			return false;
		}
		return psiMethod.hasAnnotation(Constants.ANNOTATION_ARGUMENT);
	}

	public static boolean verifyFallbackMethod(@Nullable final PsiMethod psiMethod) {
		if (psiMethod == null || !verifyClass(psiMethod.getContainingClass())) {
			return false;
		}
		return psiMethod.hasAnnotation(Constants.ANNOTATION_TYPE_FALLBACK)
				|| psiMethod.hasAnnotation(Constants.ANNOTATION_ARGUMENT_FALLBACK);
	}

	public static boolean verifyArgumentMethodIdentifier(final PsiIdentifier psiIdentifier) {
		return Optional.of(psiIdentifier)
				.map(PsiElement::getParent)
				.filter(PsiMethod.class::isInstance)
				.map(PsiMethod.class::cast)
				.map(Verifier::verifyArgumentMethod)
				.orElse(false);
	}

	public static boolean verifyArgumentAnnotation(final PsiAnnotation psiAnnotation) {
		return Optional.of(psiAnnotation)
				.map(PsiAnnotation::getQualifiedName)
				.filter(name -> name.equals(Constants.ANNOTATION_ARGUMENT))
				.isPresent();
	}

	public static boolean verifyParameter(@Nullable final PsiParameter psiParameter) {
		if (psiParameter == null) {
			return false;
		}
		final PsiAnnotation annotation = psiParameter.getAnnotation(Constants.ANNOTATION_MAPPER);
		if (annotation == null) {
			return false;
		}
		return Optional.of(psiParameter)
				.map(PsiElement::getParent)
				.map(PsiElement::getParent)
				.filter(PsiMethod.class::isInstance)
				.map(PsiMethod.class::cast)
				.map(Verifier::verifyArgumentMethod)
				.orElse(false);
	}

	private Verifier() {
	}
}
