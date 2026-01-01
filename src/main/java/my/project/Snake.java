package my.project;

import java.util.List;
import java.util.Set;

import static java.util.Arrays.asList;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.stream.Collectors;

public class Snake {

    public static final char PLUS = '+';
    public static final char DEAD = 'X';
    public static final char HEAD = 'H';

    public static final char BODY_V = '|';
    public static final char BODY_H = '-';
    public static final char TURN_UR = 'L'; // Up-Right connection
    public static final char TURN_UL = 'J'; // Up-Left connection
    public static final char TURN_DL = '7'; // Down-Left connection
    public static final char TURN_DR = 'F'; // Down-Right connection

    public static final char TAIL_U = '^';
    public static final char TAIL_D = 'v';
    public static final char TAIL_L = '<';
    public static final char TAIL_R = '>';

    private static final Set<Character> SNAKE_CHARS = new HashSet<>();

    static {
        SNAKE_CHARS.addAll(
                asList(TAIL_U, TAIL_D, TAIL_L, TAIL_R, BODY_H, BODY_V, TURN_DR, TURN_DL, TURN_UL, TURN_UR, HEAD));
    }

    private final int depth, width;
    private final LinkedList<int[]> snake = new LinkedList<>();
    private final PositionSelector positionSelector;
    private final char[][] board;
    private final BlockingQueue<Direction> moveBuffer = new LinkedBlockingDeque<>();
    private Direction lastDirection;
    private int[] apple = null;

    public Snake(int depth, int width) {
        this.depth = depth;
        this.width = width;
        board = new char[depth + 2][width + 2];
        positionSelector = new PositionSelector(depth, width);
        init();
    }

    public Move move() {
        boolean scored = false;
        int[] head = snake.getLast();
        int[] oldHead = head;
        Direction direction;
        int multiplier = 1;

        if (!moveBuffer.isEmpty()) {
            direction = moveBuffer.poll();
            while (!moveBuffer.isEmpty() && moveBuffer.peek() == direction) {
                moveBuffer.poll();
                multiplier++;
            }
        } else
            direction = lastDirection;

        int eatenAt = 0;

        Direction previousDirForLoop = lastDirection;
        for (int i = 0; i < multiplier; i++) {
            int[] newHead = { head[0] + direction.getDelta()[0], head[1] + direction.getDelta()[1] };

            // Collision Logic
            if (newHead[0] < 1 || newHead[0] >= board.length - 1 || newHead[1] < 1
                    || newHead[1] >= board[newHead[0]].length - 1 || isSnake(board[newHead[0]][newHead[1]])
                    || board[newHead[0]][newHead[1]] > 47) {

                board[newHead[0]][newHead[1]] = DEAD;
                int[] oldTail = snake.removeFirst();

                if (!(newHead[0] == oldTail[0] && newHead[1] == oldTail[1])) {
                    board[oldTail[0]][oldTail[1]] = ' ';
                    positionSelector.unoccupy(new int[] { oldTail[0] - 1, oldTail[1] - 1 });
                }

                flipOldHead(head, direction, previousDirForLoop);
                return new Move(false, multiplier, false);
            }

            positionSelector.occupy(new int[] { newHead[0] - 1, newHead[1] - 1 });

            if (board[newHead[0]][newHead[1]] != PLUS) {
                int[] oldTail = snake.removeFirst();
                positionSelector.unoccupy(new int[] { oldTail[0] - 1, oldTail[1] - 1 });
                board[newHead[0]][newHead[1]] = HEAD;
                board[oldTail[0]][oldTail[1]] = ' ';
            } else {
                eatenAt = i;
                board[newHead[0]][newHead[1]] = (char) (multiplier - eatenAt + 48);
                var newApple = positionSelector.randomUnoccupiedPosition();
                apple = new int[] { newApple[0] + 1, newApple[1] + 1 };
                scored = true;
            }

            flipOldHead(head, direction, previousDirForLoop);

            snake.addLast(newHead);
            head = newHead;

            // Update the previous direction for the NEXT iteration of the loop
            previousDirForLoop = direction;
            updateTailGraphic();
        }

        drawApple(oldHead);

        if (moveBuffer.isEmpty()) {
            lastDirection = direction;
        }

        System.out.println("Last move: mulitplier: " + multiplier + " scored: " + scored);
        sanityCheck();
        return new Move(true, multiplier - eatenAt, scored);
    }

