/*
 * Copyright (c) 2018 Andret Tools System. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.ats.companion.idea.inspection;

import com.intellij.codeInsight.daemon.impl.HighlightInfo;
import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase4;
import org.junit.Test;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class NullComparisonInspectionTest extends LightJavaCodeInsightFixtureTestCase4 {
	public NullComparisonInspectionTest() {
		super(null, "src/test/testData/inspection/null-comparison");
	}

	@Test
	public void nullParameterEqEq() {
		// given
		getFixture().configureByFile("eqeq.java");
		getFixture().addClass("package eu.andret.arguments;public class AnnotatedCommandExecutor<E extends org.bukkit.plugin.java.JavaPlugin> { protected org.bukkit.command.CommandSender sender;protected E plugin;public AnnotatedCommandExecutor(final org.bukkit.command.CommandSender sender, final E plugin) {}}");
		getFixture().enableInspections(new NullComparisonInspection());

		// when
		final List<HighlightInfo> highlightInfos = getFixture().doHighlighting();

		// then
		assertThat(highlightInfos).isNotEmpty();
		final Optional<HighlightInfo> optionalHighlightInfo = highlightInfos.stream()
				.filter(element ->
						Objects.equals(element.getDescription(), NullComparisonInspection.DESCRIPTION))
				.findAny();
		assertThat(optionalHighlightInfo)
				.map(HighlightInfo::getSeverity)
				.contains(HighlightSeverity.WARNING);
	}

	@Test
	public void nullParameterNE() {
		// given
		getFixture().configureByFile("ne.java");
		getFixture().addClass("package eu.andret.arguments;public class AnnotatedCommandExecutor<E extends org.bukkit.plugin.java.JavaPlugin> { protected org.bukkit.command.CommandSender sender;protected E plugin;public AnnotatedCommandExecutor(final org.bukkit.command.CommandSender sender, final E plugin) {}}");
		getFixture().enableInspections(new NullComparisonInspection());

		// when
		final List<HighlightInfo> highlightInfos = getFixture().doHighlighting();

		// then
		assertThat(highlightInfos).isNotEmpty();
		final Optional<HighlightInfo> optionalHighlightInfo = highlightInfos.stream()
				.filter(element ->
						Objects.equals(element.getDescription(), NullComparisonInspection.DESCRIPTION))
				.findAny();
		assertThat(optionalHighlightInfo)
				.map(HighlightInfo::getSeverity)
				.contains(HighlightSeverity.WARNING);
	}

	@Test
	public void nullParameterEqEqFix() {
		// given
		getFixture().configureByFile("eqeq.java");
		getFixture().addClass("package eu.andret.arguments;public class AnnotatedCommandExecutor<E extends org.bukkit.plugin.java.JavaPlugin> { protected org.bukkit.command.CommandSender sender;protected E plugin;public AnnotatedCommandExecutor(final org.bukkit.command.CommandSender sender, final E plugin) {}}");
		getFixture().enableInspections(new NullComparisonInspection());
		final IntentionAction action = getFixture().findSingleIntention(InstanceCheckInspection.UnWrapIfStatementQuickFix.NAME);
		assertThat(action).isNotNull();

		// when
		getFixture().launchAction(action);

		// then
		getFixture().checkResultByFile("eqeq.after.java");
	}

	@Test
	public void nullParameterNEFix() {
		// given
		getFixture().configureByFile("ne.java");
		getFixture().addClass("package eu.andret.arguments;public class AnnotatedCommandExecutor<E extends org.bukkit.plugin.java.JavaPlugin> { protected org.bukkit.command.CommandSender sender;protected E plugin;public AnnotatedCommandExecutor(final org.bukkit.command.CommandSender sender, final E plugin) {}}");
		getFixture().enableInspections(new NullComparisonInspection());
		final IntentionAction action = getFixture().findSingleIntention(InstanceCheckInspection.UnWrapElseStatementQuickFix.NAME);
		assertThat(action).isNotNull();

		// when
		getFixture().launchAction(action);

		// then
		getFixture().checkResultByFile("ne.after.java");
	}
}
