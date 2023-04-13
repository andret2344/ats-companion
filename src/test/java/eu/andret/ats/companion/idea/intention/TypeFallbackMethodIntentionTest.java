/*
 * Copyright (c) 2018 Andret Tools System. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.ats.companion.idea.intention;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase4;
import org.assertj.core.api.ThrowableAssert;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThatNoException;

public class TypeFallbackMethodIntentionTest extends LightJavaCodeInsightFixtureTestCase4 {
	public TypeFallbackMethodIntentionTest() {
		super(null, "src/test/testData/intention");
	}

	@Test
	public void generateFallbackMethod() {
		// given
		getFixture().configureByFile("type-fallback.java");
		final IntentionAction action = getFixture().findSingleIntention(TypeFallbackMethodIntention.NAME);

		// when
		getFixture().launchAction(action);

		// then
		getFixture().checkResultByFile("type-fallback.after.java");
	}

	@Test
	public void fallbackMethodExists() {
		// given
		getFixture().configureByFile("type-fallback.after.java");

		// when
		final ThrowableAssert.ThrowingCallable throwingCallable = () ->
				getFixture().findSingleIntention(TypeFallbackMethodIntention.NAME);

		// then
		assertThatNoException().isThrownBy(throwingCallable);
	}
}
