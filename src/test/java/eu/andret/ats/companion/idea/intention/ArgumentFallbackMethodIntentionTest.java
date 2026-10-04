package eu.andret.ats.companion.idea.intention;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase4;
import org.junit.Test;

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
}
