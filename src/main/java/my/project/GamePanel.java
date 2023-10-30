package my.project;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

/**
 * Hello world!
 *
 */
public class GamePanel extends JPanel implements Runnable {
    public static final int WIDTH = 780;
    public static final int HEIGHT = 720;
    public static final int FPS = 60;

    public static final Sound MUSIC = new Sound();
    public static final Sound SOUNDS = new Sound();

    private final Thread thread = new Thread(this);
    private final PlayManager playManager = new PlayManager();

    public GamePanel() {
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        this.setBackground(Color.BLACK);
        this.setLayout(null);
        this.addKeyListener(new KeyHandler(playManager));
        this.setFocusable(true);
    }

    public void launchGame() {
        thread.run();

        // music.play(0, true);
        // music.loop();
    }

    @Override
    public void run() {
        // Game loop
        double drawInterval = 1_000_000_000 / FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;

        while (thread != null) {
            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;
            if (delta > 1) {
                update();
                repaint();
                delta--;
            }
        }

    }

    private void update() {
        playManager.update();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D twoDGraphics = (Graphics2D) g;
        playManager.draw(twoDGraphics);
    }
}
