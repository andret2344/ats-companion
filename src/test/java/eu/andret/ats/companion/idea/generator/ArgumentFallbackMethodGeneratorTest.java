package eu.andret.ats.companion.idea.generator;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase;
import org.junit.Test;

public class ArgumentFallbackMethodGeneratorTest extends LightJavaCodeInsightFixtureTestCase {

	/**
	 * Defines path to files used for running tests.
	 *
	 * @return The path from this module's root directory ({@code $MODULE_WORKING_DIR$}) to the directory containing
	 * 		files for these tests.
	 */
	@Override
	protected String getTestDataPath() {
		return "src/test/testData";
	}

	protected void doTest(final String testName, final String hint) {
		myFixture.configureByFile(testName + ".java");
		final IntentionAction action = myFixture.findSingleIntention(hint);
		myFixture.launchAction(action);
		myFixture.checkResultByFile(testName + ".after.java");
	}

	@Test
	public void testIntention() {
		doTest("before.template", "ATS: Generate argument fallback method");
	}
}
