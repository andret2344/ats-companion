@eu.andret.arguments.api.annotation.BaseCommand("test")
public class LocalCommandExecutor extends eu.andret.arguments.AnnotatedCommandExecutor<JavaPlugin> {
	public LocalCommandExecutor(final CommandSender sender, final JavaPlugin plugin) {
		super(sender, plugin);
	}

	@eu.andret.arguments.api.annotation.Argument(executorType = ExecutorType.PLAYER)
	public void get() {
		if (sender <caret>instanceof org.bukkit.Player) {
			alwaysTrue();
		} else {
			alwaysFalse();
		}
	}
}
