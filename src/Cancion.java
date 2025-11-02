public class Cancion {

    private String titulo;
    private int duracion; //en segundos
    private int numPista;
    private int cantReproducciones;

    public Cancion(String titulo, int duracion, int numPista) {
        this.titulo = titulo;
        this.duracion = duracion;
        this.numPista = numPista;
        this.cantReproducciones = 0;
    }


    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public int getDuracion() {
        return duracion;
    }

    public void setDuracion(int duracion) {
        this.duracion = duracion;
    }

    public int getNumPista() {
        return numPista;
    }

    public void setNumPista(int numPista) {
        this.numPista = numPista;
    }

    public int getCantReproducciones() {
        return cantReproducciones;
    }

    public void setCantReproducciones(int cantReproducciones) {
        this.cantReproducciones = cantReproducciones;
    }

    public void reproducir() {
        setCantReproducciones(getCantReproducciones() + 1);
    }
}
