/*
 * Copyright (c) 2018 Andret Tools System. Copying and modifying allowed only keeping git link reference.
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

public class ArgumentMethodStaticInspectionTest extends LightJavaCodeInsightFixtureTestCase4 {
	public ArgumentMethodStaticInspectionTest() {
		super(null, "src/test/testData/inspection/argument-method-static");
	}

	@Test
	public void argumentMethodStaticHighlight() {
		// given
		getFixture().configureByFile("argument.java");
		getFixture().enableInspections(new ArgumentMethodStaticInspection());

		// when
		final List<HighlightInfo> highlightInfos = getFixture().doHighlighting();

		// then
		assertThat(highlightInfos).isNotEmpty();
		final Optional<HighlightInfo> optionalHighlightInfo = highlightInfos.stream()
				.filter(element ->
						Objects.equals(element.getDescription(), ArgumentMethodStaticInspection.DESCRIPTION))
				.findAny();
		assertThat(optionalHighlightInfo)
				.map(HighlightInfo::getSeverity)
				.contains(HighlightSeverity.ERROR);
	}

	@Test
	public void argumentMethodStaticFixRemoveQualifier() {
		// given
		getFixture().configureByFile("argument.java");
		getFixture().enableInspections(new ArgumentMethodStaticInspection());
		final IntentionAction action = getFixture().findSingleIntention(ArgumentMethodStaticInspection.RemoveQualifierQuickFix.NAME);
		assertThat(action).isNotNull();

		// when
		getFixture().launchAction(action);

		// then
		getFixture().checkResultByFile("argument.after.java");
	}

	@Test
	public void regularMethodStaticHighlight() {
		// given
		getFixture().configureByFile("regular.java");
		getFixture().enableInspections(new ArgumentMethodStaticInspection());

		// when
		final List<HighlightInfo> highlightInfos = getFixture().doHighlighting();

		// then
		assertThat(highlightInfos).isNotEmpty();
		final Optional<HighlightInfo> optionalHighlightInfo = highlightInfos.stream()
				.filter(element ->
						Objects.equals(element.getDescription(), ArgumentMethodStaticInspection.DESCRIPTION))
				.findAny();
		assertThat(optionalHighlightInfo).isEmpty();
	}
}
