package com.example;

import com.example.cards.CardManager;
import com.example.overlays.PackOpeningOverlay;
import com.example.overlays.PackOverlay;
import com.example.overlays.TaskProgressOverlay;
import com.example.panels.RogueScapePanel;
import com.example.relics.RelicManager;
import com.example.tasks.TaskManager;
//import com.example.widgets.WidgetManager;

import javax.inject.Inject;

import com.google.inject.Provides;
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
    private PackOpeningOverlay packOpeningOverlay;

    @Inject
    private RogueScapeConfig config;

    @Inject
    private ClientToolbar clientToolbar;

    @Inject
    private EventBus eventBus;

    @Inject
    @Getter
    private TaskManager taskManager;

    @Inject
    @Getter
    // Initialize the card manager first so you can inject it elsewhere
    private CardManager cardManager;

    @Inject
    // Initialize the relic manager
    private RelicManager relicManager;

//    @Inject
//    private WidgetManager widgetManager;

    // This panel needs to be injected because it is dependent on other singletons.
    // If a class depends on other singletons, it should be a singleton itself that injects those singletons
    @Inject
    private RogueScapePanel panel;

    private final String taskFilePath = "plugins/roguescape/tasks.json";
    private int gameTicksSinceLastSave = 0;
    private NavigationButton navButton;

    @Override
    protected void startUp() throws Exception {
        this.overlayManager.add(this.taskProgressOverlay);
        this.overlayManager.add(this.packOverlay);
        this.overlayManager.add(this.packOpeningOverlay);

        this.panel = new RogueScapePanel(this.taskManager, this.cardManager);

        // Create the button on the toolbar to open the panel
        this.navButton = NavigationButton.builder()
                .tooltip("RogueScape")
                .icon(ImageUtil.loadImageResource(
                        RogueScapePlugin.class,
                        "icons/roguescape_icon.png"
                ))
                .panel(this.panel)
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
