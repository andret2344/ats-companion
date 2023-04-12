/*
 * Copyright (c) 2018 Andret Tools System. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.ats.companion.idea.inspection;

import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.JavaElementVisitor;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiParameter;
import eu.andret.ats.companion.idea.utilities.Constants;
import eu.andret.ats.companion.idea.utilities.Util;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public class InvalidArgumentCompleterInspection extends AbstractBaseJavaLocalInspectionTool {
	public static final String DESCRIPTION = "Invalid argument completer.";

	@NotNull
	@Override
	public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, final boolean isOnTheFly) {
		final List<String> strings = Util.getArgumentCompleterValues(holder.getProject());

		return new JavaElementVisitor() {
			@Override
			public void visitParameter(@NotNull final PsiParameter parameter) {
				Optional.of(parameter)
						.map(psiparameter -> psiparameter.getAnnotation(Constants.ANNOTATION_COMPLETER))
						.map(annotation -> annotation.findAttributeValue("value"))
						.filter(value -> !strings.contains(value.getText()))
						.ifPresent(value -> holder.registerProblem(value, DESCRIPTION, ProblemHighlightType.ERROR));
			}
		};
	}
}
