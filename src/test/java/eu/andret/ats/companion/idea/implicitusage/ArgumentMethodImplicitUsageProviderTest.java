/*
 * Copyright (c) 2018 Andret Tools System. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.ats.companion.idea.implicitusage;

import com.intellij.codeInsight.daemon.impl.HighlightInfo;
import com.intellij.codeInspection.deadCode.UnusedDeclarationInspectionBase;
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase4;
import org.junit.Test;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class ArgumentMethodImplicitUsageProviderTest extends LightJavaCodeInsightFixtureTestCase4 {
	private static final String DESCRIPTION_METHOD = "Method 'unusedMethod()' is never used";
	private static final String DESCRIPTION_PARAMETER = "Parameter 'x' is never used";

	public ArgumentMethodImplicitUsageProviderTest() {
		super(null, "src/test/testData/implicitusage");
	}

	@Test
	public void implicitUsageMethod() {
		// given
		getFixture().configureByFile("argument-method-implicit-usage.java");
		getFixture().enableInspections(new UnusedDeclarationInspectionBase(true));

		// when
		final List<HighlightInfo> highlightInfos = getFixture().doHighlighting();

		// then
		final Optional<HighlightInfo> optionalHighlightInfo = highlightInfos.stream()
				.filter(element -> Objects.equals(element.getDescription(), DESCRIPTION_METHOD))
				.findAny();
		assertThat(optionalHighlightInfo).isEmpty();
	}

	@Test
	public void implicitUsageMethodWithoutAnnotation() {
		// given
		getFixture().configureByFile("argument-method-implicit-usage-no-annotation.java");
		getFixture().enableInspections(new UnusedDeclarationInspectionBase(true));

		// when
		final List<HighlightInfo> highlightInfos = getFixture().doHighlighting();

		// then
		final Optional<HighlightInfo> optionalHighlightInfo = highlightInfos.stream()
				.filter(element -> Objects.equals(element.getDescription(), DESCRIPTION_METHOD))
				.findAny();
		assertThat(optionalHighlightInfo).isPresent();
	}

	@Test
	public void implicitUsageParameter() {
		// given
		getFixture().configureByFile("argument-parameter-implicit-usage.java");
		getFixture().enableInspections(new UnusedDeclarationInspectionBase(true));

		// when
		final List<HighlightInfo> highlightInfos = getFixture().doHighlighting();

		// then
		final Optional<HighlightInfo> optionalHighlightInfo = highlightInfos.stream()
				.filter(element -> Objects.equals(element.getDescription(), DESCRIPTION_PARAMETER))
				.findAny();
		assertThat(optionalHighlightInfo).isEmpty();
	}

	@Test
	public void implicitUsageParameterWithoutAnnotation() {
		// given
		getFixture().configureByFile("argument-parameter-implicit-usage-no-annotation.java");
		getFixture().enableInspections(new UnusedDeclarationInspectionBase(true));

		// when
		final List<HighlightInfo> highlightInfos = getFixture().doHighlighting();

		// then
		final Optional<HighlightInfo> optionalHighlightInfo = highlightInfos.stream()
				.filter(element -> Objects.equals(element.getDescription(), DESCRIPTION_PARAMETER))
				.findAny();
		assertThat(optionalHighlightInfo).isPresent();
	}
}
