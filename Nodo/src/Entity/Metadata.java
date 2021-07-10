
package Entity;

import java.util.Date;

public class Metadata {
    private String nombre, autor;
    private Date fecha;

    public Metadata(String nombre, String autor, Date fecha) {
        this.nombre = nombre;
        this.autor = autor;
        this.fecha = fecha;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }
    
    
}//end class
