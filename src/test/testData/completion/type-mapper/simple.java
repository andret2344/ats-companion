@eu.andret.arguments.api.annotation.BaseCommand("test")
public class LocalCommandExecutor extends eu.andret.arguments.AnnotatedCommandExecutor<JavaPlugin> {
	public LocalCommandExecutor(final CommandSender sender, final JavaPlugin plugin) {
		super(sender, plugin);
	}

	@eu.andret.arguments.api.annotation.TypeFallback(<caret>)
	public void test(String test) {
	}

	private static void init() {
		final eu.andret.arguments.AnnotatedCommand command = CommandManager.registerCommand(this.getClass(), null);
		command.addTypeMapper(java.lang.Object.class, null, null);
		command.addTypeMapper(java.lang.Stream.class, null, null);
	}
}
