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
import com.intellij.util.ProcessingContext;
import eu.andret.ats.companion.idea.utilities.Constants;
import eu.andret.ats.companion.idea.utilities.Util;
import org.jetbrains.annotations.NotNull;

import static com.intellij.patterns.PsiJavaPatterns.psiElement;

public class TypeMapperCompletionContributor extends CompletionContributor {
	public TypeMapperCompletionContributor() {
		final CompletionProvider<CompletionParameters> provider = new TypeMapperCompletionProvider();
		final CompletionType type = CompletionType.BASIC;
		extend(type, psiElement().insideAnnotationParam(Constants.ANNOTATION_TYPE_FALLBACK), provider);
	}

	private static class TypeMapperCompletionProvider extends CompletionProvider<CompletionParameters> {
		@Override
		public void addCompletions(@NotNull final CompletionParameters parameters,
								   @NotNull final ProcessingContext context,
								   @NotNull final CompletionResultSet result) {
			Util.getTypeMapperValues(parameters.getEditor().getProject())
					.stream()
					.map(type -> type.getPresentableText() + ".class")
					.map(LookupElementBuilder::create)
					.forEach(result::addElement);
			result.stopHere();
		}
	}
}
