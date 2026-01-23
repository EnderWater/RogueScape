package com.example.widgets;

import net.runelite.api.Client;
import net.runelite.api.FontID;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.api.widgets.WidgetSizeMode;
import net.runelite.api.widgets.WidgetType;
import net.runelite.client.callback.ClientThread;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.*;

@Singleton
public class WidgetManager {
    @Inject
    private ClientThread clientThread;

    @Inject
    private Client client;

    private Widget rootWidget;
    private Widget cardWidget;

    public void openWidget()
    {
        clientThread.invokeLater(() ->
        {
            if (rootWidget == null)
            {
                initRoot();
            }

            if (cardWidget == null)
            {
                createCardWidget();
            }

            cardWidget.setHidden(false);
        });
    }


    public void closeWidget() {
        this.cardWidget.setHidden(true);
    }

    private void initRoot() {
        // Try the top level first
        Widget parent = client.getWidget(InterfaceID.TOPLEVEL, 0);

        // Fallback: toplevel osrs stretch
        if (parent == null) {
            parent = client.getWidget(InterfaceID.TOPLEVEL_OSRS_STRETCH, 0);
        }

        if (parent == null) {
            parent = client.getWidget(InterfaceID.TOPLEVEL_OSRS_STRETCH, 34);
        }

        if (parent == null) {
            return;
        }

        // Create a layer attached to this parent
        rootWidget = parent.createChild(WidgetType.LAYER);
        rootWidget.setOriginalX(0);
        rootWidget.setOriginalY(0);
        rootWidget.setWidthMode(WidgetSizeMode.ABSOLUTE);
        rootWidget.setHeightMode(WidgetSizeMode.ABSOLUTE);
        rootWidget.setWidth(parent.getWidth());
        rootWidget.setHeight(parent.getHeight());
        rootWidget.revalidate();
    }


    private void createCardWidget() {
        if (rootWidget == null) return;

        // Create a layer for the card container
        cardWidget = rootWidget.createChild(WidgetType.LAYER);
        cardWidget.setOriginalX(200);
        cardWidget.setOriginalY(100);
        cardWidget.setWidthMode(WidgetSizeMode.ABSOLUTE);
        cardWidget.setHeightMode(WidgetSizeMode.ABSOLUTE);
        cardWidget.setWidth(300);
        cardWidget.setHeight(200);
        cardWidget.setHidden(false); // show immediately
        cardWidget.revalidate();

        // Background rectangle
        Widget background = cardWidget.createChild(WidgetType.RECTANGLE);
        background.setOriginalX(0);
        background.setOriginalY(0);
        background.setWidthMode(WidgetSizeMode.ABSOLUTE);
        background.setHeightMode(WidgetSizeMode.ABSOLUTE);
        background.setWidth(300);
        background.setHeight(200);
        background.setFilled(true);
        background.setOpacity(180);
        background.setTextColor(Color.BLACK.getRGB());
        background.revalidate();

        // Text
        Widget text = cardWidget.createChild(WidgetType.TEXT);
        text.setOriginalX(20);
        text.setOriginalY(20);
        text.setText("You've opened the card widget!");
        text.setFontId(FontID.PLAIN_12);
        text.setTextColor(Color.WHITE.getRGB());
        text.revalidate();
    }
}
