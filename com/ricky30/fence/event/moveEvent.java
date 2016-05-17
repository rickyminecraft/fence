package com.ricky30.fence.event;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.manipulator.mutable.entity.HealthData;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.player.Player;
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
			//on ajoute les effets
			if (isBetween(SetPosition, SetWorld, player.getLocation().getPosition(), player.getWorld()))
			{
				if (config.getNode("KillPlayer").getBoolean())
				{
					final HealthData health = player.getHealthData();
					final Double level = health.health().get()-0.1;
					health.health().set(level);
					player.offer(health.health().set(level));
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
			if (!Event.getTargetEntity().getType().equals(EntityTypes.PLAYER))
			{
				//on ajoute les effets
				if (isBetween(SetPosition, SetWorld, Event.getTargetEntity().getLocation().getPosition(), Event.getTargetEntity().getWorld()))
				{
					if (config.getNode("KillMonster").getBoolean())
					{
						final HealthData health = Event.getTargetEntity().getHealthData();
						final Double level = 0.0;
						health.health().set(level);
						Event.getTargetEntity().offer(health.health().set(level));
					}
				}
			}
			//ne pas oublier d'effacer les listes avant de les remplir a nouveau
			SetPosition.clear();
			SetWorld.clear();
		}
	}

	//to make better with y take in count
	private boolean isBetween(List<Vector3i> pole, List<UUID> World, Vector3d player, World playerWorld)
	{
		boolean ishurt = false;
		if (playerWorld.getName().equals(Sponge.getServer().getWorld(World.get(0)).get().getName()))
		{
			Vector3i pole1 = new Vector3i(0,0,0);
			Vector3i pole2 = new Vector3i(0,0,0);
			int pillier = 0;
			boolean End = false;
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
				final Vector3d vecteur_pilier = new Vector3d (pole1.getX() - pole2.getX(), pole1.getY() - pole2.getY(), pole1.getZ() - pole2.getZ());
				final Vector3d vecteur_joueur = new Vector3d (pole1.getX() - player.getX(), pole1.getY() - (player.getY()-1), pole1.getZ() - player.getZ());
				final double d3 = vecteur_joueur.getZ() * vecteur_pilier.getX();
				final double d4 = vecteur_joueur.getX() * vecteur_pilier.getZ();
				final Double d5 = d3-d4;

				if (d5 >= -4.0 && d5 <= 2.0 && vector.IsInside(player, pole1, pole2.add(0, 2.0, 0)))
				{
					ishurt = true;
				}
			}
		}
		return ishurt;
	}
}
