package eu.andret.ats.companion.idea.completion;

import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase4;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ArgumentCompleterCompletionContributorTest extends LightJavaCodeInsightFixtureTestCase4 {
	public ArgumentCompleterCompletionContributorTest() {
		super(null, "src/test/testData/completion/argument-completer");
	}

	@Test
	public void argumentCompleterCompletionInsideQuote() {
		// given
		getFixture().configureByFile("inside-quote.java");
		getFixture().addClass("package eu.andret.arguments;" +
				"import java.util.function.Function;import java.util.function.Predicate;public class AnnotatedCommand<E extends org.bukkit.plugin.java.JavaPlugin> { " +
				"public <T> void addArgumentCompleter(final String s, final Class<T> clazz, final Function<String, T> mapper) {" +
				"}" +
				"}");

		// when
		getFixture().completeBasic();

		// then
		assertThat(getFixture().getLookupElementStrings())
				.containsExactly("test", "value");
	}

	@Test
	public void argumentCompleterCompletionOutsideQuote() {
		// given
		getFixture().configureByFile("outside-quote.java");
		getFixture().addClass("package eu.andret.arguments;" +
				"import java.util.function.Function;import java.util.function.Predicate;public class AnnotatedCommand<E extends org.bukkit.plugin.java.JavaPlugin> { " +
				"public <T> void addArgumentCompleter(final String s, final Class<T> clazz, final Function<String, T> mapper) {" +
				"}" +
				"}");

		// when
		getFixture().completeBasic();

		// then
		assertThat(getFixture().getLookupElementStrings())
				.containsExactly("\"test\"", "\"value\"");
	}
}
