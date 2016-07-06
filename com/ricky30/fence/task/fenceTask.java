package com.ricky30.fence.task;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.effect.particle.ParticleEffect.Builder;
import org.spongepowered.api.world.World;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import com.ricky30.fence.fence;

import ninja.leaping.configurate.ConfigurationNode;

public class fenceTask
{
	static Map<Integer, Vector3i> position = new ConcurrentHashMap<Integer, Vector3i>();
	static Map<Integer, UUID> uid = new ConcurrentHashMap<Integer, UUID>();
	static Map<Integer, Integer> Set = new ConcurrentHashMap<Integer, Integer>();
	static Map<Integer, Integer> Number = new ConcurrentHashMap<Integer, Integer>();

	private static ParticleEffect p = Sponge.getRegistry().createBuilder(Builder.class).type(ParticleTypes.REDSTONE).build();
	private static ConfigurationNode config = null;

	public static void run()
	{
		config = fence.plugin.getConfig();
		//ajoute les différentes valeurs dans leurs map respectives
		for (final Object Sets : config.getNode("fence", "pole").getChildrenMap().keySet())
		{
			//set0 is a stock set where every new pole is added before getting a new set number
			if (!Sets.toString().equals("Set0"))
			{
				for (final Object Name: config.getNode("fence", "pole", Sets.toString()).getChildrenMap().keySet())
				{
					int taille = position.size();
					int x, y, z, number;
					World world;
					x = config.getNode("fence", "pole", Sets.toString(), Name, "X").getInt();
					y = config.getNode("fence", "pole", Sets.toString(), Name, "Y").getInt();
					z = config.getNode("fence", "pole", Sets.toString(), Name, "Z").getInt();
					number = config.getNode("fence", "pole", Sets.toString(), Name, "Number").getInt();
					world = Sponge.getServer().getWorld(UUID.fromString(config.getNode("fence", "pole", Sets.toString(), Name, "world").getString())).get();
					final Vector3i vecteur = new Vector3i(x, y, z);
					taille++;
					Number.put(taille, number);
					position.put(taille, vecteur);
					uid.put(taille, world.getUniqueId());
					Set.put(taille, Integer.parseUnsignedInt(Sets.toString().substring(3)));
				}
			}
		}
		//permet de savoir combien de zones differentes sont défini
		final List<Integer> nr_Set = new ArrayList<Integer>();
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
		final List<Integer> SetNumber = new ArrayList<Integer>();
		for (final Object set_Number: nr_Set.toArray())
		{
			final int stnr = (Integer) set_Number;
			for (final int a : Set.keySet())
			{
				if (Set.get(a).equals(stnr))
				{
					SetPosition.add(position.get(a));
					SetWorld.add(uid.get(a));
					SetNumber.add(Number.get(a));
				}
			}
			//tri les different pilier par leurs numero
			boolean ok = false;
			final List<Vector3i> SetPositiontmp = new ArrayList<Vector3i>();
			int Current_Number = 0;
			int Current_Pole_Number = 0;
			int Index = 0;
			while (!ok)
			{
				//securite, quand on change de set, pas sur que les numeros suivent
				boolean Ok2 = false;
				while (!Ok2)
				{
					if (!SetNumber.contains(Current_Pole_Number))
					{
						Current_Pole_Number++;
					}
					else
					{
						Ok2 = true;
					}
				}
				while(SetNumber.get(Index) != Current_Pole_Number)
				{
					Index++;
				}
				SetPositiontmp.add(SetPosition.get(Index));
				Current_Pole_Number++;
				Index = 0;
				Current_Number++;
				if (Current_Number == SetNumber.size())
				{
					ok = true;
				}
			}
			//on ajoute les effets
			doSet(SetPositiontmp, SetWorld, stnr);
			//ne pas oublier d'effacer les listes avant de les remplir a nouveau
			SetPosition.clear();
			SetWorld.clear();
			SetNumber.clear();
		}

		//et on efface tout
		position.clear();
		uid.clear();
		Set.clear();
	}

