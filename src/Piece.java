import java.awt.Color;
import java.awt.event.KeyEvent;


public class Piece {
    private int[] pieceCoordinates;
    private String shape;
    private Color color;
    private Board board;
    private Tile tile[];
    private Boolean nextPiece, moveable;

    private int[] newRowPosition, newColPosition, currRowPosition, currColPosition;

    //don't need to pass in board because I'm already utilizing the Tiles class, which knows about the board
    public Piece(Board b, String randomPiece, Color randomColor, Boolean nxtPiece) {
        moveable = true;
        shape = randomPiece;
        color = randomColor;
        board = b;

        newRowPosition = new int[4];
        newColPosition = new int[4];
        currRowPosition = new int[4];
        currColPosition = new int[4];
        pieceCoordinates = new int[8];
        nextPiece = nxtPiece;

        //set pieceCoordinates global variable
        getShape(shape);
        tile = new Tile[4];

        int counterRow = 0, counterCol = 1;
        System.out.print("\"" + shape + "\" Coordinates: ");
        //generate 4 new Tiles at specified coordinates that will compose the Piece
        for (int i = 0; i < tile.length; i++) {
            tile[i] = new Tile(board, pieceCoordinates[counterRow], pieceCoordinates[counterCol]);
            System.out.print("(" + pieceCoordinates[counterRow] + ", " + pieceCoordinates[counterCol] + ") ");
            //increment by 2 because x,y values are next to each other in array
            counterRow+=2;
            counterCol+=2;
        }
        System.out.println("\n");

        for (int i = 0; i < tile.length; i++) {
            tile[i].setColor(color);
        }
    }

    public void calcNewPosition(int newRow, int newCol, int currTile) {
        newRowPosition[currTile] = newRow;
        newColPosition[currTile] = newCol;
    }

    public void clearCurrPosition() {
        for (int i = 0; i < tile.length; i++) {
            currRowPosition[i] = tile[i].getRow();
            currColPosition[i] = tile[i].getCol();
            board.setTileAt(null, currRowPosition[i], currColPosition[i]);
        }
    }

    public void autoMove() {
        for (int i = 0; i < tile.length; i++) {
            calcNewPosition(tile[i].getRow()+1, tile[i].getCol(), i);
        }
        clearCurrPosition();
        for (int i = 0; i < tile.length; i++) {
            board.checkEndGame(tile[i].getRow(), tile[i].getCol());
        }
        board.checkBottomFull();
        if (isCollision()) board.createNewPiece();
        move();
    }

    public void movePieceCheck(int keycode) {
        if (keycode == KeyEvent.VK_SPACE) {
            System.out.println("pause");
            //if it's already paused, un pause it...
            if (board.getPause()) {
                board.setPause(false);
                moveable = true;
            }
            //if it's not paused, pause it
            else {
                board.setPause(true);
                moveable = false;
            }
        }
        if (moveable) {
            if (keycode == KeyEvent.VK_DOWN) {
                for (int i = 0; i < tile.length; i++) {
                    calcNewPosition(tile[i].getRow()+1, tile[i].getCol(), i);
                }
                clearCurrPosition();
                for (int i = 0; i < tile.length; i++) {
                    board.checkEndGame(tile[i].getRow(), tile[i].getCol());
                }
                board.checkBottomFull();
                if (isCollision()) board.createNewPiece();
                move();
            }
            if (keycode == KeyEvent.VK_RIGHT) {
                for (int i = 0; i < tile.length; i++) {
                    calcNewPosition(tile[i].getRow(), tile[i].getCol()+1, i);
                }
                clearCurrPosition();
                move();
            }
            if (keycode == KeyEvent.VK_LEFT) {
                for (int i = 0; i < tile.length; i++) {
                    calcNewPosition(tile[i].getRow(), tile[i].getCol()-1, i);
                }
                clearCurrPosition();
                move();
            }
            //rotate left
            if (keycode == KeyEvent.VK_A) {
                int[] rotatedCoords = calcRotation("left");
                clearCurrPosition();
                rotate(rotatedCoords, "left");
            }

            //rotate right
            if (keycode == KeyEvent.VK_D) {
                int[] rotatedCoords = calcRotation("right");
                clearCurrPosition();
                rotate(rotatedCoords, "right");
            }



            if (keycode == KeyEvent.VK_UP) {
                int[] newCoords = new int[2];
                int j = 0;
                for (int k : board.hardDrop()) {
                    System.out.println("Hard drop coords: " + k);
                    newCoords[j] = k;
                    j++;
                }
                System.out.println("new coords: " + newCoords[0]);

                for (int i = 0; i < tile.length; i++) {
                    calcNewPosition(tile[i].getRow()+newCoords[0], tile[i].getCol()+newCoords[1], i);
                }
                clearCurrPosition();
                for (int i = 0; i < tile.length; i++) {
                    board.checkEndGame(tile[i].getRow(), tile[i].getCol());
                }
                board.checkBottomFull();
                if (isCollision()) board.createNewPiece();
                move();
            }
        }
    }

