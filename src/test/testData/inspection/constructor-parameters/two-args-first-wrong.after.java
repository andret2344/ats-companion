@eu.andret.arguments.api.annotation.BaseCommand("test")
public class LocalCommandExecutor extends eu.andret.arguments.AnnotatedCommandExecutor<org.bukkit.plugin.java.JavaPlugin> {
	public LocalCommandExecutor(org.bukkit.command.CommandSender object, org.bukkit.plugin.java.JavaPlugin plugin) {
		super(sender, plugin);
	}
}
