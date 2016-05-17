package com.ricky30.fence.command;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;

public class commandFence implements CommandExecutor
{

	public CommandResult execute(CommandSource src, CommandContext args)
			throws CommandException
	{
		src.sendMessage(Text.of("Fence plugin"));
		src.sendMessage(Text.of("Usage:"));
		src.sendMessage(Text.of("/fence set"));
		src.sendMessage(Text.of("/fence pole"));
		src.sendMessage(Text.of("/fence reload"));
		return CommandResult.success();
	}

}
