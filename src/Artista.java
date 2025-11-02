import cu.edu.cujae.ceis.tree.iterators.general.InDepthIterator;

public class Artista extends PlataformLevel {
    private String genero;
    private String nacionalidad;

    public Artista(String nombre, String genero, String nacionalidad) {
        super(nombre);
        this.genero = genero;
        this.nacionalidad = nacionalidad;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public String getNacionalidad() {
        return nacionalidad;
    }

    public void setNacionalidad(String nacionalidad) {
        this.nacionalidad = nacionalidad;
    }
}
