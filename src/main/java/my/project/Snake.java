package my.project;

import java.util.List;
import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.stream.Collectors;

public class Snake {
    public static final char SNAKE = '*';
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
                board[oldTail[0]][oldTail[1]] = ' ';
                flipOldHead(head);
                return new Move(false, multiplier, false);
            }
            positionSelector.occupy(new int[]{newHead[0] - 1,newHead[1] - 1});
            if (board[newHead[0]][newHead[1]] != PLUS) {
                int[] oldTail = snake.removeFirst();
                positionSelector.unoccupy(new int[]{oldTail[0] - 1, oldTail[1] - 1});
                board[newHead[0]][newHead[1]] = HEAD;
                board[oldTail[0]][oldTail[1]] = ' ';
                flipOldHead(head);
            } else {
                eatenAt = i;
                board[newHead[0]][newHead[1]] = (char)(multiplier - eatenAt + 48);
                int[] plus = positionSelector.randomUnoccupiedPosition();
                board[plus[0] + 1][plus[1] + 1] = PLUS;
                flipOldHead(head);
                scored = true;
            }
            snake.addLast(newHead);
            head = newHead;
        }
        if (moveBuffer.isEmpty()) {
            lastDirection = direction;
        }
        return new Move(true, multiplier - eatenAt, scored);
    }

    private void flipOldHead(int[] head) {
        if (board[head[0]][head[1]] == HEAD)
            board[head[0]][head[1]] = SNAKE;
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
        int[] plus = positionSelector.randomUnoccupiedPosition();
        board[plus[0] + 1][plus[1] + 1] = PLUS;
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
