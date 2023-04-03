/*
 * Copyright Andret Tools System (c) 2018-2022. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.ats.companion.idea.inspection;

import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.JavaElementVisitor;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiAnnotationMemberValue;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiParameter;
import eu.andret.ats.companion.idea.utilities.Constants;
import eu.andret.ats.companion.idea.utilities.Util;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class InvalidArgumentCompleterInspection extends AbstractBaseJavaLocalInspectionTool {
	public static final String DESCRIPTION = "Invalid argument completer!";

	@NotNull
	@Override
	public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, final boolean isOnTheFly) {
		final List<String> strings = Util.getArgumentCompleterValues(holder.getProject());

		return new JavaElementVisitor() {
			@Override
			public void visitParameter(@NotNull final PsiParameter parameter) {
				final PsiAnnotation annotation = parameter.getAnnotation(Constants.ANNOTATION_COMPLETER);
				if (annotation == null) {
					return;
				}
				final PsiAnnotationMemberValue value = annotation.findAttributeValue("value");
				if (value != null && !strings.contains(value.getText())) {
					holder.registerProblem(value, DESCRIPTION, ProblemHighlightType.ERROR);
				}
			}
		};
	}
}
