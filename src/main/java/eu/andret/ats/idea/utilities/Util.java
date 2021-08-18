package eu.andret.ats.idea.utilities;

import com.intellij.psi.PsiElement;

public class Util {
	public static <E extends PsiElement> E parentOf(final PsiElement psiElement, final Class<E> target) {
		PsiElement copy = psiElement;
		do {
			copy = copy.getParent();
		} while (!target.isInstance(copy));
		return target.cast(copy);
	}
}
