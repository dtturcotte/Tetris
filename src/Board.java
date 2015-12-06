import java.awt.Graphics;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.Timer;

import java.awt.event.*; // for ActionListener and ActionEvent

@SuppressWarnings("serial")
public class Board extends JPanel implements KeyListener {
    private Tile grid[][];
    private int totalRows, totalCols, tilesize, level, totalScore, seconds, red, green, blue;
    private final int changeLevelMultiplier, finalLevel;
    private Piece newPiece;
    private String currPiece, nextPiece;
    private boolean gameLost, pause;


    public Board(int r, int c, int ts) {
        totalRows = r;
        totalCols = c;
        tilesize = ts;
        grid = new Tile[totalRows][totalCols];
        gameLost = false;
        pause = false;
        finalLevel = 6;

        red = 255;
        green = 70;
        blue = 70;

        //multiplier to determine what score the level changes, which is:
        //level * changeLevelMultiplier;
        changeLevelMultiplier = 100;

        //initialize score to 0
        totalScore = 0;

        //initialize level to 0
        level = 0;
        seconds = 0;

        //set initial next piece
        nextPiece = randomPiece();
        currPiece = randomPiece();
        newPiece = new Piece(this, currPiece, getColor(), false);

        addKeyListener(this);
        setFocusable(true);

        timer("start");


    }
    //on collision
    public void createNewPiece() {
        //current playable piece becomes what the nextPiece was
        currPiece = nextPiece;
        newPiece = new Piece(this, nextPiece, getColor(), false);
        messageTimer();

        //generate next piece
        nextPiece = randomPiece();
    }

    //initial piece
    private String randomPiece() {
        String[] Pieces = {"L", "O", "Z", "RevZ", "T", "RevL", "Bar"};
        int rand = (int) (Math.random() * Pieces.length);
        String randomPiece = Pieces[rand];

        //limit how often Bar appears to (1/7)*2:
        int barLimiter = (int) (Math.random() * 2);
        if (randomPiece.equals("Bar") && barLimiter == 1) {
            randomPiece = "Bar";
        }
        else {
            int notBar = (int) (Math.random() * Pieces.length-1);
            randomPiece = Pieces[notBar];
        }
        return randomPiece;
    }

    public Color getColor() {
        Color color;
        if (currPiece.equals("L"))
            color = new Color(17, 255, 0);
        else if(currPiece.equals("O"))
            color = new Color(117, 168, 255);
        else if(currPiece.equals("Z"))
            color = new Color(255, 187, 82);
        else if(currPiece.equals("RevZ"))
            color = new Color(206, 27, 72);
        else if(currPiece.equals("Bar"))
            color = new Color(50, 216, 219);
        else if(currPiece.equals("T"))
            color = new Color(252, 148, 240);
        else
            color = new Color(255, 255, 52);
        return color;
    }

    //go through colors of the rainbow
    //min: 70, max: 255
    public Color changeColors() {
        if (red == 255 && green == 70 && blue < 255) {
            blue++;
        }
        else if (red > 70 && green == 70 && blue == 255) {
            red--;
        }
        else if (red == 70 && green < 255 && blue == 255) {
            green++;
        }
        else if (red == 70 && green == 255 && blue > 70) {
            blue--;
        }
        else if (red < 255 && green == 255 && blue == 70) {
            red++;
        }
        else {
            green--;
        }
        return new Color(red, green, blue);
    }

    public int getWidth() {
        return totalCols * tilesize;
    }

    public int getHeight() {
        return totalRows * tilesize;
    }

    public int getTileSize() {
        return tilesize;
    }

    public void setPause(boolean pauseStatus) {
        pause = pauseStatus;
    }

    public Boolean getPause() {
        return pause == true;
    }

    public void paintComponent(Graphics g) {
        g.setColor(Color.black);
        g.fillRect(0, 0, getWidth(), getHeight());

        for(int row = 0; row < grid.length; row++) {
            for(int col = 0; col < grid[row].length; col++) {
                //if there is a non-null space, that is a Tetris piece... fill it
                if(grid[row][col] != null) {
                    g.setColor(Color.WHITE);
                    g.fillRect(col * tilesize+2, row * tilesize+2, tilesize+2, tilesize+2);

                    g.setColor(grid[row][col].getColor());
                    g.fillRect(col * tilesize, row * tilesize, tilesize, tilesize);
                }
            }
        }

        g.setColor(Color.WHITE);
        g.drawString("Next Piece: " + nextPiece, this.getWidth()/2-170, 20);
        setBorder(BorderFactory.createMatteBorder(3, 3, 3, 3, getColor()));
        g.setColor(Color.WHITE);
        g.drawString("A: rotate left        D: rotate right        < move left        > move right", this.getWidth()/2-170, this.getHeight()-20);
        g.drawString("Level: " + level, this.getWidth()/2-170, this.getHeight()-7);
        g.drawString("Score: " + totalScore, this.getWidth()/2-110, this.getHeight()-7);
        g.drawString("Press spacebar to pause...", this.getWidth()/2+20, this.getHeight()-7);


        if (pause) {
            g.setColor(Color.BLACK);
            g.fillRect(this.getWidth()/2-90, this.getHeight()-180, this.getWidth()/2+40, 85);
            g.setColor(changeColors());
            g.drawString("Paused for " + seconds + " seconds...", this.getWidth()/2-70, this.getHeight()-150);

            if (seconds >= 10 && seconds < 20) {
                g.setColor(Color.YELLOW);
                g.drawString("You've been gone a while...", this.getWidth()/2-70, this.getHeight()-130);
            }
            else if (seconds >= 20 && seconds < 40) {
                g.setColor(Color.ORANGE);
                g.drawString("Coming back soon?!", this.getWidth()/2-70, this.getHeight()-130);
            }
            else if (seconds >= 40 && seconds < 60) {
                int countDown = 60 - seconds;
                g.setColor(Color.RED);
                g.drawString("HELLO?!?!?!", this.getWidth()/2-70, this.getHeight()-130);
                g.drawString("Game closing in " + countDown + " seconds...", this.getWidth()/2-70, this.getHeight()-110);
            }
            else if (seconds >= 60) {
                System.exit(0);
            }
            repaint();
        }

        if (gameLost) {
            g.drawString("Way to go, loser...", this.getWidth()/2, this.getHeight()/2);
            messageTimer();
        }
    }

