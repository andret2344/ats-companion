@eu.andret.arguments.api.annotation.BaseCommand("test")
public class LocalCommandExecutor extends eu.andret.arguments.AnnotatedCommandExecutor<org.bukkit.plugin.java.JavaPlugin> {
	public LocalCommandExecutor(<caret>final org.bukkit.command.CommandSender sender, final org.bukkit.plugin.java.JavaPlugin plugin, Object object) {
		super(sender, plugin);
	}
}
