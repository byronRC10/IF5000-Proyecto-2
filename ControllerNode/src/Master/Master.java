package Master;

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

    byte[] buffer = new byte[4096];
    private Master() throws UnknownHostException, SocketException, IOException {
        this.address = InetAddress.getByName("localhost");
        this.socket = new DatagramSocket(Variables.PORTNUMBER);
        this.ports = new ArrayList<>();
        this.ports.add(Variables.PORTNUMBER);
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
        //byte[] buffer = new byte[4096];
        DatagramPacket mensaje = new DatagramPacket(buffer, buffer.length);
        boolean entrar = true;
        while (true) {
            try {
                this.socket.receive(mensaje);
                String msg = new String(mensaje.getData(), 0, mensaje.getLength());
                Element element = Conversiones.stringToXML(msg.trim());
                String accion = element.getChild("accion").getValue();
                System.out.println("Mensaje: " + accion);

                switch (accion) {
                    case "GET_PORT":
                        this.enviarMensaje((this.ports.get(this.ports.size() - 1) + 1) + "", "SET_PORT", mensaje.getPort());
                        break;
                    case "READY":
                        this.ports.add(mensaje.getPort());
                        this.enviarMensaje("", "READY", mensaje.getPort());
                        
                        break;
                    default:
                        break;
                }//switch
                
                if(this.ports.size() == 6 && entrar){
                    this.enviarArchivo();
                    entrar = false;
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            } catch (JDOMException ex) {
                ex.printStackTrace();
            }//try-catch
        }//while
    }//run

    public boolean enviarMensaje(String msg, String accion, int portnumber) throws IOException {
        //byte[] buffer = new byte[4096];

        Element ePacket = new Element("packet");

        Element eMsg = new Element("msg");
        eMsg.addContent(msg);

        ePacket.addContent(eMsg);

        buffer = Conversiones.anadirAccion(ePacket, accion).getBytes();

        DatagramPacket mensaje = new DatagramPacket(buffer, buffer.length, this.address, portnumber);
        this.socket.send(mensaje);
        return true;
    }//enviar

    public void enviarArchivo() throws IOException {
        //byte[] buffer = new byte[4096];
        File documento = new File("../Boleta-Horas-Estudiante-Asistente.pdf");

        FileInputStream fileInputStreamReader = new FileInputStream(documento);
        byte[] bytes = new byte[(int) documento.length()];
        fileInputStreamReader.read(bytes);
        String encoded = Base64.getEncoder().encodeToString(bytes);
        
        int slavesLength = this.ports.size()-1;
        int partsLength = encoded.length()/slavesLength;
        
        System.out.println("PartsLength: "+partsLength);
        
        char [][] partes = new char[slavesLength][partsLength+1];
        
        for (int i = 0; i < slavesLength-1; i++) {
            encoded.getChars(partsLength*i, partsLength*(i+1), partes[i], 0);
        }
        encoded.getChars(partsLength*(slavesLength-1), encoded.length(), partes[slavesLength-1], 0);
        
        for (int i = 0; i < this.ports.size()-1; i++) {
            Element ePacket = new Element("packet");
        
            Element eMsg = new Element("msg");
            eMsg.addContent(new String(partes[i]).trim());
            
            Element eNombre = new Element("nombe");
            eNombre.addContent("prueba2");
            
            Element eFormato= new Element("formato");
            eFormato.addContent("pdf");

            ePacket.addContent(eMsg);
            ePacket.addContent(eNombre);
            ePacket.addContent(eFormato);

            buffer = Conversiones.anadirAccion(ePacket, "GUARDAR").getBytes();

            DatagramPacket mensaje = new DatagramPacket(buffer, buffer.length, this.address, this.ports.get(i+1));
            this.socket.send(mensaje);
        }//for
        
/*
        BufferedWriter bw = new BufferedWriter(new FileWriter(new File("prueba.txt"), true));
        bw.write(encoded);
        bw.flush();
        bw.close();
*/
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
        
        Path destinationFile = Paths.get("/", "prueba.pdf");
        Files.write(destinationFile, bytes);
        
    }//contruirArchivo

}//end class
