@eu.andret.arguments.api.annotation.BaseCommand("test")
public class LocalCommandExecutor extends eu.andret.arguments.AnnotatedCommandExecutor<JavaPlugin> {
	public LocalCommandExecutor(final org.bukkit.command.CommandSender sender, final org.bukkit.plugin.java.JavaPlugin plugin) {
		super(sender, plugin);
	}

	@eu.andret.arguments.api.annotation.Argument
	public void method(org.bukkit.player.<caret>Player player) {}

	@eu.andret.arguments.api.annotation.TypeFallback(org.bukkit.player.Player.class)
	public void method2(String string) {}
}
