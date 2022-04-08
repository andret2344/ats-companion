package eu.andret.ats.companion.idea.inspection;

import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaElementVisitor;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiParameterList;
import com.intellij.psi.PsiType;
import com.intellij.util.IncorrectOperationException;
import eu.andret.ats.companion.idea.utilities.Constants;
import eu.andret.ats.companion.idea.utilities.Util;
import eu.andret.ats.companion.idea.utilities.Verifier;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class FallbackArgumentsInspection extends AbstractBaseJavaLocalInspectionTool {
	@NotNull
	@Override
	public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, final boolean isOnTheFly) {
		return new JavaElementVisitor() {
			@NonNls
			private static final String DESCRIPTION_TEMPLATE = "Method parameters don't meet requirements.";

			@Override
			public void visitMethod(@NotNull final PsiMethod method) {
				if (!Verifier.verifyFallbackMethod(method)) {
					return;
				}
				final PsiParameter[] parameters = method.getParameterList().getParameters();
				final PsiType stringType = Util.createStringType(method.getProject());
				if (parameters.length == 1 && parameters[0].getType().equals(stringType)) {
					return;
				}
				Optional.of(method)
						.map(psiMethod -> psiMethod.getAnnotation(Constants.ANNOTATION_ARGUMENT_FALLBACK))
						.flatMap(Util::getArgumentFallbackValue)
						.map(Util::toCamelCase)
						.ifPresent(annotationValue -> holder.registerProblem(method.getParameterList(), DESCRIPTION_TEMPLATE,
								ProblemHighlightType.GENERIC_ERROR, new ChangeParametersQuickFix(annotationValue)));
				Optional.of(method)
						.map(psiMethod -> psiMethod.getAnnotation(Constants.ANNOTATION_TYPE_FALLBACK))
						.flatMap(Util::getTypeFallbackValue)
						.map(PsiType::getPresentableText)
						.map(Util::toCamelCase)
						.ifPresent(annotationValue -> holder.registerProblem(method.getParameterList(), DESCRIPTION_TEMPLATE,
								ProblemHighlightType.GENERIC_ERROR, new ChangeParametersQuickFix(annotationValue)));
			}
		};
	}

	@Slf4j
	@AllArgsConstructor
	public static class ChangeParametersQuickFix implements LocalQuickFix {
		@NotNull
		private final String paramName;

		@NotNull
		@Override
		public String getName() {
			return String.format("Change parameter list to (String %s)", paramName);
		}

		@Override
		public void applyFix(@NotNull final Project project, @NotNull final ProblemDescriptor descriptor) {
			try {
				Optional.of(descriptor)
						.map(ProblemDescriptor::getPsiElement)
						.map(PsiParameterList.class::cast)
						.ifPresent(psiParameterList -> {
							final PsiElementFactory factory = JavaPsiFacade.getInstance(project).getElementFactory();
							final PsiType stringType = Util.createStringType(project);
							final PsiParameterList parameterList = factory
									.createParameterList(new String[]{paramName}, new PsiType[]{stringType});
							psiParameterList.replace(parameterList);
						});
			} catch (final IncorrectOperationException e) {
				log.error(getClass().getName(), e);
			}
		}

		@Override
		@NotNull
		public String getFamilyName() {
			return getName();
		}
	}
}
