package my.project;

import java.awt.event.KeyEvent;

public class KeyHandler implements java.awt.event.KeyListener {

    private final PlayManager playManager;

    public KeyHandler(PlayManager playManager) {
        this.playManager = playManager;
    }

    @Override
    public void keyTyped(KeyEvent event) {
    }

    @Override
    public void keyPressed(KeyEvent event) {
    }

    @Override
    public void keyReleased(KeyEvent event) {
        int code = event.getKeyCode();
        if (code == KeyEvent.VK_UP || code == KeyEvent.VK_W) {
            playManager.up();
        } else if (code == KeyEvent.VK_DOWN || code == KeyEvent.VK_S) {
            playManager.down();
        } else if (code == KeyEvent.VK_LEFT || code == KeyEvent.VK_A) {
            playManager.left();
        } else if (code == KeyEvent.VK_RIGHT || code == KeyEvent.VK_D) {
            playManager.right();
        } else if (code == KeyEvent.VK_ESCAPE || code == KeyEvent.VK_Q) {
            System.exit(0);
        } else if (code == KeyEvent.VK_SPACE) {
            playManager.maybeReset();
        }
    }
    
}
