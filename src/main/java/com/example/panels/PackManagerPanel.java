package com.example.panels;

import com.example.cards.Card;
import com.example.cards.CardManager;
import com.example.cards.OverlayCard;
import com.example.overlays.OverlayStateManager;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.components.IconTextField;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.stream.Collectors;

public class PackManagerPanel extends JPanel {
    private final CardManager cardManager;
    private final OverlayStateManager overlayStateManager;
    private final JPanel selectionSection;
    private final JPanel heldCardsSection;
    private final JButton viewCardsButton;
    private final Timer filterTimer;
    private final IconTextField searchBar;
    private static final int FILTER_DELAY = 250; // in milliseconds
    private boolean isHeldCardViewOpen = false;

    @Inject
    PackManagerPanel(CardManager cardManager, OverlayStateManager overlayStateManager) {
        this.cardManager = cardManager;
        this.overlayStateManager = overlayStateManager;

        // Set the view to be a BoxLayout to add things in vertically
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        // Create the section where the buttons will be shown
        JPanel buttonSection = new JPanel();
        buttonSection.setLayout(new GridLayout(0,2, 8,8));

        this.viewCardsButton = new JButton("Held cards");
        viewCardsButton.setPreferredSize(new Dimension(100, 20));
        viewCardsButton.addActionListener(e -> {
            this.buildHeldCardsSection();
        });

        // Create the buttons for the button section
        JButton openPackButton = createButton("Open pack", e -> {
            // Check if there are available packs and if the pack opening is not open. If so, open the pack opening overlay
            if (this.cardManager.getAvailablePacks() > 0 && !this.overlayStateManager.isPackOpeningOpen()) {
                List<Card> packCards = this.cardManager.getCardsInPack();
                this.overlayStateManager.openOverlay(OverlayStateManager.OverlayComponent.PackOpening, packCards);
            }
            // If the pack opening overlay is already open, close it.
            else if (this.overlayStateManager.isPackOpeningOpen()) {
                this.overlayStateManager.closeOverlay();
            }
        });

        JButton addPackButton = createButton("Add 1 Pack", e -> this.cardManager.addAvailablePack(null));

        // Create the section where the held cards will be displayed
        this.heldCardsSection = new JPanel();
        this.heldCardsSection.setLayout(new GridLayout(0, 1, 0, 8));

        // Create the UI to select a card when the pack is opened
        this.selectionSection = new JPanel();
        this.selectionSection.setLayout(new BoxLayout(this.selectionSection, BoxLayout.Y_AXIS));

        searchBar = new IconTextField();
        searchBar.setIcon(IconTextField.Icon.SEARCH);
        searchBar.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        searchBar.setHoverBackgroundColor(ColorScheme.DARK_GRAY_HOVER_COLOR);
        searchBar.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                filterTimer.restart();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filterTimer.restart();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filterTimer.restart();
            }
        });
        filterTimer = new Timer(FILTER_DELAY, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                filter();
                filterTimer.stop();
            }
        });

        // Add the UI elements to the panel
        this.selectionSection.add(heldCardsSection);

        buttonSection.add(openPackButton);
        buttonSection.add(viewCardsButton);
        buttonSection.add(addPackButton);

        add(Box.createVerticalStrut(8));
        add(buttonSection);
        add(Box.createVerticalStrut(16));
        add(this.selectionSection);
    }

    private void buildHeldCardsSection() {
        this.selectionSection.removeAll();
        this.heldCardsSection.removeAll();

        if (!overlayStateManager.isHeldCardsOpen() || overlayStateManager.isSingleCardOpen()) {
            List<Card> heldCards = this.cardManager.getHeldCards();

            this.selectionSection.add(this.searchBar);
            this.selectionSection.add(Box.createVerticalStrut(8));

            this.renderCardList(heldCards);

            this.overlayStateManager.openOverlay(OverlayStateManager.OverlayComponent.HeldCards);
            this.overlayStateManager.addOverlayCards(this.cardManager.getHeldCards());
        }
        else {
            this.overlayStateManager.closeOverlay();
        }

        this.selectionSection.revalidate();
        this.selectionSection.repaint();
    }

    private void renderCardList(List<Card> heldCards) {
        if (!heldCards.isEmpty()) {
            for (Card card : heldCards) {
                JPanel cardRow = new JPanel(new BorderLayout(0, 8));

                JButton openCardButton = new JButton("View card");
                openCardButton.addActionListener(event -> {
                    this.overlayStateManager.openOverlay(OverlayStateManager.OverlayComponent.SingleCard, card);
                });
                JLabel cardLabel = new JLabel(card.getName());

                cardRow.add(cardLabel, BorderLayout.CENTER);
                cardRow.add(openCardButton, BorderLayout.EAST);

                this.heldCardsSection.add(cardRow);
            }
            this.selectionSection.add(this.heldCardsSection);
        }
        this.isHeldCardViewOpen = true;
    }

    void filter() {
        this.heldCardsSection.removeAll();
        List<Card> heldCards = this.cardManager.getHeldCards();

        String query = searchBar.getText().toLowerCase();
        List<Card> searchedCards = heldCards.stream().filter(card -> card.getName().toLowerCase().contains(query)).collect(Collectors.toList());

        this.renderCardList(searchedCards);

        this.heldCardsSection.revalidate();
        this.heldCardsSection.repaint();
    }

    JButton createButton(String buttonText, ActionListener actionListener) {
        JButton button = new JButton(buttonText);
        button.setPreferredSize(new Dimension(100, 20));
        button.addActionListener(actionListener);
        return button;
    }
}
