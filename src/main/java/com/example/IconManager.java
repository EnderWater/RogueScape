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
        File cardTypeIconDirectory = getBaseDirectory("assets/icons/card-type-icons");
        loadIconsFromDirectory(cardTypeIconDirectory);

        // Rarity icons
        File rarityIconDirectory = getBaseDirectory("assets/icons/rarity-icons");
        loadIconsFromDirectory(rarityIconDirectory);

        // Goal icons
        File goalIconDirectory = getBaseDirectory("assets/icons/goal-icons");
        loadIconsFromDirectory(goalIconDirectory);

        // Pack icons
        File packIconDirectory = getBaseDirectory("assets/icons/pack-icons");
        loadIconsFromDirectory(packIconDirectory);
    }

    private void loadIcon(String key, String fullPath) {
        try {
            File file = new File(fullPath);

            if (!file.exists()) {
                System.out.println("Icon not found: " + fullPath);
                return;
            }

            BufferedImage image = ImageIO.read(file);
            iconMap.put(key, image);

        } catch (Exception e) {
            System.out.println("Error loading image: " + e);
        }
    }

    private void loadIconsFromDirectory(File dir) {

        if (!dir.exists() || !dir.isDirectory()) {
            System.out.println("Invalid icon directory: " + dir.getAbsolutePath());
            return;
        }

        File[] files = dir.listFiles();
        if (files == null) return;

        for (File file : files) {
            if (!file.isFile()) continue;

            String fileName = file.getName();

            if (!fileName.toLowerCase().endsWith(".png")) continue;

            String iconKey = fileName
                    .replace("_", " ")
                    .replace(".png", "");

            loadIcon(iconKey, file.getAbsolutePath());
        }
    }

    private File getBaseDirectory(String childPath) {
        try {
            File location = new File(
                    getClass()
                            .getProtectionDomain()
                            .getCodeSource()
                            .getLocation()
                            .toURI()
            );

            File baseDir;

            if (location.isFile()) {
                // Running from shadow jar (REAL USERS)
                File jarDir = location.getParentFile();
                baseDir = new File(jarDir, childPath);
            } else {
                // Running from IntelliJ (DEV)
                baseDir = new File(System.getProperty("user.dir"), childPath);
            }

            if (!baseDir.exists()) {
                baseDir.mkdirs();
            }

            return baseDir;

        } catch (Exception e) {
            throw new RuntimeException("Failed to resolve base directory", e);
        }
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
        } else if (card instanceof GoalCard) {
            return getIconFromMap(card.getRarity().toString());
        } else
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