    private void timer (String status) {
        int interval;
        switch (level) {
            //each level increases drop speed by .10 seconds
            case 1: interval = 700;
                break;
            case 2: interval = 600;
                break;
            case 3: interval = 500;
                break;
            case 4: interval = 400;
                break;
            case 5: interval = 300;
                break;
            default: interval = 800;
                break;
        }

        Timer t = new Timer(interval, new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                if (pause) {
                    seconds++;
                }
                else {
                    seconds = 0;
                    newPiece.autoMove();
                }
            }
        });

        if (status.equals("stop")) {
            t.stop();
        }
        else {
            t.start();
        }
    }

    private void messageTimer()  {
        Timer t = new Timer(5000, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                gameLost = false;
            }
        });
        t.start();
    }

    //move piece on key input
    public void keyPressed(KeyEvent e) {
        newPiece.movePieceCheck(e.getKeyCode());
        repaint();
    }

    public void keyReleased(KeyEvent e) {
    }

    public void keyTyped(KeyEvent e) {
    }

    public boolean isValidCoordinate(int row, int col) {
        return row >= 0 && col >= 0 && col < totalCols && row < totalRows-1;
    }

    // returns the tile at (x, y) or null if empty
    public Tile getTileAt(int row, int col) {
//        System.out.println("getTileAt: " + row + ", " + col);
        if(isValidCoordinate(row, col))
            return grid[row][col];
        return null;
    }

    // sets the tile at (x, y) to tile
    public void setTileAt(Tile tile, int row, int col) {
//        System.out.println("setTileAt: " + row + ", " + col);
        if(isValidCoordinate(row, col))
            grid[row][col] = tile;
    }

    public boolean isOpen(int row, int col) {
        return isValidCoordinate(row, col) && (getTileAt(row, col) == null);
    }

    private void changeLevel () {
        int max = (level+1)*changeLevelMultiplier;
        if (totalScore >= max) {
            level++;
            totalScore = 0;
            for(int row = 0; row < grid.length; row++) {
                for(int col = 0; col < grid[row].length; col++) {
                    grid[row][col] = null;
                }
            }
        }
        if (level == finalLevel) {
            pause = true;
        }
    }

    private int tallyScore(int totalLines) {
        int score = 0;
        switch (totalLines) {
            case 1: score = 40 * (level + 1);
                break;
            case 2: score = 100 * (level + 1);
                break;
            case 3: score = 300 * (level + 1);
                break;
            case 4: score = 1200 * (level + 1);
                break;
            default: break;
        }
        return score;
    }

    public int[] hardDrop() {
        int[] newCoords = new int[2];
        for(int row = totalRows-1; row > 0; row--) {
            for (int col = 0; col <= totalCols-1; col++) {
                if (isOpen(row, col)) {
                    newCoords[0] = row-2;
                    newCoords[1] = col;
                    return newCoords;
                }
            }
        }
        return newCoords;
    }

    //loop through all rows starting at bottom (12 rows)
    public void checkBottomFull() {
        int lines = 0;
        for(int row = totalRows-1; row > 0; row--) {
            if (isFull(row)) {
                lines++;
                clearRow(row);
            }
        }
        totalScore += tallyScore(lines);
        //check if level needs to be changed based on current score...
        changeLevel();
        //reset lines after score has been incremented
        lines=0;
    }
    //loop through all columns in that row (10 columns)
    private boolean isFull(int row) {
        for (int col = 0; col <= totalCols-1; col++) {
            if(grid[row][col] == null) {
                return false;
            }
        }
        return true;
    }

    private void clearRow(int rowToClear) {
        for(int row = rowToClear; row > 0; row--) {
            for(int col = 0; col < grid[row].length; col++) {
                grid[row][col] = grid[row-1][col];
            }
        }
    }

    public void checkEndGame(int Rows, int Cols) {
        //if currPiece y location = 0 AND the space below is filled...
        if (Rows <= 2 && !isOpen(Rows+1, Cols)) {
            gameLost = true;
            level = 0;
            totalScore = 0;
            //reset timer
            timer("stop");
            for(int row = 0; row < grid.length; row++) {
                for(int col = 0; col < grid[row].length; col++) {
                    grid[row][col] = null;
                }
            }
        }
    }
}