package eu.andret.ats.companion.idea.linemarker;

import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider;
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiJvmMember;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import eu.andret.ats.companion.idea.utilities.Constants;
import eu.andret.ats.companion.idea.utilities.IconProvider;
import eu.andret.ats.companion.idea.utilities.Util;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

public class ArgumentFallbackLineMarkerProvider extends RelatedItemLineMarkerProvider {
	@Override
	protected void collectNavigationMarkers(@NotNull final PsiElement element,
											@NotNull final Collection<? super RelatedItemLineMarkerInfo<?>> result) {
		if (!(element instanceof PsiParameter psiParameter)) {
			return;
		}

		Optional.of(psiParameter)
				.map(psiAnnotation -> psiAnnotation.getAnnotation(Constants.ANNOTATION_MAPPER))
				.map(psiAnnotationMemberValue -> psiAnnotationMemberValue.findAttributeValue("value"))
				.ifPresent(psiAnnotationMemberValue -> {
					final PsiMethod psiMethod = Util.ancestorOf(element, PsiMethod.class, 2);
					Optional.ofNullable(psiMethod)
							.map(PsiJvmMember::getContainingClass)
							.map(PsiClass::getMethods)
							.stream()
							.flatMap(Arrays::stream)
							.forEach(method -> Optional.of(method)
									.map(psiAnnotation -> psiAnnotation.getAnnotation(Constants.ANNOTATION_ARGUMENT_FALLBACK))
									.map(annotationValue -> annotationValue.findAttributeValue("value"))
									.map(PsiElement::getText)
									.filter(psiAnnotationMemberValue.getText()::equals)
									.map(ignored -> NavigationGutterIconBuilder.create(IconProvider.FALLBACK)
											.setTarget(method)
											.setTooltipText("Find @ArgumentFallback method")
											.createLineMarkerInfo(psiAnnotationMemberValue.getFirstChild()))
									.ifPresent(result::add));
				});
	}
}
