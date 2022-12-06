public class LocalCommandExecutor extends eu.andret.arguments.AnnotatedCommandExecutor<org.bukkit.plugin.java.JavaPlugin> {
	public LocalCommandExecutor(<caret>) {
		super(sender, plugin);
	}

	@eu.andret.arguments.api.annotation.Argument(executorType = ExecutorType.PLAYER)
	public void get(org.bukkit.entity.Player player) {
		alwaysTrue();
	}
}
