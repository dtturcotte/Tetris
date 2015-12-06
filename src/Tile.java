import java.awt.Color;

public class Tile {
    private Board board;
    private int currentRow, currentCol;
    private Color color;

    public Tile(Board b, int row, int col) {

        board = b;

        //when Tile is instantiated, set its position
        this.setLocation(row, col);
    }

    public int getRow() {
        return currentRow;
    }

    public int getCol() {
        return currentCol;
    }

    public boolean checkNewLocation(int newRow, int newCol) {
        return board.isOpen(newRow, newCol);
    }

    public boolean collision(int newRow, int newCol) {
        return this.getRow() == ((board.getHeight()/board.getTileSize()))-2 || board.getTileAt(newRow, newCol) != null;
    }

    public void setLocation(int newRow, int newCol) {
        currentRow = newRow;
        currentCol = newCol;
        board.setTileAt(this, currentRow, currentCol);
    }

    public Color getColor() {
        return setColor(color);
    }

    public Color setColor(Color myColor) {
        color = myColor;
        return color;
    }


}
     