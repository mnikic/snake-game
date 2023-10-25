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
    private final LinkedList<int[]> snake = new LinkedList<>();
    private final PositionSelector positionSelector;
    private final char[][] board;
    private boolean lastMoveGotBigger;
    private final BlockingQueue<Direction> moveBuffer = new LinkedBlockingDeque<>();
    private Direction lastDirection;

    public Snake(int depth, int width) {
        board = new char[depth][width];
        int[] head = new int[] { depth / 2, width / 2 };
        positionSelector = new PositionSelector(depth, width);
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if (i == head[0] && j == head[1]) {
                    snake.addLast(new int[] { i, j });
                    board[i][j] = SNAKE;
                    positionSelector.occupy(new int[] { i, j });
                } else
                    board[i][j] = ' ';
            }
        }
        int[] plus = positionSelector.randomUnoccupiedPosition();
        board[plus[0]][plus[1]] = PLUS;
        right();
    }

    public boolean move() {
        lastMoveGotBigger = false;
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
        for (int i = 0; i < multiplier; i++) {
            int[] newHead = { head[0] + direction.getDelta()[0], head[1] + direction.getDelta()[1] };
            if (newHead[0] < 0 || newHead[0] >= board.length || newHead[1] < 0
                    || newHead[1] >= board[newHead[0]].length)
                return false;
            if (board[newHead[0]][newHead[1]] == SNAKE)
                return false;
            positionSelector.occupy(newHead);
            if (board[newHead[0]][newHead[1]] != PLUS) {
                int[] oldTail = snake.removeFirst();
                board[oldTail[0]][oldTail[1]] = ' ';
                positionSelector.unoccupy(oldTail);
                board[newHead[0]][newHead[1]] = SNAKE;
            } else {
                board[newHead[0]][newHead[1]] = SWALLOWED;
                int[] plus = positionSelector.randomUnoccupiedPosition();
                board[plus[0]][plus[1]] = PLUS;
                lastMoveGotBigger = true;
            }
            snake.addLast(newHead);
            head = newHead;
        }
        if (moveBuffer.isEmpty()) {
            lastDirection = direction;
        }
        return true;
    }

    public char[][] getBoard() {
        return board;
    }

    public boolean getLastMoveGotBigger() {
        return lastMoveGotBigger;
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

}
