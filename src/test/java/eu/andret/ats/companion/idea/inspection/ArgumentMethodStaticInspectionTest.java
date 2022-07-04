package eu.andret.ats.companion.idea.inspection;

import com.intellij.codeInsight.daemon.impl.HighlightInfo;
import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase;
import org.junit.Test;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ArgumentMethodStaticInspectionTest extends LightJavaCodeInsightFixtureTestCase {
/*TODO change test names
  test case involving method without @Argument
    */
    public static final String TEST_NAME = "argument.method.static.inspection";

    @Override
    protected String getTestDataPath() {
        return "src/test/testData";
    }

    @Test
    public void testHighlight() {
        //given
        myFixture.configureByFile(TEST_NAME + ".java");
        myFixture.enableInspections(new ArgumentMethodStaticInspection());

        //when
        List<HighlightInfo> highlightInfos = myFixture.doHighlighting();

        //then
        assertFalse(highlightInfos.isEmpty());
        Optional<HighlightInfo> optionalHighlightInfo = highlightInfos.stream()
                .filter(element ->
                        Objects.equals(element.getDescription(), "Method annotated with @Argument annotation cannot use static qualifier"))
                .findAny();
        assertTrue(optionalHighlightInfo.isPresent());
        HighlightInfo highlightInfo = optionalHighlightInfo.get();
        assertEquals(HighlightSeverity.ERROR, highlightInfo.getSeverity());
    }

    @Test
    public void testFix() {
        //given
        myFixture.configureByFile(TEST_NAME + ".java");
        myFixture.enableInspections(new ArgumentMethodStaticInspection());
        final IntentionAction action = myFixture.findSingleIntention("Remove qualifier");
        assertNotNull(action);

        //when
        myFixture.launchAction(action);

        //then
        myFixture.checkResultByFile(TEST_NAME + ".after.java");
    }
}
