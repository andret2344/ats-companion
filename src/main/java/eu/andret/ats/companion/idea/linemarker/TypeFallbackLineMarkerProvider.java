package eu.andret.ats.companion.idea.linemarker;

import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider;
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiJvmMember;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiStatement;
import com.intellij.psi.PsiType;
import com.intellij.psi.util.PsiTreeUtil;
import eu.andret.ats.companion.idea.utilities.Constants;
import eu.andret.ats.companion.idea.utilities.IconProvider;
import eu.andret.ats.companion.idea.utilities.Util;
import eu.andret.ats.companion.idea.utilities.Verifier;
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

		final PsiStatement parentOfType = PsiTreeUtil.getParentOfType(element, PsiStatement.class);
		if (parentOfType != null) {
			return;
		}

		Optional.of(element)
				.map(psiElement -> PsiTreeUtil.getParentOfType(psiElement, PsiMethod.class))
				.filter(Verifier::isArgumentMethod)
				.map(PsiJvmMember::getContainingClass)
				.map(PsiClass::getMethods)
				.stream()
				.flatMap(Arrays::stream)
				.forEach(method -> Optional.of(method)
						.map(psiAnnotation -> psiAnnotation.getAnnotation(Constants.ANNOTATION_TYPE_FALLBACK))
						.map(Util::getTypeFallbackValue)
						.filter(PsiType::isValid)
						.filter(((PsiParameter) element).getType()::equals)
						.map(ignored -> NavigationGutterIconBuilder.create(IconProvider.FALLBACK)
								.setTarget(method)
								.setTooltipText("Find @TypeFallback method")
								.createLineMarkerInfo(element.getLastChild()))
						.ifPresent(result::add));
	}
}
