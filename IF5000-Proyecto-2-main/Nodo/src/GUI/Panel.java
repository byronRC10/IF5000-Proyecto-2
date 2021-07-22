package GUI;

import Entity.Metadata;
import Slave.SlaveConnection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import org.jdom.JDOMException;

public class Panel extends JPanel implements ActionListener{
    private JButton jbtnObtenerArchivo, jbtnBuscarArchivo;
    private JTextField jtfNombreArchivo, jtfFormato;
    private JComboBox jcbLibros;
    private JTextArea jtaLibro;
    
    public Panel() {
        this.setLayout(null);
        
        this.jtfNombreArchivo = new JTextField();
        this.jtfNombreArchivo.setBounds(50, 10, 100, 30);
        this.add(this.jtfNombreArchivo);
        
        this.jtfFormato = new JTextField("Formato");
        this.jtfFormato.setBounds(50, 35, 100, 30);
        this.add(this.jtfFormato);
        
        this.jbtnBuscarArchivo = new JButton("Buscar Archivo");
        this.jbtnBuscarArchivo.setBounds(35,100,180,30);
        this.jbtnBuscarArchivo.addActionListener(this);
        this.add(this.jbtnBuscarArchivo);
        
        this.jcbLibros= new JComboBox();
        this.jcbLibros.setBounds(35,140,180,30);
        this.add(this.jcbLibros);
        
        this.jbtnObtenerArchivo = new JButton("Obtener Archivo");
        this.jbtnObtenerArchivo.setBounds(35,180,180,30);
        this.jbtnObtenerArchivo.addActionListener(this);
        this.add(this.jbtnObtenerArchivo);
        
        this.jtaLibro=new JTextArea();  
        this.jtaLibro.setBounds(35,250, 200,200);  
        this.add(jtaLibro);  
        
      //  this.jtaLibro.append("Hola \n");
        //this.jtaLibro.append("Hola \n");
        
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        try {
          SlaveConnection slave = SlaveConnection.getInstance();
            if(this.jbtnObtenerArchivo == ae.getSource()){
              String obtener = String.valueOf(this.jcbLibros.getSelectedItem());
                slave.obtenerArchivo(obtener);
                
                Thread.sleep(1000);
                String cargar= slave.obtenerDatos();
                
               this.jtaLibro.append("Datos libro: \n");
              String[] parts = cargar.split(",");
              for(int i=0; i<parts.length; i++){
               this.jtaLibro.append(parts[i]+" \n");
              }// for
             
              String ruta= "../DISK-Controller/"+obtener+".pdf";
              VistaPDF v= new VistaPDF(ruta);
              
            }else if(this.jbtnBuscarArchivo == ae.getSource()){
              slave.buscarArchivo(this.jtfNombreArchivo.getText());
              Thread.sleep(1000);
              String cargar= slave.obtenerResultados();
  
              String[] parts = cargar.split(" ");
              for(int i=0; i<parts.length; i++){
                  this.jcbLibros.addItem(parts[i]);
              }// for
              
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
