package com.ricky30.fence.event;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.spongepowered.api.data.manipulator.mutable.entity.HealthData;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.animal.Animal;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.DisplaceEntityEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.world.World;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import com.ricky30.fence.fence;
import com.ricky30.fence.utils.vector;

import ninja.leaping.configurate.ConfigurationNode;

public class moveEvent
{
	private static ConfigurationNode config = null;

	@Listener
	public void onPlayerMovement(DisplaceEntityEvent.Move.TargetPlayer Event, @First Player player)
	{
		config = fence.plugin.getConfig();
		final Map<Integer, Vector3i> position = new ConcurrentHashMap<Integer, Vector3i>();
		final Map<Integer, UUID> uid = new ConcurrentHashMap<Integer, UUID>();
		final Map<Integer, Integer> Set = new ConcurrentHashMap<Integer, Integer>();
		final List<Integer> nr_Set = new ArrayList<Integer>();
		for (final Object Sets : config.getNode("fence", "pole").getChildrenMap().keySet())
		{
			for (final Object pole: config.getNode("fence", "pole", Sets.toString()).getChildrenMap().keySet())
			{
				int taille = position.size();
				int x, y, z;
				x = config.getNode("fence", "pole", Sets.toString(), pole.toString(), "X").getInt();
				y = config.getNode("fence", "pole", Sets.toString(), pole.toString(), "Y").getInt();
				z = config.getNode("fence", "pole", Sets.toString(), pole.toString(), "Z").getInt();
				final Vector3i vecteur = new Vector3i(x, y, z);
				taille++;
				position.put(taille, vecteur);
				uid.put(taille, UUID.fromString(config.getNode("fence", "pole", Sets.toString(), pole.toString(), "world").getString()));
				Set.put(taille, Integer.parseUnsignedInt(Sets.toString().substring(3)));
			}
		}
		//permet de savoir combien de zones differentes sont défini
		for (final int Sets: Set.values())
		{
			if (nr_Set.isEmpty())
			{
				nr_Set.add(Sets);
			}
			if (!nr_Set.contains(Sets))
			{
				nr_Set.add(Sets);
			}
		}
		//crée les listes de vecteurs que l'on va envoyer
		final List<Vector3i> SetPosition = new ArrayList<Vector3i>();
		final List<UUID> SetWorld = new ArrayList<UUID>();
		for (final Object set_Number: nr_Set.toArray())
		{
			final int stnr = (Integer) set_Number;
			for (final int loop : Set.keySet())
			{
				if (Set.get(loop).equals(stnr))
				{
					SetPosition.add(position.get(loop));
					SetWorld.add(uid.get(loop));
				}
			}
			//on test si on est entre deux poteau
			if (isBetween(SetPosition, SetWorld, player.getLocation().getPosition(), player.getWorld()))
			{
				//if true and player is not in creative or spectator
				if (player.gameMode().get() != GameModes.CREATIVE && player.gameMode().get() != GameModes.SPECTATOR)
				{
					if (config.getNode("Dodamage").getBoolean())
					{
						if (config.getNode("KillPlayer").getBoolean())
						{
							final HealthData health = player.getHealthData();
							final Double level = 0.0;
							health.health().set(level);
							player.offer(health.health().set(level));
						}
						else
						{
							final HealthData health = player.getHealthData();
							final Double level = health.health().get()-0.5;
							health.health().set(level);
							player.offer(health.health().set(level));
						}
					}
				}
			}
			//ne pas oublier d'effacer les listes avant de les remplir a nouveau
			SetPosition.clear();
			SetWorld.clear();
		}
	}

