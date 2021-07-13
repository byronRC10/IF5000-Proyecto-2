package Data;

import Entity.Archivo;
import Slave.SlaveConnection;
import Utility.Variables;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

public class ArchivoData {

    private Document document;
    private Element root;
    private String path;
    private String nombre;

    public ArchivoData(String nombre) throws IOException, JDOMException {
        File directorio = new File("../" + Variables.PATH + Variables.DISKID);
        if (!directorio.exists()) {
            directorio.mkdirs();
        }

        this.nombre = nombre;

        this.path = "../" + Variables.PATH
                + Variables.DISKID + "/"
                + this.nombre + ".xml";

        File archivoMetadata = new File(this.path);
        if (archivoMetadata.exists()) {
            SAXBuilder saxBuilder = new SAXBuilder();
            saxBuilder.setIgnoringElementContentWhitespace(true);
            this.document = saxBuilder.build(this.path);
            this.root = this.document.getRootElement();
        } else {
            this.root = new Element("Archivo");
            this.document = new Document(this.root);
            this.guardarXML();
        } // if-else

    }//constructor

    public void escribirEnArchivo(Archivo archivo) throws IOException {
        Element eParte = new Element("Parte");
        eParte.setAttribute("Numero", archivo.getParte());

        Element eEncoded = new Element("Encoded");
        eEncoded.addContent(archivo.getEncoded());

        eParte.addContent(eEncoded);

        this.root.addContent(eParte);
        this.guardarXML();

    }//escribirEnArchivo

    public void guardarXML() throws FileNotFoundException, IOException {
        XMLOutputter xmlOutputter = new XMLOutputter();
        xmlOutputter.output(this.document, new PrintWriter(this.path));
    }// guardarXML

    public void obtenerArchivo(SlaveConnection slave) throws IOException {

        ArrayList<Archivo> archivo = new ArrayList<>();
        List elementList = this.root.getChildren();

        for (Object objetoActual : elementList) {
            Element elementoActual = (Element) objetoActual;
            Archivo archivoActual = new Archivo(
                    this.nombre,
                    elementoActual.getAttributeValue("Numero"),
                    elementoActual.getChild("Encoded").getValue()
            );
            archivo.add(archivoActual);
        }//for-each
        for (int i = 0; i < archivo.size(); i++) {
            slave.enviarParte(this.nombre, archivo.get(i).getParte(), archivo.get(i).getEncoded());
        }//for i        

    }//obtenerArchivo

    /*
        this.bw = new BufferedWriter(
                new FileWriter(
                        new File("../" + Variables.PATH
                                + Variables.DISKID + "/"
                                + nombre + "-" + parte + ".txt"
                        ),
                        false
                )
        );
        this.bw.write(encoded);
        this.bw.flush();
        this.bw.close();
     */
 /*
        this.bw = new BufferedWriter(
                new FileWriter(
                        new File("../" + Variables.PATH
                                + Variables.DISKID + "/"
                                + metadata.getNombre() + "-metadata.txt"
                        ),
                        false
                )
        );
        this.bw.write(
                metadata.getNombre()
                + "-" + metadata.getAutor()
                + "-" + metadata.getFecha()
                + "-" + metadata.getFormato()
        );
        this.bw.flush();
        this.bw.close();
     */
 /*
    public byte[] leerArchivo(String nombre) throws FileNotFoundException, IOException {
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
        return null;
    }
     */
}//end class
