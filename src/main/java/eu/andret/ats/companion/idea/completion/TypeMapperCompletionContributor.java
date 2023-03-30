/*
 * Copyright Andret Tools System (c) 2018-2022. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.ats.companion.idea.completion;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionType;
import eu.andret.ats.companion.idea.utilities.Constants;

import static com.intellij.patterns.PsiJavaPatterns.psiElement;

public class TypeMapperCompletionContributor extends CompletionContributor {
	public TypeMapperCompletionContributor() {
		final CompletionProvider<CompletionParameters> provider = new TypeMapperCompletionProvider();
		final CompletionType type = CompletionType.BASIC;
		extend(type, psiElement().insideAnnotationParam(Constants.ANNOTATION_TYPE_FALLBACK), provider);
	}
}
