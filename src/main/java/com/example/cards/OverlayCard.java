package com.example.cards;

import com.example.Coordinate;
import lombok.Getter;

public class OverlayCard {
    @Getter
    private final Card card; // This means that any type of card can be shown in an overlay
//    @Getter
//    private Coordinate topLeft;
//    @Getter
//    private Coordinate topRight;
//    @Getter
//    private Coordinate bottomLeft;
//    @Getter
//    private Coordinate bottomRight;
    @Getter
    private final int width = 150;
    @Getter
    private final int height = 250;

    @Getter
    public int x;
    @Getter
    public int y;

    OverlayCard(Card card) {
        this.card = card;
//        this.topLeft = new Coordinate(0,0);
//        this.topRight = new Coordinate(topLeft.x + this.width, topLeft.y);
//        this.bottomLeft = new Coordinate(topLeft.x, topLeft.y + this.height);
//        this.bottomRight = new Coordinate(topLeft.x + this.width, topLeft.y + this.height);
//        this.x = topLeft.x;
//        this.y = topLeft.y;
    }

    OverlayCard(Card card, int x, int y) {
        this.card = card;
//        this.topLeft = new Coordinate(x, y);
//        this.topRight = new Coordinate(topLeft.x + this.width, topLeft.y);
//        this.bottomLeft = new Coordinate(topLeft.x, topLeft.y + this.height);
//        this.bottomRight = new Coordinate(topLeft.x + this.width, topLeft.y + this.height);
//        this.x = topLeft.x;
//        this.y = topLeft.y;
    }

    /**
     * Move the card x and y number of pixels horizontally and vertically
     * @param x - the number of pixels to move horizontally
     * @param y - the number of pixels to move vertically
     */
    void move(int x, int y) {
//        this.topLeft.x += x;
//        this.topLeft.y += y;
        this.updatePoints();
    }

    /**
     * This method moves the overlay card's top left corner to the specified x and y position
     * and updates the rest of the points accordingly
     * @param x - the x coordinate
     * @param y - the y coordinate
     */
    void moveToPos(int x, int y) {
//        this.topLeft.x = x;
//        this.topLeft.y = y;
        this.updatePoints();
    }

    /**
     * Update all the card's points based on the topLeft point
     */
    private void updatePoints() {
//        this.topRight.x = topLeft.x + this.width;
//        this.topRight.y = topLeft.y;

//        this.bottomLeft.x = topLeft.x;
//        this.bottomLeft.y = topLeft.y + this.height;

//        this.bottomRight.x = topLeft.x + this.width;
//        this.bottomRight.y = topLeft.y + this.height;

//        this.x = topLeft.x;
//        this.y = topLeft.y;
    }
}
