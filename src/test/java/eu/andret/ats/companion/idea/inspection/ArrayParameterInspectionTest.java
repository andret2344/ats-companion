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

import static org.junit.jupiter.api.Assertions.*;

public class ArrayParameterInspectionTest extends LightJavaCodeInsightFixtureTestCase4 {
	public ArrayParameterInspectionTest() {
		super(null, "src/test/testData/inspection");
	}

	@Test
	public void testArrayParameterVarArg() {
		// given
		getFixture().configureByFile("array-parameter-vararg.java");
		getFixture().enableInspections(new ArrayParameterInspection());

		// when
		final List<HighlightInfo> highlightInfos = getFixture().doHighlighting();

		// then
		assertFalse(highlightInfos.isEmpty());
		final Optional<HighlightInfo> optionalHighlightInfo = highlightInfos.stream()
				.filter(element ->
						Objects.equals(element.getDescription(), ArrayParameterInspection.DESCRIPTION))
				.findAny();
		assertFalse(optionalHighlightInfo.isPresent());
	}

	@Test
	public void testArrayParameterNonVarArg() {
		// given
		getFixture().configureByFile("array-parameter-non-vararg.java");
		getFixture().enableInspections(new ArrayParameterInspection());

		// when
		final List<HighlightInfo> highlightInfos = getFixture().doHighlighting();

		// then
		assertFalse(highlightInfos.isEmpty());
		final Optional<HighlightInfo> optionalHighlightInfo = highlightInfos.stream()
				.filter(element ->
						Objects.equals(element.getDescription(), ArrayParameterInspection.DESCRIPTION))
				.findAny();
		assertTrue(optionalHighlightInfo.isPresent());
		final HighlightInfo highlightInfo = optionalHighlightInfo.get();
		assertEquals(HighlightSeverity.ERROR, highlightInfo.getSeverity());
	}

	@Test
	public void testArrayParameterFixToVarArg() {
		// given
		getFixture().configureByFile("array-parameter-brackets.java");
		getFixture().enableInspections(new ArrayParameterInspection());
		final IntentionAction action = getFixture().findSingleIntention(ArrayParameterInspection.ChangeToVarargQuickFix.NAME);
		assertNotNull(action);

		// when
		getFixture().launchAction(action);

		// then
		getFixture().checkResultByFile("array-parameter-fix-to-vararg.after.java");
	}

	@Test
	public void testArrayParameterFixToVariable() {
		// given
		getFixture().configureByFile("array-parameter-brackets.java");
		getFixture().enableInspections(new ArrayParameterInspection());
		final IntentionAction action = getFixture().findSingleIntention(ArrayParameterInspection.ConvertToSimpleVariableQuickFix.NAME);
		assertNotNull(action);

		// when
		getFixture().launchAction(action);

		// then
		getFixture().checkResultByFile("array-parameter-fix-to-variable.after.java");
	}
}