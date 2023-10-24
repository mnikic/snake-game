package my.project;

import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class Snake {
    public static final char SNAKE = '*';
    public static final char PLUS = '+';
    private final LinkedList<int[]> snake = new LinkedList<>();
    private final PositionSelector positionSelector;
    private final char[][] board;
    private boolean lastMoveGotBigger;
    BlockingQueue<int[]> blockingQueue = new LinkedBlockingDeque<>();
    private int[] lastDirection;

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
        int[] direction;
        int multiplier = 1;
        if (blockingQueue.size() > 0) {
            direction = blockingQueue.poll();
            if (lastDirection != null && direction[0] == lastDirection[0] && direction[1] == lastDirection[1]) {
                multiplier = 2;
            }
        } else
            direction = lastDirection;
        for (int i = 0; i < multiplier; i++) {
            int[] newHead = { head[0] + direction[0], head[1] + direction[1] };
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
            } else {
                int[] plus = positionSelector.randomUnoccupiedPosition();
                board[plus[0]][plus[1]] = PLUS;
                lastMoveGotBigger = true;
            }
            board[newHead[0]][newHead[1]] = SNAKE;
            snake.addLast(newHead);
            head = newHead;
        }
        if (blockingQueue.isEmpty()) {
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
        blockingQueue.offer(new int[] { -1, 0 });
    }

    public void down() {
        blockingQueue.offer(new int[] { 1, 0 });
    }

    public void right() {
        blockingQueue.offer(new int[] { 0, 1 });
    }

    public void left() {
        blockingQueue.offer(new int[] { 0, -1 });
    }

}
