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

public class MissingBaseCommandAnnotationInspectionTest extends LightJavaCodeInsightFixtureTestCase4 {
	public MissingBaseCommandAnnotationInspectionTest() {
		super(null, "src/test/testData/inspection/missing-base");
	}

	@Test
	public void baseCommandMissing() {
		// given
		getFixture().configureByFile("sample.java");
		getFixture().addClass("package eu.andret.arguments;public class AnnotatedCommandExecutor<E extends org.bukkit.plugin.java.JavaPlugin> { protected org.bukkit.command.CommandSender sender;protected E plugin;public AnnotatedCommandExecutor(final org.bukkit.command.CommandSender sender, final E plugin) {}}");
		getFixture().enableInspections(new MissingBaseCommandAnnotationInspection());

		// when
		final List<HighlightInfo> highlightInfos = getFixture().doHighlighting();

		// then
		assertThat(highlightInfos).isNotEmpty();
		final Optional<HighlightInfo> optionalHighlightInfo = highlightInfos.stream()
				.filter(element ->
						Objects.equals(element.getDescription(), MissingBaseCommandAnnotationInspection.DESCRIPTION))
				.findAny();
		assertThat(optionalHighlightInfo)
				.map(HighlightInfo::getSeverity)
				.contains(HighlightSeverity.ERROR);
	}

	@Test
	public void baseCommandMissingFix() {
		// given
		getFixture().configureByFile("sample.java");
		getFixture().addClass("package eu.andret.arguments;public class AnnotatedCommandExecutor<E extends org.bukkit.plugin.java.JavaPlugin> { protected org.bukkit.command.CommandSender sender;protected E plugin;public AnnotatedCommandExecutor(final org.bukkit.command.CommandSender sender, final E plugin) {}}");
		getFixture().enableInspections(new MissingBaseCommandAnnotationInspection());
		final IntentionAction action = getFixture().findSingleIntention(MissingBaseCommandAnnotationInspection.AddMissingAnnotationQuickFix.NAME);
		assertThat(action).isNotNull();

		// when
		getFixture().launchAction(action);

		// then
		getFixture().checkResultByFile("sample.after.java");
	}

}
