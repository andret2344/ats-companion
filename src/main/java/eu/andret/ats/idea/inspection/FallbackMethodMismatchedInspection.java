package eu.andret.ats.idea.inspection;

import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.ide.DataManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaElementVisitor;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifierList;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import com.intellij.refactoring.RefactoringActionHandler;
import com.intellij.refactoring.RefactoringActionHandlerFactory;
import com.intellij.util.IncorrectOperationException;
import eu.andret.arguments.api.annotation.Argument;
import eu.andret.arguments.api.annotation.Fallback;
import eu.andret.ats.idea.utilities.Verifier;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class FallbackMethodMismatchedInspection extends AbstractBaseJavaLocalInspectionTool {
	@NotNull
	@Override
	public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, final boolean isOnTheFly) {
		return new JavaElementVisitor() {
			@NonNls
			private static final String DESCRIPTION_TEMPLATE = "Not found matching @Argument method";

			@Override
			public void visitMethod(@NotNull final PsiMethod method) {
				if (!Verifier.verifyArgumentMethod(method)) {
					return;
				}
				if (method.getContainingClass() == null) {
					return;
				}
				final List<PsiMethod> allClassMethods = getAllMethods(method.getContainingClass());
				final Optional<PsiMethod> argumentMethod = allClassMethods.stream()
						.filter(psiMethod -> psiMethod.hasAnnotation(Argument.class.getName()))
						.filter(psiMethod -> psiMethod.getName().equals(method.getName()))
						.findAny();
				if (argumentMethod.isEmpty() && method.getNameIdentifier() != null) {
					holder.registerProblem(method.getNameIdentifier(), DESCRIPTION_TEMPLATE,
							ProblemHighlightType.GENERIC_ERROR, getFixes());
				}
			}

			@NotNull
			private List<PsiMethod> getAllMethods(@NotNull final PsiClass psiClass) {
				return Optional.of(psiClass)
						.map(PsiClass::getAllMethods)
						.stream()
						.flatMap(Arrays::stream)
						.collect(Collectors.toList());
			}

			@NotNull
			private LocalQuickFix[] getFixes() {
				return new LocalQuickFix[]{
						new RemoveMethodQuickFix(),
						new RefactorMethodQuickFix(),
						new RemoveAnnotationQuickFix(),
						new ChangeAnnotationQuickFix()
				};
			}
		};
	}

	@Slf4j
	public static class ChangeAnnotationQuickFix implements LocalQuickFix {
		@NotNull
		@Override
		public String getName() {
			return "Change to @Argument";
		}

		@Override
		public void applyFix(@NotNull final Project project, @NotNull final ProblemDescriptor descriptor) {
			try {
				Optional.of(descriptor)
						.map(ProblemDescriptor::getPsiElement)
						.map(PsiElement::getParent)
						.map(PsiMethod.class::cast)
						.ifPresent(method -> {
							final PsiAnnotation annotation = method.getAnnotation(Fallback.class.getName());
							if (annotation != null) {
								annotation.delete();
								final PsiElementFactory factory = JavaPsiFacade.getInstance(project)
										.getElementFactory();
								final PsiModifierList psiModifierList = method.getModifierList();
								final PsiAnnotation psiAnnotation = factory.createAnnotationFromText(
										"@" + Argument.class.getName(), method);
								final PsiElement firstChild = psiModifierList.getFirstChild();
								final PsiElement inserted = psiModifierList.addBefore(psiAnnotation, firstChild);
								JavaCodeStyleManager.getInstance(project).shortenClassReferences(inserted);
							}
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

	@Slf4j
	public static class RefactorMethodQuickFix implements LocalQuickFix {
		@NotNull
		@Override
		public String getName() {
			return "Rename reference";
		}

		@Override
		public void applyFix(@NotNull final Project project, @NotNull final ProblemDescriptor descriptor) {
			try {
				Optional.of(descriptor)
						.map(ProblemDescriptor::getPsiElement)
						.map(PsiElement::getParent)
						.map(PsiMethod.class::cast)
						.ifPresent(method -> {
							final Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
							if (editor == null) {
								return;
							}
							final RefactoringActionHandler handler = RefactoringActionHandlerFactory.getInstance()
									.createRenameHandler();
							handler.invoke(project, editor, method.getContainingFile(),
									DataManager.getInstance().getDataContext(editor.getComponent()));
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

	@Slf4j
	public static class RemoveAnnotationQuickFix implements LocalQuickFix {
		@NotNull
		@Override
		public String getName() {
			return "Remove @Fallback annotation";
		}

		@Override
		public void applyFix(@NotNull final Project project, @NotNull final ProblemDescriptor descriptor) {
			try {
				Optional.of(descriptor)
						.map(ProblemDescriptor::getPsiElement)
						.map(PsiElement::getParent)
						.map(PsiMethod.class::cast)
						.map(method -> method.getAnnotation(Fallback.class.getName()))
						.ifPresent(PsiElement::delete);
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

	@Slf4j
	public static class RemoveMethodQuickFix implements LocalQuickFix {
		@NotNull
		@Override
		public String getName() {
			return "Remove method";
		}

		@Override
		public void applyFix(@NotNull final Project project, @NotNull final ProblemDescriptor descriptor) {
			try {
				Optional.of(descriptor)
						.map(ProblemDescriptor::getPsiElement)
						.map(PsiElement::getParent)
						.ifPresent(PsiElement::delete);
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
