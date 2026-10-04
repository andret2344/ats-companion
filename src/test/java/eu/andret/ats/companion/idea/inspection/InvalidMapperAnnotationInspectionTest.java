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

public class InvalidMapperAnnotationInspectionTest extends LightJavaCodeInsightFixtureTestCase4 {
	public InvalidMapperAnnotationInspectionTest() {
		super(null, "src/test/testData/inspection/invalid-mapper-annotation");
	}

	@Test
	public void annotationPresentHighlight() {
		// given
		getFixture().configureByFile("annotation-present.java");
		getFixture().enableInspections(new InvalidMapperAnnotationInspection());

		// when
		final List<HighlightInfo> highlightInfos = getFixture().doHighlighting();

		// then
		assertThat(highlightInfos).isNotEmpty();
		final Optional<HighlightInfo> optionalHighlightInfo = highlightInfos.stream()
				.filter(element ->
						Objects.equals(element.getDescription(), InvalidMapperAnnotationInspection.DESCRIPTION))
				.findAny();
		assertThat(optionalHighlightInfo).isEmpty();
	}

	@Test
	public void annotationAbsentHighlight() {
		// given
		getFixture().configureByFile("annotation-absent.java");
		getFixture().enableInspections(new InvalidMapperAnnotationInspection());

		// when
		final List<HighlightInfo> highlightInfos = getFixture().doHighlighting();

		// then
		assertThat(highlightInfos).isNotEmpty();
		final Optional<HighlightInfo> optionalHighlightInfo = highlightInfos.stream()
				.filter(element ->
						Objects.equals(element.getDescription(), InvalidMapperAnnotationInspection.DESCRIPTION))
				.findAny();
		assertThat(optionalHighlightInfo)
				.map(HighlightInfo::getSeverity)
				.contains(HighlightSeverity.ERROR);
	}

	@Test
	public void annotationAbsentQuickFix() {
		// given
		getFixture().configureByFile("annotation-absent.java");
		getFixture().enableInspections(new InvalidMapperAnnotationInspection());
		final IntentionAction action = getFixture().findSingleIntention(InvalidMapperAnnotationInspection.RemoveAnnotationQuickFix.NAME);
		assertThat(action).isNotNull();

		// when
		getFixture().launchAction(action);

		// then
		getFixture().checkResultByFile("annotation-absent.after.java");
	}
}
