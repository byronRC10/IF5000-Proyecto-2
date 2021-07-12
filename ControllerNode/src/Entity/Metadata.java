
package Entity;


public class Metadata {
    private String nombre, autor, fecha, formato, parte;

    public Metadata(String nombre, String autor, String fecha, String formato, String parte) {
        this.nombre = nombre;
        this.autor = autor;
        this.fecha = fecha;
        this.formato = formato;
        this.parte = parte;
    }

    public Metadata(String nombre, String autor, String fecha, String formato) {
        this.nombre = nombre;
        this.autor = autor;
        this.fecha = fecha;
        this.formato = formato;
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

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getFormato() {
        return formato;
    }

    public void setFormato(String formato) {
        this.formato = formato;
    }

    public String getParte() {
        return parte;
    }

    public void setParte(String parte) {
        this.parte = parte;
    }

    
    
}//end class
