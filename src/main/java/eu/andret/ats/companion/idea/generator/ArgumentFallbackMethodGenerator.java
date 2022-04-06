package eu.andret.ats.companion.idea.generator;

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
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import com.intellij.psi.search.GlobalSearchScope;
import eu.andret.ats.companion.idea.utilities.Constants;
import eu.andret.ats.companion.idea.utilities.Util;
import eu.andret.ats.companion.idea.utilities.Verifier;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@NonNls
public class ArgumentFallbackMethodGenerator extends PsiElementBaseIntentionAction implements IntentionAction {
	@Override
	@NotNull
	public String getText() {
		return "ATS: Generate argument fallback method";
	}

	@Override
	@NotNull
	public String getFamilyName() {
		return "Generate @ArgumentFallback method";
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
		final PsiAnnotation annotation = psiParameter.getAnnotation(Constants.MAPPER_ARGUMENT);
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

		final PsiMethod factoryMethod = factory.createMethodFromText(
				String.format(
						"@ArgumentFallback(\"%s\")\n\tpublic String %sFallback(String %s) {\n\t\treturn null;\n\t}",
						value, value, value),
				method.getContext());
		final PsiClass psiClass = JavaPsiFacade.getInstance(project).findClass(Constants.ANNOTATION_ARGUMENT_FALLBACK,
				GlobalSearchScope.allScope(project));
		if (psiClass == null) {
			return;
		}
		final PsiClass containingClass = method.getContainingClass();
		if (containingClass == null) {
			return;
		}
		containingClass.addAfter(factoryMethod, method);
		JavaCodeStyleManager.getInstance(project).addImport((PsiJavaFile) method.getContainingFile(), psiClass);
	}

	@Override
	public boolean startInWriteAction() {
		return true;
	}
}
