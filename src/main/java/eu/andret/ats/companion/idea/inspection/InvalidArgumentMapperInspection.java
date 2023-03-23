package eu.andret.ats.companion.idea.inspection;

import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.JavaElementVisitor;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiParameter;
import com.intellij.util.indexing.FileBasedIndex;
import eu.andret.ats.companion.idea.index.Index;
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
					System.out.println(FileBasedIndex.getInstance().getAllKeys(Index.NAME, holder.getProject()));
				}
			}
		};
	}
}
