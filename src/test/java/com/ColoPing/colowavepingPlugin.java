package com.ColoPing;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class colowavepingPlugin
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(plugin.class);
		RuneLite.main(args);
	}
}