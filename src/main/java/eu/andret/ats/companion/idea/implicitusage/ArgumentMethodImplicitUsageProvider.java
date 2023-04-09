/*
 * Copyright (c) 2018 Andret Tools System. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.ats.companion.idea.implicitusage;

import com.intellij.codeInsight.daemon.ImplicitUsageProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import eu.andret.ats.companion.idea.utilities.Constants;
import eu.andret.ats.companion.idea.utilities.Util;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Optional;

public class ArgumentMethodImplicitUsageProvider implements ImplicitUsageProvider {
	@Override
	public boolean isImplicitUsage(@NotNull final PsiElement element) {
		return isImplicitRead(element);
	}

	@Override
	public boolean isImplicitRead(@NotNull final PsiElement element) {
		return isImplicitWrite(element);
	}

	@Override
	public boolean isImplicitWrite(@NotNull final PsiElement element) {
		return verifyElement(element);
	}

	@Nullable
	private PsiMethod getMethod(@NotNull final PsiElement element) {
		if (element instanceof final PsiMethod psiMethod) {
			return psiMethod;
		}
		if (element instanceof final PsiParameter psiParameter) {
			return Util.ancestorOf(psiParameter, PsiMethod.class, 2);
		}
		return null;
	}

	private boolean verifyElement(@NotNull final PsiElement element) {
		return Optional.of(element)
				.map(this::getMethod)
				.map(method -> Arrays.stream(Constants.getMethodAnnotations())
						.anyMatch(method::hasAnnotation))
				.orElse(false);
	}
}
