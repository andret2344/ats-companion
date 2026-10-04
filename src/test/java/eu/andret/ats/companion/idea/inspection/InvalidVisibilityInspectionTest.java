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

public class InvalidVisibilityInspectionTest extends LightJavaCodeInsightFixtureTestCase4 {
	public InvalidVisibilityInspectionTest() {
		super(null, "src/test/testData/inspection/invalid-visibility");
	}

	@Test
	public void argumentPublicMethodHighlight() {
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
	public void argumentPrivateMethodHighlight() {
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
	public void argumentPackagePrivateMethodHighlight() {
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
	public void argumentProtectedMethodHighlight() {
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
	public void argumentPrivateMethodFix() {
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
	public void argumentPackagePrivateMethodFix() {
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
	public void argumentProtectedMethodFix() {
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
