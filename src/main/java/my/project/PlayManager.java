package my.project;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyListener;
import java.awt.BasicStroke;

/**
 * Hello world!
 *
 */
public class PlayManager {

    private static final int WIDTH = 360;
    private static final int HEIGHT = 600;
    public static int LEFT_X = (GamePanel.WIDTH / 2) - (WIDTH / 2);
    public static int RIGHT_X = LEFT_X + WIDTH;
    public static int TOP_Y = 50;
    public static int BOTTOM_Y = TOP_Y + HEIGHT;
    private final Snake snake;
    private final KeyHandler keyHandler;

    private int updateCount = 0;

    public PlayManager() {
        snake = new Snake(20, 12);
        keyHandler = new KeyHandler(snake);
    }

    public void update() {
        updateCount++;

        if (updateCount == 30) {
            updateCount = 0;
            boolean stillPlaying = snake.move();
            System.out.println(snake.print());
            if (!stillPlaying)
                System.out.println("Game over!!!");
        }
    }

    public void draw(Graphics2D graphics) {
        graphics.setColor(Color.WHITE);
        graphics.setStroke(new BasicStroke(4f));
        graphics.drawRect(LEFT_X - 4, TOP_Y - 4, WIDTH + 8, HEIGHT + 8);

        char[][] board = snake.getBoard();
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                int x = LEFT_X + j * Block.SIZE + 1;
                int y = TOP_Y + i * Block.SIZE + 1;
                if (board[i][j] == Snake.SNAKE) {
                    graphics.setColor(Color.WHITE);
                    graphics.fillRect(x, y, Block.SIZE - 2, Block.SIZE - 2);
                }
                if (board[i][j] == Snake.PLUS) {
                    graphics.setColor(Color.RED);
                    graphics.fillRect(x, y, Block.SIZE - 2, Block.SIZE - 2);
                }
            }

        }
    }

    public KeyListener getKeyListener() {
        return keyHandler;
    }
}
