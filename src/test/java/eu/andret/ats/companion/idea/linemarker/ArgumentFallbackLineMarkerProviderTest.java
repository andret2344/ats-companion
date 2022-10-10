/*
 * Copyright Andret Tools System (c) 2018-2022. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.ats.companion.idea.linemarker;

import com.google.common.collect.Iterables;
import com.intellij.codeInsight.daemon.GutterMark;
import com.intellij.codeInsight.daemon.LineMarkerInfo;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo;
import com.intellij.navigation.GotoRelatedItem;
import com.intellij.psi.PsiElement;
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase;
import org.junit.Test;

import java.util.List;

public class ArgumentFallbackLineMarkerProviderTest extends LightJavaCodeInsightFixtureTestCase {

	@Override
	protected String getTestDataPath() {
		return "src/test/testData/linemarker";
	}

	@Test
	public void test() {
		//given
		myFixture.configureByFile("argument-fallback-line-marker.java");
		myFixture.addClass("package org.bukkit.player; public class Player {}");
		//when
		List<GutterMark> allGutters = myFixture.findAllGutters();
		//then
		LineMarkerInfo.LineMarkerGutterIconRenderer gutter = (LineMarkerInfo.LineMarkerGutterIconRenderer) Iterables.getOnlyElement(allGutters);
//		assert
		RelatedItemLineMarkerInfo<PsiElement> lineMarkerInfo = (RelatedItemLineMarkerInfo<PsiElement>) (gutter.getLineMarkerInfo());
		lineMarkerInfo.createGotoRelatedItems().stream().findFirst().map(GotoRelatedItem.class::cast).ifPresent(o -> System.out.println(o.getElement()));
//		((RelatedItemLineMarkerGutterIconRenderer) (allGutters.get(0))).getLineMarkerInfo().createGotoRelatedItems().get(0).getElement()
	}
}