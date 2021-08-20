package eu.andret.ats.idea.utilities;

import com.intellij.psi.PsiElement;

import java.util.function.UnaryOperator;

public class Util {
	public static <E extends PsiElement> E parentOf(final PsiElement psiElement, final Class<E> target) {
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

	public static <E extends PsiElement> E repeat(final PsiElement psiElement, final int count, final UnaryOperator<PsiElement> fn, final Class<E> clazz) {
		PsiElement copy = psiElement;
		for (int i = 0; i < count; i++) {
			copy = fn.apply(copy);
		}
		return clazz.cast(copy);
	}
	
	private Util() {
	}
}
