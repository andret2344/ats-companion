/*
 * Copyright Andret Tools System (c) 2018-2022. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.ats.companion.idea.utilities;

import com.intellij.openapi.util.IconLoader;

import javax.annotation.processing.Generated;
import javax.swing.Icon;

public final class IconProvider {
	public static final Icon FALLBACK = IconLoader.getIcon("/icons/gutter-fallback.svg", IconProvider.class);

	@Generated("private-constructor")
	private IconProvider() {
	}
}
