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

public class MapperWithPrimitiveInspectionTest extends LightJavaCodeInsightFixtureTestCase4 {
	public MapperWithPrimitiveInspectionTest() {
		super(null, "src/test/testData/inspection");
	}

	@Test
	public void testPrimitiveParameterWithNoMapperHighlight() {
		// given
		getFixture().configureByFile("primitive-parameter-with-no-mapper.java");
		getFixture().enableInspections(new MapperWithPrimitiveInspection());

		// when
		final List<HighlightInfo> highlightInfos = getFixture().doHighlighting();

		// then
		assertThat(highlightInfos).isNotEmpty();
		final Optional<HighlightInfo> optionalHighlightInfo = highlightInfos.stream()
				.filter(element ->
						Objects.equals(element.getDescription(), MapperWithPrimitiveInspection.DESCRIPTION))
				.findAny();
		assertThat(optionalHighlightInfo).isEmpty();
	}

	@Test
	public void testPrimitiveParameterWithMapperHighlight() {
		// given
		getFixture().configureByFile("primitive-parameter-with-mapper.java");
		getFixture().enableInspections(new MapperWithPrimitiveInspection());

		// when
		final List<HighlightInfo> highlightInfos = getFixture().doHighlighting();

		// then
		assertThat(highlightInfos).isNotEmpty();
		final Optional<HighlightInfo> optionalHighlightInfo = highlightInfos.stream()
				.filter(element ->
						Objects.equals(element.getDescription(), MapperWithPrimitiveInspection.DESCRIPTION))
				.findAny();
		assertThat(optionalHighlightInfo)
				.map(HighlightInfo::getSeverity)
				.contains(HighlightSeverity.ERROR);
	}

	@Test
	public void testPrimitiveParameterWithMapperFixRemoveMapper() {
		// given
		getFixture().configureByFile("primitive-parameter-with-mapper.java");
		getFixture().enableInspections(new MapperWithPrimitiveInspection());
		final IntentionAction action = getFixture().findSingleIntention(MapperWithPrimitiveInspection.RemoveAnnotationQuickFix.NAME);
		assertThat(action).isNotNull();

		// when
		getFixture().launchAction(action);

		// then
		getFixture().checkResultByFile("primitive-parameter-with-no-mapper.java");
	}
}