    private void flipOldHead(int[] head, Direction direction, Direction prevDirection) {
        if (board[head[0]][head[1]] == HEAD)
            drawComponentAfterHead(head, direction, prevDirection);
    }

    void drawComponentAfterHead(int[] oldHead, Direction direction, Direction prevDirection) {
        if (snake.size() == 0 || board[oldHead[0]][oldHead[1]] == ' ')
            return;

        if (prevDirection != direction) {
            char ch = ' ';

            if (direction == Direction.UP) {
                if (prevDirection == Direction.LEFT)
                    ch = TURN_UR; // Came from RIGHT (L), going UP
                else if (prevDirection == Direction.RIGHT)
                    ch = TURN_UL; // Came from LEFT (J), going UP
            } else if (direction == Direction.DOWN) {
                if (prevDirection == Direction.LEFT)
                    ch = TURN_DR; // Came from RIGHT (F), going DOWN
                else if (prevDirection == Direction.RIGHT)
                    ch = TURN_DL; // Came from LEFT (7), going DOWN
            } else if (direction == Direction.LEFT) {
                if (prevDirection == Direction.UP)
                    ch = TURN_DL; // Came from DOWN (7), going LEFT
                else if (prevDirection == Direction.DOWN)
                    ch = TURN_UL; // Came from UP (J), going LEFT
            } else { // RIGHT
                if (prevDirection == Direction.UP)
                    ch = TURN_DR; // Came from DOWN (F), going RIGHT
                else if (prevDirection == Direction.DOWN)
                    ch = TURN_UR; // Came from UP (L), going RIGHT
            }

            if (ch == ' ') {
                // Should be unreachable unless 180 turn (which game rules should prevent)
                // Fallback to simple body part
                board[oldHead[0]][oldHead[1]] = (direction == Direction.LEFT || direction == Direction.RIGHT) ? BODY_H
                        : BODY_V;
            } else {
                board[oldHead[0]][oldHead[1]] = ch;
            }
        } else {
            // Straight line
            board[oldHead[0]][oldHead[1]] = (direction == Direction.LEFT || direction == Direction.RIGHT) ? BODY_H
                    : BODY_V;
        }
    }

    private void updateTailGraphic() {
        if (snake.size() < 2)
            return; // Can't determine direction if size is 1

        int[] tail = snake.getFirst(); // The current end of the snake
        int[] neck = snake.get(1); // The part attached to the tail

        // Determine which way the tail should point
        // Logic: The tail points AWAY from the neck
        char tailChar = ' ';

        if (neck[0] < tail[0])
            tailChar = TAIL_D; // Neck is Above, Tail points Down (v)
        else if (neck[0] > tail[0])
            tailChar = TAIL_U; // Neck is Below, Tail points Up (^)
        else if (neck[1] < tail[1])
            tailChar = TAIL_R; // Neck is Left, Tail points Right (>)
        else if (neck[1] > tail[1])
            tailChar = TAIL_L; // Neck is Right, Tail points Left (<)

        // Only update if it's not currently digesting a number
        if (board[tail[0]][tail[1]] < '0' || board[tail[0]][tail[1]] > '9') {
            board[tail[0]][tail[1]] = tailChar;
        }
    }

