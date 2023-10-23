package my.project;

import java.awt.event.KeyEvent;

public class KeyHandler implements java.awt.event.KeyListener {

    private final Snake snake;

    public KeyHandler(Snake snake) {
        this.snake = snake;
    }

    @Override
    public void keyTyped(KeyEvent event) {
    }

    @Override
    public void keyPressed(KeyEvent event) {
        int code = event.getKeyCode();
        if (code == KeyEvent.VK_UP || code == KeyEvent.VK_W) {
            snake.up();
        } else if (code == KeyEvent.VK_DOWN || code == KeyEvent.VK_S) {
            snake.down();
        } else if (code == KeyEvent.VK_LEFT || code == KeyEvent.VK_A) {
            snake.left();
        } else if (code == KeyEvent.VK_RIGHT || code == KeyEvent.VK_D) {
            snake.right();
        }
    }

    @Override
    public void keyReleased(KeyEvent event) {
    }
}
