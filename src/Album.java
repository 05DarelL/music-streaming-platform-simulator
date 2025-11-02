import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Album extends PlataformLevel {
    private int aniolanzamiento;
    private String generoPredominante;
    private int cantCanciones;
    private ArrayList<Cancion> canciones;

    public Album(String nombre, int anioLanzamiento, String generoPredominante) {
        super(nombre);
        this.aniolanzamiento = anioLanzamiento;
        this.generoPredominante = generoPredominante;
        this.cantCanciones = 0;
        this.canciones = new ArrayList<Cancion>();
    }

    public int getAniolanzamiento() {
        return aniolanzamiento;
    }

    public void setAniolanzamiento(int aniolanzamiento) {
        this.aniolanzamiento = aniolanzamiento;
    }

    public String getGeneroPredominante() {
        return generoPredominante;
    }

    public void setGeneroPredominante(String generoPredominante) {
        this.generoPredominante = generoPredominante;
    }

    private int getCantCanciones() {
        return cantCanciones;
    }

    public ArrayList<Cancion> getCanciones() {
        return canciones;
    }

    private void setCantCanciones(int cantCanciones) {
        this.cantCanciones = cantCanciones;
    }

    public void agregarCancion(Cancion cancion) {
        getCanciones().add(cancion);
        setCantCanciones(getCantCanciones() + 1);
    }

    public Cancion cancionMasReproducidaDelAlbum() {
        Cancion masReproducidaDelAlbum = null;
        int record = 0;

        Iterator<Cancion> i = getCanciones().iterator();

        while(i.hasNext()) {
            Cancion c = i.next();
            if(c.getCantReproducciones() > record) {
                record = c.getCantReproducciones();
                masReproducidaDelAlbum = c;
            }
        }

        return masReproducidaDelAlbum;
    }

    public void agregarCanciones(List<Cancion> nuevasCanciones) {
        for(Cancion c : nuevasCanciones) {
            agregarCancion(c);
        }
    }
}
