package com.ricky30.fence.utils;

import java.util.List;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;

public class vector
{
	public static Vector3i Min(Vector3i first, Vector3i second)
	{
		int x1, y1, z1;
		if (first.getX() < second.getX())
		{
			x1 = first.getX();
		}
		else
		{
			x1 = second.getX();

		}
		if (first.getY() < second.getY())
		{
			y1 = first.getY();
		}
		else
		{
			y1 = second.getY();
		}
		if (first.getZ() < second.getZ())
		{
			z1 = first.getZ();
		}
		else
		{
			z1 = second.getZ();
		}
		final Vector3i vector3i = new Vector3i(x1, y1, z1);
		return vector3i;
	}

	public static Vector3i Max(Vector3i first, Vector3i second)
	{
		int x1, y1, z1;
		if (first.getX() < second.getX())
		{
			x1 = second.getX();
		}
		else
		{
			x1 = first.getX();
		}
		if (first.getY() < second.getY())
		{
			y1 = second.getY();
		}
		else
		{
			y1 = first.getY();
		}
		if (first.getZ() < second.getZ())
		{
			z1 = second.getZ();
		}
		else
		{
			z1 = first.getZ();
		}
		final Vector3i vector3i = new Vector3i(x1, y1, z1);
		return vector3i;
	}

	public static boolean IsInside(Vector3d player, Vector3i one, Vector3i two)
	{
		final Vector3i Min = Min(one, two);
		final Vector3i Max = Max(one, two);
		final Vector3i Joueur = player.toInt();
		Boolean InsideX = false;
		Boolean InsideY = false;
		Boolean InsideZ = false;
		if ((Joueur.getX() >= Min.getX()) && (Joueur.getX() <= Max.getX()))
		{
			InsideX = true;
		}
		if ((Joueur.getY() >= Min.getY()) && (Joueur.getY() <= Max.getY()))
		{
			InsideY = true;
		}
		if ((Joueur.getZ() >= Min.getZ()) && (Joueur.getZ() <= Max.getZ()))
		{
			InsideZ = true;
		}
		if (InsideX && InsideY && InsideZ)
		{
			return true;
		}

		return false;
	}

	public static boolean IsPositionzero(List<Double> Positions)
	{
		boolean Ok = false;
		for (int loop = 0; loop < Positions.size(); loop++)
		{
			//need to make it bigger (-1.0 - 1.0) not high enough for special case
			if (Positions.get(loop)>= -2.5 && Positions.get(loop) <= 2.5)
			{
				Ok = true;
			}
		}
		return Ok;
	}
}
