package com.example;

import com.example.cards.CardManager;
import com.example.cards.CardReaderWriter;
import com.example.overlays.PackOverlay;
import com.example.overlays.TaskProgressOverlay;
import com.example.panels.RogueScapePanel;
import com.example.relics.RelicManager;
import com.example.tasks.TaskManager;
import com.example.widgets.WidgetManager;
import com.google.inject.Provides;

import javax.inject.Inject;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.FakeXpDrop;
import net.runelite.api.events.GameTick;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.NpcLootReceived;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.ImageUtil;

@Slf4j
@PluginDescriptor(name = "RogueScape")
public class RogueScapePlugin extends Plugin {
    @Inject
    private Client client;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private TaskProgressOverlay taskProgressOverlay;

    @Inject
    private PackOverlay packOverlay;

    @Inject
    private RogueScapeConfig config;

    @Inject
    private ClientToolbar clientToolbar;

    private EventBus eventBus;

    @Getter
    private TaskManager taskManager;

    @Getter
    // Initialize the card manager first so you can inject it elsewhere
    private CardManager cardManager;

    // Initialize the relic manager
    private RelicManager relicManager;

    private WidgetManager widgetManager;

    private final String taskFilePath = "plugins/roguescape/tasks.json";
    private int gameTicksSinceLastSave = 0;
    private NavigationButton navButton;

    @Override
    protected void startUp() throws Exception {
        this.overlayManager.add(this.taskProgressOverlay);
        this.overlayManager.add(this.packOverlay);

        // Initialize the card manager
        this.cardManager = CardReaderWriter.loadCardManager();

        // Initialize the task manager
        this.taskManager = new TaskManager(this.cardManager);

        // Create the event bus
        this.eventBus = new EventBus();

        // Initialize the relic manager
        this.relicManager = new RelicManager(this.cardManager, this.eventBus);

        // Initialize the widget manager
        this.widgetManager = new WidgetManager(this.client);

        // Create the side panel
        RogueScapePanel panel = new RogueScapePanel(this.taskManager, this.cardManager);

        // Create the button on the toolbar to open the panel
        this.navButton = NavigationButton.builder()
                .tooltip("RogueScape")
                .icon(ImageUtil.loadImageResource(
                        RogueScapePlugin.class,
                        "icons/roguescape_icon.png"
                ))
                .panel(panel)
                .build();

        this.clientToolbar.addNavigation(this.navButton);
    }

    @Override
    protected void shutDown() throws Exception {
        if (navButton != null)
        {
            this.clientToolbar.removeNavigation(navButton);
            this.navButton = null;
        }
    }

    // Relates only to kill tasks
    @Subscribe
    public void onNpcLootReceived(NpcLootReceived npcLootReceived) {
        NPC npc = npcLootReceived.getNpc();
        String npcName = npc.getName();
    }

    @Subscribe
    public void onFakeXpDrop(FakeXpDrop fakeXpDrop) {
        Skill skill = fakeXpDrop.getSkill();
        float xpAmount = fakeXpDrop.getXp();
    }

    @Subscribe
    public void onGameTick(GameTick gameTick) {
        if (gameTicksSinceLastSave >= 25) {
            taskManager.saveTasks();
            gameTicksSinceLastSave = 0;
        }
        gameTicksSinceLastSave++;
    }

    @Provides
    RogueScapeConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(RogueScapeConfig.class);
    }
}
