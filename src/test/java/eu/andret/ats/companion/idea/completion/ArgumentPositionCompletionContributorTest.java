/*
 * Copyright Andret Tools System (c) 2018-2022. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.ats.companion.idea.completion;

import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase4;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ArgumentPositionCompletionContributorTest extends LightJavaCodeInsightFixtureTestCase4 {

	public ArgumentPositionCompletionContributorTest() {
		super(null, "src/test/testData/completion");
	}

	@Test
	public void testPositionCompletionWithArguments() {
		getFixture().configureByFile("argument-position-with-parameter.java");
		getFixture().completeBasic();
		assertEquals(List.of("0", "1", "2", "3"), getFixture().getLookupElementStrings());
	}

	@Test
	public void testPositionCompletionWithoutArguments() {
		getFixture().configureByFile("argument-position-without-parameter.java");
		getFixture().completeBasic();
		assertEquals(Collections.singletonList("0"), getFixture().getLookupElementStrings());
	}
}
