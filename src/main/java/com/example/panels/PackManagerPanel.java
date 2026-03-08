package com.example.panels;

import com.example.cards.Card;
import com.example.cards.CardManager;
import com.example.overlays.OverlayStateManager;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.components.IconTextField;

import javax.inject.Inject;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
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
    private final List<Card> filterCards = new ArrayList<>();
    private boolean isAddCardSection = false;

    @Inject
    PackManagerPanel(CardManager cardManager, OverlayStateManager overlayStateManager) {
        this.cardManager = cardManager;
        this.overlayStateManager = overlayStateManager;

        // Set the view to be a BoxLayout to add things in vertically
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        // Create the section where the buttons will be shown
        JPanel buttonSection = new JPanel();
        buttonSection.setLayout(new GridLayout(0,2, 8,8));

        this.viewCardsButton = createButton("Held cards", e -> this.buildHeldCardsSection());

        // Create the open pack and add pack buttons for the button section
        JButton openPackButton = createButton("Open pack", e -> openPack());
        JButton addPackButton = createButton("Add 1 Pack", e -> this.cardManager.addAvailablePack(null));
        JButton addCardButton = createButton("Add card", e-> this.renderAddCardSection());

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
                filter(filterCards);
                filterTimer.stop();
            }
        });

        // Add the UI elements to the panel
        this.selectionSection.add(heldCardsSection);

        buttonSection.add(openPackButton);
        buttonSection.add(viewCardsButton);
        buttonSection.add(addPackButton);
        buttonSection.add(addCardButton);

        add(Box.createVerticalStrut(8));
        add(buttonSection);
        add(Box.createVerticalStrut(16));
        add(this.selectionSection);
    }

    private void buildHeldCardsSection() {
        this.heldCardsSection.removeAll();
        this.selectionSection.removeAll();
        this.isAddCardSection = false;

        if (!overlayStateManager.isHeldCardsOpen() || overlayStateManager.isSingleCardOpen()) {
            addCardsToSection(cardManager.getHeldCards());
            this.overlayStateManager.openOverlay(OverlayStateManager.OverlayComponent.HeldCards);
            this.overlayStateManager.addOverlayCards(this.cardManager.getHeldCards());
        }
        else {
            this.overlayStateManager.closeOverlay();
        }

        this.selectionSection.revalidate();
        this.selectionSection.repaint();
    }

    private void renderAddCardSection() {
        this.heldCardsSection.removeAll();
        this.selectionSection.removeAll();

        if (!this.isAddCardSection) {
            this.isAddCardSection = true;
            addCardsToSection(cardManager.getAvailableCards());
            // Reset the state of the overlay since none of them are technically open
            this.overlayStateManager.closeOverlay();
        }
        else
            this.isAddCardSection = false;

        this.selectionSection.revalidate();
        this.selectionSection.repaint();
    }

    private void addCardsToSection(List<Card> cards) {
        this.filterCards.clear();
        this.filterCards.addAll(cards);

        this.selectionSection.add(this.searchBar);
        this.selectionSection.add(Box.createVerticalStrut(8));

        this.renderCardList(cards);
    }

    private void renderCardList(List<Card> heldCards) {
        if (!heldCards.isEmpty()) {
            for (Card card : heldCards) {
                JPanel cardRow = new JPanel(new BorderLayout(0, 8));
                JPanel buttonPanel = new JPanel(new BorderLayout(0, 2));

                // Create the label for the card name
                JLabel cardLabel = new JLabel(card.getName());

                // Create the view card button
                JButton openCardButton = new JButton("View card");
                openCardButton.addActionListener(event -> this.overlayStateManager.openOverlay(OverlayStateManager.OverlayComponent.SingleCard, card));

                // Create the delete card button
                JButton deleteCardButton = new JButton("Delete card");
                deleteCardButton.addActionListener(event -> this.deleteCard(card, buttonPanel));

                // Create the add card button
                JButton addCardButton = new JButton("Add card");
                addCardButton.addActionListener(event -> this.addCard(card));

                if (isAddCardSection)
                    buttonPanel.add(addCardButton, BorderLayout.SOUTH);
                else
                    buttonPanel.add(deleteCardButton, BorderLayout.SOUTH);

                buttonPanel.add(openCardButton, BorderLayout.NORTH);

                cardRow.add(cardLabel, BorderLayout.CENTER);
                cardRow.add(buttonPanel, BorderLayout.EAST);

                this.heldCardsSection.add(cardRow);
            }
            this.selectionSection.add(this.heldCardsSection);
        }
    }

    public void filter(List<Card> cards) {
        this.heldCardsSection.removeAll();

        String query = searchBar.getText().toLowerCase();
        List<Card> searchedCards = cards.stream().filter(card -> card.getName().toLowerCase().contains(query)).collect(Collectors.toList());

        this.renderCardList(searchedCards);

        this.heldCardsSection.revalidate();
        this.heldCardsSection.repaint();
    }

    public JButton createButton(String buttonText, ActionListener actionListener) {
        JButton button = new JButton(buttonText);
        button.setPreferredSize(new Dimension(100, 20));
        button.addActionListener(actionListener);
        return button;
    }

    public void openPack() {
        // Check if there are available packs and if the pack opening is not open. If so, open the pack opening overlay
        if (this.cardManager.getAvailablePacks() > 0 && !this.overlayStateManager.isPackOpeningOpen()) {
            List<Card> packCards = this.cardManager.getCardsInPack();
            this.overlayStateManager.openOverlay(OverlayStateManager.OverlayComponent.PackOpening, packCards);
        }
        // If the pack opening overlay is already open, close it.
        else if (this.overlayStateManager.isPackOpeningOpen()) {
            this.overlayStateManager.closeOverlay();
        }
    }

    public void deleteCard(Card card, JPanel parent) {
        int result = JOptionPane.showConfirmDialog(
                parent,
                "Are you sure you want to delete this task?",
                "Confirm Reset",
                JOptionPane.YES_NO_OPTION
        );
        if (result == JOptionPane.YES_OPTION) {
            cardManager.deleteHeldCard(card);
            this.selectionSection.removeAll();
            this.heldCardsSection.removeAll();
            this.addCardsToSection(cardManager.getHeldCards());
        }
    }

    public void addCard(Card card) {
        cardManager.addCard(card);
        // Do this to reset the add card section so it re-renders
        this.isAddCardSection = false;
        this.renderAddCardSection();
        this.filter(this.filterCards);
    }
}
