package eu.andret.ats.companion.idea.completion;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameterList;
import com.intellij.util.ProcessingContext;
import eu.andret.ats.companion.idea.utilities.Util;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.stream.Stream;

public class ArgumentPositionCompletionProvider extends CompletionProvider<CompletionParameters> {
	@Override
	public void addCompletions(@NotNull final CompletionParameters parameters, @NotNull final ProcessingContext context,
							   @NotNull final CompletionResultSet result) {
		final PsiMethod method = Util.ancestorOf(parameters.getPosition(), PsiMethod.class);
		Stream.iterate(1, i -> i + 1)
				.limit(Optional.ofNullable(method)
						.map(PsiMethod::getParameterList)
						.map(PsiParameterList::getParametersCount)
						.orElse(0))
				.map(LookupElementBuilder::create)
				.forEach(result::addElement);
		result.stopHere();
	}
}
