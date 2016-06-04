package com.ricky30.fence.utils;

public class managePole
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
