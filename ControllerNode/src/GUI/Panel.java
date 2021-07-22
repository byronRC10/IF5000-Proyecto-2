package GUI;

import Entity.Metadata;
import Master.Master;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.jdom.JDOMException;

public class Panel extends JPanel implements ActionListener{
    private JButton jbtnObtenerArchivo, jbtnEnviarArchivo;
    private JTextField jtfNombreArchivo, jtfFormato;

    public Panel() {
        this.setLayout(null);
        
        this.jtfNombreArchivo = new JTextField();
        this.jtfNombreArchivo.setBounds(50, 10, 100, 30);
        this.add(this.jtfNombreArchivo);
        
        this.jtfFormato = new JTextField("Formato");
        this.jtfFormato.setBounds(50, 35, 100, 30);
        this.add(this.jtfFormato);
        
        this.jbtnObtenerArchivo = new JButton("Obtener Archivo");
        this.jbtnObtenerArchivo.setBounds(35,100,130,30);
        this.jbtnObtenerArchivo.addActionListener(this);
        this.add(this.jbtnObtenerArchivo);
        
        this.jbtnEnviarArchivo = new JButton("Eviar Archivo");
        this.jbtnEnviarArchivo.setBounds(35,140,130,30);
        this.jbtnEnviarArchivo.addActionListener(this);
        this.add(this.jbtnEnviarArchivo);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        try {
            Master master = Master.getInstance();
            if(this.jbtnObtenerArchivo == ae.getSource()){
                master.obtenerArchivo(this.jtfNombreArchivo.getText());
            }else{
                Metadata metadata = new Metadata(this.jtfNombreArchivo.getText()
                        , "Fabricio", "02/05/2021", this.jtfFormato.getText());
                master.enviarArchivo(metadata);
          
            }
            
       
        } catch (SocketException ex) {
            Logger.getLogger(Panel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Panel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(Panel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JDOMException ex) {
            Logger.getLogger(Panel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}//end class
