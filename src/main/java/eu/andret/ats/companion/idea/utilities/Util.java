package eu.andret.ats.companion.idea.utilities;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiType;
import lombok.experimental.UtilityClass;

import java.util.Arrays;
import java.util.function.UnaryOperator;

@UtilityClass
public class Util {
	public <E extends PsiElement> E ancestorOf(final PsiElement psiElement, final Class<E> target) {
		PsiElement copy = psiElement;
		do {
			copy = copy.getParent();
		} while (!target.isInstance(copy));
		return target.cast(copy);
	}

	public PsiElement repeat(final PsiElement psiElement, final int count, final UnaryOperator<PsiElement> fn) {
		PsiElement copy = psiElement;
		for (int i = 0; i < count; i++) {
			copy = fn.apply(copy);
		}
		return copy;
	}

	public boolean isDescendant(final PsiType type, final String qualifiedName) {
		return Arrays.stream(type.getSuperTypes())
				.map(PsiType::getCanonicalText)
				.anyMatch(qualifiedName::equals);
	}
}
