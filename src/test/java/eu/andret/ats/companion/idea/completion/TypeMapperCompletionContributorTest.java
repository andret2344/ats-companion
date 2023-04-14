/*
 * Copyright (c) 2018 Andret Tools System. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.ats.companion.idea.completion;

import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase4;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TypeMapperCompletionContributorTest extends LightJavaCodeInsightFixtureTestCase4 {
	public TypeMapperCompletionContributorTest() {
		super(null, "src/test/testData/completion/type-mapper");
	}

	@Test
	public void typeMapperCompletion() {
		// given
		getFixture().configureByFile("simple.java");
		getFixture().addClass("package eu.andret.arguments;" +
				"import java.util.function.Function;import java.util.function.Predicate;public class AnnotatedCommand<E extends org.bukkit.plugin.java.JavaPlugin> { " +
				"public <T> void addTypeMapper(final Class<T> clazz, final Function<String, T> mapper, final Predicate<T> predicate) {" +
				"}" +
				"}");

		// when
		getFixture().completeBasic();

		// then
		assertThat(getFixture().getLookupElementStrings())
				.containsExactly("Object.class", "Stream.class");
	}
}
