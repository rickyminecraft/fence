package com.ricky30.fence.command;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;

import com.ricky30.fence.utils.manageSet;

public class commandSet implements CommandExecutor
{

	public CommandResult execute(CommandSource src, CommandContext args)
			throws CommandException
	{
		final int Set = args.<Integer>getOne("Set").get();
		manageSet.saveSetNumber(Set);
		manageSet.SetActive(true);
		src.sendMessage(Text.of("hit the pole with right click"));
		return CommandResult.success();
	}

}
