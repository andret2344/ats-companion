@eu.andret.arguments.api.annotation.BaseCommand("test")
public class LocalCommandExecutor extends eu.andret.arguments.AnnotatedCommandExecutor<JavaPlugin> {
	public LocalCommandExecutor(final CommandSender sender, final JavaPlugin plugin) {
		super(sender, plugin);
	}

	@eu.andret.arguments.api.annotation.Argument
	private void get(@eu.andret.arguments.api.annotation.Completer("<caret>test") java.lang.Object object) {
	}

	private static void init() {
		final eu.andret.arguments.AnnotatedCommand command = CommandManager.registerCommand(this.getClass(), null);
		command.addArgumentCompleter("test", java.lang.Object.class, null);
	}
}
