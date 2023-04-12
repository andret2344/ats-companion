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
import com.intellij.psi.JavaTokenType;
import com.intellij.psi.PsiJavaToken;
import com.intellij.util.ProcessingContext;
import eu.andret.ats.companion.idea.utilities.Constants;
import eu.andret.ats.companion.idea.utilities.Util;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

import static com.intellij.patterns.PsiJavaPatterns.psiElement;

public class ArgumentMapperCompletionContributor extends CompletionContributor {
	public ArgumentMapperCompletionContributor() {
		final CompletionProvider<CompletionParameters> provider = new ArgumentMapperCompletionProvider();
		final CompletionType type = CompletionType.BASIC;
		extend(type, psiElement().insideAnnotationParam(Constants.ANNOTATION_MAPPER), provider);
		extend(type, psiElement().insideAnnotationParam(Constants.ANNOTATION_ARGUMENT_FALLBACK), provider);
	}

	private static class ArgumentMapperCompletionProvider extends CompletionProvider<CompletionParameters> {
		@Override
		public void addCompletions(@NotNull final CompletionParameters parameters,
								   @NotNull final ProcessingContext context,
								   @NotNull final CompletionResultSet result) {
			final boolean withQuotes = Optional.of(parameters)
					.map(CompletionParameters::getPosition)
					.map(PsiJavaToken.class::cast)
					.map(PsiJavaToken::getTokenType)
					.filter(JavaTokenType.STRING_LITERAL::equals)
					.isEmpty();
			Util.getArgumentMapperValues(parameters.getEditor().getProject(), withQuotes)
					.stream()
					.map(LookupElementBuilder::create)
					.forEach(result::addElement);
			result.stopHere();
		}
	}
}
