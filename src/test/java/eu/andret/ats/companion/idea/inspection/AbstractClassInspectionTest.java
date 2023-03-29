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

public class AbstractClassInspectionTest extends LightJavaCodeInsightFixtureTestCase4 {
	public AbstractClassInspectionTest() {
		super(null, "src/test/testData/inspection/abstract-class");
	}

	@Test
	public void testNonAbstractClassHighlight() {
		// given
		getFixture().configureByFile("base-command-class.java");
		getFixture().enableInspections(new AbstractClassInspection());

		// when
		final List<HighlightInfo> highlightInfos = getFixture().doHighlighting();

		// then
		assertThat(highlightInfos).isNotEmpty();
		final Optional<HighlightInfo> optionalHighlightInfo = highlightInfos.stream()
				.filter(element ->
						Objects.equals(element.getDescription(), AbstractClassInspection.DESCRIPTION))
				.findAny();
		assertThat(optionalHighlightInfo).isEmpty();
	}

	@Test
	public void testAbstractClassHighlight() {
		// given
		getFixture().configureByFile("base-command-abstract-class.java");
		getFixture().enableInspections(new AbstractClassInspection());

		// when
		final List<HighlightInfo> highlightInfos = getFixture().doHighlighting();

		// then
		assertThat(highlightInfos).isNotEmpty();
		final Optional<HighlightInfo> optionalHighlightInfo = highlightInfos.stream()
				.filter(element ->
						Objects.equals(element.getDescription(), AbstractClassInspection.DESCRIPTION))
				.findAny();
		assertThat(optionalHighlightInfo)
				.map(HighlightInfo::getSeverity)
				.contains(HighlightSeverity.ERROR);
	}

	@Test
	public void testAbstractClassQuickFix() {
		// given
		getFixture().configureByFile("base-command-abstract-class.java");
		getFixture().enableInspections(new AbstractClassInspection());
		final IntentionAction action = getFixture().findSingleIntention(AbstractClassInspection.RemoveModifierQuickFix.NAME);
		assertThat(action).isNotNull();

		// when
		getFixture().launchAction(action);

		// then
		getFixture().checkResultByFile("base-command-abstract-class.after.java");
	}


	@Test
	public void testAbstractClassNoAnnotation() {
		// given
		getFixture().configureByFile("base-command-no-annotation-class.java");
		getFixture().enableInspections(new AbstractClassInspection());

		// when
		final List<HighlightInfo> highlightInfos = getFixture().doHighlighting();

		// then
		assertThat(highlightInfos).isNotEmpty();
		final Optional<HighlightInfo> optionalHighlightInfo = highlightInfos.stream()
				.filter(element ->
						Objects.equals(element.getDescription(), AbstractClassInspection.DESCRIPTION))
				.findAny();
		assertThat(optionalHighlightInfo).isEmpty();
	}
}
