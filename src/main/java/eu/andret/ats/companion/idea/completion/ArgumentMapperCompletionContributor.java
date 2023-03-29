/*
 * Copyright Andret Tools System (c) 2018-2022. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.ats.companion.idea.completion;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionType;
import eu.andret.ats.companion.idea.utilities.Constants;

import static com.intellij.patterns.PsiJavaPatterns.psiElement;

public class ArgumentMapperCompletionContributor extends CompletionContributor {
	public ArgumentMapperCompletionContributor() {
		final ArgumentMapperCompletionProvider provider = new ArgumentMapperCompletionProvider();
		final CompletionType type = CompletionType.BASIC;
		extend(type, psiElement().insideAnnotationParam(Constants.ANNOTATION_MAPPER), provider);
		extend(type, psiElement().insideAnnotationParam(Constants.ANNOTATION_ARGUMENT_FALLBACK), provider);
	}
}
