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
import com.ricky30.fence.utils.poleSet;

import ninja.leaping.configurate.ConfigurationNode;

public class blocEvent
{
	private ConfigurationNode config = null;

	@Listener
	public void oninteractblockPlace(ChangeBlockEvent.Place Event, @First Player player)
	{
		this.config = fence.plugin.getConfig();
		for (final Transaction<BlockSnapshot> blocksnap : Event.getTransactions())
		{
			Vector3i Pos = blocksnap.getDefault().getLocation().get().getBlockPosition();
			final World world = player.getWorld();
			final Map<Object, ? extends ConfigurationNode> Pole = config.getNode("pole").getChildrenMap();
			int Position = 0;
			Pos = Pos.add(0, Pole.size(), 0);
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
				if (Position > Pole.size())
				{
					Ok = true;
				}
			}
			Position = 0;
			Pos = Pos.sub(0, Pole.size()-1, 0);
			while (Position < Pole.size())
			{
				final ConfigurateTranslator tr = ConfigurateTranslator.instance();
				final ConfigurationNode node = Pole.get(String.valueOf(Position));
				final DataContainer cont = tr.translateFrom(node);
				//a cause d'une erreur eclipse, obliger de faire ça
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
			if (Ok)
			{
				int PoleNumber = 0;
				if (!config.getNode("fence", "pole", "Set0").getChildrenMap().isEmpty())
				{
					for (final Object pole : config.getNode("fence", "pole", "Set0").getChildrenMap().keySet())
					{
						int PoleNumbertmp;
						PoleNumbertmp = this.config.getNode("fence", "pole", "Set0", pole.toString(), "Number").getInt();
						if (PoleNumbertmp > PoleNumber)
						{
							PoleNumber = PoleNumbertmp;
						}
					}
					PoleNumber++;
				}
				Pos = Pos.sub(0,Pole.size(),0);
				final String Number = String.valueOf(Pos.getX()) + String.valueOf(Pos.getY()) + String.valueOf(Pos.getZ());
				final byte[] b = Number.getBytes();
				final String Name = UUID.nameUUIDFromBytes(b).toString();
				this.config.getNode("fence", "pole", "Set0", Name, "X").setValue(Pos.getX());
				this.config.getNode("fence", "pole", "Set0", Name, "Y").setValue(Pos.getY());
				this.config.getNode("fence", "pole", "Set0", Name, "Z").setValue(Pos.getZ());
				this.config.getNode("fence", "pole", "Set0", Name, "world").setValue(world.getUniqueId().toString());
				this.config.getNode("fence", "pole", "Set0", Name, "Number").setValue(PoleNumber);
				fence.plugin.save();
				player.sendMessage(Text.of("A pole has been made"));
			}
		}
	}

