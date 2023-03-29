/*
 * Copyright Andret Tools System (c) 2018-2023. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.ats.companion.idea.intention;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase4;
import org.junit.Test;

public class TypeFallbackMethodIntentionTest extends LightJavaCodeInsightFixtureTestCase4 {
	public TypeFallbackMethodIntentionTest() {
		super(null, "src/test/testData/intention");
	}

	@Test
	public void testGenerateFallbackMethod() {
		// given
		getFixture().configureByFile("type-fallback.java");
		final IntentionAction action = getFixture().findSingleIntention(TypeFallbackMethodIntention.NAME);

		// when
		getFixture().launchAction(action);

		// then
		getFixture().checkResultByFile("type-fallback.after.java");
	}
}
