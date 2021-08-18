package eu.andret.ats.idea.completion;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.patterns.StandardPatterns;
import eu.andret.arguments.api.annotation.Argument;

import static com.intellij.patterns.PsiJavaPatterns.psiElement;

public class ArgumentPositionCompletionContributor extends CompletionContributor {
	public ArgumentPositionCompletionContributor() {
		extend(
				CompletionType.BASIC,
				psiElement().insideAnnotationParam(
						StandardPatterns.string().equalTo(Argument.class.getName()),
						"position"
				),
				new ArgumentPositionCompletionProvider());
	}
}
