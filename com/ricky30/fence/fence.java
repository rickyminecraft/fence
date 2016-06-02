package com.ricky30.fence;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.scheduler.Scheduler;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;

import com.google.inject.Inject;
import com.ricky30.fence.command.commandFence;
import com.ricky30.fence.command.commandPole;
import com.ricky30.fence.command.commandReload;
import com.ricky30.fence.command.commandSet;
import com.ricky30.fence.event.blocEvent;
import com.ricky30.fence.event.moveEvent;
import com.ricky30.fence.task.fenceTask;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;

@Plugin(id = "com.ricky30.fence", name = "fence", version = "1.0.2")
public class fence
{
	@Inject
	private Logger logger;
	private ConfigurationNode config = null;
	public static fence plugin;

	@Inject
	@DefaultConfig(sharedRoot = true)
	private Path defaultConfig;

	@Inject
	@DefaultConfig(sharedRoot = true)
	private ConfigurationLoader<CommentedConfigurationNode> configManager;

	private final Scheduler scheduler = Sponge.getScheduler();
	private final Task.Builder taskBuilder = scheduler.createTaskBuilder();
	private Task task;

	public Task gettasks()
	{
		return this.task;
	}

	public Task.Builder getTaskbuilder()
	{
		return this.taskBuilder;
	}

	public ConfigurationNode getConfig()
	{
		return this.config;
	}

	public Path getDefaultConfig() 
	{
		return this.defaultConfig;
	}

	public ConfigurationLoader<CommentedConfigurationNode> getConfigManager() 
	{
		return this.configManager;
	}

	public Logger getLogger()
	{
		return this.logger;
	}

	@Listener
	public void onServerStart(GameInitializationEvent event)
	{
		getLogger().info("Fence start.");
		plugin = this;
		try
		{
			reload();
			if (!Files.exists(getDefaultConfig())) 
			{

				Files.createFile(getDefaultConfig());
				setupconfig();
			}
		}
		catch (final IOException e)
		{
			getLogger().error("Couldn't create default configuration file!");
		}

		task = plugin.getTaskbuilder().execute(new Runnable()
		{
			public void run()
			{
				fenceTask.run();
			}
		}).interval(500, TimeUnit.MILLISECONDS).name("fence").submit(this);

		Sponge.getEventManager().registerListeners(this, new blocEvent());
		Sponge.getEventManager().registerListeners(this, new moveEvent());

		final HashMap<List<String>, CommandSpec> subcommands = new HashMap<List<String>, CommandSpec>();
		subcommands.put(Arrays.asList("reload"), CommandSpec.builder()
				.description(Text.of("reload config file"))
				.permission("fence.reload")
				.executor(new commandReload())
				.build());
		subcommands.put(Arrays.asList("pole"), CommandSpec.builder()
				.description(Text.of("define your own pole"))
				.permission("fence.pole")
				.executor(new commandPole())
				.build());
		subcommands.put(Arrays.asList("set"), CommandSpec.builder()
				.description(Text.of("define which set a pole is linked"))
				.permission("fence.set")
				.arguments(GenericArguments.onlyOne(GenericArguments.integer(Text.of("Set"))))
				.executor(new commandSet())
				.build());
		final CommandSpec barriercommand = CommandSpec.builder()
				.description(Text.of("list all commands"))
				.executor(new commandFence())
				.children(subcommands)
				.build();
		Sponge.getCommandManager().register(this, barriercommand, "fence");

		getLogger().info("Fence started.");
	}

	@Listener
	public void onServerStopping(GameStoppingServerEvent event)
	{
		getLogger().info("Fence stop.");
		task.cancel();
	}

	private void setupconfig()
	{
		this.config.getNode("ConfigVersion").setValue(1);
		this.config.getNode("KillPlayer").setValue(false);
		this.config.getNode("KillMonster").setValue(false);
		this.config.getNode("KillPeacefull").setValue(false);
		this.config.getNode("Dodamage").setValue(false);
		save();
	}

	public void save()
	{
		try
		{
			getConfigManager().save(this.config);
		} catch (final IOException e) 
		{
			getLogger().error("Failed to save config file!", e);
		}
	}

	public void reload()
	{
		try
		{
			this.config = getConfigManager().load();
		} catch (final IOException e)
		{
			getLogger().error("Failed to load config file!", e);
		}
	}

}
