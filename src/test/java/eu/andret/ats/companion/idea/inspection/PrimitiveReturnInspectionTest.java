package eu.andret.ats.companion.idea.inspection;

import com.intellij.codeInsight.daemon.impl.HighlightInfo;
import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase;
import org.junit.Test;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class PrimitiveReturnInspectionTest extends LightJavaCodeInsightFixtureTestCase {

    public static final String TEST_NAME = "primitive.return.static.inspection";

    @Override
    protected String getTestDataPath() {
        return "src/test/testData";
    }

    @Test
    public void testHighlight() {
        //given
        myFixture.configureByFile(TEST_NAME + ".java");
        myFixture.enableInspections(new PrimitiveReturnInspection());

        //when
        List<HighlightInfo> highlightInfos = myFixture.doHighlighting();

        //then
        assertFalse(highlightInfos.isEmpty());
        Optional<HighlightInfo> optionalHighlightInfo = highlightInfos.stream()
                .filter(element ->
                        Objects.equals(element.getDescription(), "Method probably shouldn't return a primitive"))
                .findAny();
        assertTrue(optionalHighlightInfo.isPresent());
        HighlightInfo highlightInfo = optionalHighlightInfo.get();
        assertEquals(HighlightSeverity.WARNING, highlightInfo.getSeverity());
    }

    @Test
    public void testFix() {
        //given
        myFixture.configureByFile(TEST_NAME + ".java");
        myFixture.enableInspections(new PrimitiveReturnInspection());
        final IntentionAction action = myFixture.findSingleIntention("Change to String");
        assertNotNull(action);

        //when
        myFixture.launchAction(action);

        //then
        myFixture.checkResultByFile(TEST_NAME + ".after.java");
    }
}