	@Listener
	public void onEntityMovement(DisplaceEntityEvent.Move.TargetLiving Event)
	{
		config = fence.plugin.getConfig();
		final Map<Integer, Vector3i> position = new ConcurrentHashMap<Integer, Vector3i>();
		final Map<Integer, UUID> uid = new ConcurrentHashMap<Integer, UUID>();
		final Map<Integer, Integer> Set = new ConcurrentHashMap<Integer, Integer>();
		final List<Integer> nr_Set = new ArrayList<Integer>();
		for (final Object Sets : config.getNode("fence", "pole").getChildrenMap().keySet())
		{
			for (final Object pole: config.getNode("fence", "pole", Sets.toString()).getChildrenMap().keySet())
			{
				int taille = position.size();
				int x, y, z;
				x = config.getNode("fence", "pole", Sets.toString(), pole.toString(), "X").getInt();
				y = config.getNode("fence", "pole", Sets.toString(), pole.toString(), "Y").getInt();
				z = config.getNode("fence", "pole", Sets.toString(), pole.toString(), "Z").getInt();
				final Vector3i vecteur = new Vector3i(x, y, z);
				taille++;
				position.put(taille, vecteur);
				uid.put(taille, UUID.fromString(config.getNode("fence", "pole", Sets.toString(), pole.toString(), "world").getString()));
				Set.put(taille, Integer.parseUnsignedInt(Sets.toString().substring(3)));
			}
		}
		//permet de savoir combien de zones differentes sont défini
		for (final int Sets: Set.values())
		{
			if (nr_Set.isEmpty())
			{
				nr_Set.add(Sets);
			}
			if (!nr_Set.contains(Sets))
			{
				nr_Set.add(Sets);
			}
		}
		//crée les listes de vecteurs que l'on va envoyer
		final List<Vector3i> SetPosition = new ArrayList<Vector3i>();
		final List<UUID> SetWorld = new ArrayList<UUID>();
		for (final Object set_Number: nr_Set.toArray())
		{
			final int stnr = (Integer) set_Number;
			for (final int loop : Set.keySet())
			{
				if (Set.get(loop).equals(stnr))
				{
					SetPosition.add(position.get(loop));
					SetWorld.add(uid.get(loop));
				}
			}
			Entity entity = Event.getTargetEntity().getBaseVehicle();
			if (!Event.getTargetEntity().getType().equals(EntityTypes.PLAYER))
			{
				//on test si on est entre deux poteau
				if (isBetween(SetPosition, SetWorld, Event.getTargetEntity().getLocation().getPosition(), Event.getTargetEntity().getWorld()))
				{
					if (entity instanceof Animal)
					{
						if (config.getNode("Dodamage").getBoolean())
						{
							if (config.getNode("KillPeacefull").getBoolean())
							{
								final HealthData health = Event.getTargetEntity().getHealthData();
								final Double level = 0.0;
								health.health().set(level);
								Event.getTargetEntity().offer(health.health().set(level));
							}
//							else
//							{
//								final HealthData health = Event.getTargetEntity().getHealthData();
//								final Double level = health.health().get()-0.5;
//								health.health().set(level);
//								Event.getTargetEntity().offer(health.health().set(level));
//							}
						}
					}
					else
					{
						if (config.getNode("Dodamage").getBoolean())
						{
							if (config.getNode("KillMonster").getBoolean())
							{
								final HealthData health = Event.getTargetEntity().getHealthData();
								final Double level = 0.0;
								health.health().set(level);
								Event.getTargetEntity().offer(health.health().set(level));
							}
							else
							{
								final HealthData health = Event.getTargetEntity().getHealthData();
								final Double level = health.health().get()-0.5;
								health.health().set(level);
								Event.getTargetEntity().offer(health.health().set(level));
							}
						}
					}
				}
			}
			//ne pas oublier d'effacer les listes avant de les remplir a nouveau
			SetPosition.clear();
			SetWorld.clear();
		}
	}

	private boolean isBetween(List<Vector3i> pole, List<UUID> World, Vector3d Entity, World playerWorld)
	{
		final Map<Object, ? extends ConfigurationNode> Pole = config.getNode("pole").getChildrenMap();
		boolean ishurt = false;
		if (playerWorld.getUniqueId().equals(World.get(0)))
		{
			Vector3i pole1 = new Vector3i(0,0,0);
			Vector3i pole2 = new Vector3i(0,0,0);
			int pillier = 0;
			boolean End = false;
			final List<Double> Position_test = new ArrayList<Double>();
			while (!End)
			{
				pole1 = pole.get(pillier);
				pillier++;
				if (pillier == pole.size())
				{
					End = true;
					pole2 = pole.get(0);
				}
				else
				{
					pole2 = pole.get(pillier);
				}
				//necessaire ajouter 0.5 pour le centre du bloc
				final double Position = ((pole1.getX()+0.5) - (pole2.getX()+0.5)) * (Entity.getZ() - (pole2.getZ()+0.5)) - ((pole1.getZ()+0.5) - (pole2.getZ()+0.5)) * (Entity.getX() - (pole2.getX()+0.5));
				//player y = 64 -> pole y = 63
				for (int loop = 0; loop < Pole.size(); loop++)
				{
					final double Position2 = ((pole1.getY()+loop+1) - (pole2.getY()+loop+1)) * (Entity.getZ() - (pole2.getZ()+0.5)) - ((pole1.getZ()+0.5) - (pole2.getZ()+0.5)) * (Entity.getY()+loop - (pole2.getY()+loop+1));
					Position_test.add(loop, Position2);
				}

				if (Position >= -0.7 && Position <= 0.7 && vector.IsPositionzero(Position_test) && vector.IsInside(Entity, pole1, pole2.add(0, Pole.size(), 0)))
				{
					ishurt = true;
				}
			}
		}
		return ishurt;
	}
}
