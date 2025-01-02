package my.project;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.StringJoiner;
import java.awt.BasicStroke;

public class PlayManager {

    private static final int WIDTH = 360;
    private static final int HEIGHT = 600;
    private static int LEFT_X = 35;
    private static int RIGHT_X = LEFT_X + WIDTH;
    private static int TOP_Y = 50;
    private static int BOTTOM_Y = TOP_Y + HEIGHT;
    private static final Color GREEN = new Color(0, 135, 62, 200);

    // Game state
    private final Snake snake;
    private int framesLimit;
    private int updateCount;
    private int gameOverCount;
    private boolean gameOver;
    private boolean firstStart = true;
    private int level;
    private int score;
    private int thingsEaten;

    public PlayManager() {
        snake = new Snake(20, 12);
        init();
    }

    public void update() {
        if (!gameOver && !firstStart) {
            updateCount++;
            if (updateCount == framesLimit) {
                updateCount = 0;
                Snake.Move move = snake.move();
                System.out.println(snake.print());
                if (!move.isAlive()) {
                    gameOver = true;
                    GamePanel.SOUNDS.play(3, false);
                } else if (move.eaten()) {
                    thingsEaten++;
                    switch (move.distance()) {
                        case 1:
                            GamePanel.SOUNDS.play(0, false);   
                            break;
                        case 2:
                            GamePanel.SOUNDS.play(4, false);
                            break;
                        default:
                            GamePanel.SOUNDS.play(5, false);
                    }
                    score += level * move.distance();
                    if (thingsEaten % 5 == 0) {
                        level++;
                        framesLimit--;
                        framesLimit = Math.max(5, framesLimit);
                    }
                } else if (move.distance() == 1) {
                    GamePanel.SOUNDS.play(2, false);
                } else {
                    GamePanel.SOUNDS.play(1, false);
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
                int x = LEFT_X + j * Block.SIZE + 3;
                int y = TOP_Y + i * Block.SIZE + 3;
                int width = Block.SIZE - 6;
                if (board[i][j] == Snake.SNAKE) {
                    graphics.setColor(GREEN);
                    graphics.fillRect(x, y, width, width);
                } else if (board[i][j] == Snake.PLUS) {
                    graphics.setColor(Color.RED);
                    graphics.fillRect(x - 3, y - 3, Block.SIZE, Block.SIZE);
                } else if (board[i][j] >= 48 && board[i][j] < 60){
                    graphics.setColor(Color.DARK_GRAY);
                    graphics.fillRect(x - 3, y - 3, Block.SIZE, Block.SIZE);
                    graphics.setColor(Color.WHITE);
                    graphics.setFont(graphics.getFont().deriveFont(30f));
                    graphics.drawString(""+(board[i][j] - 48)+"x", x - 3, y - 3);
                } else if (board[i][j] == Snake.DEAD) {
                    graphics.setColor(Color.BLUE);
                    graphics.fillRect(x - 3, y - 3, Block.SIZE, Block.SIZE);
                }
            }

        }

        graphics.setColor(Color.WHITE);
        graphics.setFont(graphics.getFont().deriveFont(30f));
        // draw score and level etc.
        graphics.drawRect(RIGHT_X + 100, TOP_Y, 250, 250);
        graphics.drawString("LEVEL: " + level, RIGHT_X + 140, TOP_Y + 70);
        graphics.drawString("SCORE: " + score, RIGHT_X + 140, TOP_Y + 140);
        graphics.drawString("EATEN: " + thingsEaten, RIGHT_X + 140, TOP_Y + 210);

        if (gameOver || firstStart) {
            if (gameOver) {
                if (gameOverCount++ < GamePanel.FPS) {
                    graphics.setColor(Color.RED);
                    graphics.setFont(graphics.getFont().deriveFont(50f));
                    graphics.drawString("GAME OVER!", LEFT_X + 20, TOP_Y + 320);
                }
                gameOverCount %= GamePanel.FPS * 2;
                graphics.setColor(Color.WHITE);
                graphics.setFont(graphics.getFont().deriveFont(30f));
                graphics.drawString("SPACE to start!", RIGHT_X + 100, TOP_Y + 350);
                graphics.drawString("Esc to quit.", RIGHT_X + 100, TOP_Y + 400);
            } else {
                if (gameOverCount++ * 3 < GamePanel.FPS) 
                    graphics.setColor(Color.WHITE);
                else if (gameOverCount * 3 < GamePanel.FPS * 2)
                    graphics.setColor(Color.YELLOW);
                else
                    graphics.setColor(Color.GREEN);

                gameOverCount %= GamePanel.FPS;
                graphics.setFont(graphics.getFont().deriveFont(35f));
                graphics.drawString("SPACE to start!", LEFT_X + 45, TOP_Y + 120);
                graphics.drawString("Esc to quit.", LEFT_X + 45, TOP_Y + 170);
            }

        } else {
            StringJoiner joiner = new StringJoiner(",");
            for (Direction direction : snake.getMoveBuffer()) {
                joiner.add(direction.getText());
            }
            graphics.setFont(graphics.getFont().deriveFont(20f));
            graphics.drawString(joiner.toString(), LEFT_X, BOTTOM_Y + 30);
        }
    }

    public void up() {
        snake.up();
    }

    public void down() {
        snake.down();
    }

    public void left() {
        snake.left();
    }

    public void right() {
        snake.right();
    }

    public void maybeReset() {
        if (firstStart) {
            firstStart = false;
        } else if (gameOver) {
            snake.reset();
            init();
        }
    }

    private void init() {
        framesLimit = 30;
        updateCount = 0;
        gameOver = false;
        gameOverCount = 0;
        level = 1;
        score = 0;
        thingsEaten = 0;
    }
}
