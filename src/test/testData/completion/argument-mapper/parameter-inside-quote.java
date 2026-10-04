@eu.andret.arguments.api.annotation.BaseCommand("test")
public class LocalCommandExecutor extends eu.andret.arguments.AnnotatedCommandExecutor<JavaPlugin> {
	public LocalCommandExecutor(final CommandSender sender, final JavaPlugin plugin) {
		super(sender, plugin);
	}

	@eu.andret.arguments.api.annotation.Argument
	public void test(@eu.andret.arguments.api.annotation.Mapper("<caret>") Object test) {
	}

	private static void init() {
		final eu.andret.arguments.AnnotatedCommand command = CommandManager.registerCommand(this.getClass(), null);
		command.addArgumentMapper("test", java.lang.Object.class, null, null);
		command.addArgumentMapper("value", java.lang.Stream.class, null, null);
	}
}
