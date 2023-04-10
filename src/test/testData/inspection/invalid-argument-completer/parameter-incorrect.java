/*
 * Copyright (c) 2018 Andret Tools System. Copying and modifying allowed only keeping git link reference.
 */

@eu.andret.arguments.api.annotation.BaseCommand("test")
public class LocalCommandExecutor extends eu.andret.arguments.AnnotatedCommandExecutor<JavaPlugin> {
	public LocalCommandExecutor(final CommandSender sender, final JavaPlugin plugin) {
		super(sender, plugin);
	}

	@eu.andret.arguments.api.annotation.Argument
	private void get(@eu.andret.arguments.api.annotation.Completer("<caret>test") java.lang.Object object) {
	}
}
