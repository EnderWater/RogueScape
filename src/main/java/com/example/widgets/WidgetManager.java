package com.example.widgets;

import net.runelite.api.Client;
import net.runelite.api.FontID;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.widgets.Widget;
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
    private Widget root;
    private Widget cardWidget;

    public void openWidget() {
        if (this.root == null) getRootWidget();
        clientThread.invokeLater(this::createCardWidget);
        this.cardWidget.setHidden(false);
    }

    public void closeWidget() {
        this.cardWidget.setHidden(true);
    }

    private void getRootWidget() {
        // In widget inspector, the widgets are shown like the following: R 161.0 ToplevelOsrsStretch.CONTROL
        // the groupId is the 161 and the child is 0. Additionally, the groupName is ToplevelOsrsStretch and the child is CONTROL
        this.root = this.client.getWidget(InterfaceID.TOPLEVEL_OSRS_STRETCH, 34);

        if (this.root == null) {
            this.cardWidget = null;
            return;
        }
    }

    private void createCardWidget() {
        if (root == null) return;

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
}
