@eu.andret.arguments.api.annotation.BaseCommand("test")
public class LocalCommandExecutor extends eu.andret.arguments.AnnotatedCommandExecutor<org.bukkit.plugin.java.JavaPlugin> {
	public LocalCommandExecutor(<caret>Object object, org.bukkit.plugin.java.JavaPlugin plugin) {
		super(sender, plugin);
	}
}
