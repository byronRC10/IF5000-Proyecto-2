package controllernode;

import GUI.Panel;
import Master.Master;
import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import javax.swing.JFrame;
import org.jdom.JDOMException;

public class Main {

    public static void main(String[] args) throws JDOMException {     
        try {
            Master master = Master.getInstance();
            
            JFrame frame = new JFrame();
            frame.setSize(200,200);
            frame.setLocationRelativeTo(null);
            
            Panel panel = new Panel();
            
            
            frame.add(panel);
            frame.setResizable(false);
            frame.setVisible(true);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            
        }catch (UnknownHostException ex) {
            ex.printStackTrace();
        } catch (SocketException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }//main
    
}//end class
