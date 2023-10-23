package my.project;

/**
 * Hello world!
 *
 */
public class App {
    public static void main(String[] args) {
        Snake snake = new Snake(5, 5);
        System.out.println(snake.print());
        for (int i = 0; i < 5; i++) {
            if (!snake.move()) {
                System.out.println("GAME OVER!");
                System.exit(1);
            }
            snake.down();
            System.out.println(snake.print());
        }
    }
}
