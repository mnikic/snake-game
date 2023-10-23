package my.project;

import java.util.LinkedList;

public class Snake {
    public static final char SNAKE = '*';
    public static final char PLUS = '+';
    private final LinkedList<int[]> snake = new LinkedList<>();
    private final PositionSelector positionSelector;
    private int[] direction = new int[2];
    private final char[][] board;

    public Snake(int depth, int width) {
        board = new char[depth][width];
        int[] head = new int[] { depth / 2, width / 2 };
        positionSelector = new PositionSelector(depth, width);
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if ((i == head[0] - 1 || i == head[0]) && j == head[1]) {
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
        int[] head = snake.getLast();
        int[] newHead = { head[0] + direction[0], head[1] + direction[1] };
        if (newHead[0] < 0 || newHead[1] >= board.length || newHead[1] < 0 || newHead[1] >= board[newHead[0]].length)
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
        }
        board[newHead[0]][newHead[1]] = '*';
        snake.addLast(newHead);
        return true;
    }

    public char[][] getBoard() {
        return board;
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
        direction = new int[] { -1, 0 };
    }

    public void down() {
        direction = new int[] { 1, 0 };
    }

    public void right() {
        direction = new int[] { 0, 1 };
    }

    public void left() {
        direction = new int[] { 0, -1 };
    }

}
