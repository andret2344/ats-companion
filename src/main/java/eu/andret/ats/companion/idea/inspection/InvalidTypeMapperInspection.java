/*
 * Copyright (c) 2018 Andret Tools System. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.ats.companion.idea.inspection;

import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaElementVisitor;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiAnnotationMemberValue;
import com.intellij.psi.PsiArrayType;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiPrimitiveType;
import com.intellij.psi.PsiStatement;
import com.intellij.psi.PsiType;
import com.intellij.psi.util.PsiTreeUtil;
import eu.andret.ats.companion.idea.utilities.Constants;
import eu.andret.ats.companion.idea.utilities.Util;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class InvalidTypeMapperInspection extends AbstractBaseJavaLocalInspectionTool {
	public static final String DESCRIPTION = "Invalid type mapper.";

	@NotNull
	@Override
	public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, final boolean isOnTheFly) {
		return new JavaElementVisitor() {
			@Override
			public void visitMethod(@NotNull final PsiMethod method) {
				final PsiAnnotation annotation = method.getAnnotation(Constants.ANNOTATION_TYPE_FALLBACK);
				if (annotation == null) {
					return;
				}
				final PsiAnnotationMemberValue value = annotation.findAttributeValue("value");
				final Optional<PsiType> type = Util.getTypeMapperValues(holder.getProject())
						.stream()
						.filter(psiType -> Util.getTypeFallbackValue(annotation)
								.map(psiType::equals)
								.orElse(false))
						.findAny();
				if (value != null && type.isEmpty()) {
					holder.registerProblem(value, DESCRIPTION, ProblemHighlightType.ERROR);
				}
			}

			@Override
			public void visitParameter(@NotNull final PsiParameter parameter) {
				final PsiStatement parentOfType = PsiTreeUtil.getParentOfType(parameter, PsiStatement.class);
				if (parentOfType != null) {
					return;
				}
				final PsiMethod parent = PsiTreeUtil.getParentOfType(parameter, PsiMethod.class);
				if (parent == null || !parent.hasAnnotation(Constants.ANNOTATION_ARGUMENT)) {
					return;
				}
				final PsiType psiType = getPsiTypeElement(parameter.getType(), holder.getProject());
				if (psiType == null) {
					return;
				}
				final Optional<PsiType> type = Util.getTypeMapperValues(holder.getProject())
						.stream()
						.filter(psiType::equals)
						.findAny();
				if (type.isEmpty() && parameter.getTypeElement() != null) {
					holder.registerProblem(parameter.getTypeElement(), DESCRIPTION, ProblemHighlightType.ERROR);
				}
			}
		};
	}

	@Nullable
	private static PsiType getPsiTypeElement(@NotNull final PsiType psiType, @NotNull final Project project) {
		if (psiType instanceof final PsiArrayType psiArrayType) {
			return getPsiTypeElement(psiArrayType.getDeepComponentType(), project);
		}
		if (psiType instanceof PsiPrimitiveType) {
			return null;
		}
		final PsiClassType string = Util.createStringType(project);
		if (psiType.equals(string)) {
			return null;
		}
		return psiType;
	}
}
