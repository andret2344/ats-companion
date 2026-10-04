package eu.andret.ats.companion.idea.inspection;

import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.JavaElementVisitor;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import eu.andret.ats.companion.idea.utilities.Constants;
import eu.andret.ats.companion.idea.utilities.Util;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class InvalidArgumentMapperInspection extends AbstractBaseJavaLocalInspectionTool {
	public static final String DESCRIPTION = "Invalid argument mapper.";

	@NotNull
	@Override
	public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, final boolean isOnTheFly) {
		final List<String> strings = Util.getArgumentMapperValues(holder.getProject());

		return new JavaElementVisitor() {
			@Override
			public void visitParameter(@NotNull final PsiParameter parameter) {
				final PsiAnnotation annotation = parameter.getAnnotation(Constants.ANNOTATION_MAPPER);
				analyzeAnnotation(annotation, holder);
			}

			@Override
			public void visitMethod(@NotNull final PsiMethod method) {
				final PsiAnnotation annotation = method.getAnnotation(Constants.ANNOTATION_ARGUMENT_FALLBACK);
				analyzeAnnotation(annotation, holder);
			}

			private void analyzeAnnotation(@Nullable final PsiAnnotation annotation,
										   @NotNull final ProblemsHolder holder) {
				Optional.ofNullable(annotation)
						.map(psiAnnotation -> psiAnnotation.findAttributeValue("value"))
						.filter(value -> !strings.contains(value.getText()))
						.ifPresent(value -> holder.registerProblem(value, DESCRIPTION, ProblemHighlightType.ERROR));
			}
		};
	}
}
