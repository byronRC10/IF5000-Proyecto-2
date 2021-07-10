package nodo;

import Data.DiskData;
import Slave.SlaveConnection;
import Utility.Variables;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Main {

    public static void main(String[] args) {
        try {
            
            SlaveConnection slave = SlaveConnection.getInstance();
            
            JFrame frame = new JFrame();
            frame.setSize(200, 200);
            frame.setLocationRelativeTo(null);

            JPanel panel = new JPanel(null);
            panel.setSize(200, 200);
            JLabel jlblIP = new JLabel(Variables.IPSERVER);
            jlblIP.setBounds(0, 0, 200, 200);
            panel.add(jlblIP);

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
