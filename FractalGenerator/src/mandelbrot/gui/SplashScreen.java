/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mandelbrot.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JWindow;
import javax.swing.SwingWorker;

/**
 *
 * @author Gregoire
 */
public class SplashScreen extends JWindow{
    
    public static SplashScreen startDefaultSplashScreen(){
        ImageIcon i = new ImageIcon("ressources\\splash.png");
        SplashScreen sp = new SplashScreen(i);
        return sp;
    }
    ImageIcon img;

    public SplashScreen(ImageIcon img) {
        this.img = img;
        EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                pack();
                showSplash();
            }
        });
        
       
    }
    public  void showSplash() {
        JPanel p = (JPanel)getContentPane();
         p.setBackground(Color.BLACK);
        int width = img.getIconWidth();
        int height = img.getIconHeight();
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (screen.width - width) / 2;
        int y = (screen.height - height) / 2;
        setBounds(x, y, width, height);
        JLabel l = new JLabel(img);
        p.add(l,BorderLayout.CENTER);
        setVisible(true);
        toFront();
        
       new ResourceLoader().execute();
    }
    public class ResourceLoader extends SwingWorker<Object, Object> {

        @Override
        protected Object doInBackground() throws Exception {

            // Wait a little while, maybe while loading resources
            try {
                Thread.sleep(5000);
                
            } catch (Exception e) {
            }
            return null;

        }

        @Override
        protected void done() {
            setVisible(false);
        }


    }
    
    public static void main(String[] args) throws IOException {
        
    }
}
