package Master;

import Data.DiskData;
import Entity.Archivo;
import Entity.Metadata;
import Utility.Conversiones;
import Utility.Variables;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Base64;
import org.jdom.Element;
import org.jdom.JDOMException;

public class Master extends Thread {

    private static Master INSTANCE;
    private DatagramSocket socket;
    private InetAddress address;
    private ArrayList<Integer> ports;
    private int indiceParidad, ubicacionParidad;

    byte[] buffer = new byte[60000];

    private Master() throws UnknownHostException, SocketException, IOException, JDOMException {
        this.address = InetAddress.getByName("localhost");
        this.socket = new DatagramSocket(Variables.PORTNUMBER);
        this.ports = new ArrayList<>();
        DiskData diskData = DiskData.getInstance();
        this.indiceParidad = 0;
        this.ubicacionParidad = 0;
        this.start();
    }//SlavaeConnection

    public static Master getInstance() throws UnknownHostException, SocketException, IOException, JDOMException {
        if (INSTANCE == null) {
            INSTANCE = new Master();
        }

        return INSTANCE;
    }//getInstance

    @Override
    public void run() {
        DatagramPacket mensaje = new DatagramPacket(buffer, buffer.length);
        boolean entrar = true;
        try {
            DiskData diskData = DiskData.getInstance();
            while (true) {

                this.socket.receive(mensaje);
                String msg = new String(mensaje.getData(), 0, mensaje.getLength());
                Element element = Conversiones.stringToXML(msg.trim());
                String accion = element.getChild("accion").getValue();
                System.out.println("Mensaje: " + accion);

                switch (accion) {
                    case "GET_PORT":
                        if(!this.ports.isEmpty())
                            this.enviarPuerto((this.ports.get(this.ports.size() - 1) + 1) + "", "SET_PORT", mensaje.getPort());
                        else
                            this.enviarPuerto((Variables.PORTNUMBER + 1) + "", "SET_PORT", mensaje.getPort());
                        break;
                    case "READY":
                        this.ports.add(mensaje.getPort());
                        this.enviarMensaje("nonmsg", "nonmsg", "READY", mensaje.getPort());

                        break;

                    case "PARTE":

                        diskData.agregarParte(
                                new Archivo(
                                        Integer.parseInt(element.getChild("DiskId").getValue()),
                                        Integer.parseInt(element.getChild("ParteId").getValue()),
                                        element.getChild("Nombre").getValue(),
                                        element.getChild("Encoded").getValue()
                                )
                        );
                        break;

                    case "METADATA":
                        Metadata metadata = new Metadata(
                                element.getChild("Nombre").getValue(),
                                element.getChild("Autor").getValue(),
                                element.getChild("Fecha").getValue(),
                                element.getChild("Formato").getValue()
                        );
                        diskData = DiskData.getInstance();
                        diskData.setMetadata(metadata);
                        
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

    public void enviarPuerto(String msg, String accion, int portnumber) throws IOException {
        Element ePacket = new Element("packet");

        Element eMsg = new Element("msg");
        eMsg.addContent(msg);

        Element eDisk = new Element("disk");
        eDisk.addContent((this.ports.size()+1) + "");

        ePacket.addContent(eMsg);
        ePacket.addContent(eDisk);

        buffer = Conversiones.anadirAccion(ePacket, accion).getBytes();

        DatagramPacket mensaje = new DatagramPacket(buffer, buffer.length, this.address, portnumber);
        this.socket.send(mensaje);
    }//enviarPuerto

    public void enviarMensaje(String msgName, String msg, String accion, int portNumber) throws IOException {
        Element ePacket = new Element("packet");

        Element eMsg = new Element(msgName);
        eMsg.addContent(msg);

        ePacket.addContent(eMsg);

        buffer = Conversiones.anadirAccion(ePacket, accion).getBytes();

        DatagramPacket mensaje = new DatagramPacket(buffer, buffer.length, this.address, portNumber);
        this.socket.send(mensaje);
    }//enviarMensaje

    public void enviarArchivo(Metadata metadata) throws IOException, InterruptedException, JDOMException {
        File documento = new File("../"+metadata.getNombre()+"."+metadata.getFormato());

        //Se lee el archivo
        FileInputStream fileInputStreamReader = new FileInputStream(documento);
        
        //Se obtiene el tamaño y se codifica
        byte[] bytes = new byte[(int) documento.length()];
        fileInputStreamReader.read(bytes);
        String encoded = Base64.getEncoder().encodeToString(bytes);
        
        //Tamaño de las partes
        int partsLength = encoded.length()/this.ports.size();
        
        //Si sobrepasa el tamaño del buffer entonces se sigue diviendo hasta que quepa
        while(partsLength > 59000)
            partsLength = partsLength / this.ports.size();
        
        //Número de partes en que se va a divir el archivo codificado
        int numParts = (int) Math.ceil((encoded.length() * 1d) / partsLength);
        
        //Se pasa el indice del nodo que va a tener la paridad para tenerlo
        //en los datos del controller
        DiskData diskData = DiskData.getInstance();
        metadata.setIndiceParidad(this.indiceParidad);
        metadata.setNumeroDePartes(numParts);
        diskData.escribirEnMetadata(metadata);
        
        //Matris que va a contener el las partes separadas
        char[][] partes = new char[numParts][partsLength + 1];

        //Se divide el archivo codificado en partes
        for (int i = 0; i < numParts - 1; i++) {
            encoded.getChars(partsLength * i, partsLength * (i + 1), partes[i], 0);
        }//for
        
        //Para evitar que falte información, la última parte empieza desde
        //el fin de la anterior y termina en el largo del archivo completo
        encoded.getChars(partsLength * (numParts - 1), encoded.length(), partes[numParts - 1], 0);

        //Se envian las partes a los nodos
        for (int i = 0; i < numParts; i++) {

            Element ePacket = new Element("packet");

            Element eMsg = new Element("msg");
            eMsg.addContent(new String(partes[i]).trim());

            Element eNombre = new Element("nombre");
            eNombre.addContent(metadata.getNombre());

            Element eParte = new Element("parte");
            eParte.addContent(i + "");

            ePacket.addContent(eMsg);
            ePacket.addContent(eNombre);
            ePacket.addContent(eParte);

            buffer = Conversiones.anadirAccion(ePacket, "PARTE").getBytes();
            DatagramPacket mensaje;
            if(i%this.ports.size() == this.indiceParidad)
                mensaje = new DatagramPacket(buffer, buffer.length, this.address, this.ports.get((i+1)%this.ports.size()));
            else
                mensaje = new DatagramPacket(buffer, buffer.length, this.address, this.ports.get(i%this.ports.size()));
            this.socket.send(mensaje);
            
            mensaje = new DatagramPacket(buffer, buffer.length, this.address, this.ports.get(this.indiceParidad));
            this.socket.send(mensaje);
            
            Thread.sleep(100);
        }//for i
        
        this.indiceParidad = (this.indiceParidad+1)%this.ports.size();
        
        //Se envia la metadata a los nodos
        for (int i = 0; i < this.ports.size(); i++) {
            Element ePacket = new Element("packet");

            Element eNombre = new Element("nombre");
            eNombre.addContent(metadata.getNombre());

            Element eAutor = new Element("autor");
            eAutor.addContent(metadata.getAutor());

            Element eFecha = new Element("fecha");
            eFecha.addContent(metadata.getFecha());

            Element eFormato = new Element("formato");
            eFormato.addContent(metadata.getFormato());

            ePacket.addContent(eNombre);
            ePacket.addContent(eAutor);
            ePacket.addContent(eFecha);
            ePacket.addContent(eFormato);

            buffer = Conversiones.anadirAccion(ePacket, "METADATA").getBytes();

            DatagramPacket mensaje = new DatagramPacket(buffer, buffer.length, this.address, this.ports.get(i));
            this.socket.send(mensaje);
        }//for i
    }//enviarArchivo

    public void obtenerArchivo(String nombreArchivo) throws IOException, InterruptedException, JDOMException {
        DiskData diskData = DiskData.getInstance();
        diskData.resetArchivo();
        diskData.setMetadata(null);
        
        Metadata meta = diskData.obtenerMetadata(nombreArchivo);
        
        
        for (int i = 0; i < this.ports.size(); i++) {
            if(i!=meta.getIndiceParidad())
                this.enviarMensaje("Nombre", nombreArchivo, "OBTENER_ARCHIVO", this.ports.get(i));
        }//for i
        
        
        this.enviarMensaje("Nombre", nombreArchivo, "OBTENER_METADATA", this.ports.get(0));
        
        int espera = 0;//si la espera alcanza 10 entonces se rompe el ciclo
        while(diskData.getArchivo().size() != meta.getNumeroDePartes() && espera <10){
            Thread.sleep(1000);
            espera++;
        }
        
        if(espera == 10){
            ArrayList<Integer> indices = diskData.obtenerFaltantes(meta);
            this.obtenerPartes(meta, indices);
        }
        
        espera = 0;//si la espera alcanza 10 entonces se rompe el ciclo
        while(diskData.getArchivo().size() != meta.getNumeroDePartes() && espera <10){
            Thread.sleep(1000);
            espera++;
        }
        
        diskData.construirArchivo();
    }//obtenerArchivo
    

    public void obtenerPartes(Metadata meta, ArrayList<Integer> indices) throws IOException{
        Element ePacket = new Element("packet");

        Element eMsg = new Element("Archivo");
        eMsg.setAttribute("Nombre", meta.getNombre());
        
        
        for (int i = 0; i < indices.size(); i++) {
            Element eIndice = new Element("Indice");
            eIndice.addContent(indices.get(i)+"");
            
            eMsg.addContent(eIndice);
        }
        ePacket.addContent(eMsg);

        buffer = Conversiones.anadirAccion(ePacket, "OBTENER_PARTES").getBytes();
        System.out.println("Paridad en el disco: "+meta.getIndiceParidad());
        DatagramPacket mensaje = new DatagramPacket(buffer, buffer.length, this.address, this.ports.get(meta.getIndiceParidad()));
        this.socket.send(mensaje);
    }//obtenerPartes

}//end class
