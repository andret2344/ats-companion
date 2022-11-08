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

import static org.assertj.core.api.Assertions.assertThat;

public class PositionOutOfBoundsInspectionTest extends LightJavaCodeInsightFixtureTestCase4 {
	public PositionOutOfBoundsInspectionTest() {
		super(null, "src/test/testData/inspection");
	}

	@Test
	public void testExceededPosition() {
		// given
		getFixture().configureByFile("argument-with-exceeded-position.java");
		getFixture().enableInspections(new PositionOutOfBoundsInspection());

		// when
		final List<HighlightInfo> highlightInfos = getFixture().doHighlighting();

		// then
		assertThat(highlightInfos).isNotEmpty();
		final Optional<HighlightInfo> optionalHighlightInfo = highlightInfos.stream()
				.filter(element ->
						Objects.equals(element.getDescription(), PositionOutOfBoundsInspection.DESCRIPTION))
				.findAny();
		assertThat(optionalHighlightInfo)
				.map(HighlightInfo::getSeverity)
				.contains(HighlightSeverity.ERROR);
	}

	@Test
	public void testNegativePosition() {
		// given
		getFixture().configureByFile("argument-with-negative-position.java");
		getFixture().enableInspections(new PositionOutOfBoundsInspection());

		// when
		final List<HighlightInfo> highlightInfos = getFixture().doHighlighting();

		// then
		assertThat(highlightInfos).isNotEmpty();
		final Optional<HighlightInfo> optionalHighlightInfo = highlightInfos.stream()
				.filter(element ->
						Objects.equals(element.getDescription(), PositionOutOfBoundsInspection.DESCRIPTION))
				.findAny();
		assertThat(optionalHighlightInfo)
				.map(HighlightInfo::getSeverity)
				.contains(HighlightSeverity.ERROR);
	}

	@Test
	public void testCorrectPosition() {
		// given
		getFixture().configureByFile("argument-with-correct-position.java");
		getFixture().enableInspections(new PositionOutOfBoundsInspection());

		// when
		final List<HighlightInfo> highlightInfos = getFixture().doHighlighting();

		// then
		assertThat(highlightInfos).isNotEmpty();
		final Optional<HighlightInfo> optionalHighlightInfo = highlightInfos.stream()
				.filter(element ->
						Objects.equals(element.getDescription(), PositionOutOfBoundsInspection.DESCRIPTION))
				.findAny();
		assertThat(optionalHighlightInfo).isEmpty();
	}

	@Test
	public void testRemoveParameterQuickFix() {
		// given
		getFixture().configureByFile("argument-with-negative-position.java");
		getFixture().enableInspections(new PositionOutOfBoundsInspection());
		final IntentionAction action = getFixture().findSingleIntention(PositionOutOfBoundsInspection.RemoveParameterQuickFix.NAME);
		assertThat(action).isNotNull();

		// when
		getFixture().launchAction(action);

		// then
		getFixture().checkResultByFile("argument-with-negative-position.after.java");
	}
}
