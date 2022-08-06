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
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiModifierListOwner;
import com.intellij.psi.PsiTypeParameter;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import eu.andret.ats.companion.idea.utilities.Constants;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class MissingBaseCommandAnnotationInspection extends AbstractBaseJavaLocalInspectionTool {
	@NotNull
	@Override
	public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, final boolean isOnTheFly) {
		return new JavaElementVisitor() {
			@NonNls
			private static final String DESCRIPTION_TEMPLATE = "Missing @BaseCommand annotation";

			@Override
			public void visitClass(final PsiClass aClass) {
				Optional.of(aClass)
						.filter(psiClass -> !(psiClass instanceof PsiTypeParameter))
						.filter(psiClass -> !psiClass.hasAnnotation(Constants.ANNOTATION_BASE_COMMAND))
						.map(PsiClass::getSuperClass)
						.map(PsiClass::getQualifiedName)
						.filter(Constants.CLASS_ANNOTATED_COMMAND_EXECUTOR::equals)
						.map(name -> aClass.getNameIdentifier())
						.ifPresent(psiIdentifier -> holder.registerProblem(psiIdentifier, DESCRIPTION_TEMPLATE,
								ProblemHighlightType.GENERIC_ERROR, getFixes()));
			}

			@NotNull
			private LocalQuickFix[] getFixes() {
				return new LocalQuickFix[]{
						new AddMissingAnnotationQuickFix(),
				};
			}
		};
	}

	@Slf4j
	public static class AddMissingAnnotationQuickFix implements LocalQuickFix {
		@NotNull
		@Override
		public String getName() {
			return "Add @BaseCommand annotation";
		}

		@Override
		public void applyFix(@NotNull final Project project, @NotNull final ProblemDescriptor descriptor) {
			Optional.of(descriptor)
					.map(ProblemDescriptor::getPsiElement)
					.map(PsiElement::getParent)
					.map(PsiClass.class::cast)
					.map(PsiModifierListOwner::getModifierList)
					.ifPresent(modifierList -> {
						final PsiElementFactory factory = JavaPsiFacade.getInstance(project).getElementFactory();
						final PsiAnnotation psiAnnotation = factory.createAnnotationFromText(
								"@" + Constants.ANNOTATION_BASE_COMMAND + "(\"\")",
								modifierList.getParent());
						final PsiElement firstChild = modifierList.getFirstChild();
						final PsiElement inserted = modifierList.addBefore(psiAnnotation, firstChild);
						JavaCodeStyleManager.getInstance(project).shortenClassReferences(inserted);
					});
		}

		@Override
		@NotNull
		public String getFamilyName() {
			return getName();
		}
	}
}
