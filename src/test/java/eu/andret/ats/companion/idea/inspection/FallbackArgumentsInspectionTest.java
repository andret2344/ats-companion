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

public class FallbackArgumentsInspectionTest extends LightJavaCodeInsightFixtureTestCase4 {
	public FallbackArgumentsInspectionTest() {
		super(null, "src/test/testData/inspection");
	}

	@Test
	public void testArgumentFallbackWrongParameters() {
		// given
		getFixture().configureByFile("argument-fallback-wrong-parameters.java");
		getFixture().enableInspections(new FallbackArgumentsInspection());

		// when
		final List<HighlightInfo> highlightInfos = getFixture().doHighlighting();

		// then
		assertFalse(highlightInfos.isEmpty());
		final Optional<HighlightInfo> optionalHighlightInfo = highlightInfos.stream()
				.filter(element ->
						Objects.equals(element.getDescription(), FallbackArgumentsInspection.DESCRIPTION))
				.findAny();
		assertTrue(optionalHighlightInfo.isPresent());
		final HighlightInfo highlightInfo = optionalHighlightInfo.get();
		assertEquals(HighlightSeverity.ERROR, highlightInfo.getSeverity());
	}

	@Test
	public void testTypeFallbackWrongParameters() {
		// given
		getFixture().configureByFile("type-fallback-wrong-parameters.java");
		getFixture().enableInspections(new FallbackArgumentsInspection());

		// when
		final List<HighlightInfo> highlightInfos = getFixture().doHighlighting();

		// then
		assertFalse(highlightInfos.isEmpty());
		final Optional<HighlightInfo> optionalHighlightInfo = highlightInfos.stream()
				.filter(element ->
						Objects.equals(element.getDescription(), FallbackArgumentsInspection.DESCRIPTION))
				.findAny();
		assertTrue(optionalHighlightInfo.isPresent());
		final HighlightInfo highlightInfo = optionalHighlightInfo.get();
		assertEquals(HighlightSeverity.ERROR, highlightInfo.getSeverity());
	}

	@Test
	public void testRemoveParameterQuickFix() {
		// given
		getFixture().configureByFile("argument-with-negative-position.java");
		getFixture().enableInspections(new PositionOutOfBoundsInspection());
		final IntentionAction action = getFixture().findSingleIntention(PositionOutOfBoundsInspection.RemoveParameterQuickFix.NAME);
		assertNotNull(action);

		// when
		getFixture().launchAction(action);

		// then
		getFixture().checkResultByFile("argument-with-negative-position.after.java");
	}
}
