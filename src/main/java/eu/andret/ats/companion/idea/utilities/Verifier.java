/*
 * Copyright (c) 2018 Andret Tools System. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.ats.companion.idea.utilities;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import org.jetbrains.annotations.Nullable;

import javax.annotation.processing.Generated;

public final class Verifier {
	@Generated("private-constructor")
	private Verifier() {
	}

	public static boolean isBaseCommandClass(@Nullable final PsiClass psiClass) {
		return psiClass != null && psiClass.hasAnnotation(Constants.ANNOTATION_BASE_COMMAND);
	}

	public static boolean isArgumentMethod(@Nullable final PsiMethod psiMethod) {
		return psiMethod != null && psiMethod.hasAnnotation(Constants.ANNOTATION_ARGUMENT);
	}

	public static boolean isFallbackMethod(@Nullable final PsiMethod psiMethod) {
		if (psiMethod == null) {
			return false;
		}
		return psiMethod.hasAnnotation(Constants.ANNOTATION_TYPE_FALLBACK)
				|| psiMethod.hasAnnotation(Constants.ANNOTATION_ARGUMENT_FALLBACK);
	}

	public static boolean isMapperParameter(@Nullable final PsiParameter psiParameter) {
		return psiParameter != null && psiParameter.hasAnnotation(Constants.ANNOTATION_MAPPER);
	}
}
