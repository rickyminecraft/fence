package com.ricky30.fence.utils;

public class poleSet
{
	private static boolean isSet;

	public static boolean IsSetactive()
	{
		return isSet;
	}

	public static void SetActive(boolean Active)
	{
		isSet = Active;
	}
}
