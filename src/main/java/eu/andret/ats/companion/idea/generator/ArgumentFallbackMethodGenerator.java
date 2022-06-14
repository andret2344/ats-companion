package eu.andret.ats.companion.idea.generator;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction;
import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiAnnotationMemberValue;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.PsiImportList;
import com.intellij.psi.PsiImportStatement;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.codeStyle.CodeStyleManager;
import eu.andret.ats.companion.idea.utilities.Constants;
import eu.andret.ats.companion.idea.utilities.Util;
import eu.andret.ats.companion.idea.utilities.Verifier;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

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
		final PsiImportStatement importStatement = createImportStatement(project);
		final PsiImportList importList = ((PsiJavaFile) containingClass.getParent()).getImportList();
		if (importStatement == null || importList == null) {
			return;
		}
		importList.add(importStatement);
	}

	@Nullable
	private PsiImportStatement createImportStatement(@NotNull final Project project) {
		final PsiFileFactory fileFactory = PsiFileFactory.getInstance(project);
		final CodeStyleManager codeStyleManager = CodeStyleManager.getInstance(project);
		final PsiJavaFile aFile = (PsiJavaFile) fileFactory.createFileFromText("_Dummy_.java", JavaFileType.INSTANCE, "import " + Constants.ANNOTATION_ARGUMENT_FALLBACK + ";");
		return Optional.of(aFile)
				.map(PsiJavaFile::getImportList)
				.map(PsiImportList::getImportStatements)
				.map(statements -> statements[0])
				.map(codeStyleManager::reformat)
				.map(PsiImportStatement.class::cast)
				.orElse(null);
	}

	@Override
	public boolean startInWriteAction() {
		return true;
	}
}
