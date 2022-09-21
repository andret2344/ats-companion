/*
 * Copyright Andret Tools System (c) 2018-2022. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.ats.companion.idea.linemarker;

import com.intellij.codeInsight.daemon.GutterMark;
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase4;
import org.junit.Test;

import java.util.List;

public class ArgumentFallbackLineMarkerProviderTest extends LightJavaCodeInsightFixtureTestCase4 {
	public ArgumentFallbackLineMarkerProviderTest() {
		super(null, "src/test/testData/linemarker");
	}

	@Test
	public void test() {
		//given
		getFixture().configureByFile("argument-fallback-line-marker.java");
		getFixture().addClass("package org.bukkit.player; public class Player {}");
		//when
		List<GutterMark> allGutters = getFixture().findAllGutters();
		//then
		System.out.println("dupa");
	}
}