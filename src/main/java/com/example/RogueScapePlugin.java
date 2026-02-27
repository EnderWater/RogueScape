package com.example;

import com.example.cards.CardManager;
import com.example.overlays.ChunkIndicatorOverlay;
import com.example.overlays.PackOpeningOverlay;
import com.example.overlays.PackCountOverlay;
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
import net.runelite.client.input.MouseListener;
import net.runelite.client.input.MouseManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.ImageUtil;

import java.awt.event.MouseEvent;

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
    private PackCountOverlay packCountOverlay;

    @Inject
    private PackOpeningOverlay packOpeningOverlay;

    @Inject
    private ChunkIndicatorOverlay chunkIndicatorOverlay;

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

    // This panel needs to be injected because it is dependent on other singletons.
    // If a class depends on other singletons, it should be a singleton itself that injects those singletons
    @Inject
    private RogueScapePanel panel;

    @Inject
    private MouseManager mouseManager;

    private final MouseListener mouseListener = new MouseListener()
    {
        @Override
        public MouseEvent mouseClicked(MouseEvent mouseEvent) {
            return mouseEvent;
        }

        @Override
        public MouseEvent mousePressed(MouseEvent event)
        {
            int overlayIndex = packOpeningOverlay.isClickOnButton(event);
            if (overlayIndex >= 0 && cardManager.isPackOpen())
            {
                cardManager.selectCard(cardManager.getOverlayCards().get(overlayIndex));
                cardManager.completePackOpening();
                event.consume();
            }

            return event;
        }

        @Override
        public MouseEvent mouseReleased(MouseEvent mouseEvent) {
            return mouseEvent;
        }

        @Override
        public MouseEvent mouseEntered(MouseEvent mouseEvent) {
            return mouseEvent;
        }

        @Override
        public MouseEvent mouseExited(MouseEvent mouseEvent) {
            return mouseEvent;
        }

        @Override
        public MouseEvent mouseDragged(MouseEvent mouseEvent) {
            return mouseEvent;
        }

        @Override
        public MouseEvent mouseMoved(MouseEvent mouseEvent) {
            return mouseEvent;
        }
    };

    private final String taskFilePath = "plugins/roguescape/tasks.json";
    private int gameTicksSinceLastSave = 0;
    private NavigationButton navButton;

    @Override
    protected void startUp() throws Exception {
        this.overlayManager.add(this.taskProgressOverlay);
        this.overlayManager.add(this.packCountOverlay);
        this.overlayManager.add(this.packOpeningOverlay);
        this.overlayManager.add(this.chunkIndicatorOverlay);

        this.mouseManager.registerMouseListener(this.mouseListener);

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