	@Listener
	public void oninteractblockRemove(ChangeBlockEvent.Break Event, @First Player player)
	{
		this.config = fence.plugin.getConfig();
		for (final Transaction<BlockSnapshot> blocksnap : Event.getTransactions())
		{
			Vector3i Pos = blocksnap.getDefault().getLocation().get().getBlockPosition();
			final World world = player.getWorld();
			final Map<Object, ? extends ConfigurationNode> Pole = config.getNode("pole").getChildrenMap();
			int Position = 0;
			Pos = Pos.add(0, Pole.size(), 0);
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
				if (Position > Pole.size())
				{
					Ok = true;
				}
			}
			Pos = Pos.add(0, -1, 0);
			final String Number = String.valueOf(Pos.getX()) + String.valueOf(Pos.getY()) + String.valueOf(Pos.getZ());
			final byte[] b = Number.getBytes();
			final String Name = UUID.nameUUIDFromBytes(b).toString();
			for (final Object node : this.config.getNode("fence", "pole").getChildrenMap().keySet())
			{
				if (this.config.getNode("fence", "pole", node.toString()).getChildrenMap().containsKey(Name))
				{
					if (world.getUniqueId().equals(UUID.fromString(this.config.getNode("fence", "pole", node.toString(), Name, "world").getString())))
					{
						this.config.getNode("fence", "pole", node.toString()).removeChild(Name);
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
		this.config = fence.plugin.getConfig();
		if (manageSet.IsSetactive())
		{
			manageSet.SetActive(false);
			Vector3i Pos = Event.getTargetBlock().getPosition();
			final World world = player.getWorld();
			final Map<Object, ? extends ConfigurationNode> Pole = config.getNode("pole").getChildrenMap();
			int Position = 0;
			Pos = Pos.add(0, Pole.size(), 0);
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
				if (Position > Pole.size())
				{
					Ok = true;
				}
			}
			Position = 0;
			Pos = Pos.sub(0, Pole.size()-1, 0);
			while (Position < Pole.size())
			{
				final ConfigurateTranslator tr = ConfigurateTranslator.instance();
				final ConfigurationNode node = Pole.get(String.valueOf(Position));
				final DataContainer cont = tr.translateFrom(node);
				//a cause d'une erreur eclipse, obliger de faire ça
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

			if (Ok)
			{
				Pos = Pos.sub(0,Pole.size(),0);
				final String Number = String.valueOf(Pos.getX()) + String.valueOf(Pos.getY()) + String.valueOf(Pos.getZ());
				final byte[] b = Number.getBytes();
				final String Name = UUID.nameUUIDFromBytes(b).toString();
				int x = 0, y = 0, z = 0;
				UUID Worlduid = null;
				boolean Done = false;
				for (final Object node : this.config.getNode("fence", "pole").getChildrenMap().keySet())
				{
					if (this.config.getNode("fence", "pole", node.toString()).getChildrenMap().containsKey(Name))
					{
						x = config.getNode("fence", "pole", node.toString(), Name, "X").getInt();
						y = config.getNode("fence", "pole", node.toString(), Name, "Y").getInt();
						z = config.getNode("fence", "pole", node.toString(), Name, "Z").getInt();
						Worlduid = UUID.fromString(config.getNode("fence", "pole", node.toString(), Name, "world").getString());
						if (world.getUniqueId().equals(Worlduid))
						{
							Done = true;
							this.config.getNode("fence", "pole", node.toString()).removeChild(Name);
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
							PoleNumbertmp = this.config.getNode("fence", "pole", set, pole.toString(), "Number").getInt();
							if (PoleNumbertmp > PoleNumber)
							{
								PoleNumber = PoleNumbertmp;
							}
						}
						PoleNumber++;
					}
					this.config.getNode("fence", "pole", set, Name, "X").setValue(x);
					this.config.getNode("fence", "pole", set, Name, "Y").setValue(y);
					this.config.getNode("fence", "pole", set, Name, "Z").setValue(z);
					this.config.getNode("fence", "pole", set, Name, "world").setValue(Worlduid.toString());
					this.config.getNode("fence", "pole", set, Name, "Number").setValue(PoleNumber);
					fence.plugin.save();
					player.sendMessage(Text.of("Pole set ",manageSet.loadSetnumber()));
				}
			}
		}
		if (poleSet.IsSetactive())
		{
			poleSet.SetActive(false);
			//faire une boucle jusqu'en haut
			Vector3i pos = Event.getTargetBlock().getPosition();
			final World world = player.getWorld();
			int position = 0;
			final Map<Object, ? extends ConfigurationNode> cn = this.config.getNode("pole").getChildrenMap();
			for (final Object node :cn.keySet())
			{
				this.config.getNode("pole").removeChild(node);
			}
			while (!world.getBlock(pos).getType().equals(BlockTypes.AIR))
			{
				final BlockState block = world.getBlock(pos);
				this.config.getNode("pole", String.valueOf(position), "BlockState").setValue(block.toString());
				position++;
				pos = pos.add(0, 1, 0);
			}
			fence.plugin.save();
			player.sendMessage(Text.of("Pole type defined"));
		}
	}
}
