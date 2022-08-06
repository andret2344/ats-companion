package eu.andret.ats.companion.idea.utilities;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

@UtilityClass
public class Constants {
	private static final String ARGUMENTS_MAIN = "eu.andret.arguments";
	private static final String ARGUMENTS_API = ARGUMENTS_MAIN + ".api";
	private static final String ARGUMENTS_ANNOTATION = ARGUMENTS_API + ".annotation";

	public static final String ANNOTATION_BASE_COMMAND = ARGUMENTS_ANNOTATION + ".BaseCommand";
	public static final String ANNOTATION_ARGUMENT = ARGUMENTS_ANNOTATION + ".Argument";
	public static final String ANNOTATION_ARGUMENT_FALLBACK = ARGUMENTS_ANNOTATION + ".ArgumentFallback";
	public static final String ANNOTATION_TYPE_FALLBACK = ARGUMENTS_ANNOTATION + ".TypeFallback";
	public static final String ANNOTATION_MAPPER = ARGUMENTS_ANNOTATION + ".Mapper";
	public static final String CLASS_ANNOTATED_COMMAND_EXECUTOR = ARGUMENTS_MAIN + ".AnnotatedCommandExecutor";

	public static final String BUKKIT_JAVA_PLUGIN = "org.bukkit.plugin.java.JavaPlugin";
	public static final String BUKKIT_COMMAND_SENDER = "org.bukkit.command.CommandSender";
	public static final String BUKKIT_CONSOLE_COMMAND_SENDER = "org.bukkit.command.ConsoleCommandSender";
	public static final String BUKKIT_PLAYER = "org.bukkit.entity.Player";

	@NotNull
	public String[] getMethodAnnotations() {
		return new String[]{ANNOTATION_ARGUMENT, ANNOTATION_ARGUMENT_FALLBACK, ANNOTATION_TYPE_FALLBACK};
	}
}
