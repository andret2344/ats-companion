import eu.andret.arguments.AnnotatedCommandExecutor;
import eu.andret.arguments.api.annotation.Argument;
import eu.andret.arguments.api.annotation.BaseCommand;
import eu.andret.arguments.api.entity.ExecutorType;

@BaseCommand("test")
public class LocalCommandExecutor extends AnnotatedCommandExecutor<JavaPlugin> {
	public LocalCommandExecutor(final CommandSender sender, final JavaPlugin plugin) {
		super(sender, plugin);
	}

	@Argument(executorType = ExecutorType.PLAYER, permission = "ats.explosivepotion.get")
	public void get(final <caret>ExplosivePotion explosivePotion) {
	}
}
