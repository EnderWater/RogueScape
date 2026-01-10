package com.example;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("RogueScape")
public interface RogueScapeConfig extends Config
{
	@ConfigItem(
		keyName = "recentTasks",
		name = "Recent Task Number",
		description = "The number of most recent tasks being progressed."
	)
	default int recentTasks()
	{
		return 4;
	}
}
