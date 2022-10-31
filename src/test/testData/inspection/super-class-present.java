@eu.andret.arguments.api.annotation.BaseCommand("test")
public class LocalCommandExecutor<caret> extends eu.andret.arguments.AnnotatedCommandExecutor<JavaPlugin> {
	public LocalCommandExecutor(final CommandSender sender, final JavaPlugin plugin) {
		super(sender, plugin);
	}
}
