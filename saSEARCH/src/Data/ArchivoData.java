package Data;

import Entity.Archivo;
import saSearch.SaSEARCHConnection;
import Utility.Variables;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
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

    public ArchivoData() throws IOException, JDOMException {
        File directorio = new File("../" + Variables.PATH + Variables.DISKID);
        if (!directorio.exists()) {
            directorio.mkdirs();
        }
    }//archivoData

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

    public ArrayList<Archivo> obtenerArchivo() throws IOException {

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
        return archivo;
    }//obtenerArchivo

    public void obtenerMetadata(SaSEARCHConnection slave, String nombreArchivo) {

    }//obtenerMetadata

    public ArrayList<Archivo> obtenerPartes(Element element) throws IOException {
        ArrayList<Integer> indices = new ArrayList<>();
        List elementList = element.getChild("Archivo").getChildren("Indice");

        for (Object objetoActual : elementList) {
            Element elementoActual = (Element) objetoActual;
            indices.add(Integer.parseInt(elementoActual.getValue()));
        }//for i
        ArrayList<Archivo> archivo = this.obtenerArchivo();
        ArrayList<Archivo> partes = new ArrayList<>();

        for (int i = 0; i < indices.size(); i++) {
            partes.add(new Archivo(this.nombre, indices.get(i)
                    + "", archivo.get(indices.get(i)).getEncoded()));
        }
        return partes;
    }//obtenerPartes
   

}//end class
