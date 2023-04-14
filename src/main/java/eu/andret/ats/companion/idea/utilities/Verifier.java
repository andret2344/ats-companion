/*
 * Copyright (c) 2018 Andret Tools System. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.ats.companion.idea.utilities;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.Nullable;

import javax.annotation.processing.Generated;

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
}
