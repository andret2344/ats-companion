package eu.andret.ats.idea.inspection;

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
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import com.intellij.util.IncorrectOperationException;
import eu.andret.arguments.AnnotatedCommandExecutor;
import eu.andret.arguments.api.annotation.BaseCommand;
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
						.filter(psiClass -> !psiClass.hasAnnotation(BaseCommand.class.getName()))
						.map(PsiClass::getSuperClass)
						.map(PsiClass::getQualifiedName)
						.filter(AnnotatedCommandExecutor.class.getName()::equals)
						.map(x -> aClass.getNameIdentifier())
						.ifPresent(psiIdentifier -> holder.registerProblem(psiIdentifier, DESCRIPTION_TEMPLATE,
								ProblemHighlightType.GENERIC_ERROR, getFixes()));
			}

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
			try {
				Optional.of(descriptor)
						.map(ProblemDescriptor::getPsiElement)
						.map(PsiElement::getParent)
						.map(PsiClass.class::cast)
						.map(PsiModifierListOwner::getModifierList)
						.ifPresent(modifierList -> {
							final PsiElementFactory factory = JavaPsiFacade.getInstance(project).getElementFactory();
							final PsiAnnotation psiAnnotation = factory.createAnnotationFromText("@" + BaseCommand.class.getName() + "(\"\")", modifierList.getParent());
							final PsiElement inserted = modifierList.addBefore(psiAnnotation, modifierList.getFirstChild());
							JavaCodeStyleManager.getInstance(project).shortenClassReferences(inserted);
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
