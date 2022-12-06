/*
 * Copyright Andret Tools System (c) 2018-2022. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.ats.companion.idea.completion;

import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase4;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ArgumentPositionCompletionContributorTest extends LightJavaCodeInsightFixtureTestCase4 {
	public ArgumentPositionCompletionContributorTest() {
		super(null, "src/test/testData/completion");
	}

	@Test
	public void testPositionCompletionWithArguments() {
		// given
		getFixture().configureByFile("argument-position-with-parameter.java");

		// when
		getFixture().completeBasic();

		// then
		assertThat(getFixture().getLookupElementStrings())
				.containsExactly("0", "1", "2", "3");
	}

	@Test
	public void testPositionCompletionWithoutArguments() {
		// given
		getFixture().configureByFile("argument-position-without-parameter.java");

		// when
		getFixture().completeBasic();

		// then
		assertThat(getFixture().getLookupElementStrings())
				.containsExactly("0");
	}
}
