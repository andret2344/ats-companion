@eu.andret.arguments.api.annotation.BaseCommand("test")
public class LocalCommandExecutor extends eu.andret.arguments.AnnotatedCommandExecutor<JavaPlugin> {
	public LocalCommandExecutor(final CommandSender sender, final JavaPlugin plugin) {
		super(sender, plugin);
	}

	@eu.andret.arguments.api.annotation.Argument(executorType = ExecutorType.PLAYER, permission = "ats.explosivepotion.get")
	public void get(@eu.andret.arguments.api.annotation.Mapper("test") final <caret>ExplosivePotion explosivePotion) {
	}

    @ArgumentFallback("test")
    public String testFallback(String test) {
        return null;
    }
}
