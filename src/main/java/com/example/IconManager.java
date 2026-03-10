package com.example;

import lombok.Setter;
import net.runelite.client.game.ItemManager;
import net.runelite.client.util.AsyncBufferedImage;
import net.runelite.client.util.ImageUtil;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

@Singleton
public class IconManager {
    private final ItemManager itemManager;
    private final ItemLookup itemLookup;

    // Caches the current chunks icon
    private BufferedImage currentChunkIcon = null;

    // This map stores the region pack ids by packName : packIcon
    private final Map<String, BufferedImage> iconMap = new HashMap<>();

    @Inject
    public IconManager(ItemManager itemManager, ItemLookup itemLookup) {
        this.itemManager = itemManager;
        this.itemLookup = itemLookup;
        loadIcons();
    }

    // Load every icon the app should need.
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

    public BufferedImage getIconForRegion() {
        return currentChunkIcon;
    }
    public BufferedImage getOverlayIcon(String key) {
        BufferedImage image = this.iconMap.get(key);
        return image;
    }

    public void setCurrentChunkIcon(String packName) {
        this.currentChunkIcon = getOverlayIcon(packName);
    }

    public BufferedImage getItemIcon(String itemName) {
        int itemId = itemLookup.getItemId(itemName);
        BufferedImage image = this.itemManager.getImage(itemId);

        if (image == null) {
            System.out.println("Hey! The icon for " + itemName + " could not be found.");
        }

        return image;
    }
}
