@eu.andret.arguments.api.annotation.BaseCommand("test")
public class LocalCommandExecutor extends eu.andret.arguments.AnnotatedCommandExecutor<JavaPlugin> {
    public LocalCommandExecutor(final CommandSender sender, final JavaPlugin plugin) {
        super(sender, plugin);
    }

    @eu.andret.arguments.api.annotation.Argument(position = <caret>"fsdafdsg")
    public String test(String test, String rtfmdeswhg, int sadar) {
        return test;
    }
}
