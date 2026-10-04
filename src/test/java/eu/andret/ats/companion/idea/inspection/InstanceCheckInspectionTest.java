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

public class InstanceCheckInspectionTest extends LightJavaCodeInsightFixtureTestCase4 {
	public InstanceCheckInspectionTest() {
		super(null, "src/test/testData/inspection/instance-check");
	}

	@Test
	public void instanceCheckPlayerTrue() {
		// given
		getFixture().configureByFile("player-true.java");
		getFixture().addClass("package eu.andret.arguments;public class AnnotatedCommandExecutor<E extends org.bukkit.plugin.java.JavaPlugin> { protected org.bukkit.command.CommandSender sender;protected E plugin;public AnnotatedCommandExecutor(final org.bukkit.command.CommandSender sender, final E plugin) {}}");
		getFixture().enableInspections(new InstanceCheckInspection());

		// when
		final List<HighlightInfo> highlightInfos = getFixture().doHighlighting();

		// then
		assertThat(highlightInfos).isNotEmpty();
		final Optional<HighlightInfo> optionalHighlightInfo = highlightInfos.stream()
				.filter(element ->
						Objects.equals(element.getDescription(), InstanceCheckInspection.DESCRIPTION_UNUSED))
				.findAny();
		assertThat(optionalHighlightInfo)
				.map(HighlightInfo::getSeverity)
				.contains(HighlightSeverity.WARNING);
	}

	@Test
	public void instanceCheckPlayerFalse() {
		// given
		getFixture().configureByFile("player-false.java");
		getFixture().addClass("package eu.andret.arguments;public class AnnotatedCommandExecutor<E extends org.bukkit.plugin.java.JavaPlugin> { protected org.bukkit.command.CommandSender sender;protected E plugin;public AnnotatedCommandExecutor(final org.bukkit.command.CommandSender sender, final E plugin) {}}");
		getFixture().enableInspections(new InstanceCheckInspection());

		// when
		final List<HighlightInfo> highlightInfos = getFixture().doHighlighting();

		// then
		assertThat(highlightInfos).isNotEmpty();
		final Optional<HighlightInfo> optionalHighlightInfo = highlightInfos.stream()
				.filter(element ->
						Objects.equals(element.getDescription(), InstanceCheckInspection.DESCRIPTION_PROBLEM))
				.findAny();
		assertThat(optionalHighlightInfo)
				.map(HighlightInfo::getSeverity)
				.contains(HighlightSeverity.WARNING);
	}

	@Test
	public void instanceCheckConsoleTrue() {
		// given
		getFixture().configureByFile("console-true.java");
		getFixture().addClass("package eu.andret.arguments;public class AnnotatedCommandExecutor<E extends org.bukkit.plugin.java.JavaPlugin> { protected org.bukkit.command.CommandSender sender;protected E plugin;public AnnotatedCommandExecutor(final org.bukkit.command.CommandSender sender, final E plugin) {}}");
		getFixture().enableInspections(new InstanceCheckInspection());

		// when
		final List<HighlightInfo> highlightInfos = getFixture().doHighlighting();

		// then
		assertThat(highlightInfos).isNotEmpty();
		final Optional<HighlightInfo> optionalHighlightInfo = highlightInfos.stream()
				.filter(element ->
						Objects.equals(element.getDescription(), InstanceCheckInspection.DESCRIPTION_UNUSED))
				.findAny();
		assertThat(optionalHighlightInfo)
				.map(HighlightInfo::getSeverity)
				.contains(HighlightSeverity.WARNING);
	}

