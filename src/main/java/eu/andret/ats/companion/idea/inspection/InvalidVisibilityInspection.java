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
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiKeyword;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiNameIdentifierOwner;
import eu.andret.ats.companion.idea.utilities.Verifier;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class InvalidVisibilityInspection extends AbstractBaseJavaLocalInspectionTool {
	@NonNls
	public static final String DESCRIPTION = "Method annotated with @Argument must be public";

	@NotNull
	@Override
	public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, final boolean isOnTheFly) {
		return new JavaElementVisitor() {
			@Override
			public void visitMethod(@NotNull final PsiMethod method) {
				if (!Verifier.verifyArgumentMethod(method)) {
					return;
				}

				final List<PsiKeyword> keywords = getKeywords(method.getModifierList().getChildren());
				validateWrongVisibilityModifier(keywords);
				validateNoVisibilityModifier(method, keywords);
			}

			private void validateNoVisibilityModifier(@NotNull final PsiMethod method,
													  @NotNull final List<PsiKeyword> keywords) {
				if (keywords.stream().anyMatch(this::isVisibilityModifier)) {
					return;
				}
				Optional.of(method)
						.map(PsiNameIdentifierOwner::getIdentifyingElement)
						.ifPresent(element -> holder.registerProblem(element, DESCRIPTION,
								ProblemHighlightType.GENERIC_ERROR, new AddPublicQuickFix()));
			}

			private void validateWrongVisibilityModifier(@NotNull final List<PsiKeyword> keywords) {
				keywords.stream()
						.filter(kw -> Stream.of(PsiKeyword.PRIVATE, PsiKeyword.PROTECTED).anyMatch(kw::textMatches))
						.findAny()
						.ifPresent(keyword -> holder.registerProblem(keyword, DESCRIPTION,
								ProblemHighlightType.GENERIC_ERROR, new ChangeToPublicQuickFix()));
			}

			private boolean isVisibilityModifier(@NotNull final PsiKeyword keyword) {
				return Stream.of(PsiKeyword.PRIVATE, PsiKeyword.PROTECTED, PsiKeyword.PUBLIC)
						.anyMatch(keyword::textMatches);
			}

			@NotNull
			private List<PsiKeyword> getKeywords(@NotNull final PsiElement[] children) {
				return Arrays.stream(children)
						.filter(PsiKeyword.class::isInstance)
						.map(PsiKeyword.class::cast)
						.toList();
			}
		};
	}

	@Slf4j
	public static class ChangeToPublicQuickFix implements LocalQuickFix {
		public static final String NAME = "Change visibility to public";

		@NotNull
		@Override
		public String getName() {
			return NAME;
		}

		@Override
		public void applyFix(@NotNull final Project project, @NotNull final ProblemDescriptor descriptor) {
			Optional.of(descriptor)
					.map(ProblemDescriptor::getPsiElement)
					.ifPresent(psiElement -> {
						final PsiElementFactory factory = JavaPsiFacade.getInstance(project).getElementFactory();
						psiElement.replace(factory.createKeyword("public"));
					});
		}

		@Override
		@NotNull
		public String getFamilyName() {
			return getName();
		}
	}

	@Slf4j
	public static class AddPublicQuickFix implements LocalQuickFix {
		public static final String NAME = "Add visibility modifier";

		@NotNull
		@Override
		public String getName() {
			return NAME;
		}

		@Override
		public void applyFix(@NotNull final Project project, @NotNull final ProblemDescriptor descriptor) {
			Optional.of(descriptor)
					.map(ProblemDescriptor::getPsiElement)
					.map(PsiElement::getParent)
					.map(PsiMethod.class::cast)
					.ifPresent(psiElement -> {
						final PsiElementFactory factory = JavaPsiFacade.getInstance(project).getElementFactory();
						psiElement.getModifierList().add(factory.createKeyword("public"));
					});
		}

		@Override
		@NotNull
		public String getFamilyName() {
			return getName();
		}
	}
}
