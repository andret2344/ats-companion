package eu.andret.ats.companion.idea.intention;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase4;
import org.junit.Test;

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
}
