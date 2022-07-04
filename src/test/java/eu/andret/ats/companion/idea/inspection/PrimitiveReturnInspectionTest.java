/*
 * Copyright Andret Tools System (c) 2018-2022. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.ats.companion.idea.inspection;

import com.intellij.codeInsight.daemon.impl.HighlightInfo;
import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase4;
import org.junit.Test;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PrimitiveReturnInspectionTest extends LightJavaCodeInsightFixtureTestCase4 {
	public PrimitiveReturnInspectionTest() {
		super(null, "src/test/testData/inspection");
	}

	@Test
	public void testPrimitiveReturnHighlight() {
		//given
		getFixture().configureByFile("primitive-return.java");
		getFixture().enableInspections(new PrimitiveReturnInspection());

		//when
		final List<HighlightInfo> highlightInfos = getFixture().doHighlighting();

		//then
		assertFalse(highlightInfos.isEmpty());
		final Optional<HighlightInfo> optionalHighlightInfo = highlightInfos.stream()
				.filter(element ->
						Objects.equals(element.getDescription(), PrimitiveReturnInspection.DESCRIPTION))
				.findAny();
		assertTrue(optionalHighlightInfo.isPresent());
		final HighlightInfo highlightInfo = optionalHighlightInfo.get();
		assertEquals(HighlightSeverity.WARNING, highlightInfo.getSeverity());
	}

	@Test
	public void testPrimitiveReturnFixChangeToString() {
		//given
		getFixture().configureByFile("primitive-return.java");
		getFixture().enableInspections(new PrimitiveReturnInspection());
		final IntentionAction action = getFixture().findSingleIntention(PrimitiveReturnInspection.ChangeToStringQuickFix.NAME);
		assertNotNull(action);

		//when
		getFixture().launchAction(action);

		//then
		getFixture().checkResultByFile("primitive-return.after.java");
	}
}
