package eu.andret.ats.companion.idea.intention;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.util.PsiTreeUtil;
import eu.andret.ats.companion.idea.utilities.Constants;
import eu.andret.ats.companion.idea.utilities.Util;
import eu.andret.ats.companion.idea.utilities.Verifier;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

@NonNls
public class ArgumentFallbackMethodIntention extends PsiElementBaseIntentionAction implements IntentionAction {
	public static final String NAME = "Generate @ArgumentFallback method";
	public static final String FAMILY_NAME = "ATS: Generate argument fallback method";
	private static final String METHOD_TEMPLATE = "@ArgumentFallback(\"%s\") public String %sFallback(String %s) {return null;}";

	@Override
	@NotNull
	public String getText() {
		return NAME;
	}

	@Override
	@NotNull
	public String getFamilyName() {
		return FAMILY_NAME;
	}

	@Override
	public boolean isAvailable(@NotNull final Project project, final Editor editor,
							   @Nullable final PsiElement element) {
		return Optional.ofNullable(element)
				.map(psiElement -> PsiTreeUtil.getParentOfType(element, PsiParameter.class))
				.filter(Verifier::isMapperParameter)
				.filter(Predicate.not(this::isFallbackMethodPresent))
				.map(psiElement -> PsiTreeUtil.getParentOfType(element, PsiMethod.class))
				.map(Verifier::isArgumentMethod)
				.orElse(false);
	}

	@Override
	public void invoke(@NotNull final Project project, final Editor editor, @NotNull final PsiElement element) {
		Optional.ofNullable(PsiTreeUtil.getParentOfType(element, PsiParameter.class))
				.ifPresent(psiParameter -> {
					final String value = Optional.of(psiParameter)
							.map(parameter -> parameter.getAnnotation(Constants.ANNOTATION_MAPPER))
							.map(annotation -> annotation.findAttributeValue("value"))
							.map(attributeValue -> attributeValue.getText().substring(1, attributeValue.getText().length() - 1))
							.orElse("");
					Optional.ofNullable(PsiTreeUtil.getParentOfType(psiParameter, PsiMethod.class))
							.ifPresent(method -> {
								final PsiElementFactory factory = JavaPsiFacade.getInstance(project).getElementFactory();
								final PsiMethod newMethod = factory.createMethodFromText(
										String.format(METHOD_TEMPLATE, value, value, value),
										method.getContext());
								injectCode(project, method, newMethod);
							});
				});
	}

	private static void injectCode(@NotNull final Project project, @NotNull final PsiMethod method,
								   @NotNull final PsiMethod newMethod) {
		Optional.ofNullable(method.getContainingClass())
				.ifPresent(containingClass -> {
					containingClass.addAfter(newMethod, method);
					Optional.of(containingClass)
							.map(PsiElement::getParent)
							.map(PsiJavaFile.class::cast)
							.map(PsiJavaFile::getImportList)
							.filter(importList -> importList.findSingleClassImportStatement(Constants.ANNOTATION_ARGUMENT_FALLBACK) == null)
							.ifPresent(importList -> Util.createImportStatement(project, Constants.ANNOTATION_ARGUMENT_FALLBACK)
									.ifPresent(importList::add));

				});
	}

	private boolean isFallbackMethodPresent(@NotNull final PsiParameter psiParameter) {
		return Optional.ofNullable(PsiTreeUtil.getParentOfType(psiParameter, PsiClass.class))
				.map(PsiClass::getMethods)
				.stream()
				.flatMap(Arrays::stream)
				.map(psiMethod -> psiMethod.getAnnotation(Constants.ANNOTATION_ARGUMENT_FALLBACK))
				.map(Util::getArgumentFallbackValue)
				.filter(Objects::nonNull)
				.anyMatch(psiAnnotation -> psiAnnotation.equals(Util.getMapperFallbackValue(psiParameter)));
	}
}
