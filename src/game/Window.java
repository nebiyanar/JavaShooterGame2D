package game;

import java.awt.Dimension;
import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

public class Window {

    private JFrame frame;

    public Window(int width, int height, String title, Game game) {
        frame = new JFrame(title);
        try {
            Image icon = ImageIO.read(new File("src/game/icon.png"));
            frame.setIconImage(icon);
        } catch (IOException e) {
            System.out.println("İkon yüklenemedi!");
        }

        frame.setPreferredSize(new Dimension(width, height));
        frame.setMaximumSize(new Dimension(width, height));
        frame.setMinimumSize(new Dimension(width, height));

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.add(game); 
        game.requestFocus(); 


        
        
        frame.setVisible(true);
    }
}
