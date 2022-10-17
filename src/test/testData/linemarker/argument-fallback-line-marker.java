@eu.andret.arguments.api.annotation.BaseCommand("test")
public class LocalCommandExecutor extends eu.andret.arguments.AnnotatedCommandExecutor<JavaPlugin> {
	public LocalCommandExecutor(final CommandSender sender, final JavaPlugin plugin) {
		super(sender, plugin);
	}

	@eu.andret.arguments.api.annotation.Argument
	public void method(@eu.andret.arguments.api.annotation<caret>.Mapper("text") org.bukkit.player.Player player) {}

	@eu.andret.arguments.api.annotation.ArgumentFallback("text")
	public void method2(String string) {}
}
