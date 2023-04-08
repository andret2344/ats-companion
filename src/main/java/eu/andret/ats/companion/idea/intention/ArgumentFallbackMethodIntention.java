/*
 * Copyright (c) 2018 Andret Tools System. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.ats.companion.idea.intention;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiAnnotationMemberValue;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiImportList;
import com.intellij.psi.PsiImportStatement;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import eu.andret.ats.companion.idea.utilities.Constants;
import eu.andret.ats.companion.idea.utilities.Util;
import eu.andret.ats.companion.idea.utilities.Verifier;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@NonNls
public class ArgumentFallbackMethodIntention extends PsiElementBaseIntentionAction implements IntentionAction {
	public static final String TEXT = "ATS: Generate argument fallback method";
	public static final String FAMILY_NAME = "Generate @ArgumentFallback method";

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
		if (element == null) {
			return false;
		}
		final PsiParameter psiParameter = Util.ancestorOf(element, PsiParameter.class, 4);
		return Verifier.verifyParameter(psiParameter);
	}

	@Override
	public void invoke(@NotNull final Project project, final Editor editor, @NotNull final PsiElement element) {
		final PsiElementFactory factory = JavaPsiFacade.getInstance(project).getElementFactory();
		final PsiParameter psiParameter = Util.ancestorOf(element, PsiParameter.class, 4);
		if (psiParameter == null) {
			return;
		}
		final PsiAnnotation annotation = psiParameter.getAnnotation(Constants.ANNOTATION_MAPPER);
		if (annotation == null) {
			return;
		}
		final PsiAnnotationMemberValue attributeValue = annotation.findAttributeValue("value");
		if (attributeValue == null) {
			return;
		}
		final String value = attributeValue.getText().substring(1, attributeValue.getText().length() - 1);
		final PsiMethod method = Util.ancestorOf(psiParameter, PsiMethod.class);
		if (method == null) {
			return;
		}

		final PsiMethod psiMethod = factory.createMethodFromText(
				String.format(
						"@ArgumentFallback(\"%s\") public String %sFallback(String %s) {return null;}",
						value, value, value),
				method.getContext());
		final PsiClass containingClass = method.getContainingClass();
		if (containingClass == null) {
			return;
		}
		containingClass.addAfter(psiMethod, method);
		final PsiImportStatement importStatement = Util.createImportStatement(project, Constants.ANNOTATION_ARGUMENT_FALLBACK);
		final PsiImportList importList = ((PsiJavaFile) containingClass.getParent()).getImportList();
		if (importStatement == null || importList == null) {
			return;
		}
		importList.add(importStatement);
	}
}
