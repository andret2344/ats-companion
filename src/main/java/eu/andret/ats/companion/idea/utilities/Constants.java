package eu.andret.ats.companion.idea.utilities;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Constants {
	private static final String ARGUMENTS_MAIN = "eu.andret.arguments";
	private static final String ARGUMENTS_API = ARGUMENTS_MAIN + ".api";
	private static final String ARGUMENTS_ANNOTATION = ARGUMENTS_API + ".annotation";

	public static final String ANNOTATION_BASE_COMMAND = ARGUMENTS_ANNOTATION + ".BaseCommand";
	public static final String ANNOTATION_ARGUMENT = ARGUMENTS_ANNOTATION + ".Argument";
	public static final String ANNOTATION_FALLBACK = ARGUMENTS_ANNOTATION + ".Fallback";
	public static final String CLASS_ANNOTATED_COMMAND_EXECUTOR = ARGUMENTS_MAIN + ".AnnotatedCommandExecutor";
}
