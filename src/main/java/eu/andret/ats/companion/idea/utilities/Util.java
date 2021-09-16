package eu.andret.ats.companion.idea.utilities;

import com.intellij.psi.PsiElement;

import java.util.function.UnaryOperator;

public class Util {
	public static <E extends PsiElement> E ancestorOf(final PsiElement psiElement, final Class<E> target) {
		PsiElement copy = psiElement;
		do {
			copy = copy.getParent();
		} while (!target.isInstance(copy));
		return target.cast(copy);
	}

	public static PsiElement repeat(final PsiElement psiElement, final int count, final UnaryOperator<PsiElement> fn) {
		PsiElement copy = psiElement;
		for (int i = 0; i < count; i++) {
			copy = fn.apply(copy);
		}
		return copy;
	}

	private Util() {
	}
}
