package com.ricky30.fence.utils;

public class manageSet
{
	private static boolean isSet;
	private static int Set_number = 0;

	public static boolean IsSetactive()
	{
		return isSet;
	}

	public static void SetActive(boolean Active)
	{
		isSet = Active;
	}

	public static void saveSetNumber(int Set)
	{
		Set_number = Set;
	}

	public static int loadSetnumber()
	{
		return Set_number;
	}
}
