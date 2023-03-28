package eu.andret.ats.companion.idea.inspection;

import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.JavaElementVisitor;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.PsiParameter;
import eu.andret.ats.companion.idea.index.MyClassLocator;
import eu.andret.ats.companion.idea.utilities.Constants;
import org.jetbrains.annotations.NotNull;

public class InvalidArgumentMapperInspection extends AbstractBaseJavaLocalInspectionTool {
	@NotNull
	@Override
	public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, final boolean isOnTheFly) {
		return new JavaElementVisitor() {
			@Override
			public void visitParameter(@NotNull final PsiParameter parameter) {
				final PsiAnnotation annotation = parameter.getAnnotation(Constants.ANNOTATION_MAPPER);
				if (annotation != null) {
					System.out.println("In inspection: ");
					MyClassLocator.findMethod(holder.getProject()).forEach(psiReference -> {
						System.out.println("PSI REFERENCE ==================");
						if (psiReference instanceof PsiMethodCallExpression) {
							System.out.println("Tak");
						}
						final PsiMethod resolve = (PsiMethod) psiReference.resolve();
						System.out.println(resolve.getClass().getName());

					});
				}
			}
		};
	}
}