    private boolean movePieceValid() {
        for (int i = 0; i < tile.length; i++) {
            if(!tile[i].checkNewLocation(newRowPosition[i], newColPosition[i]))
                return false;
        }
        return true;
    }

    private boolean validRotation(int[] rotatedCoordinates) {
        int counterRow = 0, counterCol = 1;
        for (int i = 0; i < tile.length; i++) {
            if(!tile[i].checkNewLocation(rotatedCoordinates[counterRow], rotatedCoordinates[counterCol]))
                return false;
            counterRow +=2;
            counterCol +=2;
        }
        return true;
    }

    private void move()  {
        if (movePieceValid()) {
            for (int i = 0; i < tile.length; i++) {
                tile[i].setLocation(newRowPosition[i], newColPosition[i]);
            }
        } else {
            for (int i = 0; i < tile.length; i++) {
                tile[i].setLocation(currRowPosition[i], currColPosition[i]);
            }
        }
    }

    private void rotate(int[] rotatedCoordinates, String rotation) {
        int counterRow = 0, counterCol = 1;
        if (validRotation(rotatedCoordinates)) {
            for (int i = 0; i < tile.length; i++) {
                tile[i].setLocation(rotatedCoordinates[counterRow], rotatedCoordinates[counterCol]);
                counterRow+=2;
                counterCol+=2;
            }
            //else, if not valid move set the original location
        } else {
            for (int i = 0; i < tile.length; i++) {
                tile[i].setLocation(currRowPosition[i], currColPosition[i]);
            }
        }
    }

    private boolean isCollision()   {
        for (int i = 0; i < tile.length; i++) {
            if(tile[i].collision(newRowPosition[i], newColPosition[i])) {
                return true;
            }
        }
        return false;
    }

    //calc curr coordinates, send them to getRotation... which will create new piece based on coords
    private int[] calcRotation(String direction) {
        for (int i = 0; i < tile.length; i++) {
            currRowPosition[i] = tile[i].getRow();
            currColPosition[i] = tile[i].getCol();
            System.out.println("Current position: (" + currRowPosition[i] + "," + currColPosition[i]+")");
        }
        return getRotation(currRowPosition, currColPosition, direction);
    }

    private int[] getRotation (int coordinatesX[], int coordinatesY[], String direction) {

        int[] rotationDirection;

        int[] coordinates = new int[8];
        int[] origin = new int[2];
        int[] newCoordinates = new int[8];
        int[] resultCoordinates = new int[8];
        int[] finalCoordinates = new int[8];

        int vectorMatrix[][] = new int[2][4];

        //set either R(90) or R(-90) rotation matrix values:
        if (direction.equals("right")) {
            rotationDirection = new int[] {0, -1, 1, 0};
        }
        else {
            rotationDirection = new int[] {0, 1, -1, 0};
        }

        int counterRow = 0, counterCol = 1, x = 0;
        while (counterCol < coordinates.length) {
            //add arrays coordinatesX and coordinatesY into a single array: coordinates
            coordinates[counterRow] = coordinatesX[x];
            coordinates[counterCol] = coordinatesY[x];
            counterRow+=2;
            counterCol+=2;
            x++;
        }

        //set origin so it rotates around center...
        if (shape.equals("RevZ")) {
            origin[0] = coordinates[6];
            origin[1] = coordinates[7];
        }
        else if (shape.equals("T")) {
            origin[0] = coordinates[4];
            origin[1] = coordinates[5];
        }
        else {
            origin[0] = coordinates[2];
            origin[1] = coordinates[3];
        }

        //subtract origin from vectors
        System.out.println();
        counterRow = 0;
        counterCol = 1;
        while (counterCol < newCoordinates.length) {
            newCoordinates[counterRow] = coordinates[counterRow] - origin[0];
            newCoordinates[counterCol] = coordinates[counterCol] - origin[1];
            System.out.println("Translated coordinates: (" + newCoordinates[counterRow] + ", " + newCoordinates[counterCol] + ")");
            counterRow+=2;
            counterCol+=2;
        }
        System.out.println();
        System.out.println("vector matrix:");

        //fill up vectorMatrix with coordinates
        int k = 0;
        for (int col = 0; col < 4; col++) {
            for (int row = 0; row < 2; row++) {
                vectorMatrix[row][col] = newCoordinates[k++];
            }
        }

        //print vectorMatrix:
        for (int i = 0; i < vectorMatrix.length; i++) {
            System.out.print("[");
            for (int j = 0; j < vectorMatrix[i].length; j++) {
                System.out.print(vectorMatrix[i][j]);
            }
            System.out.println("]");
        }

        int rotationMatrix[][] = new int[2][2];

        //fill up rotationMatrix
        System.out.println();
        System.out.println("multiplicative matrix:");
        k = 0;
        for (int row = 0; row < 2; row++) {
            System.out.print("[");
            for (int col = 0; col < 2; col++) {
                rotationMatrix[row][col] = rotationDirection[k++];
                System.out.print(rotationMatrix[row][col]);
            }
            System.out.println("]");
        }

        //perform matrix multiplication
        int[][] result = multiplyMatrices(rotationMatrix, vectorMatrix);

        //print resulting matrix
        System.out.println();
        System.out.println("result matrix:");
        for (int i = 0; i < result.length; i++) {
            System.out.print("[");
            for (int j = 0; j < result[i].length; j++) {
                System.out.print(result[i][j]);
            }
            System.out.println("]");
        }

        //load new matrix coordinates back into array
        k = 0;
        for (int col = 0; col < 4; col++) {
            for (int row = 0; row < 2; row++) {
                resultCoordinates[k] = result[row][col];
                k++;
            }
        }

        System.out.println();
        System.out.println("result coordinates:");
        counterRow = 0;
        counterCol = 1;

        while (counterCol < resultCoordinates.length) {
            finalCoordinates[counterRow] = resultCoordinates[counterRow] + origin[0];
            finalCoordinates[counterCol] = resultCoordinates[counterCol] + origin[1];
            System.out.print("("+finalCoordinates[counterRow] + ", " + finalCoordinates[counterCol]+")");

            counterRow+=2;
            counterCol+=2;
        }

        return finalCoordinates;
    }

