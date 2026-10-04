package eu.andret.ats.companion.idea.inspection;

import com.intellij.codeInsight.daemon.impl.HighlightInfo;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase4;
import org.junit.Test;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class InvalidArgumentCompleterInspectionTest extends LightJavaCodeInsightFixtureTestCase4 {
	public InvalidArgumentCompleterInspectionTest() {
		super(null, "src/test/testData/inspection/invalid-argument-completer");
	}

	@Test
	public void parameterIncorrect() {
		// given
		getFixture().configureByFile("parameter-incorrect.java");
		getFixture().enableInspections(new InvalidArgumentCompleterInspection());

		// when
		final List<HighlightInfo> highlightInfos = getFixture().doHighlighting();

		// then
		assertThat(highlightInfos).isNotEmpty();
		final Optional<HighlightInfo> optionalHighlightInfo = highlightInfos.stream()
				.filter(element ->
						Objects.equals(element.getDescription(), InvalidArgumentCompleterInspection.DESCRIPTION))
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
				"public <T> void addArgumentCompleter(final String s, final Class<T> clazz, final Function<String, T> mapper) {" +
				"}" +
				"}");
		getFixture().enableInspections(new InvalidArgumentCompleterInspection());

		// when
		final List<HighlightInfo> highlightInfos = getFixture().doHighlighting();

		// then
		assertThat(highlightInfos).isNotEmpty();
		final Optional<HighlightInfo> optionalHighlightInfo = highlightInfos.stream()
				.filter(element ->
						Objects.equals(element.getDescription(), InvalidArgumentCompleterInspection.DESCRIPTION))
				.findAny();
		assertThat(optionalHighlightInfo).isEmpty();
	}
}
