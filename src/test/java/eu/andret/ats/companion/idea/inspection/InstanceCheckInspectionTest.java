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

public class InstanceCheckInspectionTest extends LightJavaCodeInsightFixtureTestCase4 {
	public InstanceCheckInspectionTest() {
		super(null, "src/test/testData/inspection");
	}

	@Test
	public void testInstanceCheckPlayerTrue() {
		// given
		getFixture().configureByFile("instance-check-player-true.java");
		getFixture().enableInspections(new InstanceCheckInspection());

		// when
		final List<HighlightInfo> highlightInfos = getFixture().doHighlighting();

		// then
		assertFalse(highlightInfos.isEmpty());
		final Optional<HighlightInfo> optionalHighlightInfo = highlightInfos.stream()
				.filter(element ->
						Objects.equals(element.getDescription(), InstanceCheckInspection.DESCRIPTION_UNUSED))
				.findAny();
		assertTrue(optionalHighlightInfo.isPresent());
		final HighlightInfo highlightInfo = optionalHighlightInfo.get();
		assertEquals(HighlightSeverity.WARNING, highlightInfo.getSeverity());
	}

	@Test
	public void testInstanceCheckPlayerTrueFix() {
		// given
		getFixture().configureByFile("instance-check-player-true.java");
		getFixture().enableInspections(new InstanceCheckInspection());
		final IntentionAction action = getFixture().findSingleIntention(InstanceCheckInspection.RemoveExpressionQuickFix.NAME);
		assertNotNull(action);

		// when
		getFixture().launchAction(action);

		// then
		getFixture().checkResultByFile("instance-check-player-true.after.java");
	}
}