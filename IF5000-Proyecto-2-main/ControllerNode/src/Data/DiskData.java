package Data;

import Entity.Archivo;
import Entity.Metadata;
import Utility.OrdenarArray;
import Utility.Variables;
import java.io.BufferedWriter;
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

public class DiskData {

    private static DiskData INSTANCE = null;
    private Document document;
    private Element root;
    private String path, pathMetadata;
    private BufferedWriter bw;
    private Metadata metadata;
    private ArrayList<Archivo> archivo;

    private DiskData() throws IOException, JDOMException {
        this.path = "../" + Variables.PATH + "Controller";
        File directorio = new File(this.path);
        if (!directorio.exists()) {
            directorio.mkdirs();
        }
        this.pathMetadata = "../" + Variables.PATH
                + "Controller/MetadataNodos.xml";

        File archivoMetadata = new File(this.pathMetadata);
        if (archivoMetadata.exists()) {
            SAXBuilder saxBuilder = new SAXBuilder();
            saxBuilder.setIgnoringElementContentWhitespace(true);
            this.document = saxBuilder.build(this.pathMetadata);
            this.root = this.document.getRootElement();
        } else {
            this.root = new Element("Nodos");
            this.document = new Document(this.root);
            this.guardarXML();
        } // if-else        

        this.archivo = new ArrayList<>();
    }//constructor

    public void guardarXML() throws FileNotFoundException, IOException {
        XMLOutputter xmlOutputter = new XMLOutputter();
        xmlOutputter.output(this.document, new PrintWriter(this.pathMetadata));
    }// guardarXML

    public static DiskData getInstance() throws IOException, JDOMException {
        if (INSTANCE == null) {
            INSTANCE = new DiskData();
        }

        return INSTANCE;
    }//getInstance

    public void escribirEnMetadata(Metadata metadata) throws IOException {
        Element eArchivo = new Element("Nodos");
        eArchivo.setAttribute("Nombre", metadata.getNombre());

        Element eAutor = new Element("Autor");
        eAutor.addContent(metadata.getAutor());

        Element eFecha = new Element("Fecha");
        eFecha.addContent(metadata.getFecha());

        Element eFormato = new Element("Formato");
        eFormato.addContent(metadata.getFormato());

        Element eIndiceParidad = new Element("IndiceParidad");
        eIndiceParidad.addContent(metadata.getIndiceParidad() + "");
        
        Element eNumeroDePartes = new Element("NumeroDePartes");
        eNumeroDePartes.addContent(metadata.getNumeroDePartes() + "");

        eArchivo.addContent(eAutor);
        eArchivo.addContent(eFecha);
        eArchivo.addContent(eFormato);
        eArchivo.addContent(eIndiceParidad);
        eArchivo.addContent(eNumeroDePartes);

        this.root.addContent(eArchivo);

        this.guardarXML();
    }//escribirEnMetadata

    public Metadata obtenerMetadata(String nombreArchivo) {
        List elementList = this.root.getChildren();

        for (Object objetoActual : elementList) {
            Element elementoActual = (Element) objetoActual;
            Metadata metadataActual = new Metadata(
                    elementoActual.getAttributeValue("Nombre"),
                    elementoActual.getChild("Autor").getValue(),
                    elementoActual.getChild("Fecha").getValue(),
                    elementoActual.getChild("Formato").getValue(),
                    Integer.parseInt(elementoActual.getChild("IndiceParidad").getValue()),
                    Integer.parseInt(elementoActual.getChild("NumeroDePartes").getValue())
                    );
            if (metadataActual.getNombre().equals(nombreArchivo)) {
                return metadataActual;
            }
        }//for-each

        return null;
    }//obtenerMetadata
    
    public String obtenerPosiblesLibros(String nombreArchivo) {
        String salida="";
        
        List elementList = this.root.getChildren();

        for (Object objetoActual : elementList) {
            Element elementoActual = (Element) objetoActual;
            Metadata metadataActual = new Metadata(
                    elementoActual.getAttributeValue("Nombre"),
                    elementoActual.getChild("Autor").getValue(),
                    elementoActual.getChild("Fecha").getValue(),
                    elementoActual.getChild("Formato").getValue(),
                    Integer.parseInt(elementoActual.getChild("IndiceParidad").getValue()),
                    Integer.parseInt(elementoActual.getChild("NumeroDePartes").getValue())
                    );
           if (metadataActual.getNombre().toLowerCase().startsWith(nombreArchivo.toLowerCase())) {
               salida+=metadataActual.getNombre()+" ";
           }// if
        }//for-each
        
        return salida;
    }// obtenerPosiblesLibros
    
    public String obtenerDatosLibro(String nombreArchivo) {
        String salida="";
        
        List elementList = this.root.getChildren();

        for (Object objetoActual : elementList) {
            Element elementoActual = (Element) objetoActual;
            Metadata metadataActual = new Metadata(
                    elementoActual.getAttributeValue("Nombre"),
                    elementoActual.getChild("Autor").getValue(),
                    elementoActual.getChild("Fecha").getValue(),
                    elementoActual.getChild("Formato").getValue(),
                    Integer.parseInt(elementoActual.getChild("IndiceParidad").getValue()),
                    Integer.parseInt(elementoActual.getChild("NumeroDePartes").getValue())
                    );
            
               if (metadataActual.getNombre().equals(nombreArchivo)) { 
                 salida+="Nombre: "+metadataActual.getNombre()+",Autor: "+metadataActual.getAutor()+",Fecha: "+
                         metadataActual.getFecha()+",Formato: "+metadataActual.getFormato()+
                         ",Indice paridad: "+metadataActual.getIndiceParidad()+",Numero de partes: "+metadataActual.getNumeroDePartes();
               }// if
        }//for-each
        
        return salida;
    }// obtenerPosiblesLibros
    
    public ArrayList<Integer> obtenerFaltantes(Metadata metadata){
        ArrayList<Integer> indices = new ArrayList<>();
        
        Collections.sort(this.archivo, new OrdenarArray());
        int cont = 0;
        for (int i = 0; i < metadata.getNumeroDePartes(); i++) {
            if(cont < this.archivo.size() 
                    && i == this.archivo.get(cont).getParte())
                cont++;
            else
                indices.add(i);
        }//for i
        
        for (int i = 0; i < indices.size(); i++) {
            System.out.println("Indices faltantes: "+indices.get(i));
        }
        
        return indices;
    }//obtenerFaltantes

    public void agregarParte(Archivo archivo) {
        this.archivo.add(archivo);
    }//agregarParte

    public void resetArchivo() {
        this.archivo.clear();
    }//resetArchivo

    public void construirArchivo() throws FileNotFoundException, IOException {
        String encoded = "";
        Collections.sort(this.archivo, new OrdenarArray());
        for (int i = 0; i < this.archivo.size(); i++) {
            System.out.println("Disco -> Parte: " + this.archivo.get(i).getDiskId() + " -> " + this.archivo.get(i).getParte());
            encoded += this.archivo.get(i).getEncoded();
        }

        byte[] bytes = Base64.getDecoder().decode(encoded);

        Path destinationFile = Paths.get(this.path, this.archivo.get(0).getNombre() + ".pdf");
        System.out.println("Destination file: "+destinationFile);
        Files.write(destinationFile, bytes);

    }//contruirArchivo

    public ArrayList<Archivo> getArchivo() {
        return archivo;
    }

    public void setArchivo(ArrayList<Archivo> archivo) {
        this.archivo = archivo;
    }

    public Metadata getMetadata() {
        return metadata;
    }

    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }

}//end class
