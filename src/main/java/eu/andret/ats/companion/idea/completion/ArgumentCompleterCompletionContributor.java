/*
 * Copyright Andret Tools System (c) 2018-2022. Copying and modifying allowed only keeping git link reference.
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

import static com.intellij.patterns.PsiJavaPatterns.psiElement;

public class ArgumentCompleterCompletionContributor extends CompletionContributor {
	public ArgumentCompleterCompletionContributor() {
		final CompletionProvider<CompletionParameters> provider = new ArgumentCompleterCompletionProvider();
		final CompletionType type = CompletionType.BASIC;
		extend(type, psiElement().insideAnnotationParam(Constants.ANNOTATION_COMPLETER), provider);
	}

	private static class ArgumentCompleterCompletionProvider extends CompletionProvider<CompletionParameters> {
		@Override
		public void addCompletions(@NotNull final CompletionParameters parameters,
								   @NotNull final ProcessingContext context,
								   @NotNull final CompletionResultSet result) {
			Util.getArgumentCompleterValues(parameters.getEditor().getProject())
					.stream()
					.map(value -> {
						if (((PsiJavaToken) parameters.getPosition()).getTokenType().equals(JavaTokenType.STRING_LITERAL)) {
							return value.substring(1, value.length() - 1);
						}
						return value;
					})
					.map(LookupElementBuilder::create)
					.forEach(result::addElement);
			result.stopHere();
		}
	}
}
