/*
 * Copyright (c) 2018 Andret Tools System. Copying and modifying allowed only keeping git link reference.
 */

@eu.andret.arguments.api.annotation.BaseCommand("test")
public class LocalCommandExecutor extends eu.andret.arguments.AnnotatedCommandExecutor<JavaPlugin> {
	public LocalCommandExecutor(final CommandSender sender, final JavaPlugin plugin) {
		super(sender, plugin);
	}

	private void get(@eu.andret.arguments.api.annotation.Mapper<caret>("test") java.lang.Object object) {
	}
}
