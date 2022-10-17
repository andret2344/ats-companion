/*
 * Copyright Andret Tools System (c) 2018-2022. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.ats.companion.idea.implicitusage;

import com.intellij.codeInsight.daemon.impl.HighlightInfo;
import com.intellij.codeInspection.deadCode.UnusedDeclarationInspectionBase;
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase4;
import org.junit.Test;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ArgumentMethodImplicitUsageProviderTest extends LightJavaCodeInsightFixtureTestCase4 {
	private static final String DESCRIPTION = "Method 'unusedMethod()' is never used";

	public ArgumentMethodImplicitUsageProviderTest() {
		super(null, "src/test/testData/implicitusage");
	}

	@Test
	public void testImplicitUsage() {
		// given
		getFixture().configureByFile("argument-method-implicit-usage.java");
		getFixture().enableInspections(new UnusedDeclarationInspectionBase(true));

		// when
		final List<HighlightInfo> highlightInfos = getFixture().doHighlighting();

		// then
		final Optional<HighlightInfo> optionalHighlightInfo = highlightInfos.stream()
				.filter(element -> Objects.equals(element.getDescription(), DESCRIPTION))
				.findAny();
		assertFalse(optionalHighlightInfo.isPresent());
	}

	@Test
	public void testImplicitUsageWithoutAnnotation() {
		// given
		getFixture().configureByFile("argument-method-implicit-usage-no-annotation.java");
		getFixture().enableInspections(new UnusedDeclarationInspectionBase(true));

		// when
		final List<HighlightInfo> highlightInfos = getFixture().doHighlighting();

		// then
		final Optional<HighlightInfo> optionalHighlightInfo = highlightInfos.stream()
				.filter(element -> Objects.equals(element.getDescription(), DESCRIPTION))
				.findAny();
		assertTrue(optionalHighlightInfo.isPresent());
	}
}
