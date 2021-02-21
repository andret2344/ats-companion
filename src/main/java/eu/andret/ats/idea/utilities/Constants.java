package eu.andret.ats.idea.utilities;

public class Constants {
	public static final String INNER_PACKAGE = "eu.andret.arguments";
	public static final String API_PACKAGE = INNER_PACKAGE + ".api";
	public static final String API_ANNOTATION = API_PACKAGE + ".annotation";
	public static final String API_ANNOTATION_ARGUMENT = API_ANNOTATION + ".Argument";
	public static final String API_ANNOTATION_FALLBACK = API_ANNOTATION + ".Fallback";
	public static final String API_ANNOTATION_BASE_COMMAND = API_ANNOTATION + ".BaseCommand";
	public static final String INNER_ANNOTATED_COMMAND_EXECUTOR = INNER_PACKAGE + ".AnnotatedCommandExecutor";

	private Constants() {
	}
}
