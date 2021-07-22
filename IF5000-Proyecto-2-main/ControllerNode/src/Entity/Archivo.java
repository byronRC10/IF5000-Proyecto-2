package Entity;

public class Archivo {
    private int diskId, parte;
    private String nombre, encoded;

    public Archivo(int diskId, int parte, String nombre, String encoded) {
        this.diskId = diskId;
        this.parte = parte;
        this.nombre = nombre;
        this.encoded = encoded;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEncoded() {
        return encoded;
    }

    public void setEncoded(String encoded) {
        this.encoded = encoded;
    }

    public int getDiskId() {
        return diskId;
    }

    public void setDiskId(int diskId) {
        this.diskId = diskId;
    }

    public int getParte() {
        return parte;
    }

    public void setParte(int parte) {
        this.parte = parte;
    }  
    
    
}//end class
