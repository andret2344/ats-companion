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
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiParameterList;
import com.intellij.psi.PsiType;
import com.intellij.psi.PsiTypeElement;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import com.intellij.util.IncorrectOperationException;
import eu.andret.ats.companion.idea.utilities.Constants;
import eu.andret.ats.companion.idea.utilities.Verifier;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

public class ConstructorParametersInspection extends AbstractBaseJavaLocalInspectionTool {

	public static final String PLUGIN = "plugin";
	public static final String SENDER = "sender";

	@NotNull
	@Override
	public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, final boolean isOnTheFly) {
		return new JavaElementVisitor() {
			@NonNls
			private static final String DESCRIPTION_TEMPLATE = "Wrong constructor types used";

			@Override
			public void visitMethod(@NotNull final PsiMethod method) {
				if (!Verifier.verifyClass(method.getContainingClass())) {
					return;
				}
				if (!method.isConstructor()) {
					return;
				}
				final PsiParameterList parameterList = method.getParameterList();
				final PsiParameter[] parameters = parameterList.getParameters();
				if (parameters.length == 0) {
					holder.registerProblem(parameterList, DESCRIPTION_TEMPLATE,
							ProblemHighlightType.GENERIC_ERROR_OR_WARNING, new ChangeParametersToQuickFix());
					return;
				}
				final String param1TypeText = parameters[0].getType().getCanonicalText();
				if (parameters.length == 1) {
					if (param1TypeText.equals(Constants.BUKKIT_COMMAND_SENDER)) {
						holder.registerProblem(parameterList, DESCRIPTION_TEMPLATE,
								ProblemHighlightType.GENERIC_ERROR_OR_WARNING, new InsertSecondParametersQuickFix());
					} else if (param1TypeText.equals(Constants.BUKKIT_JAVA_PLUGIN)) {
						holder.registerProblem(parameterList, DESCRIPTION_TEMPLATE,
								ProblemHighlightType.GENERIC_ERROR_OR_WARNING, new InsertFirstParametersQuickFix());
					} else {
						holder.registerProblem(parameterList, DESCRIPTION_TEMPLATE,
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
					holder.registerProblem(parameterList, DESCRIPTION_TEMPLATE,
							ProblemHighlightType.GENERIC_ERROR_OR_WARNING, new ChangeParametersToQuickFix());
				}
				if (!param1Correct) {
					holder.registerProblem(Objects.requireNonNull(parameterList.getParameter(0)),
							DESCRIPTION_TEMPLATE,
							ProblemHighlightType.GENERIC_ERROR_OR_WARNING,
							new ChangeParameterQuickFix(Constants.BUKKIT_COMMAND_SENDER));
				}
				if (!param2Correct) {
					genericType.ifPresent(type ->
							holder.registerProblem(Objects.requireNonNull(parameterList.getParameter(1)),
									DESCRIPTION_TEMPLATE,
									ProblemHighlightType.GENERIC_ERROR_OR_WARNING,
									new ChangeParameterQuickFix(type.getCanonicalText())));
				}
			}
		};
	}

	@Slf4j
	public static class ChangeParametersToQuickFix implements LocalQuickFix {
		@NotNull
		@Override
		public String getName() {
			return "Change parameter list to match '(CommandSender, JavaPlugin)'";
		}

		@Override
		public void applyFix(@NotNull final Project project, @NotNull final ProblemDescriptor descriptor) {
			try {
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
			} catch (final IncorrectOperationException e) {
				log.error(getClass().getName(), e);
			}
		}

		@Override
		@NotNull
		public String getFamilyName() {
			return getName();
		}
	}

	@Slf4j
	public static class InsertSecondParametersQuickFix implements LocalQuickFix {
		@NotNull
		@Override
		public String getName() {
			return "Insert 2nd parameter";
		}

		@Override
		public void applyFix(@NotNull final Project project, @NotNull final ProblemDescriptor descriptor) {
			try {
				Optional.of(descriptor)
						.map(ProblemDescriptor::getPsiElement)
						.map(PsiParameterList.class::cast)
						.ifPresent(psiParameterList -> {
							final PsiElementFactory factory = JavaPsiFacade.getInstance(project).getElementFactory();
							final PsiParameter type = factory.createParameter(PLUGIN, factory.createTypeByFQClassName(Constants.BUKKIT_JAVA_PLUGIN));
							psiParameterList.addAfter(type, psiParameterList.getParameter(0));
							JavaCodeStyleManager.getInstance(project).shortenClassReferences(type);
						});
			} catch (final IncorrectOperationException e) {
				log.error(getClass().getName(), e);
			}
		}

		@Override
		@NotNull
		public String getFamilyName() {
			return getName();
		}
	}

	@Slf4j
	public static class InsertFirstParametersQuickFix implements LocalQuickFix {
		@NotNull
		@Override
		public String getName() {
			return "Insert 1st parameter";
		}

		@Override
		public void applyFix(@NotNull final Project project, @NotNull final ProblemDescriptor descriptor) {
			try {
				Optional.of(descriptor)
						.map(ProblemDescriptor::getPsiElement)
						.map(PsiParameterList.class::cast)
						.ifPresent(psiParameterList -> {
							final PsiElementFactory factory = JavaPsiFacade.getInstance(project).getElementFactory();
							final PsiParameter type = factory.createParameter(SENDER, factory.createTypeByFQClassName(Constants.BUKKIT_COMMAND_SENDER));
							psiParameterList.addBefore(type, psiParameterList.getParameter(0));
							JavaCodeStyleManager.getInstance(project).shortenClassReferences(type);
						});
			} catch (final IncorrectOperationException e) {
				log.error(getClass().getName(), e);
			}
		}

		@Override
		@NotNull
		public String getFamilyName() {
			return getName();
		}
	}

	@Slf4j
	@Value
	public static class ChangeParameterQuickFix implements LocalQuickFix {
		String qualifiedType;

		@NotNull
		@Override
		public String getName() {
			return "Change parameter's type to " + qualifiedType;
		}

		@Override
		public void applyFix(@NotNull final Project project, @NotNull final ProblemDescriptor descriptor) {
			try {
				Optional.of(descriptor)
						.map(ProblemDescriptor::getPsiElement)
						.map(PsiParameter.class::cast)
						.ifPresent(psiParameter -> {
							final PsiElementFactory factory = JavaPsiFacade.getInstance(project).getElementFactory();
							final PsiClassType type = factory.createTypeByFQClassName(qualifiedType);
							final PsiTypeElement typeElement = factory.createTypeElement(type);
							type.annotate(psiParameter.getType().getAnnotationProvider());
							if (psiParameter.getTypeElement() != null) {
								psiParameter.getTypeElement().replace(typeElement);
							}
						});
			} catch (final IncorrectOperationException e) {
				log.error(getClass().getName(), e);
			}
		}

		@Override
		@NotNull
		public String getFamilyName() {
			return getName();
		}
	}
}
