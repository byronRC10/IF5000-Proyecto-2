package Entity;

public class Archivo {
    private String nombre, parte, encoded;

    public Archivo(String nombre, String parte, String encoded) {
        this.nombre = nombre;
        this.parte = parte;
        this.encoded = encoded;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getParte() {
        return parte;
    }

    public void setParte(String parte) {
        this.parte = parte;
    }

    public String getEncoded() {
        return encoded;
    }

    public void setEncoded(String encoded) {
        this.encoded = encoded;
    }
    
}//end class
