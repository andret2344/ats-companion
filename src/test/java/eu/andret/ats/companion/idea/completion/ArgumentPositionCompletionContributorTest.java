package eu.andret.ats.companion.idea.completion;

import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase4;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ArgumentPositionCompletionContributorTest extends LightJavaCodeInsightFixtureTestCase4 {
	public ArgumentPositionCompletionContributorTest() {
		super(null, "src/test/testData/completion/argument-position");
	}

	@Test
	public void positionCompletionWithArguments() {
		// given
		getFixture().configureByFile("with-parameter.java");

		// when
		getFixture().completeBasic();

		// then
		assertThat(getFixture().getLookupElementStrings())
				.containsExactly("0", "1", "2", "3");
	}

	@Test
	public void positionCompletionWithoutArguments() {
		// given
		getFixture().configureByFile("without-parameter.java");

		// when
		getFixture().completeBasic();

		// then
		assertThat(getFixture().getLookupElementStrings())
				.containsExactly("0");
	}
}
