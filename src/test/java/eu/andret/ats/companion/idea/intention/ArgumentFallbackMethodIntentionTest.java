/*
 * Copyright Andret Tools System (c) 2018-2022. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.ats.companion.idea.intention;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase4;
import org.junit.Test;

public class ArgumentFallbackMethodIntentionTest extends LightJavaCodeInsightFixtureTestCase4 {
	public ArgumentFallbackMethodIntentionTest() {
		super(null, "src/test/testData/intention");
	}

	@Test
	public void testGenerateFallbackMethod() {
		getFixture().configureByFile("argument-fallback.java");
		final IntentionAction action = getFixture().findSingleIntention(ArgumentFallbackMethodIntention.TEXT);
		getFixture().launchAction(action);
		getFixture().checkResultByFile("argument-fallback.after.java");
	}
}
