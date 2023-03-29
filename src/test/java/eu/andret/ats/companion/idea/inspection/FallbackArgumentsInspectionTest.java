/*
 * Copyright Andret Tools System (c) 2018-2023. Copying and modifying allowed only keeping git link reference.
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

public class FallbackArgumentsInspectionTest extends LightJavaCodeInsightFixtureTestCase4 {
	public FallbackArgumentsInspectionTest() {
		super(null, "src/test/testData/inspection/fallback-arguments");
	}

	@Test
	public void testArgumentFallbackWrongParameters() {
		// given
		getFixture().configureByFile("argument-wrong-parameters.java");
		getFixture().enableInspections(new FallbackArgumentsInspection());

		// when
		final List<HighlightInfo> highlightInfos = getFixture().doHighlighting();

		// then
		assertThat(highlightInfos).isNotEmpty();
		final Optional<HighlightInfo> optionalHighlightInfo = highlightInfos.stream()
				.filter(element ->
						Objects.equals(element.getDescription(), FallbackArgumentsInspection.DESCRIPTION))
				.findAny();
		assertThat(optionalHighlightInfo)
				.map(HighlightInfo::getSeverity)
				.contains(HighlightSeverity.ERROR);
	}

	@Test
	public void testArgumentFallbackWrongParametersFix() {
		// given
		getFixture().configureByFile("argument-wrong-parameters.java");
		getFixture().enableInspections(new FallbackArgumentsInspection());
		final IntentionAction action = getFixture()
				.findSingleIntention(FallbackArgumentsInspection.ChangeParametersQuickFix.NAME.formatted("test"));
		assertThat(action).isNotNull();

		// when
		getFixture().launchAction(action);

		// then
		getFixture().checkResultByFile("argument-wrong-parameters.after.java");
	}

	@Test
	public void testArgumentFallbackCorrectParameters() {
		// given
		getFixture().configureByFile("argument-wrong-parameters.after.java");
		getFixture().enableInspections(new FallbackArgumentsInspection());

		// when
		final List<HighlightInfo> highlightInfos = getFixture().doHighlighting();

		// then
		assertThat(highlightInfos).isNotEmpty();
		final Optional<HighlightInfo> optionalHighlightInfo = highlightInfos.stream()
				.filter(element ->
						Objects.equals(element.getDescription(), FallbackArgumentsInspection.DESCRIPTION))
				.findAny();
		assertThat(optionalHighlightInfo).isEmpty();
	}

	@Test
	public void testTypeFallbackWrongParameters() {
		// given
		getFixture().configureByFile("type-wrong-parameters.java");
		getFixture().enableInspections(new FallbackArgumentsInspection());

		// when
		final List<HighlightInfo> highlightInfos = getFixture().doHighlighting();

		// then
		assertThat(highlightInfos).isNotEmpty();
		final Optional<HighlightInfo> optionalHighlightInfo = highlightInfos.stream()
				.filter(element ->
						Objects.equals(element.getDescription(), FallbackArgumentsInspection.DESCRIPTION))
				.findAny();
		assertThat(optionalHighlightInfo)
				.map(HighlightInfo::getSeverity)
				.contains(HighlightSeverity.ERROR);
	}

	@Test
	public void testTypeFallbackWrongParametersFix() {
		// given
		getFixture().configureByFile("type-wrong-parameters.java");
		getFixture().enableInspections(new FallbackArgumentsInspection());
		final IntentionAction action = getFixture()
				.findSingleIntention(FallbackArgumentsInspection.ChangeParametersQuickFix.NAME.formatted("world"));
		assertThat(action).isNotNull();

		// when
		getFixture().launchAction(action);

		// then
		getFixture().checkResultByFile("type-wrong-parameters.after.java");
	}

	@Test
	public void testTypeFallbackCorrectParameters() {
		// given
		getFixture().configureByFile("type-wrong-parameters.after.java");
		getFixture().enableInspections(new FallbackArgumentsInspection());

		// when
		final List<HighlightInfo> highlightInfos = getFixture().doHighlighting();

		// then
		assertThat(highlightInfos).isNotEmpty();
		final Optional<HighlightInfo> optionalHighlightInfo = highlightInfos.stream()
				.filter(element ->
						Objects.equals(element.getDescription(), FallbackArgumentsInspection.DESCRIPTION))
				.findAny();
		assertThat(optionalHighlightInfo).isEmpty();
	}
}
