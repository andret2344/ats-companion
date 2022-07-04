package eu.andret.ats.companion.idea.completion;

import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase;
import org.junit.Test;

import java.util.List;

public class ArgumentPositionCompletionContributorTest extends LightJavaCodeInsightFixtureTestCase {

    /**
     * Defines path to files used for running tests.
     *
     * @return The path from this module's root directory ({@code $MODULE_WORKING_DIR$}) to the directory containing
     * files for these tests.
     */
    @Override
    protected String getTestDataPath() {
        return "src/test/testData";
    }

    protected void doTest(final String testName) {
        myFixture.configureByFile(testName + ".java");
        myFixture.completeBasic();
        assertEquals(List.of("0", "1", "2", "3"), myFixture.getLookupElementStrings());
    }

    @Test
    public void testCompletion() {
        doTest("completion");
    }
}
