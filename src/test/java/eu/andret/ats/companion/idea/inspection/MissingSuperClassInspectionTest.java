/*
 * Copyright Andret Tools System (c) 2018-2023. Copying and modifying allowed only keeping git link reference.
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

public class MissingSuperClassInspectionTest extends LightJavaCodeInsightFixtureTestCase4 {
	public MissingSuperClassInspectionTest() {
		super(null, "src/test/testData/inspection/missing-super-class");
	}

	@Test
	public void testSuperClassPresent() {
		// given
		getFixture().configureByFile("super-present.java");
		getFixture().addClass("package eu.andret.arguments; public class AnnotatedCommandExecutor<E extends org.bukkit.plugin.java.JavaPlugin> { public AnnotatedCommandExecutor(final org.bukkit.command.CommandSender sender, final E plugin) {}}");
		getFixture().enableInspections(new MissingSuperClassInspection());

		// when
		final List<HighlightInfo> highlightInfos = getFixture().doHighlighting();

		// then
		assertThat(highlightInfos).isNotEmpty();
		final Optional<HighlightInfo> optionalHighlightInfo = highlightInfos.stream()
				.filter(element ->
						Objects.equals(element.getDescription(), MissingSuperClassInspection.DESCRIPTION))
				.findAny();
		assertThat(optionalHighlightInfo).isEmpty();
	}

	@Test
	public void testSuperClassMissing() {
		// given
		getFixture().configureByFile("super-missing.java");
		getFixture().enableInspections(new MissingSuperClassInspection());

		// when
		final List<HighlightInfo> highlightInfos = getFixture().doHighlighting();

		// then
		assertThat(highlightInfos).isNotEmpty();
		final Optional<HighlightInfo> optionalHighlightInfo = highlightInfos.stream()
				.filter(element ->
						Objects.equals(element.getDescription(), MissingSuperClassInspection.DESCRIPTION))
				.findAny();
		assertThat(optionalHighlightInfo)
				.map(HighlightInfo::getSeverity)
				.contains(HighlightSeverity.ERROR);
	}

	@Test
	public void testNoBaseCommand() {
		// given
		getFixture().configureByFile("no-base-command.java");
		getFixture().enableInspections(new MissingSuperClassInspection());

		// when
		final List<HighlightInfo> highlightInfos = getFixture().doHighlighting();

		// then
		assertThat(highlightInfos).isNotEmpty();
		final Optional<HighlightInfo> optionalHighlightInfo = highlightInfos.stream()
				.filter(element ->
						Objects.equals(element.getDescription(), MissingSuperClassInspection.DESCRIPTION))
				.findAny();
		assertThat(optionalHighlightInfo).isEmpty();
	}
}
