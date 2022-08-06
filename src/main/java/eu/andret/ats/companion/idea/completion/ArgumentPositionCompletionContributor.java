/*
 * Copyright Andret Tools System (c) 2018-2022. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.ats.companion.idea.completion;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.patterns.StandardPatterns;
import eu.andret.ats.companion.idea.utilities.Constants;

import static com.intellij.patterns.PsiJavaPatterns.psiElement;

public class ArgumentPositionCompletionContributor extends CompletionContributor {
	public ArgumentPositionCompletionContributor() {
		extend(
				CompletionType.BASIC,
				psiElement().insideAnnotationParam(
						StandardPatterns.string().equalTo(Constants.ANNOTATION_ARGUMENT), "position"),
				new ArgumentPositionCompletionProvider());
	}
}
