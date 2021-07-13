package GUI;

import Master.Master;
import Utility.Variables;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Panel extends JPanel implements ActionListener{
    private JButton jbtnObtenerArchivo;

    public Panel() {
        
        this.jbtnObtenerArchivo = new JButton("Obtener Archivo");
        this.jbtnObtenerArchivo.setBounds(100,100,100,50);
        this.jbtnObtenerArchivo.addActionListener(this);
        this.add(this.jbtnObtenerArchivo);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        try {
            Master master = Master.getInstance();
            
            master.obtenerArchivo();
            
        } catch (SocketException ex) {
            Logger.getLogger(Panel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Panel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(Panel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}//end class
