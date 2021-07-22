package Data;

import Entity.Metadata;
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

public class MetadataData {
    
    public static MetadataData INSTANCE = null;
    private Document document;
    private Element root;
    private String path;

    private MetadataData() throws JDOMException, IOException {
        this.path = "../" + Variables.PATH
                + Variables.DISKID + "/metadata.xml";

        File archivoMetadata = new File(path);
        if (archivoMetadata.exists()) {
            SAXBuilder saxBuilder = new SAXBuilder();
            saxBuilder.setIgnoringElementContentWhitespace(true);
            this.document = saxBuilder.build(path);
            this.root = this.document.getRootElement();
        } else {
            this.root = new Element("Meta");
            this.document = new Document(this.root);
            this.guardarXML();
        } // if-else

    }//constructor
    
    public static MetadataData getInstance() throws JDOMException, IOException{
        if(INSTANCE == null)
            INSTANCE = new MetadataData();
        
        return INSTANCE;
    }//getInstance

    public void guardarXML() throws FileNotFoundException, IOException {
        XMLOutputter xmlOutputter = new XMLOutputter();
        xmlOutputter.output(this.document, new PrintWriter(this.path));
    }// guardarXML
    
    public void escribirEnMetadata(Metadata metadata) throws IOException{
        Element eArchivo = new Element("Archivo");
        eArchivo.setAttribute("Nombre", metadata.getNombre());
        
        Element eAutor = new Element("Autor");
        eAutor.addContent(metadata.getAutor());
        
        Element eFecha = new Element("Fecha");
        eFecha.addContent(metadata.getFecha());
        
        Element eFormato = new Element("Formato");
        eFormato.addContent(metadata.getFormato());
        
        eArchivo.addContent(eAutor);
        eArchivo.addContent(eFecha);
        eArchivo.addContent(eFormato);
        
        this.root.addContent(eArchivo);
        
        this.guardarXML();
    }//escribirEnMetadata
    
    public ArrayList<Metadata> obtenerMetadata(){
        ArrayList<Metadata> metadata = new ArrayList<>();
        List elementList = this.root.getChildren();

        for (Object objetoActual : elementList) {
            Element elementoActual = (Element) objetoActual;
            Metadata metadataActual = new Metadata(
                    elementoActual.getAttributeValue("Nombre"),
                    elementoActual.getChild("Autor").getValue(),
                    elementoActual.getChild("Fecha").getValue(),
                    elementoActual.getChild("Formato").getValue()
            );
            metadata.add(metadataActual);
        }//for-each
        return metadata;
    }//obtenerMetadata
    
    public void buscarMetadata(String nombre, SlaveConnection slave) throws IOException{
        List elementList = this.root.getChildren();

        for (Object objetoActual : elementList) {
            Element elementoActual = (Element) objetoActual;
            Metadata metadataActual = new Metadata(
                    elementoActual.getAttributeValue("Nombre"),
                    elementoActual.getChild("Autor").getValue(),
                    elementoActual.getChild("Fecha").getValue(),
                    elementoActual.getChild("Formato").getValue()
            );
            if(metadataActual.getNombre().equals(nombre)){
                slave.enviarMetadata(metadataActual);
            }//if
        }//for-each
    }//buscarMetadata
    
}//end class
