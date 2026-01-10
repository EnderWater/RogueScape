package com.example;

import com.google.inject.Provides;
import javax.inject.Inject;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.NpcLootReceived;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@PluginDescriptor(
	name = "RogueScape"
)
public class RogueScapePlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private TaskProgressOverlay taskProgressOverlay;

	@Inject
	private RogueScapeConfig config;

	@Getter
	private final List<String> previousKills = new ArrayList<>();
	private int MAX_KILL_QUEUE_SIZE;
	private String previousKilledActorName;

	@Override
	protected void startUp() throws Exception
	{
		overlayManager.add(taskProgressOverlay);
		log.debug("Example started!");
		MAX_KILL_QUEUE_SIZE = config.recentTasks();
	}

	@Override
	protected void shutDown() throws Exception
	{
		log.debug("Example stopped!");
	}

	public String getMostRecentKillName() {
		return this.previousKills.get(0);
	}

	@Subscribe
	public void onNpcLootReceived(NpcLootReceived npcLootReceived) {
		NPC npc = npcLootReceived.getNpc();
		String npcName = npc.getName();
		addKill(npcName);

		client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "Loot received from " + npcName, "");
	}

	public void addKill(String npcName) {
//		If there are more names in the list than the max allowed, remove the latest one (at the front)
		if (previousKills.size() >= MAX_KILL_QUEUE_SIZE) {
			previousKills.remove(0);
		}
		previousKills.add(npcName);
	}

	@Provides
	RogueScapeConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(RogueScapeConfig.class);
	}
}
