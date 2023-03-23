import eu.andret.arguments.api.annotation.TypeFallback;

@eu.andret.arguments.api.annotation.BaseCommand("test")
public class LocalCommandExecutor extends eu.andret.arguments.AnnotatedCommandExecutor<JavaPlugin> {
	public LocalCommandExecutor(final CommandSender sender, final JavaPlugin plugin) {
		super(sender, plugin);
	}

	@eu.andret.arguments.api.annotation.Argument(executorType = ExecutorType.PLAYER, permission = "ats.explosivepotion.get")
	public void get(final <caret>ExplosivePotion explosivePotion) {
	}

    @TypeFallback(ExplosivePotion.class)
    public String explosivePotionFallback(String explosivePotion) {
        return null;
    }
}
