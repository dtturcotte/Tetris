import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

@SuppressWarnings("unused")
public class Main extends JFrame {
    private static final long serialVersionUID = 1L;
    static Main runMe;
    Board gameBoard;

    public Main() {
        JFrame f = new JFrame("Dan Turcotte's Tetris");
        gameBoard = new Board(15, 10, 35);
        f.add(gameBoard);
        f.setSize(gameBoard.getWidth()+6, gameBoard.getHeight()+30);
        f.setVisible(true);
        f.setResizable(false);
        f.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
        f.setLocation( (screensize.width - f.getWidth())/2,
                (screensize.height - f.getHeight())/2-100 );
    }

    public static void main(String[] args) {
        runMe = new Main();
    }

}