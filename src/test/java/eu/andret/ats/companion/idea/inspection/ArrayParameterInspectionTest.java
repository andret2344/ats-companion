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

public class ArrayParameterInspectionTest extends LightJavaCodeInsightFixtureTestCase4 {
	public ArrayParameterInspectionTest() {
		super(null, "src/test/testData/inspection/array-parameter");
	}

	@Test
	public void testArrayParameterVarArg() {
		// given
		getFixture().configureByFile("vararg.java");
		getFixture().enableInspections(new ArrayParameterInspection());

		// when
		final List<HighlightInfo> highlightInfos = getFixture().doHighlighting();

		// then
		assertThat(highlightInfos).isNotEmpty();
		final Optional<HighlightInfo> optionalHighlightInfo = highlightInfos.stream()
				.filter(element ->
						Objects.equals(element.getDescription(), ArrayParameterInspection.DESCRIPTION))
				.findAny();
		assertThat(optionalHighlightInfo).isEmpty();
	}

	@Test
	public void testArrayParameterNonVarArg() {
		// given
		getFixture().configureByFile("non-vararg.java");
		getFixture().enableInspections(new ArrayParameterInspection());

		// when
		final List<HighlightInfo> highlightInfos = getFixture().doHighlighting();

		// then
		assertThat(highlightInfos).isNotEmpty();
		final Optional<HighlightInfo> optionalHighlightInfo = highlightInfos.stream()
				.filter(element ->
						Objects.equals(element.getDescription(), ArrayParameterInspection.DESCRIPTION))
				.findAny();
		assertThat(optionalHighlightInfo)
				.map(HighlightInfo::getSeverity)
				.contains(HighlightSeverity.ERROR);
	}

	@Test
	public void testArrayParameterFixToVarArg() {
		// given
		getFixture().configureByFile("brackets.java");
		getFixture().enableInspections(new ArrayParameterInspection());
		final IntentionAction action = getFixture().findSingleIntention(ArrayParameterInspection.ChangeToVarargQuickFix.NAME);
		assertThat(action).isNotNull();

		// when
		getFixture().launchAction(action);

		// then
		getFixture().checkResultByFile("fix-to-vararg.after.java");
	}

	@Test
	public void testArrayParameterFixToVariable() {
		// given
		getFixture().configureByFile("brackets.java");
		getFixture().enableInspections(new ArrayParameterInspection());
		final IntentionAction action = getFixture().findSingleIntention(ArrayParameterInspection.ConvertToSimpleVariableQuickFix.NAME);
		assertThat(action).isNotNull();

		// when
		getFixture().launchAction(action);

		// then
		getFixture().checkResultByFile("fix-to-variable.after.java");
	}
}
