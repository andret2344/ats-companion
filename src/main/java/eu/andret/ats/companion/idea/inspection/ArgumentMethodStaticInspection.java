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
import com.intellij.psi.PsiKeyword;
import com.intellij.psi.PsiMethod;
import com.intellij.util.IncorrectOperationException;
import eu.andret.ats.companion.idea.utilities.Verifier;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Optional;

public class ArgumentMethodStaticInspection extends AbstractBaseJavaLocalInspectionTool {
	@NonNls
	public static final String DESCRIPTION = "Method annotated with @Argument annotation cannot use static qualifier";

	@NotNull
	@Override
	public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, final boolean isOnTheFly) {
		return new JavaElementVisitor() {
			@Override
			public void visitMethod(@NotNull final PsiMethod method) {
				if (!Verifier.verifyArgumentMethod(method)) {
					return;
				}
				Arrays.stream(method.getModifierList().getChildren())
						.filter(PsiKeyword.class::isInstance)
						.map(PsiKeyword.class::cast)
						.filter(keyword -> keyword.textMatches(PsiKeyword.STATIC))
						.findAny()
						.ifPresent(keyword -> holder.registerProblem(keyword, DESCRIPTION,
								ProblemHighlightType.GENERIC_ERROR, new RemoveQualifierQuickFix()));
			}
		};
	}

	@Slf4j
	public static class RemoveQualifierQuickFix implements LocalQuickFix {
		public static final String NAME = "Remove qualifier";

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
