package eu.andret.ats.companion.idea.utilities;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.UnaryOperator;

public class Util {
	@Nullable
	public static <E extends PsiElement> E ancestorOf(@NotNull final PsiElement psiElement,
													  @NotNull final Class<E> target) {
		PsiElement copy = psiElement;
		do {
			if (copy == null) {
				return null;
			}
			copy = copy.getParent();
		} while (!target.isInstance(copy));
		if (!target.isInstance(copy)) {
			return null;
		}
		return target.cast(copy);
	}

	@Nullable
	public static <E extends PsiElement> E ancestorOf(@NotNull final PsiElement psiElement,
													  @NotNull final Class<E> target, final int limit) {
		PsiElement copy = psiElement;
		int x = 0;
		do {
			if (copy == null) {
				return null;
			}
			copy = copy.getParent();
		} while (!target.isInstance(copy) && ++x <= limit);
		if (!target.isInstance(copy)) {
			return null;
		}
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