    private void sanityCheck() {
        for (int i = 1; i < board.length - 1; i++) {
            for (int j = 1; j < board[i].length - 1; j++) {
                var position = new int[] { i - 1, j - 1 };
                var occupied = !positionSelector.checkUnoccupied(position);
                var ch = board[i][j];
                if (ch == ' ') {
                    if (occupied) {
                        System.out.println("Position " + i + ", " + j + " shouldnt be considered occupied");
                        board[i][j] = 'I';
                        System.out.println(print());
                        positionSelector.debug(position);
                        System.exit(1);
                    }
                } else if (SNAKE_CHARS.contains(ch)) {
                    if (!occupied) {
                        board[i][j] = 'I';
                        System.out.println(print());
                        System.out.println("Position " + i + ", " + j + " snake should be occupied.");
                        positionSelector.debug(position);
                        System.exit(1);
                    }
                } else if (ch == HEAD) {
                    if (!occupied) {
                        board[i][j] = 'I';
                        System.out.println(print());
                        System.out.println("Position " + i + ", " + j + " head should be occupied.");
                        positionSelector.debug(position);
                        System.exit(1);
                    }
                } else if (ch == PLUS) {
                    if (occupied) {
                        board[i][j] = 'I';
                        System.out.println(print());
                        System.out.println("Position " + i + ", " + j + " apple should not be occupied.");
                        positionSelector.debug(position);
                        System.exit(1);
                    }
                } else if (!occupied) {
                    board[i][j] = 'I';
                    System.out.println(print());
                    System.out.println("Position " + i + ", " + j + " char " + ch + " should be occupied.");
                    positionSelector.debug(position);
                    System.exit(1);
                }
            }
        }
    }

    private void drawApple(int[] oldHead) {
        int[] currentHead = snake.getLast();
        int x = apple[0];
        int y = apple[1];
        int xDistance = currentHead[0] - oldHead[0];
        int xDelta = xDistance > 0 ? 1 : (xDistance == 0 ? 0 : -1);
        x += xDelta;
        if (x < 1)
            x = 2;
        if (x > board.length - 2)
            x = board.length - 2;
        if (xDelta == 0) {
            y += (currentHead[1] - oldHead[1]) > 0 ? 1 : -1;
            if (y < 1)
                y = 2;
            else if (y > board[x].length - 2)
                y = board[x].length - 2;
        }
        if (board[x][y] != ' ')
            return;
        board[apple[0]][apple[1]] = ' ';
        board[x][y] = PLUS;
        apple[0] = x;
        apple[1] = y;
    }

    public void reset() {
        positionSelector.reset();
        snake.clear();
        moveBuffer.clear();
        lastDirection = null;
        init();
    }

    private void init() {
        int[] head = new int[] { depth / 2, width / 2 };
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if (i == head[0] && j == head[1]) {
                    snake.addLast(new int[] { i, j });
                    board[i][j] = HEAD;
                    positionSelector.occupy(new int[] { i - 1, j - 1 });
                } else
                    board[i][j] = ' ';
            }
        }
        var newApple = positionSelector.randomUnoccupiedPosition();
        apple = new int[] { newApple[0] + 1, newApple[1] + 1 };
        drawApple(head);
        right();
    }

    public char[][] getBoard() {
        return board;
    }

    public List<Direction> getMoveBuffer() {
        return moveBuffer.stream().collect(Collectors.toList());
    }

    public String print() {
        StringBuilder sb = new StringBuilder();
        for (char[] row : board) {
            sb.append('|');
            for (char cell : row) {
                sb.append(cell);
            }
            sb.append("|\n");
        }
        return sb.toString();
    }

    public void up() {
        moveBuffer.offer(Direction.UP);
    }

    public void down() {
        moveBuffer.offer(Direction.DOWN);
    }

    public void right() {
        moveBuffer.offer(Direction.RIGHT);
    }

    public void left() {
        moveBuffer.offer(Direction.LEFT);
    }

    public static final class Move {
        private final boolean alive;
        private final int distance;
        private final boolean eaten;

        private Move(boolean alive, int distance, boolean eaten) {
            this.alive = alive;
            this.distance = distance;
            this.eaten = eaten;
        }

        public boolean isAlive() {
            return alive;
        }

        public boolean eaten() {
            return eaten;
        }

        public int distance() {
            return distance;
        }
    }

    public static boolean isSnake(char ch) {
        return SNAKE_CHARS.contains(ch);
    }
}
