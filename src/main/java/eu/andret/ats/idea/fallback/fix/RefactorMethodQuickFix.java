package eu.andret.ats.idea.fallback.fix;

import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.ide.DataManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.refactoring.RefactoringActionHandler;
import com.intellij.refactoring.RefactoringActionHandlerFactory;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class RefactorMethodQuickFix implements LocalQuickFix {
	private static final Logger LOG = Logger.getInstance("#eu.andret.ats.idea.fallback.fix.RefactorMethodQuickFix");

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
					.map(psiElement -> (PsiMethod) psiElement)
					.ifPresent(psiMethod -> {
						final Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
						final RefactoringActionHandler handler = RefactoringActionHandlerFactory.getInstance().createRenameHandler();
						handler.invoke(project, editor, psiMethod.getContainingFile(), DataManager.getInstance().getDataContext(editor.getComponent()));
					});
		} catch (final IncorrectOperationException e) {
			LOG.error(e);
		}
	}

	@Override
	@NotNull
	public String getFamilyName() {
		return getName();
	}
}
