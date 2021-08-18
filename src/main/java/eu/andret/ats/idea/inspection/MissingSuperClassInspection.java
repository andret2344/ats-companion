package eu.andret.ats.idea.inspection;

import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.JavaElementVisitor;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElementVisitor;
import eu.andret.arguments.AnnotatedCommandExecutor;
import eu.andret.ats.idea.utilities.Verifier;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Optional;

public class MissingSuperClassInspection extends AbstractBaseJavaLocalInspectionTool {
	@NotNull
	@Override
	public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, final boolean isOnTheFly) {
		return new JavaElementVisitor() {
			@NonNls
			private static final String DESCRIPTION_TEMPLATE = "@BaseCommand class not extends AnnotatedCommandExecutor";

			@Override
			public void visitClass(final PsiClass aClass) {
				if (!Verifier.verifyClass(aClass)) {
					return;
				}
				if (aClass.getNameIdentifier() == null) {
					return;
				}
				final Optional<String> superClass = Arrays.stream(aClass.getSupers())
						.map(PsiClass::getQualifiedName)
						.filter(AnnotatedCommandExecutor.class.getName()::equals)
						.findAny();
				if (superClass.isEmpty()) {
					holder.registerProblem(aClass.getNameIdentifier(), DESCRIPTION_TEMPLATE, ProblemHighlightType.GENERIC_ERROR);
				}
			}
		};
	}
}
