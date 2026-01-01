package my.project;

import java.util.List;
import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.stream.Collectors;

public class Snake {
    public static final char SNAKE = '*';
    public static final char SNAKE_BEND_LEFT = 'L';
    public static final char PLUS = '+';
    public static final char SWALLOWED = 'O';
    public static final char DEAD = 'X';
    public static final char HEAD = 'H';

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
        for (int i = 0; i < multiplier; i++) {
            int[] newHead = { head[0] + direction.getDelta()[0], head[1] + direction.getDelta()[1] };
            if (newHead[0] < 1 || newHead[0] >= board.length - 1 || newHead[1] < 1
                    || newHead[1] >= board[newHead[0]].length - 1 || board[newHead[0]][newHead[1]] == SNAKE
                    || board[newHead[0]][newHead[1]] > 47) {
                board[newHead[0]][newHead[1]] = DEAD;
                int[] oldTail = snake.removeFirst();
                if (!(newHead[0] == oldTail[0] && newHead[1] == oldTail[1]))
                    board[oldTail[0]][oldTail[1]] = ' ';
                flipOldHead(head);
                return new Move(false, multiplier, false);
            }
            positionSelector.occupy(new int[] { newHead[0] - 1, newHead[1] - 1 });
            if (board[newHead[0]][newHead[1]] != PLUS) {
                int[] oldTail = snake.removeFirst();
                positionSelector.unoccupy(new int[] { oldTail[0] - 1, oldTail[1] - 1 });
                board[newHead[0]][newHead[1]] = HEAD;
                board[oldTail[0]][oldTail[1]] = ' ';
                flipOldHead(head);
            } else {
                eatenAt = i;
                board[newHead[0]][newHead[1]] = (char) (multiplier - eatenAt + 48);
                var newApple = positionSelector.randomUnoccupiedPosition();
                apple = new int[] { newApple[0] + 1, newApple[1] + 1 };
                flipOldHead(head);
                scored = true;
            }
            snake.addLast(newHead);
            head = newHead;
        }
        drawApple(oldHead);
        if (lastDirection != direction && snake.size() > 1) {
            if (board[oldHead[0]][oldHead[1]] != ' ')
                board[oldHead[0]][oldHead[1]] = SNAKE_BEND_LEFT;
        }
        if (moveBuffer.isEmpty()) {
            lastDirection = direction;
        }
        System.out.println("Last move: mulitplier: " + multiplier + " scored: " + scored);
        sanityCheck();
        return new Move(true, multiplier - eatenAt, scored);
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
                } else if (ch == SNAKE || ch == SNAKE_BEND_LEFT) {
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

    private void flipOldHead(int[] head) {
        if (board[head[0]][head[1]] == HEAD)
            board[head[0]][head[1]] = SNAKE;
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
}