	//set sert juste d'indicateur pour debug
	private static void doSet(List<Vector3i> pole, List<UUID> World, int Set)
	{
		final Map<Object, ? extends ConfigurationNode> Pole = config.getNode("pole").getChildrenMap();
		final int Size = Pole.size() -1;
		// combien de poteau sont pret à traiter
		int pole_ready = 0;
		//on crée les vecteurs de nos 2 poteaux
		Vector3i pole1 = new Vector3i(0,0,0);
		Vector3i pole2 = new Vector3i(0,0,0);
		//si pas plus d'un poteau référencer, alors on ne fait rien
		if (pole.size() > 1)
		{
			World world;
			//et enfin ici on boucle
			//a noter ici l'important etant le <= au lieu de <
			for (int nr_pole = 0; nr_pole <= pole.size(); nr_pole++)
			{
				//si la boucle egal i alors on est en dehors du tableau (vu que c'est une arraylist, 5 valeurs egal 0,1,2,3,4) est on récupere la premiere valeur
				//c'est ce comportement qui permet de 'fermer la boucle'
				//sinon on recupere la valeur courante
				if (nr_pole == pole.size())
				{
					world = Sponge.getServer().getWorld(World.get(0)).get();
				}
				else
				{
					world = Sponge.getServer().getWorld(World.get(nr_pole)).get();
				}
				//pilier pret a traiter +1
				pole_ready++;
				//et on switch
				switch(pole_ready)
				{
					case 1:
						//on recupere le vecteur courant dans la liste de vecteur
						//a noter que cette 'case' n'est atteinte qu'un fois
						pole1 = pole.get(nr_pole);
						break;
					case 2:
						//si la boucle egal i alors on est en dehors du tableau (vu que c'est une arraylist, 5 valeurs egal 0,1,2,3,4) est on récupere la premiere valeur
						//c'est ce comportement qui permet de 'fermer la boucle'
						//sinon on recupere la valeur courante
						if (nr_pole == pole.size())
						{
							pole2 = pole.get(0);
						}
						else
						{
							pole2 = pole.get(nr_pole);
						}
						break;
				}

				//si on a au moins 2 piliers a relier, alors on ajoute les effets
				// ne renvoie faux qu'a la premiere passe
				if (pole_ready == 2)
				{
					//boucle pour ajouter les effets
					//important ici on doit convertir les integer des vecteurs en double sinon on peut pas avoir 1,1 1,2 ... on a juste 1 2 ...
					for (final Vector3d b : raytrace(new Vector3d(pole1.getX()+0.5, pole1.getY()+0.5, pole1.getZ()+0.5), new Vector3d(pole2.getX()+0.5, pole2.getY()+0.5, pole2.getZ()+0.5)))
					{
						//on ajoute l'effet sur toute les lignes
						for (int loop = 0; loop <= Size; loop++)
						{
							world.spawnParticles(p, b.add(0, loop, 0));
						}
					}
					//et pour continuer (si plus de 2 pilier) on fait ici ce qu'on peut appeler un decalage
					//on deplace le vecteur du pilier2 dans pilier1
					//et le numero du pilier pret a etre rempli avec une nouvelle valeur est mit a 1
					//pour toujours remplir le pilier2 et eviter pilier1
					pole1 = pole2;
					pole_ready = 1;
				}
			}
		}
	}

	//////
	//
	// renvoie une liste de vecteur indiquant les position des effet a ajouter
	// arguments: les 2 vecteurs utile au calcul
	// notons que pour la facilite de positionnement, il est necessaire d'avoir des vecteurs Double et non Integer
	//
	//////
	private static List<Vector3d> raytrace(Vector3d un, Vector3d deux)
	{
		//d'abord on cree une liste qui contiendra nos vecteurs. c'est elle que l'on renvoie
		final List<Vector3d> list = new ArrayList<Vector3d>();
		//ici on obtient un vecteur qui contient les distance entre nos 2 vecteurs fournit
		//si vecteur 1 (x) egal 10 et vecteur 2 (x) egal 16 le resultat egal -6
		final Vector3d vecteur = new Vector3d (un.getX() - deux.getX(), un.getY() - deux.getY(), un.getZ() - deux.getZ());
		//ici on obtient le nombre de vecteur a mettre dans la liste
		//ou pour faire simple, le nombre d'effet a ajouter entre 2 pilier
		int nb_effet = 0;
		//ici pour chaque direction, si la valeur est negative, on la convertit en positive
		//evite bug genant du genre nb_effet = 1 + 3 + -4 = 0
		if (vecteur.getX() < 0)
		{
			nb_effet += (int) -vecteur.getX();
		}
		else
		{
			nb_effet += (int) vecteur.getX();
		}
		if (vecteur.getY() < 0)
		{
			nb_effet += (int) -vecteur.getY();
		}
		else
		{
			nb_effet += (int) vecteur.getY();
		}
		if (vecteur.getZ() < 0)
		{
			nb_effet += (int) -vecteur.getZ();
		}
		else
		{
			nb_effet += (int) vecteur.getZ();
		}
		//ici c'est le plus utile, on prend nos distance et on les divise par le nombre d'effet a ajouter
		//exemple: si la distance entre 2 point et de 5, avec 10 effet on obtient 0.5
		//en resultat, le premier vecteur contient 0, le suivant 0.5 etc
		final Double x = vecteur.getX() / nb_effet;
		final Double y = vecteur.getY() / nb_effet;
		final Double z = vecteur.getZ() / nb_effet;
		//boucle en partant de 0 pour que le premier effet soit sur le pilier
		for (int effet = 0; effet < nb_effet; effet++)
		{
			//ici on ajoute les vecteurs de la facon suivante:
			//on prend le vecteur du premier pilier
			//on lui supprime la distance fournit par les vecteurs avant la boucle multiplier par la position actuel (-277 - (1x0.5) par exemple)
			//et on obtient avec ca nos vecteurs intermediaire ou vont se trouver les effets
			list.add(new Vector3d(un.getX()-(x*effet), un.getY()-(y*effet), un.getZ()-(z*effet)));
		}
		//et enfin, on retourne la liste des vecteurs (position) de nos effets
		return list;
	}
}
