package com.ricky30.fence.command;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;

import com.ricky30.fence.utils.managePole;

public class commandPole implements CommandExecutor
{

	public CommandResult execute(CommandSource src, CommandContext args)
			throws CommandException
	{
		managePole.SetActive(true);
		src.sendMessage(Text.of("hit the root of your pole with right click"));
		return CommandResult.success();
	}

}
