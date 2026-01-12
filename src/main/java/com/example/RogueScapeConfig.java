package com.example;

import net.runelite.client.config.*;

import java.awt.*;

@ConfigGroup("RogueScape")
public interface RogueScapeConfig extends Config
{
	@Alpha
	@ConfigItem(
			keyName = "overlayBackgroundColor",
			name = "Pinned task background color",
			description = "Controls the background color of the pinned tasks.",
			position = 1
	)
	// This returns a lovely brown.
	default Color pinnedTaskBackgroundColor()
	{
		return new Color(162, 102, 11, 100);
	}

	@Alpha
	@ConfigItem(
			keyName = "overlayTextColor",
			name = "Pinned task text color",
			description = "Controls the text color of the pinned tasks.",
			position = 2
	)
	default Color pinnedTaskTextColor() { return Color.WHITE; }

	@Alpha
	@ConfigItem(
			keyName = "overlayWidth",
			name = "Overlay width",
			description = "Controls the width of the overlay.",
			position = 3
	)
	default int overlayWidth() { return 120; }

	@ConfigItem(
			keyName = "overlayNameVisible",
			name = "Enable Name",
			description = "Enables or disables the visibility of the task name in the overlay.",
			position = 4
	)
	default boolean isNameVisible() { return true; }

	@ConfigItem(
			keyName = "overlayDescriptionVisible",
			name = "Enable Description",
			description = "Enables or disables the visibility of the task description in the overlay.",
			position = 5
	)
	default boolean isDescriptionVisible() { return true; }

	@ConfigItem(
			keyName = "overlayStatusVisible",
			name = "Enable Status",
			description = "Enables or disables the visibility of the task status in the overlay.",
			position = 6
	)
	default boolean isStatusVisible() { return true; }
}
