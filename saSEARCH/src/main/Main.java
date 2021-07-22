package main;

import Data.ArchivoData;
import GUI.Panel;
import saSearch.SaSEARCHConnection;
import Utility.Variables;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import org.icepdf.ri.common.ComponentKeyBinding;
import org.icepdf.ri.common.SwingController;
import org.icepdf.ri.common.SwingViewBuilder;

public class Main {

    public static void main(String[] args) {
        
        try {
            
            SaSEARCHConnection slave = SaSEARCHConnection.getInstance();
            
             JFrame frame = new JFrame();
            frame.setSize(500,500);
            frame.setLocationRelativeTo(null);
            
            Panel panel = new Panel();
            
            frame.add(panel);
            frame.setResizable(false);
            frame.setVisible(true);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
 
        }catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
               
    }//main

}//end class
