package com.example.overlays;

import com.example.cards.Card;
import com.example.packs.Pack;
import com.example.packs.PackManager;
import lombok.Getter;
import lombok.Setter;
import net.runelite.client.util.ImageUtil;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Singleton
public class OverlayStateManager {
    private final PackManager packManager;

    private final int MAX_CARDS_PER_PAGE = 10;

    @Getter
    private boolean windowOpen = false;

    @Getter
    private int currentPage = 1;

    private final List<Card> overlayCards = new ArrayList<>();

    @Getter
    private final List<Card> paginatedCards = new ArrayList<>();

    @Getter
    private OverlayComponent overlayComponent = OverlayComponent.None;

    @Getter
    @Setter
    private int currentChunkId = 0;
    private BufferedImage currentChunkIcon;

    private final Map<String, BufferedImage> iconMap = new HashMap<>();

    public enum OverlayComponent {
        None,
        AvailableCards,
        SingleCard,
        HeldCards,
        PackOpening
    }

    @Inject
    public OverlayStateManager(PackManager packManager) {
        this.packManager = packManager;
        loadIcons();
    }

    public void openWindow() {
        windowOpen = true;
    }

    public void closeOverlay() {
        windowOpen = false;
        overlayComponent = OverlayComponent.None;
        currentPage = 1;
        clearOverlayCards();
    }

    public void openOverlay(OverlayComponent component) {
        openWindow();
        currentPage = 1;
        overlayComponent = component;
    }

    public void openOverlay(OverlayComponent component, Card card) {
        openWindow();
        currentPage = 1;
        overlayComponent = component;
        addOverlayCards(card);
    }

    public void openOverlay(OverlayComponent component, List<Card> cards) {
        openWindow();
        currentPage = 1;
        overlayComponent = component;
        addOverlayCards(cards);
    }

    public boolean isNoneOpen() {
        return this.overlayComponent == OverlayComponent.None;
    }

    public boolean isAvailableCardsOpen() {
        return this.overlayComponent == OverlayComponent.AvailableCards;
    }

    public boolean isHeldCardsOpen() {
        return this.overlayComponent == OverlayComponent.HeldCards;
    }

    public boolean isSingleCardOpen() {
        return this.overlayComponent == OverlayComponent.SingleCard;
    }

    public boolean isPackOpeningOpen() {
        return this.overlayComponent == OverlayComponent.PackOpening;
    }

    // Add the cards that should be displayed
    public void addOverlayCards(Card card) {
        // Use this to clear any overlayCards that may be left over
        clearOverlayCards();

        // Reset the page back to 1
        resetPage();

        // Add them to the overlay
        this.overlayCards.add(card);

        this.paginateCards();
    }

    // Add the cards that should be displayed
    public void addOverlayCards(List<Card> cards) {
        // Use this to clear any overlayCards that may be left over
        clearOverlayCards();

        // Reset the page back to 1
        resetPage();

        // Add them to the overlay
        this.overlayCards.addAll(cards);
        this.paginateCards();
    }

    public void clearOverlayCards() {
        this.overlayCards.clear();
        this.paginatedCards.clear();
    }

    private void paginateCards() {
        this.paginatedCards.clear();

        int startingIndex = (currentPage-1) * MAX_CARDS_PER_PAGE; // 0 for first page if cards per page is 20
        int endingIndex = (currentPage * MAX_CARDS_PER_PAGE); // 20 for first page if cards per page is 20

        if (endingIndex >= overlayCards.size())
            endingIndex = overlayCards.size();

        this.paginatedCards.addAll(overlayCards.subList(startingIndex, endingIndex));
    }

    public int getTotalPages() {
        return (this.overlayCards.size() - 1) / MAX_CARDS_PER_PAGE + 1;
    }

    public void setCurrentPage(int page) {
        currentPage = page;
        this.paginateCards();
    }

    private void resetPage() {
        currentPage = 1;
    }

    public List<Card> getOverlayCards() {
        return this.paginatedCards;
    }

