/*
 * Copyright (c) 2018 Andret Tools System. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.ats.companion.idea.inspection;

import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.JavaElementVisitor;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.util.PsiTreeUtil;
import eu.andret.ats.companion.idea.utilities.Constants;
import eu.andret.ats.companion.idea.utilities.Verifier;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class InvalidMapperAnnotationInspection extends AbstractBaseJavaLocalInspectionTool {
	@NonNls
	public static final String DESCRIPTION = "Method annotated with @Argument cannot be static";

	@NotNull
	@Override
	public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, final boolean isOnTheFly) {
		return new JavaElementVisitor() {
			@Override
			public void visitParameter(@NotNull final PsiParameter parameter) {
				final PsiAnnotation annotation = parameter.getAnnotation(Constants.ANNOTATION_MAPPER);
				if (annotation == null) {
					return;
				}
				final PsiMethod psiMethod = PsiTreeUtil.getParentOfType(parameter, PsiMethod.class);
				if (!Verifier.isArgumentMethod(psiMethod)) {
					holder.registerProblem(annotation, "Nope!", ProblemHighlightType.GENERIC_ERROR);
				}
				super.visitParameter(parameter);
			}
		};
	}
}
