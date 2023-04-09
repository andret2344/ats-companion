/*
 * Copyright (c) 2018 Andret Tools System. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.ats.companion.idea.utilities;

import org.jetbrains.annotations.NotNull;

import javax.annotation.processing.Generated;

public final class Constants {
	public static final String ANNOTATION_BASE_COMMAND = "eu.andret.arguments.api.annotation.BaseCommand";
	public static final String ANNOTATION_ARGUMENT = "eu.andret.arguments.api.annotation.Argument";
	public static final String ANNOTATION_ARGUMENT_FALLBACK = "eu.andret.arguments.api.annotation.ArgumentFallback";
	public static final String ANNOTATION_TYPE_FALLBACK = "eu.andret.arguments.api.annotation.TypeFallback";
	public static final String ANNOTATION_MAPPER = "eu.andret.arguments.api.annotation.Mapper";
	public static final String ANNOTATION_COMPLETER = "eu.andret.arguments.api.annotation.Completer";
	public static final String CLASS_ANNOTATED_COMMAND_EXECUTOR = "eu.andret.arguments.AnnotatedCommandExecutor";
	public static final String CLASS_ANNOTATED_COMMAND = "eu.andret.arguments.AnnotatedCommand";

	public static final String BUKKIT_JAVA_PLUGIN = "org.bukkit.plugin.java.JavaPlugin";
	public static final String BUKKIT_COMMAND_SENDER = "org.bukkit.command.CommandSender";
	public static final String BUKKIT_CONSOLE_COMMAND_SENDER = "org.bukkit.command.ConsoleCommandSender";
	public static final String BUKKIT_PLAYER = "org.bukkit.entity.Player";

	public static final String METHOD_ADD_TYPE_MAPPER = "addTypeMapper";
	public static final String METHOD_ADD_ENUM_MAPPER = "addEnumMapper";
	public static final String METHOD_ADD_ARGUMENT_MAPPER = "addArgumentMapper";
	public static final String METHOD_ADD_ARGUMENT_COMPLETER = "addArgumentCompleter";

	@Generated("private-constructor")
	private Constants() {
	}

	@NotNull
	public static String[] getMethodAnnotations() {
		return new String[]{ANNOTATION_ARGUMENT, ANNOTATION_ARGUMENT_FALLBACK, ANNOTATION_TYPE_FALLBACK};
	}
}
