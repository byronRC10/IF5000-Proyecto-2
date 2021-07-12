package Data;

import Entity.Archivo;
import Entity.Metadata;
import Utility.Variables;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;

public class DiskData {

    private static DiskData INSTANCE = null;
    private String path;
    private BufferedWriter bw;
    private BufferedReader br;
    private ArrayList<Archivo> archivo;

    private DiskData() throws IOException {
        this.path = "../" + Variables.PATH + "controller";
        File directorio = new File(this.path);
        if (!directorio.exists()) {
            directorio.mkdirs();
        }
        this.archivo = new ArrayList<>();
    }//constructor

    public static DiskData getInstance() throws IOException {
        if (INSTANCE == null) {
            INSTANCE = new DiskData();
        }

        return INSTANCE;
    }//getInstance
    
    public void agregarParte(Archivo archivo){
        this.archivo.add(archivo);
    }//agregarParte
    
    public void construirArchivo() throws FileNotFoundException, IOException {
        String encoded = "";
        System.out.println("Disco -> Parte: "+ this.archivo.isEmpty());
        for (int i = 0; i < this.archivo.size(); i++) {
            System.out.println("Disco -> Parte: "+ this.archivo.get(i).getDiskId()+" -> "+this.archivo.get(i).getParte());
            encoded += this.archivo.get(i).getEncoded();
        }
        
        byte[] bytes = Base64.getDecoder().decode(encoded);

        Path destinationFile = Paths.get(this.path, "ArchivoRecuperado.pdf");
        Files.write(destinationFile, bytes);

    }//contruirArchivo
    
    public void guardarMetadata(Metadata metadata) throws IOException {
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
    }//guardarMetadata

    public ArrayList<Archivo> getArchivo() {
        return archivo;
    }

    public void setArchivo(ArrayList<Archivo> archivo) {
        this.archivo = archivo;
    }
    
    

}//end class
