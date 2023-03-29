package eu.andret.ats.companion.idea.completion;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.JavaTokenType;
import com.intellij.psi.PsiJavaToken;
import com.intellij.util.ProcessingContext;
import eu.andret.ats.companion.idea.utilities.Util;
import org.jetbrains.annotations.NotNull;

public class ArgumentMapperCompletionProvider extends CompletionProvider<CompletionParameters> {
	@Override
	public void addCompletions(@NotNull final CompletionParameters parameters, @NotNull final ProcessingContext context,
							   @NotNull final CompletionResultSet result) {
		Util.getArgumentMapperValues(parameters.getEditor().getProject())
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
