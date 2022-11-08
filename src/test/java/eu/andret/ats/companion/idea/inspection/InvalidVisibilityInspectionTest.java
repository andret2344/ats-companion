/*
 * Copyright Andret Tools System (c) 2018-2022. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.ats.companion.idea.inspection;

import com.intellij.codeInsight.daemon.impl.HighlightInfo;
import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.testFramework.Parameterized;
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase4;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class InvalidVisibilityInspectionTest extends LightJavaCodeInsightFixtureTestCase4 {
	public InvalidVisibilityInspectionTest() {
		super(null, "src/test/testData/inspection");
	}

	@Test
	public void testArgumentPublicMethodHighlight() {
		// given
		getFixture().configureByFile("argument-public-method.java");
		getFixture().enableInspections(new InvalidVisibilityInspection());

		// when
		final List<HighlightInfo> highlightInfos = getFixture().doHighlighting();

		// then
		assertThat(highlightInfos).isNotEmpty();
		final Optional<HighlightInfo> optionalHighlightInfo = highlightInfos.stream()
				.filter(element ->
						Objects.equals(element.getDescription(), InvalidVisibilityInspection.DESCRIPTION))
				.findAny();
		assertThat(optionalHighlightInfo).isEmpty();
	}

	@Test
	public void testArgumentPrivateMethodHighlight() {
		// given
		getFixture().configureByFile("argument-private-method.java");
		getFixture().enableInspections(new InvalidVisibilityInspection());

		// when
		final List<HighlightInfo> highlightInfos = getFixture().doHighlighting();

		// then
		assertThat(highlightInfos).isNotEmpty();
		final Optional<HighlightInfo> optionalHighlightInfo = highlightInfos.stream()
				.filter(element ->
						Objects.equals(element.getDescription(), InvalidVisibilityInspection.DESCRIPTION))
				.findAny();
		assertThat(optionalHighlightInfo)
				.map(HighlightInfo::getSeverity)
				.contains(HighlightSeverity.ERROR);
	}

	@Test
	public void testArgumentPackagePrivateMethodHighlight() {
		// given
		getFixture().configureByFile("argument-package-private-method.java");
		getFixture().enableInspections(new InvalidVisibilityInspection());

		// when
		final List<HighlightInfo> highlightInfos = getFixture().doHighlighting();

		// then
		assertThat(highlightInfos).isNotEmpty();
		final Optional<HighlightInfo> optionalHighlightInfo = highlightInfos.stream()
				.filter(element ->
						Objects.equals(element.getDescription(), InvalidVisibilityInspection.DESCRIPTION))
				.findAny();
		assertThat(optionalHighlightInfo)
				.map(HighlightInfo::getSeverity)
				.contains(HighlightSeverity.ERROR);
	}

	@Test
	public void testArgumentProtectedMethodHighlight() {
		// given
		getFixture().configureByFile("argument-protected-method.java");
		getFixture().enableInspections(new InvalidVisibilityInspection());

		// when
		final List<HighlightInfo> highlightInfos = getFixture().doHighlighting();

		// then
		assertThat(highlightInfos).isNotEmpty();
		final Optional<HighlightInfo> optionalHighlightInfo = highlightInfos.stream()
				.filter(element ->
						Objects.equals(element.getDescription(), InvalidVisibilityInspection.DESCRIPTION))
				.findAny();
		assertThat(optionalHighlightInfo)
				.map(HighlightInfo::getSeverity)
				.contains(HighlightSeverity.ERROR);
	}

	@Test
	public void testArgumentPrivateMethodFix() {
		// given
		getFixture().configureByFile("argument-private-method.java");
		getFixture().enableInspections(new InvalidVisibilityInspection());
		final IntentionAction action = getFixture().findSingleIntention(InvalidVisibilityInspection.ChangeToPublicQuickFix.NAME);
		assertThat(action).isNotNull();

		// when
		getFixture().launchAction(action);

		// then
		getFixture().checkResultByFile("argument-public-method.java");
	}

	@Test
	public void testArgumentPackagePrivateMethodFix() {
		// given
		getFixture().configureByFile("argument-package-private-method.java");
		getFixture().enableInspections(new InvalidVisibilityInspection());
		final IntentionAction action = getFixture().findSingleIntention(InvalidVisibilityInspection.AddPublicQuickFix.NAME);
		assertThat(action).isNotNull();

		// when
		getFixture().launchAction(action);

		// then
		getFixture().checkResultByFile("argument-package-private-method.after.java");
	}

	@Test
	public void testArgumentProtectedMethodFix() {
		// given
		getFixture().configureByFile("argument-protected-method.java");
		getFixture().enableInspections(new InvalidVisibilityInspection());
		final IntentionAction action = getFixture().findSingleIntention(InvalidVisibilityInspection.ChangeToPublicQuickFix.NAME);
		assertThat(action).isNotNull();

		// when
		getFixture().launchAction(action);

		// then
		getFixture().checkResultByFile("argument-public-method.java");
	}
}
