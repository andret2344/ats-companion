/*
 * Copyright (c) 2018 Andret Tools System. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.ats.companion.idea.completion;

import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase4;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class ArgumentMapperCompletionContributorTest extends LightJavaCodeInsightFixtureTestCase4 {
	private final String fileName;
	private final String[] result;

	public ArgumentMapperCompletionContributorTest(final String fileName, final String[] result) {
		super(null, "src/test/testData/completion/argument-mapper");
		this.fileName = fileName;
		this.result = result;
	}

	@Parameters
	public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][]{
				{"method-outside-quote.java", new String[]{"\"test\"", "\"value\""}},
				{"method-inside-quote.java", new String[]{"test", "value"}},
				{"parameter-outside-quote.java", new String[]{"\"test\"", "\"value\""}},
				{"parameter-inside-quote.java", new String[]{"test", "value"}}
		});
	}


	@Test
	public void argumentMapperCompletion() {
		// given
		getFixture().configureByFile(fileName);
		getFixture().addClass("package eu.andret.arguments;" +
				"import java.util.function.Function;import java.util.function.Predicate;public class AnnotatedCommand<E extends org.bukkit.plugin.java.JavaPlugin> { " +
				"public <T> void addArgumentMapper(final String s, final Class<T> clazz, final Function<String, T> mapper, final Predicate<T> predicate) {" +
				"}" +
				"}");

		// when
		getFixture().completeBasic();

		// then
		assertThat(getFixture().getLookupElementStrings())
				.containsExactly(result);
	}
}
