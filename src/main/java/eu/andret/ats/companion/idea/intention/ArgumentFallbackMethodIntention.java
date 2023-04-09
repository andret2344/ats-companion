/*
 * Copyright (c) 2018 Andret Tools System. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.ats.companion.idea.intention;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import eu.andret.ats.companion.idea.utilities.Constants;
import eu.andret.ats.companion.idea.utilities.Util;
import eu.andret.ats.companion.idea.utilities.Verifier;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

@NonNls
public class ArgumentFallbackMethodIntention extends PsiElementBaseIntentionAction implements IntentionAction {
	public static final String TEXT = "ATS: Generate argument fallback method";
	public static final String FAMILY_NAME = "Generate @ArgumentFallback method";
	private static final String METHOD_TEMPLATE = "@ArgumentFallback(\"%s\") public String %sFallback(String %s) {return null;}";

	@Override
	@NotNull
	public String getText() {
		return TEXT;
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
				.map(el -> Util.ancestorOf(el, PsiParameter.class, 4))
				.map(Verifier::verifyParameter)
				.orElse(false);
	}

	@Override
	public void invoke(@NotNull final Project project, final Editor editor, @NotNull final PsiElement element) {
		Optional.ofNullable(Util.ancestorOf(element, PsiParameter.class, 4))
				.ifPresent(psiParameter -> {
					final String value = Optional.of(psiParameter)
							.map(parameter -> parameter.getAnnotation(Constants.ANNOTATION_MAPPER))
							.map(annotation -> annotation.findAttributeValue("value"))
							.map(attributeValue -> attributeValue.getText().substring(1, attributeValue.getText().length() - 1))
							.orElse("");
					Optional.ofNullable(Util.ancestorOf(psiParameter, PsiMethod.class))
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
							.ifPresent(importList -> Util.createImportStatement(project, Constants.ANNOTATION_ARGUMENT_FALLBACK)
									.ifPresent(importList::add));

				});
	}
}
