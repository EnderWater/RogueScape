package com.example.panels;

import com.example.cards.Card;
import com.example.cards.CardManager;
import com.example.listeners.CardChangeListener;
import com.example.overlays.OverlayStateManager;
import com.example.packs.PackManager;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.components.IconTextField;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PackManagerPanel extends JPanel implements CardChangeListener {
    private final CardManager cardManager;
    private final OverlayStateManager overlayStateManager;
    private final PackManager packManager;

    private final JPanel selectionSection;
    private final JPanel heldCardsSection;
    private final Timer filterTimer;
    private final IconTextField searchBar;
    private static final int FILTER_DELAY = 250; // in milliseconds
    private final List<Card> filterCards = new ArrayList<>();

    PackManagerPanel(CardManager cardManager, OverlayStateManager overlayStateManager, PackManager packManager) {
        this.cardManager = cardManager;
        this.overlayStateManager = overlayStateManager;
        this.packManager = packManager;

        // Add this as a listener so it can update after the cards have been updated
        this.cardManager.addListener(this);

        // Set the view to be a BoxLayout to add things in vertically
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        // Create the section where the buttons will be shown
        JPanel buttonSection = new JPanel();
        buttonSection.setLayout(new GridLayout(0, 2, 8, 8));

        JButton viewCardsButton = createButton("Held cards", e -> this.buildHeldCardsSection());

        // Create the open pack and add pack buttons for the button section
        JButton openPackButton = createButton("Open pack", e -> {
            // Reset this so the held cards can be opened again immediately
            openPack();
        });
        JButton addPackButton = createButton("Add 1 Pack", e -> this.packManager.addPacks(1, packManager.getCurrentPackName()));
        JButton addCardButton = createButton("Add card", e -> this.renderAddCardSection());

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
        if (!overlayStateManager.isHeldCardsOpen()) {
            renderHeldCards();
        } else {
            this.overlayStateManager.closeOverlay();
            heldCardsSection.removeAll();
            selectionSection.removeAll();
            this.selectionSection.revalidate();
            this.selectionSection.repaint();
        }
    }

    private void renderAddCardSection() {
        if (!this.overlayStateManager.isAvailableCardsOpen()) {
            renderAvailableCards();
        } else {
            this.overlayStateManager.closeOverlay();
            this.heldCardsSection.removeAll();
            this.selectionSection.removeAll();
            this.selectionSection.revalidate();
            this.selectionSection.repaint();
        }
    }

    private void addCardsToSection(List<Card> cards) {
        this.filterCards.clear();
        this.filterCards.addAll(cards);

        JButton deleteAllButton = createButton("Delete all held cards?",
                e -> this.deleteAllCards(this.searchBar));

        this.selectionSection.add(this.searchBar);

        // Only add the delete all button if the held card overlay is open
        if (this.overlayStateManager.isHeldCardsOpen()) {
            this.selectionSection.add(Box.createVerticalStrut(2));
            this.selectionSection.add(deleteAllButton);
        }

        this.selectionSection.add(Box.createVerticalStrut(8));

        this.renderCardList(cards);
    }

    //    private void renderCardList(List<Card> heldCards) {
//        if (heldCards.isEmpty())
//            return;
//
//        for (Card card : heldCards) {
//            JPanel cardRow = new JPanel(new BorderLayout(0, 8));
//            JPanel buttonPanel = new JPanel(new BorderLayout(0, 2));
//
//            // Create the label for the card name
//            JLabel cardLabel = new JLabel(card.getName());
//
//            // Create the view card button
//            JButton openCardButton = new JButton("View card");
//            openCardButton.addActionListener(event -> this.overlayStateManager.openOverlay(OverlayStateManager.OverlayComponent.SingleCard, card));
//
//            // Create the delete card button
//            JButton deleteCardButton = new JButton("Delete card");
//            deleteCardButton.addActionListener(event -> this.deleteCard(card, buttonPanel));
//
//            // Create the add card button
//            JButton addCardButton = new JButton("Add card");
//            addCardButton.addActionListener(event -> this.addCard(card));
//
//            if (overlayStateManager.isAvailableCardsOpen())
//                buttonPanel.add(addCardButton, BorderLayout.SOUTH);
//            else
//                buttonPanel.add(deleteCardButton, BorderLayout.SOUTH);
//
//            buttonPanel.add(openCardButton, BorderLayout.NORTH);
//
//            cardRow.add(cardLabel, BorderLayout.CENTER);
//            cardRow.add(buttonPanel, BorderLayout.EAST);
//
//            this.heldCardsSection.add(cardRow);
//        }
//        this.selectionSection.add(this.heldCardsSection);
//    }
    private void renderCardList(List<Card> heldCards) {
        if (heldCards.isEmpty())
            return;

        final int batchSize = 50;
        final int[] index = {0};

        Timer timer = new Timer(1, null);
        timer.addActionListener(e ->
        {
            int processed = 0;

            while (index[0] < heldCards.size() && processed < batchSize) {
                Card card = heldCards.get(index[0]++);

                JPanel cardRow = new JPanel(new BorderLayout(0, 8));
                JPanel buttonPanel = new JPanel(new BorderLayout(0, 2));

                // Create the label for the card name
                JLabel cardLabel = new JLabel(card.getName());

                // Create the view card button
                JButton openCardButton = new JButton("View card");
                openCardButton.addActionListener(event ->
//                    this.overlayStateManager.openOverlay(OverlayStateManager.OverlayComponent.SingleCard, card)
                        this.cardManager.openSingleCardOverlay(card)
                );

                // Create the delete card button
                JButton deleteCardButton = new JButton("Delete card");
                deleteCardButton.addActionListener(event ->
                        this.deleteCard(card, buttonPanel)
                );

                // Create the add card button
                JButton addCardButton = new JButton("Add card");
                addCardButton.addActionListener(event ->
                        this.addCard(card)
                );

                if (overlayStateManager.isAvailableCardsOpen())
                    buttonPanel.add(addCardButton, BorderLayout.SOUTH);
                else
                    buttonPanel.add(deleteCardButton, BorderLayout.SOUTH);

                buttonPanel.add(openCardButton, BorderLayout.NORTH);

                cardRow.add(cardLabel, BorderLayout.CENTER);
                cardRow.add(buttonPanel, BorderLayout.EAST);

                this.heldCardsSection.add(cardRow);

                processed++;
            }

            this.heldCardsSection.revalidate();
            this.heldCardsSection.repaint();

            if (index[0] >= heldCards.size()) {
                this.selectionSection.add(this.heldCardsSection);
                this.selectionSection.revalidate();
                this.selectionSection.repaint();
                timer.stop();
            }
        });

        timer.start();
    }

    public void filter(List<Card> cards) {
        this.heldCardsSection.removeAll();

        String query = searchBar.getText().toLowerCase();
        List<Card> searchedCards = cards.stream().filter(card -> card.getName().toLowerCase().contains(query)).collect(Collectors.toList());

        this.renderCardList(searchedCards);
        this.cardManager.openFilteredCardsOverlay(searchedCards);

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
        if (this.packManager.getAvailablePacks(packManager.getCurrentPackName()) > 0 && !this.overlayStateManager.isPackOpeningOpen()) {
            // Let the card manager handle opening the overlay
            this.cardManager.openPackOverlay(packManager.getCurrentPackName());
        }
        // If the pack opening overlay is already open, close it.
        else if (this.overlayStateManager.isPackOpeningOpen()) {
            this.overlayStateManager.closeOverlay();
        }
    }

    public void deleteCard(Card card, JPanel parent) {
        if (openConfirmDialog("Are you sure you want to delete this card?", parent))
            cardManager.deleteHeldCard(card);
    }

    private void deleteAllCards(JPanel parent) {
        if (openConfirmDialog("Are you sure you want to delete all held cards?", parent)) {
            cardManager.deleteAllHeldCards();
        }
    }

    private boolean openConfirmDialog(String message, JPanel parent) {
        int result = JOptionPane.showConfirmDialog(
                parent,
                message,
                "Confirm",
                JOptionPane.YES_NO_OPTION
        );
        return result == JOptionPane.YES_OPTION;
    }

    public void addCard(Card card) {
        cardManager.addCard(card);
        this.renderAddCardSection();
        this.filter(this.filterCards);
    }

    @Override
    public void onCardsChanged() {
        if (this.overlayStateManager.isAvailableCardsOpen()) {
            renderAvailableCards();
        } else {
            renderHeldCards();
        }
    }

    private void renderAvailableCards() {
        this.heldCardsSection.removeAll();
        this.selectionSection.removeAll();

        // Open the overlay of available cards
        cardManager.openAvailableCardsOverlay();

        List<Card> availableCards = cardManager.getAvailableCards();
        addCardsToSection(availableCards);

        this.selectionSection.revalidate();
        this.selectionSection.repaint();
    }

    private void renderHeldCards() {
        heldCardsSection.removeAll();
        selectionSection.removeAll();

        this.cardManager.openHeldCardsOverlay();

        addCardsToSection(cardManager.getHeldCards());

        this.selectionSection.revalidate();
        this.selectionSection.repaint();
    }
}