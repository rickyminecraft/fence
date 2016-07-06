package com.ricky30.fence.event;

import java.util.Map;
import java.util.UUID;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockState.Builder;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.data.translator.ConfigurateTranslator;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.World;

import com.flowpowered.math.vector.Vector3i;
import com.ricky30.fence.fence;
import com.ricky30.fence.utils.manageSet;
import com.ricky30.fence.utils.managePole;

import ninja.leaping.configurate.ConfigurationNode;

public class blocEvent
{

	@Listener
	public void oninteractblockPlace(ChangeBlockEvent.Place Event, @First Player player)
	{
		final ConfigurationNode config = fence.plugin.getConfig();
		if (config.getNode("pole").isVirtual())
		{
			return;
		}
		for (final Transaction<BlockSnapshot> blocksnap : Event.getTransactions())
		{
			Vector3i Pos = blocksnap.getDefault().getLocation().get().getBlockPosition();
			final World world = player.getWorld();
			final Map<Object, ? extends ConfigurationNode> Pole = config.getNode("pole").getChildrenMap();
			boolean Ok = false;
			Pos = Pos.add(0, Pole.size(), 0);
			Pos = CheckPole(Pos, world, Pole.size());
			Pos = Pos.sub(0, Pole.size()-1, 0);
			Ok = CheckPole2(Pos, world, Pole, Pole.size());

			if (Ok)
			{
				int PoleNumber = 0;
				if (!config.getNode("fence", "pole", "Set0").getChildrenMap().isEmpty())
				{
					for (final Object pole : config.getNode("fence", "pole", "Set0").getChildrenMap().keySet())
					{
						int PoleNumbertmp;
						PoleNumbertmp = config.getNode("fence", "pole", "Set0", pole.toString(), "Number").getInt();
						if (PoleNumbertmp > PoleNumber)
						{
							PoleNumber = PoleNumbertmp;
						}
					}
					PoleNumber++;
				}
				final String Number = String.valueOf(Pos.getX()) + String.valueOf(Pos.getY()) + String.valueOf(Pos.getZ());
				final byte[] b = Number.getBytes();
				final String Name = UUID.nameUUIDFromBytes(b).toString();
				config.getNode("fence", "pole", "Set0", Name, "X").setValue(Pos.getX());
				config.getNode("fence", "pole", "Set0", Name, "Y").setValue(Pos.getY());
				config.getNode("fence", "pole", "Set0", Name, "Z").setValue(Pos.getZ());
				config.getNode("fence", "pole", "Set0", Name, "world").setValue(world.getUniqueId().toString());
				config.getNode("fence", "pole", "Set0", Name, "Number").setValue(PoleNumber);
				fence.plugin.save();
				player.sendMessage(Text.of("A pole has been made"));
			}
		}
	}

	@Listener
	public void oninteractblockRemove(ChangeBlockEvent.Break Event, @First Player player)
	{
		final ConfigurationNode config = fence.plugin.getConfig();
		if (config.getNode("pole").isVirtual())
		{
			return;
		}
		for (final Transaction<BlockSnapshot> blocksnap : Event.getTransactions())
		{
			Vector3i Pos = blocksnap.getDefault().getLocation().get().getBlockPosition();
			final World world = player.getWorld();
			final Map<Object, ? extends ConfigurationNode> Pole = config.getNode("pole").getChildrenMap();
			Pos = Pos.add(0, Pole.size(), 0);
			Pos = CheckPole(Pos, world, Pole.size());
			Pos = Pos.sub(0, 1, 0);
			final String Number = String.valueOf(Pos.getX()) + String.valueOf(Pos.getY()) + String.valueOf(Pos.getZ());
			final byte[] b = Number.getBytes();
			final String Name = UUID.nameUUIDFromBytes(b).toString();
			for (final Object node : config.getNode("fence", "pole").getChildrenMap().keySet())
			{
				if (config.getNode("fence", "pole", node.toString()).getChildrenMap().containsKey(Name))
				{
					if (world.getUniqueId().equals(UUID.fromString(config.getNode("fence", "pole", node.toString(), Name, "world").getString())))
					{
						config.getNode("fence", "pole", node.toString()).removeChild(Name);
						fence.plugin.save();
						player.sendMessage(Text.of("A pole has been broken"));
					}
				}
			}
		}
	}