	@Test
	public void instanceCheckConsoleFalse() {
		// given
		getFixture().configureByFile("console-false.java");
		getFixture().addClass("package eu.andret.arguments;public class AnnotatedCommandExecutor<E extends org.bukkit.plugin.java.JavaPlugin> { protected org.bukkit.command.CommandSender sender;protected E plugin;public AnnotatedCommandExecutor(final org.bukkit.command.CommandSender sender, final E plugin) {}}");
		getFixture().enableInspections(new InstanceCheckInspection());

		// when
		final List<HighlightInfo> highlightInfos = getFixture().doHighlighting();

		// then
		assertThat(highlightInfos).isNotEmpty();
		final Optional<HighlightInfo> optionalHighlightInfo = highlightInfos.stream()
				.filter(element ->
						Objects.equals(element.getDescription(), InstanceCheckInspection.DESCRIPTION_PROBLEM))
				.findAny();
		assertThat(optionalHighlightInfo)
				.map(HighlightInfo::getSeverity)
				.contains(HighlightSeverity.WARNING);
	}

	@Test
	public void instanceCheckPlayerTrueFix() {
		// given
		getFixture().configureByFile("player-true.java");
		getFixture().addClass("package eu.andret.arguments;public class AnnotatedCommandExecutor<E extends org.bukkit.plugin.java.JavaPlugin> { protected org.bukkit.command.CommandSender sender;protected E plugin;public AnnotatedCommandExecutor(final org.bukkit.command.CommandSender sender, final E plugin) {}}");
		getFixture().enableInspections(new InstanceCheckInspection());
		final IntentionAction action = getFixture().findSingleIntention(InstanceCheckInspection.UnWrapIfStatementQuickFix.NAME);
		assertThat(action).isNotNull();

		// when
		getFixture().launchAction(action);

		// then
		getFixture().checkResultByFile("player-true.after.java");
	}

	@Test
	public void instanceCheckPlayerFalseFix() {
		// given
		getFixture().configureByFile("player-false.java");
		getFixture().addClass("package eu.andret.arguments;public class AnnotatedCommandExecutor<E extends org.bukkit.plugin.java.JavaPlugin> { protected org.bukkit.command.CommandSender sender;protected E plugin;public AnnotatedCommandExecutor(final org.bukkit.command.CommandSender sender, final E plugin) {}}");
		getFixture().enableInspections(new InstanceCheckInspection());
		final IntentionAction action = getFixture().findSingleIntention(InstanceCheckInspection.UnWrapElseStatementQuickFix.NAME);
		assertThat(action).isNotNull();

		// when
		getFixture().launchAction(action);

		// then
		getFixture().checkResultByFile("player-false.after.java");
	}

	@Test
	public void instanceCheckConsoleTrueFix() {
		// given
		getFixture().configureByFile("console-true.java");
		getFixture().addClass("package eu.andret.arguments;public class AnnotatedCommandExecutor<E extends org.bukkit.plugin.java.JavaPlugin> { protected org.bukkit.command.CommandSender sender;protected E plugin;public AnnotatedCommandExecutor(final org.bukkit.command.CommandSender sender, final E plugin) {}}");
		getFixture().enableInspections(new InstanceCheckInspection());
		final IntentionAction action = getFixture().findSingleIntention(InstanceCheckInspection.UnWrapIfStatementQuickFix.NAME);
		assertThat(action).isNotNull();

		// when
		getFixture().launchAction(action);

		// then
		getFixture().checkResultByFile("console-true.after.java");
	}

	@Test
	public void instanceCheckConsoleFalseFix() {
		// given
		getFixture().configureByFile("console-false.java");
		getFixture().addClass("package eu.andret.arguments;public class AnnotatedCommandExecutor<E extends org.bukkit.plugin.java.JavaPlugin> { protected org.bukkit.command.CommandSender sender;protected E plugin;public AnnotatedCommandExecutor(final org.bukkit.command.CommandSender sender, final E plugin) {}}");
		getFixture().enableInspections(new InstanceCheckInspection());
		final IntentionAction action = getFixture().findSingleIntention(InstanceCheckInspection.UnWrapElseStatementQuickFix.NAME);
		assertThat(action).isNotNull();

		// when
		getFixture().launchAction(action);

		// then
		getFixture().checkResultByFile("console-false.after.java");
	}
}
