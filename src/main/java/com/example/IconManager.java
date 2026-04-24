package com.example;

import com.example.cards.Card;
import com.example.cards.GoalCard;
import com.example.cards.ItemCard;
import net.runelite.client.game.ItemManager;
import net.runelite.client.util.ImageUtil;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

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
        loadIconsFromDirectory("/com/example/icons/card-type-icons");
//        loadIcon("Boon", "/com/example/icons/card-type-icons/Boon.png");
//        loadIcon("Goal", "/com/example/icons/card-type-icons/Goal.png");
//        loadIcon("Item", "/com/example/icons/card-type-icons/Item.png");
//        loadIcon("Land", "/com/example/icons/card-type-icons/Land.png");
//        loadIcon("Main_hand", "/com/example/icons/card-type-icons/Main_Hand.png");
//        loadIcon("Minigame", "/com/example/icons/card-type-icons/Minigame.png");
//        loadIcon("Off_hand", "/com/example/icons/card-type-icons/Off_Hand.png");
//        loadIcon("Quest", "/com/example/icons/card-type-icons/Quest.png");
//        loadIcon("Relic", "/com/example/icons/card-type-icons/Relic.png");
//        loadIcon("Skill", "/com/example/icons/card-type-icons/Skill.png");

        // Rarity icons
        loadIconsFromDirectory("/com/example/icons/rarity-icons");
//        loadIcon("Common", "/com/example/icons/rarity-icons/Common.png");
//        loadIcon("Uncommon", "/com/example/icons/rarity-icons/Uncommon.png");
//        loadIcon("Rare", "/com/example/icons/rarity-icons/Rare.png");
//        loadIcon("Mythic", "/com/example/icons/rarity-icons/Mythic.png");
//        loadIcon("Legendary", "/com/example/icons/rarity-icons/Legendary.png");
//        loadIcon("Special", "/com/example/icons/rarity-icons/Special.png");

        // Goal icons
        loadIconsFromDirectory("/com/example/icons/goal-icons");
//        loadIcon("Humble Beginnings Goal", "/com/example/icons/goal-icons/Humble_Beginnings_Goal.png");
//        loadIcon("Jewel of Misthalin Goal", "/com/example/icons/goal-icons/Jewel_of_Misthalin_Goal.png");
//        loadIcon("Light of Saradomin Goal", "/com/example/icons/goal-icons/Light_of_Saradomin_Goal.png");
//        loadIcon("Shadows over Hallowvale Goal", "/com/example/icons/goal-icons/Shadows_Over_Hallowvale_Goal.png");

        // Pack icons
        loadIconsFromDirectory("/com/example/icons/pack-icons");
//        loadIcon("Creatures of the Night", "/com/example/icons/pack-icons/Creatures_of_the_Night.png");
//        loadIcon("Echoes of the Past", "/com/example/icons/pack-icons/Echoes_of_the_Past.png");
//        loadIcon("Emirs Dominion", "/com/example/icons/pack-icons/Emir's_Dominion.png");
//        loadIcon("Fallen Empires", "/com/example/icons/pack-icons/Fallen_Empires.png");
//        loadIcon("Humble Beginnings", "/com/example/icons/pack-icons/Humble_Beginnings.png");
//        loadIcon("Jewel of Misthalin", "/com/example/icons/pack-icons/Jewel_of_Misthalin.png");
//        loadIcon("Keystone of the Ardent", "/com/example/icons/pack-icons/Keystone_of_the_Ardent.png");
//        loadIcon("Knights of Saradomin", "/com/example/icons/pack-icons/Knights_of_Saradomin.png");
//        loadIcon("Light of Saradomin", "/com/example/icons/pack-icons/Light_of_Saradomin.png");
//        loadIcon("Mages of the Shore", "/com/example/icons/pack-icons/Mages_of_the_Shore.png");
//        loadIcon("Ports and POHs", "/com/example/icons/pack-icons/Ports_and_POHs.png");
//        loadIcon("Ritual of Balance", "/com/example/icons/pack-icons/Ritual_of_Balance.png");
//        loadIcon("Ruins of Senntisten", "/com/example/icons/pack-icons/Ruins_of_Senntisten.png");
//        loadIcon("Scars of Forinthry", "/com/example/icons/pack-icons/Scars_of_Forinthry.png");
//        loadIcon("Shadows over Hallowvale", "/com/example/icons/pack-icons/Shadows_Over_Hallowvale.png");
//        loadIcon("Shifting Sands", "/com/example/icons/pack-icons/Shifting_Sands.png");
//        loadIcon("The Concealed King", "/com/example/icons/pack-icons/The_Concealed_King.png");
    }

    private void loadIcon(String key, String path) {
        try {
            BufferedImage image = ImageIO.read(getClass().getResourceAsStream(path));
            iconMap.put(key, image);
        } catch (Exception e) {
            System.out.println("Error loading image" + e);
        }
    }

    private void loadIconsFromDirectory(String directoryPath) {
        List<String> iconNames = getFilesInDirectory(directoryPath);
        for (String iconName : iconNames) {
            // If the filename is not a png, move on to the next one
            if (!iconName.contains(".png")) continue;

            String iconKey = iconName.replace("_", " ").replace(".png", "");
            loadIcon(iconKey, directoryPath + "/" + iconName);
        }
    }

    public List<String> getFilesInDirectory(String directoryPath) {
        List<String> fileNames = new ArrayList<>();

        var resource = getClass().getResource(directoryPath);

        if (resource == null) {
            return fileNames;
        }

        File dir = new File(resource.getFile());

        File[] files = dir.listFiles();
        if (files == null) return fileNames;

        for (File file : files) {
            if (file.isFile()) {
                fileNames.add(file.getName());
            }
        }

        return fileNames;
    }

    public BufferedImage getIconForRegion() {
        return currentChunkIcon;
    }

    public BufferedImage getOverlayIcon(String iconKey) {
        return getIconFromMap(iconKey);
    }

    public BufferedImage getOverlayIcon(Card card) {
        if (card instanceof ItemCard) {
            return getItemIcon(card.getName());
        }
        else if (card instanceof GoalCard) {
            return getIconFromMap(card.getRarity().toString());
        }
        else
            return getIconFromMap(card.getType());
    }

    private BufferedImage getIconFromMap(String key) {
        BufferedImage image = this.iconMap.get(key);
        return image;
    }

    private BufferedImage getItemIcon(String itemName) {
        int itemId = itemLookup.getItemId(itemName);
        BufferedImage image = this.itemManager.getImage(itemId);

        if (image == null) {
            System.out.println("Hey! The icon for " + itemName + " could not be found.");
        }

        return image;
    }

    public BufferedImage getRarityIcon(Card card) {
        return getIconFromMap(card.getRarity().toString());
    }

    public BufferedImage getTypeIcon(Card card) {
        return getIconFromMap(card.getType());
    }

    public void setCurrentChunkIcon(String packName) {
        if (packName == null)
            this.currentChunkIcon = null;

        this.currentChunkIcon = ImageUtil.resizeImage(getIconFromMap(packName), 32, 32);
    }
}
