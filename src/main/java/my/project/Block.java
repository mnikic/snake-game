package my.project;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

public class Block {
    private int x;
    private int y;
    private final Color color;
    public static final int SIZE = 30;

    public Block(Color color) {
        this.color = color;
    }

    public void draw(Graphics2D graphics) {
        graphics.setColor(color);
        graphics.fillRect(x, y, SIZE, SIZE);
    }

}
