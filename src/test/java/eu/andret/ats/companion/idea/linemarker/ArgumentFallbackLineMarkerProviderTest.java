/*
 * Copyright Andret Tools System (c) 2018-2022. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.ats.companion.idea.linemarker;

import com.intellij.codeInsight.daemon.GutterIconNavigationHandler;
import com.intellij.codeInsight.daemon.GutterMark;
import com.intellij.codeInsight.daemon.LineMarkerInfo;
import com.intellij.codeInsight.navigation.NavigationGutterIconRenderer;
import com.intellij.psi.presentation.java.SymbolPresentationUtil;
import com.intellij.testFramework.UsefulTestCase;
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase;
import com.intellij.util.containers.ContainerUtil;
import eu.andret.ats.companion.idea.utilities.IconProvider;

public class ArgumentFallbackLineMarkerProviderTest extends LightJavaCodeInsightFixtureTestCase {
	@Override
	protected String getTestDataPath() {
		return "src/test/testData/linemarker";
	}

	public void testArgumentFallbackLineMarkerProvider() {
		// given
		myFixture.configureByFile("argument-fallback-line-marker.java");

		// when
		final GutterMark gutterMark = myFixture.findGutter("argument-fallback-line-marker.java");

		// then
		assertNotNull(gutterMark);
		assertEquals("Find @ArgumentFallback method", gutterMark.getTooltipText());
		assertEquals(IconProvider.FALLBACK, gutterMark.getIcon());

		if (!(gutterMark instanceof LineMarkerInfo.LineMarkerGutterIconRenderer)) {
			throw new IllegalArgumentException(gutterMark.getClass() + ": gutter not supported");
		}
		final LineMarkerInfo.LineMarkerGutterIconRenderer<?> renderer =
				UsefulTestCase.assertInstanceOf(gutterMark, LineMarkerInfo.LineMarkerGutterIconRenderer.class);
		final LineMarkerInfo<?> lineMarkerInfo = renderer.getLineMarkerInfo();
		final GutterIconNavigationHandler<?> handler = lineMarkerInfo.getNavigationHandler();

		if (!(handler instanceof final NavigationGutterIconRenderer iconRenderer)) {
			throw new IllegalArgumentException(handler + ": handler not supported");
		}
		UsefulTestCase.assertSameElements(
				ContainerUtil.map(iconRenderer.getTargetElements(), SymbolPresentationUtil::getSymbolPresentableText),
				"method2(String)");
	}
}
