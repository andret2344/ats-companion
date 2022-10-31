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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
		assertFalse(highlightInfos.isEmpty());
		final Optional<HighlightInfo> optionalHighlightInfo = highlightInfos.stream()
				.filter(element ->
						Objects.equals(element.getDescription(), InvalidVisibilityInspection.DESCRIPTION))
				.findAny();
		assertFalse(optionalHighlightInfo.isPresent());
	}

	@Test
	public void testArgumentPrivateMethodHighlight() {
		// given
		getFixture().configureByFile("argument-private-method.java");
		getFixture().enableInspections(new InvalidVisibilityInspection());

		// when
		final List<HighlightInfo> highlightInfos = getFixture().doHighlighting();

		// then
		assertFalse(highlightInfos.isEmpty());
		final Optional<HighlightInfo> optionalHighlightInfo = highlightInfos.stream()
				.filter(element ->
						Objects.equals(element.getDescription(), InvalidVisibilityInspection.DESCRIPTION))
				.findAny();
		assertTrue(optionalHighlightInfo.isPresent());
		final HighlightInfo highlightInfo = optionalHighlightInfo.get();
		assertEquals(HighlightSeverity.ERROR, highlightInfo.getSeverity());
	}

	@Test
	public void testArgumentPackagePrivateMethodHighlight() {
		// given
		getFixture().configureByFile("argument-package-private-method.java");
		getFixture().enableInspections(new InvalidVisibilityInspection());

		// when
		final List<HighlightInfo> highlightInfos = getFixture().doHighlighting();

		// then
		assertFalse(highlightInfos.isEmpty());
		final Optional<HighlightInfo> optionalHighlightInfo = highlightInfos.stream()
				.filter(element ->
						Objects.equals(element.getDescription(), InvalidVisibilityInspection.DESCRIPTION))
				.findAny();
		assertTrue(optionalHighlightInfo.isPresent());
		final HighlightInfo highlightInfo = optionalHighlightInfo.get();
		assertEquals(HighlightSeverity.ERROR, highlightInfo.getSeverity());
	}

	@Test
	public void testArgumentProtectedMethodHighlight() {
		// given
		getFixture().configureByFile("argument-protected-method.java");
		getFixture().enableInspections(new InvalidVisibilityInspection());

		// when
		final List<HighlightInfo> highlightInfos = getFixture().doHighlighting();

		// then
		assertFalse(highlightInfos.isEmpty());
		final Optional<HighlightInfo> optionalHighlightInfo = highlightInfos.stream()
				.filter(element ->
						Objects.equals(element.getDescription(), InvalidVisibilityInspection.DESCRIPTION))
				.findAny();
		assertTrue(optionalHighlightInfo.isPresent());
		final HighlightInfo highlightInfo = optionalHighlightInfo.get();
		assertEquals(HighlightSeverity.ERROR, highlightInfo.getSeverity());
	}

	@Test
	public void testArgumentPrivateMethodFix() {
		// given
		getFixture().configureByFile("argument-private-method.java");
		getFixture().enableInspections(new InvalidVisibilityInspection());
		final IntentionAction action = getFixture().findSingleIntention(InvalidVisibilityInspection.ChangeToPublicQuickFix.NAME);
		assertNotNull(action);

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
		assertNotNull(action);

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
		assertNotNull(action);

		// when
		getFixture().launchAction(action);

		// then
		getFixture().checkResultByFile("argument-public-method.java");
	}
}