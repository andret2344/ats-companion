/*
 * Copyright (c) 2018 Andret Tools System. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.ats.companion.idea.intention;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase4;
import org.assertj.core.api.ThrowableAssert;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThatNoException;

public class ArgumentFallbackMethodIntentionTest extends LightJavaCodeInsightFixtureTestCase4 {
	public ArgumentFallbackMethodIntentionTest() {
		super(null, "src/test/testData/intention");
	}

	@Test
	public void generateFallbackMethod() {
		// given
		getFixture().configureByFile("argument-fallback.java");
		final IntentionAction action = getFixture().findSingleIntention(ArgumentFallbackMethodIntention.NAME);

		// when
		getFixture().launchAction(action);

		// then
		getFixture().checkResultByFile("argument-fallback.after.java");
	}

	@Test
	public void fallbackMethodExists() {
		// given
		getFixture().configureByFile("argument-fallback.after.java");

		// when
		final ThrowableAssert.ThrowingCallable throwingCallable = () ->
				getFixture().findSingleIntention(ArgumentFallbackMethodIntention.NAME);

		// then
		assertThatNoException().isThrownBy(throwingCallable);
	}
}
