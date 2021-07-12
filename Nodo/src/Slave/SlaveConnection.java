package Slave;

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
import java.util.Enumeration;
import org.jdom.Element;
import org.jdom.JDOMException;

public class SlaveConnection extends Thread {

    private static SlaveConnection INSTANCE;
    private DatagramSocket socket;
    private InetAddress address;
    public String ipServer;
    public String slaveId;

    byte[] buffer = new byte[4096];

    private SlaveConnection() throws UnknownHostException, SocketException, IOException {
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
        this.enviar("", "GET_PORT", Variables.MASTERPORTNUMBER);
    }//SlavaeConnection

    public static SlaveConnection getInstance() throws UnknownHostException, SocketException, IOException {
        if (INSTANCE == null) {
            INSTANCE = new SlaveConnection();
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
                        this.enviar("", "READY", mensaje.getPort());
                        break;
                    case "READY":
                        System.out.println("Puerto asignado: " + Variables.SLAVEPORTNUMBER
                                + "\nDisco asignado: " + Variables.DISKID);
                        break;

                    case "PARTE":
                        archivoData = new ArchivoData(element.getChild("nombre").getValue());
                        archivoData.escribirEnArchivo(new Archivo(
                                element.getChild("nombre").getValue(),
                                element.getChild("parte").getValue(),
                                element.getChild("msg").getValue())
                        );
                        break;

                    case "METADATA":
                        Metadata metadata = new Metadata(
                                element.getChild("nombre").getValue(),
                                element.getChild("autor").getValue(),
                                element.getChild("fecha").getValue(),
                                element.getChild("formato").getValue()
                        );
                        metadataData = MetadataData.getInstance();
                        metadataData.escribirEnMetadata(metadata);
                        break;

                    case "OBTENER_ARCHIVO":
                        archivoData = new ArchivoData(element.getChild("nombre").getValue());
                        archivoData.obtenerArchivo(this);
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

    public void enviar(String msg, String accion, int portnumber) throws IOException {

        Element ePacket = new Element("packet");

        Element eMsg = new Element("msg");
        eMsg.addContent(msg);

        ePacket.addContent(eMsg);

        buffer = Conversiones.anadirAccion(ePacket, accion).getBytes();

        DatagramPacket mensaje = new DatagramPacket(buffer, buffer.length, this.address, portnumber);
        this.socket.send(mensaje);
    }//enviar

    public void enviarParte(String nombre, String id, String encoded) throws IOException {
        Element ePacket = new Element("p");

        Element eDiskId = new Element("d");
        eDiskId.addContent(Variables.DISKID+"");

        Element ePartId = new Element("pi");
        ePartId.addContent(id+"");
        
        Element eEncoded = new Element("m");
        eEncoded.addContent(encoded);

        Element eNombre = new Element("n");
        eNombre.addContent(nombre);
        
        ePacket.addContent(eDiskId);
        ePacket.addContent(ePartId);
        ePacket.addContent(eEncoded);        
        ePacket.addContent(eNombre);
        
        System.out.println("Impresión: "+ Conversiones.anadirAccion(ePacket, "PARTE").getBytes().length);
        
        buffer = Conversiones.anadirAccion(ePacket, "PARTE").getBytes();

        DatagramPacket mensaje = new DatagramPacket(
                buffer,
                buffer.length,
                this.address,
                Variables.MASTERPORTNUMBER
        );
        this.socket.send(mensaje);
    }//enviarParte

}//end class
