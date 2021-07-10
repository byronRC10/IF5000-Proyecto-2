package Data;

import Entity.Metadata;
import Utility.Variables;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class DiskData {
    
    private static DiskData INSTANCE=null;
    private BufferedWriter bw;
    private BufferedReader br;

    private DiskData() throws IOException {
        File directorio = new File("../"+Variables.ABSOLUTEPATH);
        if (!directorio.exists())
            directorio.mkdirs();
    }//constructor
    
    public static DiskData getInstance() throws IOException{
        if(INSTANCE == null)
            INSTANCE = new DiskData();
        
        return INSTANCE;
    }//getInstance
    
    public boolean guardarArchivo(Metadata metadata,String contenido) throws IOException{
        this.bw = new BufferedWriter(new FileWriter(new File("../"+Variables.ABSOLUTEPATH+"/"+metadata.getNombre()+".txt"), true));
        this.bw.write(contenido);
        this.bw.flush();
        return true;
    }//guardarArchivo
    
}//end class
