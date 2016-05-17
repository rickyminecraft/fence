package com.ricky30.fence.event;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.manipulator.mutable.entity.HealthData;
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
			for (final Object pillar: config.getNode("fence", "pole", Sets.toString()).getChildrenMap().keySet())
			{
				int taille = position.size();
				int x, y, z;
				x = config.getNode("fence", "pole", Sets.toString(), pillar.toString(), "X").getInt();
				y = config.getNode("fence", "pole", Sets.toString(), pillar.toString(), "Y").getInt();
				z = config.getNode("fence", "pole", Sets.toString(), pillar.toString(), "Z").getInt();
				final Vector3i vecteur = new Vector3i(x, y, z);
				taille++;
				position.put(taille, vecteur);
				uid.put(taille, UUID.fromString(config.getNode("fence", "pole", Sets.toString(), pillar.toString(), "world").getString()));
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

	private boolean isBetween(List<Vector3i> pillar, List<UUID> World, Vector3d player, World playerWorld)
	{
		boolean ishurt = false;
		if (playerWorld.getName().equals(Sponge.getServer().getWorld(World.get(0)).get().getName()))
		{
			Vector3i pilier1 = new Vector3i(0,0,0);
			Vector3i pilier2 = new Vector3i(0,0,0);
			int pillier = 0;
			boolean End = false;
			while (!End)
			{
				pilier1 = pillar.get(pillier);
				pillier++;
				if (pillier == pillar.size())
				{
					End = true;
					pilier2 = pillar.get(0);
				}
				else
				{
					pilier2 = pillar.get(pillier);
				}
				final Vector3d vecteur_pilier = new Vector3d (pilier1.getX() - pilier2.getX(), pilier1.getY() - pilier2.getY(), pilier1.getZ() - pilier2.getZ());
				final Vector3d vecteur_joueur = new Vector3d (pilier1.getX() - player.getX(), pilier1.getY() - (player.getY()-1), pilier1.getZ() - player.getZ());
				final double d3 = vecteur_joueur.getZ() * vecteur_pilier.getX();
				final double d4 = vecteur_joueur.getX() * vecteur_pilier.getZ();
				final Double d5 = d3-d4;

				if (d5 >= -4.0 && d5 <= 3.0 && vector.IsInside(player, pilier1, pilier2.add(0, 2.0, 0)))
				{
					ishurt = true;
				}
			}
		}
		return ishurt;
	}
}
