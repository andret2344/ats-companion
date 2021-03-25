package eu.andret.ats.idea.basecommand;

import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.navigation.NavigationItem;
import com.intellij.psi.JavaElementVisitor;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElementVisitor;
import eu.andret.arguments.AnnotatedCommandExecutor;
import eu.andret.arguments.api.annotation.BaseCommand;
import eu.andret.ats.idea.basecommand.fix.AddMissingAnnotationQuickFix;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class MissingBaseCommandAnnotation extends AbstractBaseJavaLocalInspectionTool {
	@NotNull
	@Override
	public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, final boolean isOnTheFly) {
		return new JavaElementVisitor() {

			@NonNls
			private static final String DESCRIPTION_TEMPLATE = "Missing @BaseCommand annotation";

			@Override
			public void visitClass(final PsiClass aClass) {
				// FIXME temporary restriction
				if (Optional.of(aClass)
						.map(NavigationItem::getName)
						.filter(x -> x.equals("TestCommand"))
						.isEmpty()) {
					return;
				}
				Optional.of(aClass)
						.filter(psiClass -> !psiClass.hasAnnotation(BaseCommand.class.getName()))
						.map(PsiClass::getSuperClass)
						.map(PsiClass::getQualifiedName)
						.filter(AnnotatedCommandExecutor.class.getName()::equals)
						.map(x -> aClass.getNameIdentifier())
						.ifPresent(psiIdentifier -> holder.registerProblem(psiIdentifier, DESCRIPTION_TEMPLATE, getFixes()));
			}

			private LocalQuickFix[] getFixes() {
				return new LocalQuickFix[]{
						new AddMissingAnnotationQuickFix(),
				};
			}
		};
	}
}