    private void loadIcons() {
        // Card type icons
        loadIcon("Boon", "/com/example/icons/Boon.png");
        loadIcon("Goal", "/com/example/icons/Goal.png");
        loadIcon("Item", "/com/example/icons/Item.png");
        loadIcon("Land", "/com/example/icons/Land.png");
        loadIcon("Main_hand", "/com/example/icons/Main_Hand.png");
        loadIcon("Minigame", "/com/example/icons/Minigame.png");
        loadIcon("Off_hand", "/com/example/icons/Off_Hand.png");
        loadIcon("Quest", "/com/example/icons/Quest.png");
        loadIcon("Relic", "/com/example/icons/Relic.png");
        loadIcon("Skill", "/com/example/icons/Skill.png");

        // Rarity icons
        loadIcon("Common", "/com/example/icons/Common.png");
        loadIcon("Uncommon", "/com/example/icons/Uncommon.png");
        loadIcon("Rare", "/com/example/icons/Rare.png");
        loadIcon("Mythic", "/com/example/icons/Mythic.png");
        loadIcon("Legendary", "/com/example/icons/Legendary.png");

        // Pack icons
        loadIcon("Creatures of the Night", "/com/example/icons/pack-icons/Creatures_of_the_Night.png");
        loadIcon("Emirs Dominion", "/com/example/icons/pack-icons/Emirs_Dominion.png");
        loadIcon("Fallen Empires", "/com/example/icons/pack-icons/Fallen_Empires.png");
        loadIcon("Humble Beginnings", "/com/example/icons/pack-icons/Humble_Beginnings.png");
        loadIcon("Humble Beginnings Goal", "/com/example/icons/pack-icons/Humble_Beginnings_Goal.png");
        loadIcon("Jewel of Misthalin", "/com/example/icons/pack-icons/Jewel_of_Misthalin.png");
        loadIcon("Jewel of Misthalin Goal", "/com/example/icons/pack-icons/Jewel_of_Misthalin_Goal.png");
        loadIcon("Knights of Saradomin", "/com/example/icons/pack-icons/Knights_of_Saradomin.png");
        loadIcon("Light of Saradomin", "/com/example/icons/pack-icons/Light_of_Saradomin.png");
        loadIcon("Light of Saradomin Goal Pack", "/com/example/icons/pack-icons/Light_of_Saradomin_Goal_Pack.png");
        loadIcon("Mages of the Shore", "/com/example/icons/pack-icons/Mages_of_the_Shore.png");
        loadIcon("Ports and POHs Edit", "/com/example/icons/pack-icons/Ports_and_POHs_Edit.png");
        loadIcon("Ruins of Senntisten", "/com/example/icons/pack-icons/Ruins_of_Senntisten.png");
        loadIcon("Shadows Over Hallowvale Edit", "/com/example/icons/pack-icons/Shadows_Over_Hallowvale_Edit.png");
        loadIcon("Shadows Over Hallowvale Goal", "/com/example/icons/pack-icons/Shadows_Over_Hallowvale_Goal.png");
        loadIcon("Shifting Sands", "/com/example/icons/pack-icons/Shifting_Sands.png");
    }

    private void loadIcon(String key, String path) {
        try {
            BufferedImage image = ImageIO.read(getClass().getResourceAsStream(path));
            BufferedImage resizedImage = ImageUtil.resizeImage(image, 32, 32);
            iconMap.put(key, resizedImage);
        } catch (Exception e) {
            System.out.println("Error loading image" + e);
        }
    }

    public void updateRegionIcon(int chunkId) {
        if (chunkId == this.currentChunkId)
            return;

        for (Pack pack : packManager.getPacks()) {
            if (pack.getChunkIds().contains(chunkId)) {
                currentChunkIcon = this.iconMap.get(pack.getName());
                currentChunkId = chunkId;
                break;
            }
        }
    }

    public BufferedImage getIconForRegion() {
        return currentChunkIcon;
    }

    public BufferedImage getOverlayIcon(String key) {
        return this.iconMap.get(key);
    }
}