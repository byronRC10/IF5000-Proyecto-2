package Master;

import Data.DiskData;
import Entity.Archivo;
import Entity.Metadata;
import Utility.Conversiones;
import Utility.Variables;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import org.jdom.Element;
import org.jdom.JDOMException;

public class Master extends Thread {

    private static Master INSTANCE;
    private DatagramSocket socket;
    private InetAddress address;
    private ArrayList<Integer> ports;

    byte[] buffer = new byte[60000];

    private Master() throws UnknownHostException, SocketException, IOException {
        this.address = InetAddress.getByName("localhost");
        this.socket = new DatagramSocket(Variables.PORTNUMBER);
        this.ports = new ArrayList<>();
        //this.ports.add(Variables.PORTNUMBER);
        this.start();
    }//SlavaeConnection

    public static Master getInstance() throws UnknownHostException, SocketException, IOException {
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
                                element.getChild("nombre").getValue(),
                                element.getChild("autor").getValue(),
                                element.getChild("fecha").getValue(),
                                element.getChild("formato").getValue()
                        );
                        diskData = DiskData.getInstance();
                        diskData.guardarMetadata(metadata);
                        diskData.construirArchivo();
                        break;
                    default:
                        break;
                }//switch

                if (this.ports.size() == 3 && entrar) {
                    this.enviarArchivo();
                    entrar = false;
                }

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

    public void enviarArchivo() throws IOException {
        File documento = new File("../ejemplo.pdf");

        FileInputStream fileInputStreamReader = new FileInputStream(documento);
        byte[] bytes = new byte[(int) documento.length()];
        fileInputStreamReader.read(bytes);
        String encoded = Base64.getEncoder().encodeToString(bytes);

        int numParts = (int) Math.ceil((encoded.length() * 1d) / 4000d);
        int partsLength = 4001;
        char[][] partes = new char[numParts][partsLength + 1];

        for (int i = 0; i < numParts - 1; i++) {
            encoded.getChars(partsLength * i, partsLength * (i + 1), partes[i], 0);
        }//for
        //Para evitar que falte información, la última parte empieza desde
        //el fin de la anterior y termina en el largo del archivo completo
        encoded.getChars(partsLength * (numParts - 1), encoded.length(), partes[numParts - 1], 0);

        for (int i = 0; i < numParts; i++) {

            Element ePacket = new Element("packet");

            Element eMsg = new Element("msg");
            eMsg.addContent(new String(partes[i]).trim());

            Element eNombre = new Element("nombre");
            eNombre.addContent("prueba");

            Element eParte = new Element("parte");
            eParte.addContent(i + "");

            ePacket.addContent(eMsg);
            ePacket.addContent(eNombre);
            ePacket.addContent(eParte);

            buffer = Conversiones.anadirAccion(ePacket, "PARTE").getBytes();

            DatagramPacket mensaje = new DatagramPacket(buffer, buffer.length, this.address, this.ports.get(i%this.ports.size()));
            this.socket.send(mensaje);
        }//for i
        
        for (int i = 0; i < this.ports.size(); i++) {
            Element ePacket = new Element("packet");

            Element eNombre = new Element("nombre");
            eNombre.addContent("prueba");

            Element eAutor = new Element("autor");
            eAutor.addContent("fabricio");

            Element eFecha = new Element("fecha");
            eFecha.addContent("10/07/2021");

            Element eFormato = new Element("formato");
            eFormato.addContent("pdf");

            ePacket.addContent(eNombre);
            ePacket.addContent(eAutor);
            ePacket.addContent(eFecha);
            ePacket.addContent(eFormato);

            buffer = Conversiones.anadirAccion(ePacket, "METADATA").getBytes();

            DatagramPacket mensaje = new DatagramPacket(buffer, buffer.length, this.address, this.ports.get(i));
            this.socket.send(mensaje);
        }//for i
    }//enviarArchivo

    public void construirArchivo() throws FileNotFoundException, IOException {
        BufferedReader br = new BufferedReader(new FileReader(new File("prueba.txt")));
        String linea = br.readLine();
        String encoded = "";
        while (linea != null) {
            encoded += linea;
            linea = br.readLine();
        }//while

        byte[] bytes = Base64.getDecoder().decode(encoded);

        Path destinationFile = Paths.get("", "prueba.pdf");
        Files.write(destinationFile, bytes);

    }//contruirArchivo

    public void obtenerArchivo() throws IOException, InterruptedException {
        for (int i = 0; i < this.ports.size(); i++) {
            this.enviarMensaje("nombre", "prueba", "OBTENER_ARCHIVO", this.ports.get(i));
            Thread.sleep(500);
        }
        DiskData diskData = DiskData.getInstance();
        Thread.sleep(1000);
        diskData.construirArchivo();
    }//obtenerArchivo

}//end class
