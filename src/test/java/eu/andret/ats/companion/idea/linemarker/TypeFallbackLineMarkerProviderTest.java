/*
 * Copyright Andret Tools System (c) 2018-2022. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.ats.companion.idea.linemarker;

import com.intellij.codeInsight.daemon.GutterIconNavigationHandler;
import com.intellij.codeInsight.daemon.GutterMark;
import com.intellij.codeInsight.daemon.LineMarkerInfo;
import com.intellij.codeInsight.navigation.NavigationGutterIconRenderer;
import com.intellij.psi.PsiElement;
import com.intellij.psi.presentation.java.SymbolPresentationUtil;
import com.intellij.testFramework.UsefulTestCase;
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase;
import com.intellij.util.containers.ContainerUtil;
import eu.andret.ats.companion.idea.utilities.IconProvider;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class TypeFallbackLineMarkerProviderTest extends LightJavaCodeInsightFixtureTestCase {

	@Override
	protected String getTestDataPath() {
		return "src/test/testData/linemarker";
	}

	public void testArgumentFallbackLineMarkerProvider() {
		//given
		myFixture.configureByFile("type-fallback-line-marker.java");
		myFixture.addClass("package org.bukkit.player; public class Player {}");
		myFixture.addClass("package org.bukkit.command; public class CommandSender {}");
		myFixture.addClass("package org.bukkit.plugin.java; public class JavaPlugin {}");
		//when
		@Nullable GutterMark gutterMark = myFixture.findGutter("type-fallback-line-marker.java");
		//then
		assertNotNull(gutterMark);
		assertEquals("Find @TypeFallback method", gutterMark.getTooltipText());
		assertEquals(IconProvider.FALLBACK, gutterMark.getIcon());

		final Collection<PsiElement> targetElements;
		if (gutterMark instanceof LineMarkerInfo.LineMarkerGutterIconRenderer) {
			final LineMarkerInfo.LineMarkerGutterIconRenderer<?> renderer =
					UsefulTestCase.assertInstanceOf(gutterMark, LineMarkerInfo.LineMarkerGutterIconRenderer.class);
			final LineMarkerInfo<?> lineMarkerInfo = renderer.getLineMarkerInfo();
			GutterIconNavigationHandler<?> handler = lineMarkerInfo.getNavigationHandler();

			if (handler instanceof NavigationGutterIconRenderer) {
				targetElements = ((NavigationGutterIconRenderer)handler).getTargetElements();
			}
			else {
				throw new IllegalArgumentException(handler + ": handler not supported");
			}
		}
		else {
			throw new IllegalArgumentException(gutterMark.getClass() + ": gutter not supported");
		}

		UsefulTestCase.assertSameElements(ContainerUtil.map(targetElements,
				SymbolPresentationUtil::getSymbolPresentableText), "method2(String)");
	}
}