package eu.andret.ats.idea.basecommand;

import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.navigation.NavigationItem;
import com.intellij.psi.JavaElementVisitor;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElementVisitor;
import eu.andret.ats.idea.utilities.Constants;
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
				if (aClass.hasAnnotation(Constants.API_ANNOTATION_BASE_COMMAND)) {
					final Optional<String> superClass = Arrays.stream(aClass.getSupers())
							.map(PsiClass::getQualifiedName)
							.filter(Constants.INNER_ANNOTATED_COMMAND_EXECUTOR::equals)
							.findAny();
					if (superClass.isEmpty()) {
						holder.registerProblem(aClass.getNameIdentifier(), DESCRIPTION_TEMPLATE);
					}
				}
			}
		};
	}
}
