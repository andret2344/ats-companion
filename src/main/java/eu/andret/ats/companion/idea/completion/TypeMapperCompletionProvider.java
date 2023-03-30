/*
 * Copyright Andret Tools System (c) 2018-2022. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.ats.companion.idea.completion;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.util.ProcessingContext;
import eu.andret.ats.companion.idea.utilities.Util;
import org.jetbrains.annotations.NotNull;

public class TypeMapperCompletionProvider extends CompletionProvider<CompletionParameters> {
	@Override
	public void addCompletions(@NotNull final CompletionParameters parameters, @NotNull final ProcessingContext context,
							   @NotNull final CompletionResultSet result) {
		Util.getTypeMapperValues(parameters.getEditor().getProject())
				.stream()
				.map(type -> type.getPresentableText() + ".class")
				.map(LookupElementBuilder::create)
				.forEach(result::addElement);
		result.stopHere();
	}
}