	@Listener
	public void oninteractblockPrimary(InteractBlockEvent.Secondary Event, @First Player player)
	{
		final ConfigurationNode config = fence.plugin.getConfig();
		if (manageSet.IsSetactive())
		{
			manageSet.SetActive(false);
			Vector3i Pos = Event.getTargetBlock().getPosition();
			final World world = player.getWorld();
			final Map<Object, ? extends ConfigurationNode> Pole = config.getNode("pole").getChildrenMap();
			boolean Ok = false;
			Pos = Pos.add(0, Pole.size(), 0);
			Pos = CheckPole(Pos, world, Pole.size());
			Pos = Pos.sub(0, Pole.size()-1, 0);
			Ok = CheckPole2(Pos, world, Pole, Pole.size());

			if (Ok)
			{
				final String Number = String.valueOf(Pos.getX()) + String.valueOf(Pos.getY()) + String.valueOf(Pos.getZ());
				final byte[] b = Number.getBytes();
				final String Name = UUID.nameUUIDFromBytes(b).toString();
				int x = 0, y = 0, z = 0;
				UUID Worlduid = null;
				boolean Done = false;
				for (final Object node : config.getNode("fence", "pole").getChildrenMap().keySet())
				{
					if (config.getNode("fence", "pole", node.toString()).getChildrenMap().containsKey(Name))
					{
						x = config.getNode("fence", "pole", node.toString(), Name, "X").getInt();
						y = config.getNode("fence", "pole", node.toString(), Name, "Y").getInt();
						z = config.getNode("fence", "pole", node.toString(), Name, "Z").getInt();
						Worlduid = UUID.fromString(config.getNode("fence", "pole", node.toString(), Name, "world").getString());
						if (world.getUniqueId().equals(Worlduid))
						{
							Done = true;
							config.getNode("fence", "pole", node.toString()).removeChild(Name);
						}

					}
				}
				if (Done)
				{
					final String set = "Set" + manageSet.loadSetnumber();
					int PoleNumber = 0;
					if (!config.getNode("fence", "pole", set).getChildrenMap().isEmpty())
					{
						for (final Object pole : config.getNode("fence", "pole", set).getChildrenMap().keySet())
						{
							int PoleNumbertmp;
							PoleNumbertmp = config.getNode("fence", "pole", set, pole.toString(), "Number").getInt();
							if (PoleNumbertmp > PoleNumber)
							{
								PoleNumber = PoleNumbertmp;
							}
						}
						PoleNumber++;
					}
					config.getNode("fence", "pole", set, Name, "X").setValue(x);
					config.getNode("fence", "pole", set, Name, "Y").setValue(y);
					config.getNode("fence", "pole", set, Name, "Z").setValue(z);
					config.getNode("fence", "pole", set, Name, "world").setValue(Worlduid.toString());
					config.getNode("fence", "pole", set, Name, "Number").setValue(PoleNumber);
					fence.plugin.save();
					player.sendMessage(Text.of("Pole added to pole set number ",manageSet.loadSetnumber()));
				}
			}
		}
		if (managePole.IsSetactive())
		{
			managePole.SetActive(false);
			//faire une boucle jusqu'en haut
			Vector3i pos = Event.getTargetBlock().getPosition();
			final World world = player.getWorld();
			int position = 0;
			final Map<Object, ? extends ConfigurationNode> cn = config.getNode("pole").getChildrenMap();
			for (final Object node :cn.keySet())
			{
				config.getNode("pole").removeChild(node);
			}
			while (!world.getBlock(pos).getType().equals(BlockTypes.AIR))
			{
				final BlockState block = world.getBlock(pos);
				config.getNode("pole", String.valueOf(position), "BlockState").setValue(block.toString());
				position++;
				pos = pos.add(0, 1, 0);
			}
			fence.plugin.save();
			player.sendMessage(Text.of("Pole style defined"));
		}
	}

	//////
	// start from above a pole and go down one by one to check size and return the lowest position (min: height - pole size)
	// args: position vector, the world, pole size
	// return: new position
	//
	//////
	private Vector3i CheckPole(Vector3i Pos, World world, int Size)
	{
		int Position = 0;
		boolean Ok = false;
		//if we get AIR then go below
		while (!Ok)
		{
			if (world.getBlock(Pos).getType().equals(BlockTypes.AIR))
			{
				Pos = Pos.add(0, -1, 0);
				Position +=1;
			}
			else
			{
				Ok = true;
			}
			if (Position > Size)
			{
				Ok = true;
			}
		}
		return Pos;
	}

	//////
	//
	// check if the blocks (starting from the current position) are equal to the defined pole style
	// args: position vector, the world, the map of poles nodes, pole size
	// return: if this is equal : true else false
	//
	//////
	private boolean CheckPole2(Vector3i Pos, World world, Map<Object, ? extends ConfigurationNode> Pole, int Size)
	{
		int Position = 0;
		boolean Ok = true;
		while (Position < Size)
		{
			final ConfigurateTranslator tr = ConfigurateTranslator.instance();
			final ConfigurationNode node = Pole.get(String.valueOf(Position));
			final DataContainer cont = tr.translateFrom(node);
			final Builder builder = Sponge.getRegistry().createBuilder(Builder.class);
			final BlockState block_Conf = builder.build(cont).get();
			final BlockState block_World = world.getBlock(Pos);
			Pos = Pos.add(0, 1, 0);
			Position++;
			if (!block_Conf.equals(block_World))
			{
				Ok = false;
			}
		}
		return Ok;
	}
}
