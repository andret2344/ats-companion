@eu.andret.arguments.api.annotation.BaseCommand("test")
public class LocalCommandExecutor extends eu.andret.arguments.AnnotatedCommandExecutor<JavaPlugin> {
	public LocalCommandExecutor(final CommandSender sender, final JavaPlugin plugin) {
		super(sender, plugin);
	}

	@eu.andret.arguments.api.annotation.TypeFallback(org.bukkit.world.World.class)
	public void testFallback(int<caret> x) {
	}
}
