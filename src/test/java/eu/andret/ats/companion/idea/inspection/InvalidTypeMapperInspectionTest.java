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
		super(null, "src/test/testData/inspection/invalid-type");
	}

	@Test
	public void methodIncorrectAnnotationArg() {
		// given
		getFixture().configureByFile("method-incorrect-annotation-arg.java");
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
	public void methodCorrectAnnotationArg() {
		// given
		getFixture().configureByFile("method-correct-annotation-arg.java");
		getFixture().addClass("package eu.andret.arguments;\n" +
				"\n" +
				"public class AnnotatedCommand<E extends org.bukkit.plugin.java.JavaPlugin> {\t\n" +
				"\tpublic <T> void addTypeMapper(final Class<T> clazz, java.util.function.Function<String, T> mapper, java.util.function.Predicate<Object> fallbackCondition) {\n" +
				"\t\t\n" +
				"\t}\n" +
				"}");
		getFixture().enableInspections(new InvalidTypeMapperInspection());
//		getFixture().addClass("package eu.andret.arguments;\n" +
//				"\n" +
//				"public class Dupa {\n" +
//				"private void dupa3() {\n" +
//				"\t\tfinal AnnotatedCommand command = CommandManager.registerCommand(this.getClass(), null);\n" +
//				"\t\tcommand.addTypeMapper(java.lang.Object.class, null, null);\n" +
//				"\t}\n" +
//				"}");

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