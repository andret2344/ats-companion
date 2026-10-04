@eu.andret.arguments.api.annotation.BaseCommand("test")
public class LocalCommandExecutor extends eu.andret.arguments.AnnotatedCommandExecutor<JavaPlugin> {
	public LocalCommandExecutor(final org.bukkit.command.CommandSender sender, final org.bukkit.plugin.java.JavaPlugin plugin) {
		super(sender, plugin);
	}

	@eu.andret.arguments.api.annotation.Argument
	public void argumentTest() {
		Optional.of(player)
				.map((org.bukkit.player.Player player) -> player.getName())
				.orElse("");
	}

	@eu.andret.arguments.api.annotation.TypeFallback(org.bukkit.player.Player.class)
	public void fallbackTest(String string) {
	}
}
