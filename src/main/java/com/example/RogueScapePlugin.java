package com.example;

import com.example.cards.CardManager;
import com.example.overlays.*;
import com.example.packs.PackManager;
import com.example.panels.RogueScapePanel;
import com.example.relics.RelicManager;
import com.example.tasks.TaskManager;
//import com.example.widgets.WidgetManager;

import javax.inject.Inject;

import com.google.inject.Provides;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
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
    private OverlayStateManager overlayStateManager;

    @Inject
    private TaskProgressOverlay taskProgressOverlay;

    @Inject
    private PackCountOverlay packCountOverlay;

    @Inject
    private CardListOverlay cardListOverlay;

    @Inject
    private ChunkIndicatorOverlay chunkIndicatorOverlay;

    @Inject
    private ContainerOverlay containerOverlay;

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
    @Getter
    private PackManager packManager;

    @Inject
    // Initialize the relic manager
    private RelicManager relicManager;

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
            // Check if the click was in the window first
            if (containerOverlay.handleClick(event)) {
                event.consume();
            }

            // Then check if the click was on the pack
            if (cardListOverlay.isClickOnButton(event))
            {
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

    // Tracks how many ticks since the plugin last saved
    private int gameTicksSinceLastSave = 0;

    // The button on the sidebar used to open the panels
    private NavigationButton navButton;

    private int lastChunkX = -1;
    private int lastChunkY = -1;

    @Override
    protected void startUp() throws Exception {
        this.overlayManager.add(this.taskProgressOverlay);
        this.overlayManager.add(this.packCountOverlay);
//        this.overlayManager.add(this.packOpeningOverlay);
        this.overlayManager.add(this.chunkIndicatorOverlay);
        this.overlayManager.add(this.containerOverlay);

        this.mouseManager.registerMouseListener(this.mouseListener);

        // Create the side panel and then add it to RuneLite
        RogueScapePanel panel = new RogueScapePanel(taskManager, cardManager, overlayStateManager);

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
        // Handle saving
        if (gameTicksSinceLastSave >= 25) {
            taskManager.saveTasks();
            packManager.savePacks();
            cardManager.saveCards();
            gameTicksSinceLastSave = 0;
        }
        gameTicksSinceLastSave++;

        // Check the user's current chunk
        WorldPoint playerlocation = client.getLocalPlayer().getWorldLocation();

        int chunkX = playerlocation.getX() >> 6;
        int chunkY = playerlocation.getY() >> 6;

        if (chunkX != lastChunkX || chunkY != lastChunkY)
        {
            lastChunkX = chunkX;
            lastChunkY = chunkY;

            onChunkChanged(chunkX, chunkY);
        }
    }

    @Provides
    RogueScapeConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(RogueScapeConfig.class);
    }

    private void onChunkChanged(int chunkX, int chunkY) {
        int regionId = chunkX << 8 | chunkY;
        overlayStateManager.updateRegionIcon(regionId);
    }
}
