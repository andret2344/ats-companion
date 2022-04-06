package eu.andret.ats.companion.idea.utilities;

import com.intellij.openapi.util.IconLoader;
import lombok.experimental.UtilityClass;

import javax.swing.Icon;

@UtilityClass
public class IconProvider {
	public static final Icon FALLBACK = IconLoader.getIcon("/icons/jar-gray.png", IconProvider.class);
}
