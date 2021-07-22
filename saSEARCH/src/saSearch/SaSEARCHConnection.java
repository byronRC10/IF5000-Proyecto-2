package saSearch;

import Data.ArchivoData;
import Data.MetadataData;
import Entity.Archivo;
import Entity.Metadata;
import Utility.Conversiones;
import Utility.Variables;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdom.Element;
import org.jdom.JDOMException;

public class SaSEARCHConnection extends Thread {

    private static SaSEARCHConnection INSTANCE;
    private DatagramSocket socket;
    private InetAddress address;
    public String ipServer;
    public String slaveId;
    public String resultados;
    public String libro;

    byte[] buffer = new byte[60000];

    private SaSEARCHConnection() throws UnknownHostException, SocketException, IOException {
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        int i = 0;
        while (i == 0) {
            NetworkInterface iface = interfaces.nextElement();
            if (iface.isLoopback() || !iface.isUp()) {
                continue;
            }

            Enumeration<InetAddress> addresses = iface.getInetAddresses();
            InetAddress addr = addresses.nextElement();

            Variables.IPSERVER = addr.getHostAddress();
            i++;
        }//while
        this.address = InetAddress.getByName(Variables.IPSERVER);
        this.socket = new DatagramSocket(Variables.SLAVEPORTNUMBER);
        this.slaveId = "-1";
        this.start();
        this.enviarMensaje("msg", "GET_PORT", "GET_PORT");
        this.resultados= "";
        this.libro= "";
    }//SlavaeConnection

    public static SaSEARCHConnection getInstance() throws UnknownHostException, SocketException, IOException {
        if (INSTANCE == null) {
            INSTANCE = new SaSEARCHConnection();
        }

        return INSTANCE;
    }//getInstance

    @Override
    public void run() {
        try {
            DatagramPacket mensaje = new DatagramPacket(buffer, buffer.length, this.address, Variables.MASTERPORTNUMBER);
            ArchivoData archivoData;
            MetadataData metadataData;
          
            while (true) {

                this.socket.receive(mensaje);
                String msg = new String(mensaje.getData(), 0, mensaje.getLength());
                Element element = Conversiones.stringToXML(msg.trim());
                String accion = element.getChild("accion").getValue();
                System.out.println("Mensaje: " + accion);
             
                switch (accion) {
                    case "SET_PORT":
                        Variables.SLAVEPORTNUMBER = Integer.parseInt(element.getChild("msg").getValue());
                        this.socket.close();
                        this.socket = new DatagramSocket(Variables.SLAVEPORTNUMBER);
                        Variables.DISKID = Integer.parseInt(element.getChild("disk").getValue());
                        this.enviarMensaje("nonmsg", "nonmsg", "READY");
                        break;
                    case "READY":
                        System.out.println("Puerto asignado: " + Variables.SLAVEPORTNUMBER
                                + "\nDisco asignado: " + Variables.DISKID);
                        archivoData = new ArchivoData();
                        break;
                    case "RESULTADO":
                        System.out.println("RESPUESTA");
                        String resultado = element.getChild("resultado").getValue();
                        System.out.println("Mensaje2: " + resultado);
                        this.resultados= resultado;
                        break;
                     case "LIBRO":
                        System.out.println("ParteF");
                        String resultado2 = element.getChild("libro").getValue();
                        System.out.println("Mensaje2: " + resultado2);
                        this.libro= resultado2;
                        break;
                    default:
                        break;

                }//switch

            }//while
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (JDOMException ex) {
            ex.printStackTrace();
        }//try-catch
    }//run
      
    public String obtenerResultados(){
     return this.resultados;    
    }
    
    public String obtenerDatos(){
     return this.libro;    
    }
    
    public void obtenerArchivo(String nombreArchivo) throws IOException, InterruptedException, JDOMException {
         this.enviarMensaje("obtener", nombreArchivo, "OBTENER");
    }//buscarArchivo

        
    public void buscarArchivo(String nombreArchivo) throws IOException, InterruptedException, JDOMException {
         this.enviarMensaje("buscar", nombreArchivo, "BUSCAR");
    }//buscarArchivo

    public void enviarMensaje(String msgName, String msg, String accion) throws IOException {

        Element ePacket = new Element("packet");

        Element eMsg = new Element(msgName);
        eMsg.addContent(msg);

        ePacket.addContent(eMsg);

        buffer = Conversiones.anadirAccion(ePacket, accion).getBytes();

        DatagramPacket mensaje = new DatagramPacket(buffer, buffer.length, this.address, Variables.MASTERPORTNUMBER);
        this.socket.send(mensaje);
    }//enviar

    public void enviarMetadata(Metadata metadata) throws IOException {
        Element ePacket = new Element("Packet");
        
        Element eNombre = new Element("Nombre");
        eNombre.addContent(metadata.getNombre());
        
        Element eAutor = new Element("Autor");
        eAutor.addContent(metadata.getAutor());
        
        Element eFecha = new Element("Fecha");
        eFecha.addContent(metadata.getFecha());
        
        Element eFormato = new Element("Formato");
        eFormato.addContent(metadata.getFormato());

        ePacket.addContent(eNombre);
        ePacket.addContent(eAutor);
        ePacket.addContent(eFecha);
        ePacket.addContent(eFormato);
        
        buffer = Conversiones.anadirAccion(ePacket, "METADATA").getBytes();

            DatagramPacket mensaje = new DatagramPacket(
                    buffer,
                    buffer.length,
                    this.address,
                    Variables.MASTERPORTNUMBER
            );
            this.socket.send(mensaje);
    }//enviarMetadata

    public void enviarParte(ArrayList<Archivo> archivo) throws IOException, InterruptedException {
        for (int i = 0; i < archivo.size(); i++) {
            System.out.println("Parte id: "+ archivo.get(i).getParte());
            Element ePacket = new Element("Packet");

            Element eDiskId = new Element("DiskId");
            eDiskId.addContent(Variables.DISKID + "");

            Element ePartId = new Element("ParteId");
            ePartId.addContent(archivo.get(i).getParte() + "");

            Element eEncoded = new Element("Encoded");
            eEncoded.addContent(archivo.get(i).getEncoded());

            Element eNombre = new Element("Nombre");
            eNombre.addContent(archivo.get(i).getNombre());

            ePacket.addContent(eDiskId);
            ePacket.addContent(ePartId);
            ePacket.addContent(eEncoded);
            ePacket.addContent(eNombre);

            buffer = Conversiones.anadirAccion(ePacket, "PARTE").getBytes();

            DatagramPacket mensaje = new DatagramPacket(
                    buffer,
                    buffer.length,
                    this.address,
                    Variables.MASTERPORTNUMBER
            );
            this.socket.send(mensaje);
            Thread.sleep(1000);
        }//for
        
    }//enviarParte

}//end class
