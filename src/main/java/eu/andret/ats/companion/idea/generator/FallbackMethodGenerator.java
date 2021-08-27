package eu.andret.ats.companion.idea.generator;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiIdentifier;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiPrimitiveType;
import com.intellij.psi.PsiType;
import eu.andret.ats.companion.idea.utilities.Verifier;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

@NonNls
public class FallbackMethodGenerator extends PsiElementBaseIntentionAction implements IntentionAction {
	@Override
	@NotNull
	public String getText() {
		return "ATS: Generate fallback method";
	}

	@Override
	@NotNull
	public String getFamilyName() {
		return "Generate @Fallback method";
	}

	@Override
	public boolean isAvailable(@NotNull final Project project, final Editor editor,
							   @Nullable final PsiElement element) {
		return Verifier.verifyArgumentElement(element);
	}

	@Override
	public void invoke(@NotNull final Project project, final Editor editor, @NotNull final PsiElement element) {
		final PsiElementFactory factory = JavaPsiFacade.getInstance(project).getElementFactory();

		final PsiMethod method;
		if (element instanceof PsiMethod) {
			method = (PsiMethod) element;
		} else if (element instanceof PsiAnnotation) {
			method = (PsiMethod) element.getParent();
		} else if (element instanceof PsiIdentifier) {
			method = (PsiMethod) element.getParent();
		} else {
			method = null;
		}

		if (method != null) {
			final String methodArguments = Arrays.stream(method.getParameterList().getParameters()).sequential()
					.map(this::mapParameterToStringRepresentation)
					.collect(Collectors.joining(","));

			final String returnType = Optional.of(method)
					.map(PsiMethod::getReturnType)
					.map(PsiType::getCanonicalText)
					.orElse("void");

			final PsiMethod factoryMethod = factory.createMethodFromText(
					"@Fallback\n\tpublic " + returnType + " " + method.getName() + "(String ignored, "
							+ methodArguments + ") {\n\t\treturn null;\n\t}", null);
			method.addAfter(factoryMethod, method);
		}
	}

	private String mapParameterToStringRepresentation(final PsiParameter psiParameter) {
		final StringBuilder stringBuilder = new StringBuilder();
		if (psiParameter.getType() instanceof PsiPrimitiveType) {
			stringBuilder.append(psiParameter.getType());
		} else {
			stringBuilder.append("String");
		}
		return stringBuilder.append(" ").append(psiParameter.getName()).toString();
	}

	@Override
	public boolean startInWriteAction() {
		return true;
	}
}
