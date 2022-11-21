@eu.andret.arguments.api.annotation.BaseCommand("test")
public class LocalCommandExecutor extends eu.andret.arguments.AnnotatedCommandExecutor<org.bukkit.plugin.java.JavaPlugin> {
	public LocalCommandExecutor(final org.bukkit.command.CommandSender sender, org.bukkit.plugin.java.JavaPlugin plugin) {
		super(sender, plugin);
	}
}
