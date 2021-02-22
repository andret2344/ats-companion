package eu.andret.ats.idea.fallback;

import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.navigation.NavigationItem;
import com.intellij.psi.JavaElementVisitor;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiJvmMember;
import com.intellij.psi.PsiMethod;
import eu.andret.ats.idea.fallback.fix.ChangeAnnotationQuickFix;
import eu.andret.ats.idea.fallback.fix.RefactorMethodQuickFix;
import eu.andret.ats.idea.fallback.fix.RemoveAnnotationQuickFix;
import eu.andret.ats.idea.fallback.fix.RemoveMethodQuickFix;
import eu.andret.ats.idea.utilities.Constants;
import eu.andret.ats.idea.utilities.Verifier;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class FallbackMethodInspection extends AbstractBaseJavaLocalInspectionTool {
	@NotNull
	@Override
	public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, final boolean isOnTheFly) {
		return new JavaElementVisitor() {

			@NonNls
			private static final String DESCRIPTION_TEMPLATE = "Not found matching @Argument method";

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
				if (!method.hasAnnotation(Constants.API_ANNOTATION_FALLBACK)) {
					return;
				}
				final List<PsiMethod> allClassMethods = getAllMethods(method.getContainingClass());
				final Optional<PsiMethod> argumentMethod = allClassMethods.stream()
						.filter(psiMethod -> psiMethod.hasAnnotation(Constants.API_ANNOTATION_ARGUMENT))
						.filter(psiMethod -> psiMethod.getName().equals(method.getName()))
						.findAny();
				if (argumentMethod.isEmpty() && method.getNameIdentifier() != null) {
					holder.registerProblem(method.getNameIdentifier(), DESCRIPTION_TEMPLATE, getFixes());
				}
			}

			private List<PsiMethod> getAllMethods(final PsiClass psiClass) {
				return Optional.ofNullable(psiClass)
						.map(PsiClass::getAllMethods)
						.stream()
						.flatMap(Arrays::stream)
						.collect(Collectors.toList());
			}

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
}
