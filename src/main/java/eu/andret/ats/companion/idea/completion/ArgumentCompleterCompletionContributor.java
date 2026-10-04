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
			final boolean withQuotes = Optional.of(parameters)
					.map(CompletionParameters::getPosition)
					.map(PsiJavaToken.class::cast)
					.map(PsiJavaToken::getTokenType)
					.filter(JavaTokenType.STRING_LITERAL::equals)
					.isEmpty();
			Util.getArgumentCompleterValues(parameters.getEditor().getProject(), withQuotes)
					.stream()
					.map(LookupElementBuilder::create)
					.forEach(result::addElement);
			result.stopHere();
		}
	}
}
