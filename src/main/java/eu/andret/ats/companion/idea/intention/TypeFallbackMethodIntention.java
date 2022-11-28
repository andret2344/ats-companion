/*
 * Copyright Andret Tools System (c) 2018-2022. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.ats.companion.idea.intention;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
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
public class TypeFallbackMethodIntention extends PsiElementBaseIntentionAction implements IntentionAction {
	public static final String NAME = "ATS: Generate type fallback method";
	public static final String FAMILY_NAME = "Generate @TypeFallback method";

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
		if (element == null) {
			return false;
		}
		final PsiParameter psiParameter = Util.ancestorOf(element, PsiParameter.class, 4);
		if (Verifier.verifyParameter(psiParameter)) {
			return false;
		}
		final PsiMethod psiMethod = Util.ancestorOf(element, PsiMethod.class, 7);
		return Verifier.verifyArgumentMethod(psiMethod);
	}

	@Override
	public void invoke(@NotNull final Project project, final Editor editor, @NotNull final PsiElement element) {
		final PsiParameter psiParameter = Util.ancestorOf(element, PsiParameter.class, 4);
		if (psiParameter == null) {
			return;
		}
		final PsiMethod method = Util.ancestorOf(psiParameter, PsiMethod.class);
		if (method == null) {
			return;
		}

		final String value = psiParameter.getName();
		final String type = psiParameter.getType().getPresentableText();
		final PsiElementFactory factory = JavaPsiFacade.getInstance(project).getElementFactory();
		final PsiMethod psiMethod = factory.createMethodFromText(
				String.format(
						"@TypeFallback(%s.class)\n\tpublic String %sFallback(String %s) {\n\t\treturn null;\n\t}",
						type, value, value),
				method.getContext());
		final PsiClass containingClass = method.getContainingClass();
		if (containingClass == null) {
			return;
		}
		containingClass.addAfter(psiMethod, method);
		final PsiImportStatement importStatement = Util.createImportStatement(project, Constants.ANNOTATION_TYPE_FALLBACK);
		final PsiImportList importList = ((PsiJavaFile) containingClass.getParent()).getImportList();
		if (importStatement == null || importList == null) {
			return;
		}
		importList.add(importStatement);
	}

	@Override
	public boolean startInWriteAction() {
		return true;
	}
}
