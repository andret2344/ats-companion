package eu.andret.ats.companion.idea.utilities;

import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassObjectAccessExpression;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiExpressionList;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.PsiImportList;
import com.intellij.psi.PsiImportStatement;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiLiteralExpression;
import com.intellij.psi.PsiLiteralValue;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiType;
import com.intellij.psi.PsiTypeElement;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.searches.MethodReferencesSearch;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiTypesUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.processing.Generated;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

public final class Util {
	private static final String VALUE = "value";

	@Generated("private-constructor")
	private Util() {
	}

	@Nullable
	public static <E extends PsiElement> E ancestorOf(@NotNull final PsiElement psiElement,
													  @NotNull final Class<E> target) {
		return ancestorOf(psiElement, target, 1_000_000);
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

	@Nullable
	public static PsiImportStatement createImportStatement(@NotNull final Project project, @NotNull final String statement) {
		final PsiFileFactory fileFactory = PsiFileFactory.getInstance(project);
		final CodeStyleManager codeStyleManager = CodeStyleManager.getInstance(project);
		final PsiJavaFile aFile = (PsiJavaFile) fileFactory.createFileFromText("_Dummy_.java", JavaFileType.INSTANCE, "import " + statement + ";");
		return Optional.of(aFile)
				.map(PsiJavaFile::getImportList)
				.map(PsiImportList::getImportStatements)
				.map(statements -> statements[0])
				.map(codeStyleManager::reformat)
				.map(PsiImportStatement.class::cast)
				.orElse(null);
	}

	@NotNull
	public static List<String> getArgumentMapperValues(final Project project) {
		final PsiClass psiClass = JavaPsiFacade.getInstance(project)
				.findClass(Constants.CLASS_ANNOTATED_COMMAND, GlobalSearchScope.allScope(project));
		if (psiClass == null) {
			return Collections.emptyList();
		}

		final PsiMethod[] methods = psiClass.findMethodsByName(Constants.METHOD_ADD_ARGUMENT_MAPPER, true);
		if (methods.length == 0) {
			return Collections.emptyList();
		}

		return MethodReferencesSearch.search(methods[0])
				.findAll()
				.stream()
				.map(PsiReference::getElement)
				.map(element -> PsiTreeUtil.getParentOfType(element, PsiMethodCallExpression.class))
				.filter(Objects::nonNull)
				.map(PsiMethodCallExpression::getArgumentList)
				.map(PsiExpressionList::getExpressions)
				.map(expressions -> expressions[0])
				.map(argument -> {
					final PsiClass aClass = PsiTypesUtil.getPsiClass(argument.getType());
					if (aClass != null && Objects.equals(aClass.getQualifiedName(), "java.lang.String")) {
						return argument.getText();
					}
					return null;
				})
				.filter(Objects::nonNull)
				.toList();
	}
}
