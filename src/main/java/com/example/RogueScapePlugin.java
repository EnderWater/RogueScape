package com.example;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Actor;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.ActorDeath;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

@Slf4j
@PluginDescriptor(
	name = "RogueScape"
)
public class RogueScapePlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private RogueScapeConfig config;

	@Override
	protected void startUp() throws Exception
	{

		log.debug("Example started!");
	}

	@Override
	protected void shutDown() throws Exception
	{
		log.debug("Example stopped!");
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged)
	{
		if (gameStateChanged.getGameState() == GameState.LOGGED_IN)
		{
			client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "Example says " + config.greeting(), null);
		}
	}

	@Subscribe
	protected void ActorDeath(Actor actor) {
		client.addChatMessage(ChatMessageType.GAMEMESSAGE, "God", "Dang " + actor.getName() + ",you suck...", null);
	}

	@Provides
	RogueScapeConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(RogueScapeConfig.class);
	}
}
