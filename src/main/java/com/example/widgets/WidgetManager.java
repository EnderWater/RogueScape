package com.example.widgets;

import net.runelite.api.Client;
import net.runelite.api.FontID;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.api.widgets.WidgetType;

import java.awt.*;

public class WidgetManager {
    private final Client client;
    private final Widget root;
    private final Widget cardWidget;

    public WidgetManager(Client client) {
        this.client = client;

        this.root = this.client.getWidget(10551330);

        if (this.root == null) {
            this.cardWidget = null;
            return;
        }


        this.cardWidget = root.createChild(WidgetType.RECTANGLE);
        this.cardWidget.setOriginalX(200);
        this.cardWidget.setOriginalY(100);
        this.cardWidget.setSize(300, 200);
//        this.cardWidget.setWidth(300);
//        this.cardWidget.setHeight(200);
        this.cardWidget.setFilled(true);
        this.cardWidget.setOpacity(180);
        this.cardWidget.setTextColor(Color.BLACK.getRGB());
        this.cardWidget.setHidden(true);

        Widget text = this.cardWidget.createChild(WidgetType.TEXT);
        text.setOriginalX(20);
        text.setOriginalY(20);
        text.setText("You've opened the card widget!");
        text.setFontId(FontID.PLAIN_12);
        text.setTextColor(Color.WHITE.getRGB());
    }

    public void openWidget() {
        this.cardWidget.setHidden(false);
    }

    public void closeWidget() {
        this.cardWidget.setHidden(true);
    }
}
