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

public class PrimitiveReturnInspectionTest extends LightJavaCodeInsightFixtureTestCase4 {
	public PrimitiveReturnInspectionTest() {
		super(null, "src/test/testData/inspection/primitive-return");
	}

	@Test
	public void primitiveReturnHighlight() {
		// given
		getFixture().configureByFile("sample.java");
		getFixture().enableInspections(new PrimitiveReturnInspection());

		// when
		final List<HighlightInfo> highlightInfos = getFixture().doHighlighting();

		// then
		assertThat(highlightInfos).isNotEmpty();
		final Optional<HighlightInfo> optionalHighlightInfo = highlightInfos.stream()
				.filter(element ->
						Objects.equals(element.getDescription(), PrimitiveReturnInspection.DESCRIPTION))
				.findAny();
		assertThat(optionalHighlightInfo)
				.map(HighlightInfo::getSeverity)
				.contains(HighlightSeverity.WARNING);
	}

	@Test
	public void primitiveReturnFixChangeToString() {
		// given
		getFixture().configureByFile("sample.java");
		getFixture().enableInspections(new PrimitiveReturnInspection());
		final IntentionAction action = getFixture().findSingleIntention(PrimitiveReturnInspection.ChangeToStringQuickFix.NAME);
		assertThat(action).isNotNull();

		// when
		getFixture().launchAction(action);

		// then
		getFixture().checkResultByFile("sample.after.java");
	}
}
