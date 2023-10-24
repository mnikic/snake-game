package my.project;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyListener;
import java.awt.BasicStroke;

public class PlayManager {

    private static final int WIDTH = 360;
    private static final int HEIGHT = 600;
    private static int LEFT_X = 35;
    private static int RIGHT_X = LEFT_X + WIDTH;
    private static int TOP_Y = 50;
    private static int BOTTOM_Y = TOP_Y + HEIGHT;
    private final Snake snake;
    private final KeyHandler keyHandler;
    private int framesLimit = 30;
    private int updateCount = 0;
    private boolean gameOver = false;
    private int level = 1;
    private int score = 0;
    private int thingsEaten = 0;

    public PlayManager() {
        snake = new Snake(20, 12);
        keyHandler = new KeyHandler(snake);
    }

    public void update() {
        if (!gameOver) {
            updateCount++;

            if (updateCount == framesLimit) {
                updateCount = 0;
                boolean stillPlaying = snake.move();
                System.out.println(snake.print());
                if (!stillPlaying)
                    gameOver = true;
                if (snake.getLastMoveGotBigger()) {
                    thingsEaten++;
                    score += level;
                    if (thingsEaten % 5 == 0) {
                        level++;
                        framesLimit--;
                        framesLimit = Math.max(5, framesLimit);
                    }
                }
            }
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
                int width = Block.SIZE - 2;
                if (board[i][j] == Snake.SNAKE) {
                    graphics.setColor(Color.WHITE);
                    graphics.fillRect(x, y, width, width);
                } else if (board[i][j] == Snake.PLUS) {
                    graphics.setColor(Color.RED);
                    graphics.fillRect(x, y, width, width);
                }
            }

        }

        graphics.setColor(Color.WHITE);
        graphics.setFont(graphics.getFont().deriveFont(30f));
        // draw score and level etc.
        graphics.drawRect(RIGHT_X + 100, TOP_Y, 250, 250);
        graphics.drawString("LEVEL: " + level, RIGHT_X + 140, TOP_Y + 90);
        graphics.drawString("SCORE: " + score, RIGHT_X + 140, TOP_Y + 160);

        if (gameOver) {
            graphics.setColor(Color.RED);
            graphics.setFont(graphics.getFont().deriveFont(50f));
            graphics.drawString("GAME OVER!", LEFT_X + 20, TOP_Y + 320);
        }
    }

    public KeyListener getKeyListener() {
        return keyHandler;
    }
}
