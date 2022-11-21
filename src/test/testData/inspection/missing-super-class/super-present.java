@eu.andret.arguments.api.annotation.BaseCommand("test")
public class LocalCommandExecutor<caret> extends eu.andret.arguments.AnnotatedCommandExecutor<Object> {
	public LocalCommandExecutor(final CommandSender sender, final JavaPlugin plugin) {
		super(sender, plugin);
	}
}
