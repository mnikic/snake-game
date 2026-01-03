package my.project;

enum Direction {
    UP(new int[] { -1, 0 }, "^"),
    DOWN(new int[] { 1, 0 }, "v"),
    LEFT(new int[] { 0, -1 }, "<"),
    RIGHT(new int[] { 0, 1 }, ">");

    Direction(int[] delta, String text) {
        this.delta = delta;
        this.text = text;
    }

    private int[] delta;
    private String text;

    public int[] getDelta() {
        return delta;
    }

    public String getText() {
        return text;
    }
}
