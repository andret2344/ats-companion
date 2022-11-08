/*
 * Copyright Andret Tools System (c) 2018-2022. Copying and modifying allowed only keeping git link reference.
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

public class InstanceCheckInspectionTest extends LightJavaCodeInsightFixtureTestCase4 {
	public InstanceCheckInspectionTest() {
		super(null, "src/test/testData/inspection");
	}

	@Test
	public void testInstanceCheckPlayerTrue() {
		// given
		getFixture().configureByFile("instance-check-player-true.java");
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
	public void testInstanceCheckPlayerTrueFix() {
		// given
		getFixture().configureByFile("instance-check-player-true.java");
		getFixture().addClass("package eu.andret.arguments;public class AnnotatedCommandExecutor<E extends org.bukkit.plugin.java.JavaPlugin> { protected org.bukkit.command.CommandSender sender;protected E plugin;public AnnotatedCommandExecutor(final org.bukkit.command.CommandSender sender, final E plugin) {}}");
		getFixture().enableInspections(new InstanceCheckInspection());
		final IntentionAction action = getFixture().findSingleIntention(InstanceCheckInspection.UnWrapIfStatementQuickFix.NAME);
		assertThat(action).isNotNull();

		// when
		getFixture().launchAction(action);

		// then
		getFixture().checkResultByFile("instance-check-player-true.after.java");
	}
}
