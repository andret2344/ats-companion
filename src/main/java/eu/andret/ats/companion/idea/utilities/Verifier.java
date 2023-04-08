/*
 * Copyright (c) 2018 Andret Tools System. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.ats.companion.idea.utilities;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import org.jetbrains.annotations.Nullable;

import javax.annotation.processing.Generated;
import java.util.Optional;

public final class Verifier {
	@Generated("private-constructor")
	private Verifier() {
	}

	public static boolean verifyClass(@Nullable final PsiClass psiClass) {
		return psiClass != null && psiClass.hasAnnotation(Constants.ANNOTATION_BASE_COMMAND);
	}

	public static boolean verifyArgumentMethod(@Nullable final PsiMethod psiMethod) {
		return psiMethod != null
				&& verifyClass(psiMethod.getContainingClass())
				&& psiMethod.hasAnnotation(Constants.ANNOTATION_ARGUMENT);
	}

	public static boolean verifyFallbackMethod(@Nullable final PsiMethod psiMethod) {
		return psiMethod != null
				&& verifyClass(psiMethod.getContainingClass())
				&& (psiMethod.hasAnnotation(Constants.ANNOTATION_TYPE_FALLBACK)
				|| psiMethod.hasAnnotation(Constants.ANNOTATION_ARGUMENT_FALLBACK));
	}

	public static boolean verifyParameter(@Nullable final PsiParameter psiParameter) {
		return Optional.ofNullable(psiParameter)
				.filter(annotation -> annotation.hasAnnotation(Constants.ANNOTATION_MAPPER))
				.map(PsiElement::getParent)
				.map(PsiElement::getParent)
				.filter(PsiMethod.class::isInstance)
				.map(PsiMethod.class::cast)
				.map(Verifier::verifyArgumentMethod)
				.orElse(false);
	}
}
