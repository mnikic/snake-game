package my.project;

import javax.swing.JFrame;

/**
 * Hello world!
 *
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("Starting");
        JFrame window = new JFrame("Snake");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);

        GamePanel gPanel = new GamePanel();
        window.add(gPanel);
        window.pack();

        window.setLocationRelativeTo(null);
        window.setVisible(true);

        gPanel.launchGame();
    }
}