    private int[][] multiplyMatrices(int rotationMatrix[][], int vectorMatrix[][]) {
        int mA = rotationMatrix.length;
        int nA = rotationMatrix[0].length;
        int mB = vectorMatrix.length;
        int nB = vectorMatrix[0].length;
        if (nA != mB) throw new RuntimeException("Illegal matrix dimensions.");
        int[][] C = new int[mA][nB];
        for (int i = 0; i < mA; i++) {
            for (int j = 0; j < nB; j++) {
                for (int k = 0; k < nA; k++) {
                    C[i][j] += (rotationMatrix[i][k] * vectorMatrix[k][j]);
                }
            }
        }
        return C;
    }

    private int[] getShape(String shape) {
        if (shape.equals("L")) {
            if (nextPiece)
                pieceCoordinates = new int[] {0, 1, 0, 2, 1, 2, 2, 2};
            else
                pieceCoordinates = new int[] {0, 4, 0, 5, 1, 5, 2, 5};
        }
        else if (shape.equals("O")) {
            if (nextPiece)
                pieceCoordinates = new int[] {0, 1, 1, 1, 0, 2, 1, 2};
            else
                pieceCoordinates = new int[] {0, 4, 1, 4, 0, 5, 1, 5};
        }
        else if (shape.equals("Z")) {
            if (nextPiece)
                pieceCoordinates = new int[] {0, 1, 1, 1, 1, 2, 2, 2};
            else
                pieceCoordinates = new int[] {0, 4, 1, 4, 1, 5, 2, 5};
        }
        else if (shape.equals("RevZ")) {
            if (nextPiece)
                pieceCoordinates = new int[] {1, 1, 2, 1, 0, 2, 1, 2};
            else
                pieceCoordinates = new int[] {1, 4, 2, 4, 0, 5, 1, 5};
        }
        else if (shape.equals("Bar")) {
            if (nextPiece)
                pieceCoordinates = new int[] {0, 1, 1, 1, 2, 1, 3, 1};
            else
                pieceCoordinates = new int[] {0, 3, 0, 4, 0, 5, 0, 6};
        }
        else if (shape.equals("T")) {
            if (nextPiece)
                pieceCoordinates = new int[] {1, 1, 0, 2, 1, 2, 2, 2};
            else
                pieceCoordinates = new int[] {1, 4, 0, 5, 1, 5, 2, 5};
        }
        else if (shape.equals("RevL")) {
            if (nextPiece)
                pieceCoordinates = new int[] {0, 2, 1, 2, 2, 2, 2, 1};
            else
                pieceCoordinates = new int[] {0, 5, 1, 5, 2, 5, 2, 4};
        }
        return pieceCoordinates;
    }
}