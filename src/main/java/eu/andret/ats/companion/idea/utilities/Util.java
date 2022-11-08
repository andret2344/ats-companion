package eu.andret.ats.companion.idea.utilities;

import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClassObjectAccessExpression;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiLiteralExpression;
import com.intellij.psi.PsiLiteralValue;
import com.intellij.psi.PsiType;
import com.intellij.psi.PsiTypeElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.Optional;
import java.util.function.UnaryOperator;

public final class Util {
	private static final String VALUE = "value";

	private Util() {
	}

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

	@NotNull
	public static String toCamelCase(@NotNull final String input) {
		return String.format("%s%s", input.substring(0, 1).toLowerCase(Locale.ROOT), input.substring(1));
	}

	@NotNull
	public static Optional<String> getArgumentFallbackValue(@Nullable final PsiAnnotation argumentFallbackAnnotation) {
		return Optional.ofNullable(argumentFallbackAnnotation)
				.map(psiAnnotation -> psiAnnotation.findAttributeValue(VALUE))
				.filter(PsiLiteralExpression.class::isInstance)
				.map(PsiLiteralExpression.class::cast)
				.map(PsiLiteralValue::getValue)
				.filter(String.class::isInstance)
				.map(String.class::cast);
	}

	@NotNull
	public static Optional<PsiType> getTypeFallbackValue(@Nullable final PsiAnnotation argumentFallbackAnnotation) {
		return Optional.ofNullable(argumentFallbackAnnotation)
				.map(psiAnnotation -> psiAnnotation.findAttributeValue(VALUE))
				.filter(PsiClassObjectAccessExpression.class::isInstance)
				.map(PsiClassObjectAccessExpression.class::cast)
				.map(PsiClassObjectAccessExpression::getOperand)
				.map(PsiTypeElement::getType);
	}

	@NotNull
	public static PsiType createStringType(@NotNull final Project project) {
		final PsiElementFactory factory = JavaPsiFacade.getInstance(project).getElementFactory();
		return factory.createTypeByFQClassName("java.lang.String");
	}
}
