import eu.andret.arguments.AnnotatedCommandExecutor;
import eu.andret.arguments.api.annotation.Argument;
import eu.andret.arguments.api.annotation.BaseCommand;
import eu.andret.arguments.api.annotation.TypeFallback;
import eu.andret.arguments.api.entity.ExecutorType;

@BaseCommand("test")
public class LocalCommandExecutor extends AnnotatedCommandExecutor<JavaPlugin> {
	public LocalCommandExecutor(final CommandSender sender, final JavaPlugin plugin) {
		super(sender, plugin);
	}

	@Argument(executorType = ExecutorType.PLAYER, permission = "ats.explosivepotion.get")
	public void get(final ExplosivePotion explosivePotion) {
	}

	@TypeFallback(ExplosivePotion.class)
	public String explosivePotionFallback(final String explosivePotion) {
		return null;
	}
}
