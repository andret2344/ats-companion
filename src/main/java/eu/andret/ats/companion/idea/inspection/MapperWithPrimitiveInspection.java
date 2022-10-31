/*
 * Copyright Andret Tools System (c) 2018-2022. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.ats.companion.idea.inspection;

import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaElementVisitor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiPrimitiveType;
import eu.andret.ats.companion.idea.utilities.Constants;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class MapperWithPrimitiveInspection extends AbstractBaseJavaLocalInspectionTool {
	@NonNls
	public static final String DESCRIPTION = "Cannot annotate a primitive parameter";

	@NotNull
	@Override
	public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, final boolean isOnTheFly) {
		return new JavaElementVisitor() {
			@Override
			public void visitParameter(final PsiParameter parameter) {
				Optional.of(parameter)
						.filter(psiParameter -> psiParameter.getType() instanceof PsiPrimitiveType)
						.map(psiParameter -> psiParameter.getAnnotation(Constants.ANNOTATION_MAPPER))
						.ifPresent(psiAnnotation -> holder.registerProblem(psiAnnotation, DESCRIPTION,
								ProblemHighlightType.GENERIC_ERROR, new RemoveAnnotationQuickFix()));
			}
		};
	}

	@Slf4j
	public static class RemoveAnnotationQuickFix implements LocalQuickFix {
		public static final String NAME = "Remove annotation";

		@NotNull
		@Override
		public String getName() {
			return NAME;
		}

		@Override
		public void applyFix(@NotNull final Project project, @NotNull final ProblemDescriptor descriptor) {
			Optional.of(descriptor)
					.map(ProblemDescriptor::getPsiElement)
					.ifPresent(PsiElement::delete);
		}

		@Override
		@NotNull
		public String getFamilyName() {
			return getName();
		}
	}
}
