package eu.andret.ats.companion.idea.utilities;

import com.intellij.openapi.util.IconLoader;

import javax.annotation.processing.Generated;
import javax.swing.Icon;

public class IconProvider {
	public static final Icon FALLBACK = IconLoader.getIcon("/icons/gutter-fallback.svg", IconProvider.class);

	@Generated("private-constructor")
	private IconProvider() {
	}
}
