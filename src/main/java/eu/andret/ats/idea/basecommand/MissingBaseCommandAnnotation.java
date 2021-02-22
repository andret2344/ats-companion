package eu.andret.ats.idea.basecommand;

import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.navigation.NavigationItem;
import com.intellij.psi.JavaElementVisitor;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElementVisitor;
import eu.andret.ats.idea.basecommand.fix.AddMissingAnnotationQuickFix;
import eu.andret.ats.idea.utilities.Constants;
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
						.filter(psiClass -> !psiClass.hasAnnotation(Constants.API_ANNOTATION_BASE_COMMAND))
						.map(PsiClass::getSuperClass)
						.map(PsiClass::getQualifiedName)
						.filter(Constants.INNER_ANNOTATED_COMMAND_EXECUTOR::equals)
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
