package eu.andret.ats.idea.fallback;

import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.navigation.NavigationItem;
import com.intellij.psi.JavaElementVisitor;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiJvmMember;
import com.intellij.psi.PsiMethod;
import eu.andret.ats.idea.fallback.fix.ChangeAnnotationQuickFix;
import eu.andret.ats.idea.fallback.fix.RefactorMethodQuickFix;
import eu.andret.ats.idea.fallback.fix.RemoveAnnotationQuickFix;
import eu.andret.ats.idea.fallback.fix.RemoveMethodQuickFix;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class FallbackMethodInspection extends AbstractBaseJavaLocalInspectionTool {
	public static final String API_ANNOTATION_ARGUMENT = "eu.andret.arguments.api.annotation.Argument";
	public static final String API_ANNOTATION_FALLBACK = "eu.andret.arguments.api.annotation.Fallback";

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
				final PsiAnnotation fallback = method.getAnnotation(API_ANNOTATION_FALLBACK);
				if (fallback != null) {
					final List<PsiMethod> psiMethods = Optional.of(method)
							.map(PsiJvmMember::getContainingClass)
							.map(PsiClass::getAllMethods)
							.stream()
							.flatMap(Arrays::stream)
							.collect(Collectors.toList());
					final Optional<PsiMethod> matchingMethod = psiMethods.stream()
							.filter(psiMethod -> psiMethod.hasAnnotation(API_ANNOTATION_ARGUMENT))
							.filter(psiMethod -> psiMethod.getName().equals(method.getName()))
							.findAny();
					if (matchingMethod.isEmpty() && method.getNameIdentifier() != null) {
						final LocalQuickFix[] fixes = {
								new RemoveMethodQuickFix(),
								new RefactorMethodQuickFix(),
								new RemoveAnnotationQuickFix(),
								new ChangeAnnotationQuickFix()
						};
						holder.registerProblem(method.getNameIdentifier(), DESCRIPTION_TEMPLATE, fixes);
					}
				}
			}
		};
	}
}
