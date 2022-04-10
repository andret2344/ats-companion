package eu.andret.ats.companion.idea.inspection;

import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.psi.JavaElementVisitor;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiParameter;
import eu.andret.ats.companion.idea.MappingConfigService;
import eu.andret.ats.companion.idea.utilities.Constants;
import eu.andret.ats.companion.idea.utilities.Util;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class InvalidArgumentMapperInspection extends AbstractBaseJavaLocalInspectionTool {
	@NotNull
	@Override
	public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, final boolean isOnTheFly) {
		return new JavaElementVisitor() {
			@NonNls
			private static final String DESCRIPTION_TEMPLATE = "@BaseCommand class cannot be abstract";

			@Override
			public void visitParameter(@NotNull final PsiParameter parameter) {
				final PsiAnnotation annotation = parameter.getAnnotation(Constants.MAPPER_ARGUMENT);
				if (annotation != null) {
					Util.getArgumentFallbackValue(annotation)
							.ifPresent(x -> {
								final MappingConfigService service = ApplicationManager.getApplication().getService(MappingConfigService.class);
								service.getArgumentMappers().clear();
								service.getArgumentMappers().add("test1");
								service.getArgumentMappers().add("test2");
								if (!service.getArgumentMappers().contains(x)) {
									holder.registerProblem(annotation.findAttributeValue("value"), DESCRIPTION_TEMPLATE);
								}
							});
				}
			}
		};
	}
}
