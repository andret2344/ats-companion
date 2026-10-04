package eu.andret.ats.companion.idea.inspection;

import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaElementVisitor;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifierList;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiParameterList;
import com.intellij.psi.PsiType;
import com.intellij.psi.PsiTypeElement;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import eu.andret.ats.companion.idea.utilities.Constants;
import eu.andret.ats.companion.idea.utilities.Verifier;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

public class ConstructorParametersInspection extends AbstractBaseJavaLocalInspectionTool {
	@NonNls
	public static final String DESCRIPTION = "Wrong constructor types used";

	private static final String PLUGIN = "plugin";
	private static final String SENDER = "sender";

	@NotNull
	@Override
	public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, final boolean isOnTheFly) {
		return new JavaElementVisitor() {
			@Override
			public void visitMethod(@NotNull final PsiMethod method) {
				if (!Verifier.isBaseCommandClass(method.getContainingClass())) {
					return;
				}
				if (!method.isConstructor()) {
					return;
				}
				final PsiParameterList parameterList = method.getParameterList();
				final PsiParameter[] parameters = parameterList.getParameters();
				if (parameters.length == 0) {
					holder.registerProblem(parameterList, DESCRIPTION,
							ProblemHighlightType.GENERIC_ERROR_OR_WARNING, new ChangeParametersToQuickFix());
					return;
				}
				final String param1TypeText = parameters[0].getType().getCanonicalText();
				if (parameters.length == 1) {
					if (param1TypeText.equals(Constants.BUKKIT_COMMAND_SENDER)) {
						holder.registerProblem(parameterList, DESCRIPTION,
								ProblemHighlightType.GENERIC_ERROR_OR_WARNING, new InsertSecondParametersQuickFix());
					} else if (param1TypeText.equals(Constants.BUKKIT_JAVA_PLUGIN)) {
						holder.registerProblem(parameterList, DESCRIPTION,
								ProblemHighlightType.GENERIC_ERROR_OR_WARNING, new InsertFirstParametersQuickFix());
					} else {
						holder.registerProblem(parameterList, DESCRIPTION,
								ProblemHighlightType.GENERIC_ERROR_OR_WARNING, new ChangeParametersToQuickFix());
					}
					return;
				}

				final boolean param1Correct = param1TypeText.equals(Constants.BUKKIT_COMMAND_SENDER);
				final Optional<PsiType> genericType = Optional.ofNullable(method.getContainingClass())
						.map(PsiClass::getExtendsListTypes)
						.stream()
						.flatMap(Arrays::stream)
						.filter(type -> type.getCanonicalText().startsWith(Constants.CLASS_ANNOTATED_COMMAND_EXECUTOR))
						.findAny()
						.map(type -> type.getParameters()[0]);
				final boolean param2Correct = genericType
						.map(type -> type.equals(parameters[1].getType()))
						.orElse(false);

				if (!param1Correct && !param2Correct) {
					holder.registerProblem(parameterList, DESCRIPTION,
							ProblemHighlightType.GENERIC_ERROR_OR_WARNING, new ChangeParametersToQuickFix());
				}
				if (!param1Correct) {
					holder.registerProblem(Objects.requireNonNull(parameterList.getParameter(0)),
							DESCRIPTION,
							ProblemHighlightType.GENERIC_ERROR_OR_WARNING,
							new ChangeParameterQuickFix(Constants.BUKKIT_COMMAND_SENDER));
				}
				if (!param2Correct) {
					genericType.ifPresent(type ->
							holder.registerProblem(Objects.requireNonNull(parameterList.getParameter(1)),
									DESCRIPTION,
									ProblemHighlightType.GENERIC_ERROR_OR_WARNING,
									new ChangeParameterQuickFix(type.getCanonicalText())));
				}
			}
		};
	}

	public static class ChangeParametersToQuickFix implements LocalQuickFix {
		public static final String NAME = "Change parameter list to match '(CommandSender, JavaPlugin)'";

		@Override
		public void applyFix(@NotNull final Project project, @NotNull final ProblemDescriptor descriptor) {
			Optional.of(descriptor)
					.map(ProblemDescriptor::getPsiElement)
					.map(PsiParameterList.class::cast)
					.ifPresent(psiParameterList -> {
						Arrays.stream(psiParameterList.getParameters()).forEach(PsiElement::delete);
						final PsiElementFactory factory = JavaPsiFacade.getInstance(project).getElementFactory();
						final PsiParameter class1Type = factory.createParameter(SENDER, factory.createTypeByFQClassName(Constants.BUKKIT_COMMAND_SENDER));
						final PsiParameter class2Type = factory.createParameter(PLUGIN, factory.createTypeByFQClassName(Constants.BUKKIT_JAVA_PLUGIN));
						psiParameterList.add(class1Type);
						psiParameterList.add(class2Type);
						JavaCodeStyleManager.getInstance(project).shortenClassReferences(class1Type);
						JavaCodeStyleManager.getInstance(project).shortenClassReferences(class2Type);
					});
		}

		@NotNull
		@Override
		public String getFamilyName() {
			return NAME;
		}
	}

	public static class InsertSecondParametersQuickFix implements LocalQuickFix {
		public static final String NAME = "Insert 2nd parameter";

		@Override
		public void applyFix(@NotNull final Project project, @NotNull final ProblemDescriptor descriptor) {
			Optional.of(descriptor)
					.map(ProblemDescriptor::getPsiElement)
					.map(PsiParameterList.class::cast)
					.ifPresent(psiParameterList -> {
						final PsiElementFactory factory = JavaPsiFacade.getInstance(project).getElementFactory();
						final PsiParameter type = factory.createParameter(PLUGIN, factory.createTypeByFQClassName(Constants.BUKKIT_JAVA_PLUGIN));
						psiParameterList.addAfter(type, psiParameterList.getParameter(0));
						JavaCodeStyleManager.getInstance(project).shortenClassReferences(type);
					});
		}

		@NotNull
		@Override
		public String getFamilyName() {
			return NAME;
		}
	}

	public static class InsertFirstParametersQuickFix implements LocalQuickFix {
		public static final String NAME = "Insert 1st parameter";

		@Override
		public void applyFix(@NotNull final Project project, @NotNull final ProblemDescriptor descriptor) {
			Optional.of(descriptor)
					.map(ProblemDescriptor::getPsiElement)
					.map(PsiParameterList.class::cast)
					.ifPresent(psiParameterList -> {
						final PsiElementFactory factory = JavaPsiFacade.getInstance(project).getElementFactory();
						final PsiParameter type = factory.createParameter(SENDER, factory.createTypeByFQClassName(Constants.BUKKIT_COMMAND_SENDER));
						psiParameterList.addBefore(type, psiParameterList.getParameter(0));
						JavaCodeStyleManager.getInstance(project).shortenClassReferences(type);
					});
		}

		@NotNull
		@Override
		public String getFamilyName() {
			return NAME;
		}
	}

	public static class ChangeParameterQuickFix implements LocalQuickFix {
		public static final String NAME = "Change parameter's type to ";

		@NotNull
		private final String qualifiedType;

		public ChangeParameterQuickFix(@NotNull final String qualifiedType) {
			this.qualifiedType = qualifiedType;
		}

		@Override
		public void applyFix(@NotNull final Project project, @NotNull final ProblemDescriptor descriptor) {
			Optional.of(descriptor)
					.map(ProblemDescriptor::getPsiElement)
					.map(PsiParameter.class::cast)
					.ifPresent(psiParameter -> {
						final PsiElementFactory factory = JavaPsiFacade.getInstance(project).getElementFactory();
						final PsiClassType type = factory.createTypeByFQClassName(qualifiedType);
						final PsiTypeElement typeElement = factory.createTypeElement(type);
						final PsiModifierList modifierList = psiParameter.getModifierList();
						if (psiParameter.getTypeElement() != null && modifierList != null) {
							final PsiElement modifiers = modifierList.copy();
							psiParameter.getTypeElement().replace(typeElement);
							modifierList.replace(modifiers);
						}
					});
		}

		@NotNull
		@Override
		public String getFamilyName() {
			return NAME + qualifiedType;
		}
	}
}
