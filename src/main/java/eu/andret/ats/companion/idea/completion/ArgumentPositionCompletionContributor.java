/*
 * Copyright (c) 2018 Andret Tools System. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.ats.companion.idea.completion;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.patterns.PsiJavaElementPattern;
import com.intellij.patterns.StandardPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameterList;
import com.intellij.util.ProcessingContext;
import eu.andret.ats.companion.idea.utilities.Constants;
import eu.andret.ats.companion.idea.utilities.Util;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.stream.Stream;

import static com.intellij.patterns.PsiJavaPatterns.psiElement;

public class ArgumentPositionCompletionContributor extends CompletionContributor {
	public ArgumentPositionCompletionContributor() {
		final CompletionType type = CompletionType.BASIC;
		final CompletionProvider<CompletionParameters> provider = new ArgumentPositionCompletionProvider();
		final PsiJavaElementPattern.Capture<PsiElement> position = psiElement().insideAnnotationParam(
				StandardPatterns.string().equalTo(Constants.ANNOTATION_ARGUMENT), "position");
		extend(type, position, provider);
	}

	private static class ArgumentPositionCompletionProvider extends CompletionProvider<CompletionParameters> {
		@Override
		public void addCompletions(@NotNull final CompletionParameters parameters,
								   @NotNull final ProcessingContext context,
								   @NotNull final CompletionResultSet result) {
			final PsiMethod method = Util.ancestorOf(parameters.getPosition(), PsiMethod.class);
			Stream.iterate(0, i -> i + 1)
					.limit(1L + Optional.ofNullable(method)
							.map(PsiMethod::getParameterList)
							.map(PsiParameterList::getParametersCount)
							.orElse(0))
					.map(LookupElementBuilder::create)
					.forEach(result::addElement);
			result.stopHere();
		}
	}
}
