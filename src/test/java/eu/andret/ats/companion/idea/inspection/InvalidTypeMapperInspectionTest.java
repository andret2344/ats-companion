/*
 * Copyright (c) 2018 Andret Tools System. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.ats.companion.idea.inspection;

import com.intellij.codeInsight.daemon.impl.HighlightInfo;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase4;
import org.junit.Test;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class InvalidTypeMapperInspectionTest extends LightJavaCodeInsightFixtureTestCase4 {
	public InvalidTypeMapperInspectionTest() {
		super(null, "src/test/testData/inspection/invalid-type-mapper");
	}

	@Test
	public void methodIncorrect() {
		// given
		getFixture().configureByFile("method-incorrect.java");
		getFixture().enableInspections(new InvalidTypeMapperInspection());

		// when
		final List<HighlightInfo> highlightInfos = getFixture().doHighlighting();

		// then
		assertThat(highlightInfos).isNotEmpty();
		final Optional<HighlightInfo> optionalHighlightInfo = highlightInfos.stream()
				.filter(element ->
						Objects.equals(element.getDescription(), InvalidTypeMapperInspection.DESCRIPTION))
				.findAny();
		assertThat(optionalHighlightInfo)
				.map(HighlightInfo::getSeverity)
				.contains(HighlightSeverity.ERROR);
	}

	@Test
	public void methodCorrect() {
		// given
		getFixture().configureByFile("method-correct.java");
		getFixture().addClass("package eu.andret.arguments;" +
				"import java.util.function.Function;import java.util.function.Predicate;public class AnnotatedCommand<E extends org.bukkit.plugin.java.JavaPlugin> { " +
				"public <T> void addTypeMapper(final Class<T> clazz, final Function<String, T> mapper, final Predicate<Object> fallbackCondition) {" +
				"}" +
				"}");
		getFixture().enableInspections(new InvalidTypeMapperInspection());

		// when
		final List<HighlightInfo> highlightInfos = getFixture().doHighlighting();

		// then
		assertThat(highlightInfos).isNotEmpty();
		final Optional<HighlightInfo> optionalHighlightInfo = highlightInfos.stream()
				.filter(element ->
						Objects.equals(element.getDescription(), InvalidTypeMapperInspection.DESCRIPTION))
				.findAny();
		assertThat(optionalHighlightInfo).isEmpty();
	}

	@Test
	public void parameterIncorrect() {
		// given
		getFixture().configureByFile("parameter-incorrect.java");
		getFixture().enableInspections(new InvalidTypeMapperInspection());

		// when
		final List<HighlightInfo> highlightInfos = getFixture().doHighlighting();

		// then
		assertThat(highlightInfos).isNotEmpty();
		final Optional<HighlightInfo> optionalHighlightInfo = highlightInfos.stream()
				.filter(element ->
						Objects.equals(element.getDescription(), InvalidTypeMapperInspection.DESCRIPTION))
				.findAny();
		assertThat(optionalHighlightInfo)
				.map(HighlightInfo::getSeverity)
				.contains(HighlightSeverity.ERROR);
	}

	@Test
	public void parameterCorrect() {
		// given
		getFixture().configureByFile("parameter-correct.java");
		getFixture().addClass("package eu.andret.arguments;" +
				"import java.util.function.Function;import java.util.function.Predicate;public class AnnotatedCommand<E extends org.bukkit.plugin.java.JavaPlugin> { " +
				"public <T> void addTypeMapper(final Class<T> clazz, final Function<String, T> mapper, final Predicate<Object> fallbackCondition) {" +
				"}" +
				"}");
		getFixture().enableInspections(new InvalidTypeMapperInspection());

		// when
		final List<HighlightInfo> highlightInfos = getFixture().doHighlighting();

		// then
		assertThat(highlightInfos).isNotEmpty();
		final Optional<HighlightInfo> optionalHighlightInfo = highlightInfos.stream()
				.filter(element ->
						Objects.equals(element.getDescription(), InvalidTypeMapperInspection.DESCRIPTION))
				.findAny();
		assertThat(optionalHighlightInfo).isEmpty();
	}
}
