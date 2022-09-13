package eu.andret.ats.companion.idea.linemarker;

import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider;
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassObjectAccessExpression;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiJvmMember;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.impl.source.PsiImmediateClassType;
import eu.andret.ats.companion.idea.utilities.Constants;
import eu.andret.ats.companion.idea.utilities.IconProvider;
import eu.andret.ats.companion.idea.utilities.Util;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

public class TypeFallbackLineMarkerProvider extends RelatedItemLineMarkerProvider {
	@Override
	protected void collectNavigationMarkers(@NotNull final PsiElement element,
											@NotNull final Collection<? super RelatedItemLineMarkerInfo<?>> result) {
		if (!(element instanceof PsiParameter)) {
			return;
		}

		final PsiMethod psiMethod = Util.ancestorOf(element, PsiMethod.class, 2);

		Optional.ofNullable(psiMethod)
				.map(PsiJvmMember::getContainingClass)
				.map(PsiClass::getMethods)
				.stream()
				.flatMap(Arrays::stream)
				.forEach(method -> Optional.of(method)
						.map(psiAnnotation -> psiAnnotation.getAnnotation(Constants.ANNOTATION_TYPE_FALLBACK))
						.map(psiAnnotationMemberValue -> psiAnnotationMemberValue.findAttributeValue("value"))
						.map(PsiClassObjectAccessExpression.class::cast)
						.map(PsiClassObjectAccessExpression::getType)
						.map(PsiImmediateClassType.class::cast)
						.map(PsiImmediateClassType::getParameters)
						.map(psiTypes -> psiTypes[0])
						.filter(((PsiParameter) element).getType()::equals)
						.map(psiType -> NavigationGutterIconBuilder.create(IconProvider.FALLBACK)
								.setTarget(method)
								.setTooltipText("Find @TypeFallback method")
								.createLineMarkerInfo(element))
						.ifPresent(result::add));
	}


}
