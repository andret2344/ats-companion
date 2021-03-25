package eu.andret.ats.idea.basecommand;

import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.navigation.NavigationItem;
import com.intellij.psi.JavaElementVisitor;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElementVisitor;
import eu.andret.arguments.AnnotatedCommandExecutor;
import eu.andret.arguments.api.annotation.BaseCommand;
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
				// FIXME temporary restriction
				if (Optional.of(aClass)
						.map(NavigationItem::getName)
						.filter(x -> x.equals("TestCommand"))
						.isEmpty()) {
					return;
				}
				if (aClass.hasAnnotation(BaseCommand.class.getName())) {
					final Optional<String> superClass = Arrays.stream(aClass.getSupers())
							.map(PsiClass::getQualifiedName)
							.filter(AnnotatedCommandExecutor.class.getName()::equals)
							.findAny();
					if (superClass.isEmpty()) {
						holder.registerProblem(aClass.getNameIdentifier(), DESCRIPTION_TEMPLATE);
					}
				}
			}
		};
	}
}
