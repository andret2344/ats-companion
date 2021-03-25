package eu.andret.ats.idea.argument;

import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.navigation.NavigationItem;
import com.intellij.psi.JavaElementVisitor;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiJvmMember;
import com.intellij.psi.PsiKeyword;
import com.intellij.psi.PsiMethod;
import eu.andret.arguments.api.annotation.Argument;
import eu.andret.ats.idea.argument.fix.RemoveKeywordQuickFix;
import eu.andret.ats.idea.utilities.Verifier;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Optional;

public class ArgumentMethodInspection extends AbstractBaseJavaLocalInspectionTool {
	@NotNull
	@Override
	public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, final boolean isOnTheFly) {
		return new JavaElementVisitor() {

			@NonNls
			private static final String DESCRIPTION_TEMPLATE = "Static method cannot be annotated with @Argument annotation";

			@Override
			public void visitMethod(@NotNull final PsiMethod method) {
				// FIXME temporary restriction
				if (Optional.of(method)
						.map(PsiJvmMember::getContainingClass)
						.map(NavigationItem::getName)
						.filter(x -> x.equals("TestCommand"))
						.isEmpty()) {
					return;
				}
				if (!Verifier.verifyClass(method.getContainingClass())) {
					return;
				}
				if (!method.hasAnnotation(Argument.class.getName())) {
					return;
				}
				Arrays.stream(method.getModifierList().getChildren())
						.filter(x -> x instanceof PsiKeyword)
						.map(PsiKeyword.class::cast)
						.filter(x -> x.textMatches(PsiKeyword.STATIC))
						.findAny()
						.ifPresent(keyword -> holder.registerProblem(keyword, DESCRIPTION_TEMPLATE, ProblemHighlightType.GENERIC_ERROR, new RemoveKeywordQuickFix()));
			}
		};
	}
}
