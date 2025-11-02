import cu.edu.cujae.ceis.tree.binary.BinaryTreeNode;
import cu.edu.cujae.ceis.tree.general.GeneralTree;
import cu.edu.cujae.ceis.tree.iterators.general.InBreadthIterator;
import cu.edu.cujae.ceis.tree.iterators.general.InDepthIterator;

import java.util.*;

public class Plataforma {
    private String nombre;
    private int anioCreacion;
    private GeneralTree<PlataformLevel> plataforma;
    private Queue<Cancion> playList;


    public Plataforma(String nombre, int anioCreacion) {
        this.nombre = nombre;
        this.anioCreacion = anioCreacion;
        plataforma = new GeneralTree<>();
        playList = new ArrayDeque<>();
    }

    public GeneralTree<PlataformLevel> getPlataforma() { return plataforma; }
    public String getNombre() {
        return nombre;
    }
    private Queue<Cancion> getPlaylist() { return playList; }
    public void setPlaylist(Queue<Cancion> cancions) {
        playList.clear();
        playList.addAll(cancions);
    }


    /*----------------------------------------------------------------------------------------------------------------*\
    |*                                         METODOS DE LOS INCISOS                                                 *|
    \*----------------------------------------------------------------------------------------------------------------*/

/**/public Cancion cancionMasReproducidaDeLaPlataforma() {   // INCISO B

    InDepthIterator<PlataformLevel> iNode = getPlataforma().inDepthIterator();
    Cancion cancionMasReproducida = null;
    int recordGlobal = 0;

    while (iNode.hasNext()) {
        PlataformLevel p = iNode.next();

        if (p instanceof Album) {
            Album album = (Album) p;

            Cancion cancion = album.cancionMasReproducidaDelAlbum();

            if (cancion != null && cancion.getCantReproducciones() > recordGlobal) {
                cancionMasReproducida = cancion;
                recordGlobal = cancion.getCantReproducciones();
            }
        }
    }

    return cancionMasReproducida;
}

/**/public boolean transferirArtistaDeSello(String nombreArtista, String nombreNuevoSello) {
        BinaryTreeNode<PlataformLevel> nodoArtista = obtenerNodoDelArtistaDadoElNombre(nombreArtista);
        BinaryTreeNode<PlataformLevel> nodoNuevoSello = obtenerNodoDelSelloDadoElNombre(nombreNuevoSello);
        boolean exito = false;

        if (nodoArtista != null && nodoNuevoSello != null) {
            List<BinaryTreeNode<PlataformLevel>> albumesGuardados = getPlataforma().getSons(nodoArtista);

            getPlataforma().deleteNode(nodoArtista);
            nodoArtista.setLeft(null);      //Luego de borrar el nodo se limpian
            nodoArtista.setRight(null);     //las referencias por seguridad

            getPlataforma().insertNode(nodoArtista, nodoNuevoSello);

            montarReferenciasEntreArtistaYAlbumes(nodoArtista, albumesGuardados);

            exito = true;
        }

        return exito;
    }

/**/public boolean agregarCancionesAUnAlbum(String nombreArtistaDado, String nombreAlbumDado, //INCISO D -----------------------------------------------------------
                                                List<Cancion> nuevasCanciones) {
        List<BinaryTreeNode<PlataformLevel>> albumes = obtenerAlbumesDeArtistaDadoElNombre(nombreArtistaDado);
        boolean exito = false;

        if(albumes != null) {
            for (BinaryTreeNode<PlataformLevel> nodo : albumes) {
                Album actual = (Album) nodo.getInfo();
                if (actual.getNombre().equals(nombreAlbumDado)) {
                    actual.agregarCanciones(nuevasCanciones);
                    exito = true;
                }
            }
        }
        return exito;
    }

/**/public Queue<Cancion> obtenerListaDeReproduccion(ArrayList<String> listaNombres,           //INCISO E -------------------------------------------------------------
                                                     ArrayList<Integer> listaCantCanciones) {                                      //Opino que esto es un modo completamente inhabitual de
        int pedidos = listaNombres.size(); //pude haber usado tambien listaCantCanciones.size()                                    //trabajar, pero asi esta redactado. La medida que se
        Queue<Cancion> playlist = new ArrayDeque<>();                                                                              //toma es trabajar con las dos listas en paralelo, por
        LinkedList<BinaryTreeNode<PlataformLevel>> nodosArtistas = obtenerTodosArtistas();  //obtener una lista de los             //lo que los objetos de las mismas posiciones estan relacionados
        int i = 0;                                                                          //nodos de los artistas                //se asume que las dos listas tienen igual tamanio
        while(i < pedidos) {                                                                //para iterar comodamente
            String nombreActual = listaNombres.get(i);
            Integer cantActual = listaCantCanciones.get(i);
            BinaryTreeNode<PlataformLevel> nodoArtista = obtenerNodoDelArtistaBuscado(nombreActual, nodosArtistas);
            List<BinaryTreeNode<PlataformLevel>> nodosAlbumes = obtenerAlbumesDeArtistaDadoElNodo(nodoArtista);
            Queue<Cancion> canciones = obtenerCancionesPedidasDelArtista(cantActual, nodosAlbumes);

            while(!canciones.isEmpty()) {
                playlist.offer(canciones.poll());
            }
            i++;
        }
        return playlist;
    }

    /*----------------------------------------------------------------------------------------------------------------*\
    |*                                METODOS QUE LLAMAN LOS METODOS DE LOS INCISOS                                   *|
    \*----------------------------------------------------------------------------------------------------------------*/


    private BinaryTreeNode<PlataformLevel> obtenerNodoDelSelloDadoElNombre(String nombreNuevoSello) {
        InBreadthIterator<PlataformLevel> itNode = getPlataforma().inBreadthIterator();
        BinaryTreeNode<PlataformLevel> nodoRetorno = null;
        boolean parar = false;

        while(itNode.hasNext() && !parar) {
            BinaryTreeNode<PlataformLevel> nodo = itNode.nextNode();
            PlataformLevel p = nodo.getInfo();
            if(p instanceof Sello) {
                Sello s = (Sello) p;
                if(s.getNombre().equals(nombreNuevoSello)) {
                    parar = true;
                    nodoRetorno = nodo;
                }
            }
            if(p instanceof Artista) {  //validar salida temprana
                parar = true;
            }
        }

        return nodoRetorno;
    }

    private BinaryTreeNode<PlataformLevel> obtenerNodoDelArtistaDadoElNombre(String nombreArtista) {
        InBreadthIterator<PlataformLevel> itNode = getPlataforma().inBreadthIterator();
        BinaryTreeNode<PlataformLevel> nodoRetorno = null;
        boolean parar = false;

        while(itNode.hasNext() && !parar) {
            BinaryTreeNode<PlataformLevel> nodo = itNode.nextNode();
            PlataformLevel p = nodo.getInfo();
            if(p instanceof Artista) {
                Artista a = (Artista) p;
                if(a.getNombre().equals(nombreArtista)) {
                    parar = true;
                    nodoRetorno = nodo;
                }
            }
            if(p instanceof Album) { //igual que antes, validar salida temprana
                parar = true;
            }
        }

        return nodoRetorno;
    }

    private void montarReferenciasEntreArtistaYAlbumes(BinaryTreeNode<PlataformLevel> nodoArtista,
                                                       List<BinaryTreeNode<PlataformLevel>> albumesGuardados) {
        for (BinaryTreeNode<PlataformLevel> album : albumesGuardados) {
            getPlataforma().insertNode(album, nodoArtista);
        }
    }

    private BinaryTreeNode<PlataformLevel> obtenerNodoDelArtistaBuscado(String nombreActual, LinkedList<BinaryTreeNode<PlataformLevel>> nodosArtistas) {
        BinaryTreeNode<PlataformLevel> nodoArtista = null;
        Iterator<BinaryTreeNode<PlataformLevel>> i = nodosArtistas.iterator();
        boolean encontrado = false;

        while(i.hasNext() && !encontrado) {
            BinaryTreeNode<PlataformLevel> nodo = i.next();
            Artista artista = (Artista) nodo.getInfo();
            if(artista.getNombre().equals(nombreActual)) {
                encontrado = true;
                nodoArtista = nodo;
            }
        }
        return nodoArtista;
    }

    private Queue<Cancion> obtenerCancionesPedidasDelArtista(Integer cantActual, List<BinaryTreeNode<PlataformLevel>> nodosAlbumes) {
        Queue<Cancion> canciones = new ArrayDeque<>();
        Iterator<BinaryTreeNode<PlataformLevel>> itNode = nodosAlbumes.iterator();
        while(itNode.hasNext() && cantActual > 0) { //Mientras no se acaben las canciones de los albumes y no se haya cumplido el pedido de canciones para la playlist
            Album album = (Album) itNode.next().getInfo();

            Iterator<Cancion> itCancion = album.getCanciones().iterator();
            while(itCancion.hasNext() && cantActual > 0) {
                canciones.offer(itCancion.next());
                cantActual--;
            }
        }
        return canciones;
    }

    private List<BinaryTreeNode<PlataformLevel>> obtenerAlbumesDeArtistaDadoElNodo(BinaryTreeNode<PlataformLevel> nodoArtista) {
        return getPlataforma().getSons(nodoArtista);                                                //Antes hubo un metodo que hacia este mismo trabajo, pero el parametro de
    }                                                                                               //entrada era el nombre, por lo que habia que hacer toda la busqueda

    private LinkedList<BinaryTreeNode<PlataformLevel>> obtenerTodosArtistas() {
        InBreadthIterator<PlataformLevel> itNode = getPlataforma().inBreadthIterator();
        LinkedList<BinaryTreeNode<PlataformLevel>> artistas = new LinkedList<>();
        boolean parar = false;

        while(itNode.hasNext() && !parar) {
            BinaryTreeNode<PlataformLevel> node = itNode.nextNode();
            PlataformLevel p = node.getInfo();
            if(p instanceof Artista) {
                artistas.add(node);
            }
            if(p instanceof Album) {
                parar = true; //igual que antes, salir si empieza la iteracion sobre albumes
            }
        }
        return artistas;
    }

    private List<BinaryTreeNode<PlataformLevel>> obtenerAlbumesDeArtistaDadoElNombre(String nombreArtista) {
        InBreadthIterator<PlataformLevel> itNode = getPlataforma().inBreadthIterator();
        boolean salir = false;
        List<BinaryTreeNode<PlataformLevel>> lista = null;

        while(itNode.hasNext() && !salir) {
            BinaryTreeNode<PlataformLevel> node = itNode.nextNode();
            PlataformLevel p = node.getInfo();

            if(p instanceof Artista) {
                Artista a = (Artista) p;
                if(a.getNombre().equals(nombreArtista)) {
                    salir = true;
                    lista = getPlataforma().getSons(node);
                }
            }
            if(p instanceof Album) {
                salir = true;
            }
        }
        return lista;
    }

    private Cancion ObtenerCancionDadoElTitulo(String titulo) {
        List<Album> listaAlbumes = obtenerTodosLosAlbumes();
        Cancion cancionRetorno = null;

        Iterator<Album> itAlbum = listaAlbumes.iterator();
        boolean encontrada = false;

        while (itAlbum.hasNext() && !encontrada) {
            Album album = itAlbum.next();
            List<Cancion> canciones = album.getCanciones();

            if (canciones != null) {
                Iterator<Cancion> itCancion = canciones.iterator();
                while (itCancion.hasNext() && !encontrada) {
                    Cancion c = itCancion.next();
                    if (c.getTitulo().equalsIgnoreCase(titulo)) {
                        cancionRetorno = c;
                        encontrada = true; // marcar que se encontró para salir de ambos bucles
                    }
                }
            }
        }

        return cancionRetorno;
    }


    public LinkedList<Album> obtenerTodosLosAlbumes() {
        LinkedList<Album> listaAlbumes = new LinkedList<>();
        InDepthIterator<PlataformLevel> it = getPlataforma().inDepthIterator();

        while (it.hasNext()) {
            PlataformLevel p = it.next();
            if (p instanceof Album) {
                listaAlbumes.add((Album) p);
            }
        }

        return listaAlbumes;
    }

    /*----------------------------------------------------------------------------------------------------------------*\
    |*                                             METODO MAIN                                                        *|
    \*----------------------------------------------------------------------------------------------------------------*/


    public static void main(String[] args) {

        // Crear plataforma principal
        Plataforma spotify = new Plataforma("Spotify", 2008);
        GeneralTree<PlataformLevel> arbol = spotify.getPlataforma();        //Sabemos que el arbol existe dentro de la clase, pero aqui
                                                                            //se decide sacar la referencia por comodidad
        // Crear nodo raíz de la plataforma
        BinaryTreeNode<PlataformLevel> nodoPlataforma = new BinaryTreeNode<>(new PlataformLevel(spotify.getNombre()));
        arbol.insertNode(nodoPlataforma, null); // raíz

        // Crear sellos discográficos reales
        BinaryTreeNode<PlataformLevel> emi = new BinaryTreeNode<>(new Sello("EMI", "Reino Unido"));
        BinaryTreeNode<PlataformLevel> bigMachine = new BinaryTreeNode<>(new Sello("Big Machine Records", "EE.UU"));
        BinaryTreeNode<PlataformLevel> parlophone = new BinaryTreeNode<>(new Sello("Parlophone", "Reino Unido"));
        BinaryTreeNode<PlataformLevel> warner = new BinaryTreeNode<>(new Sello("Warner Music", "EE.UU"));
        BinaryTreeNode<PlataformLevel> capitol = new BinaryTreeNode<>(new Sello("Capitol Records", "EE.UU"));

        // Insertar sellos como hijos de la plataforma
        arbol.insertNode(emi, nodoPlataforma);
        arbol.insertNode(bigMachine, nodoPlataforma);
        arbol.insertNode(parlophone, nodoPlataforma);
        arbol.insertNode(warner, nodoPlataforma);
        arbol.insertNode(capitol, nodoPlataforma);

        // Crear artistas
        BinaryTreeNode<PlataformLevel> beatles = new BinaryTreeNode<>(new Artista("The Beatles", "Rock", "Reino Unido"));
        BinaryTreeNode<PlataformLevel> taylor = new BinaryTreeNode<>(new Artista("Taylor Swift", "Pop", "EE.UU"));
        BinaryTreeNode<PlataformLevel> queen = new BinaryTreeNode<>(new Artista("Queen", "Rock", "Reino Unido"));
        BinaryTreeNode<PlataformLevel> soda = new BinaryTreeNode<>(new Artista("Soda Stereo", "Rock", "Argentina"));
        BinaryTreeNode<PlataformLevel> nirvana = new BinaryTreeNode<>(new Artista("Nirvana", "Grunge", "EE.UU"));
        BinaryTreeNode<PlataformLevel> radiohead = new BinaryTreeNode<>(new Artista("Radiohead", "Alternative Rock", "Reino Unido"));
        BinaryTreeNode<PlataformLevel> rhcp = new BinaryTreeNode<>(new Artista("Red Hot Chili Peppers", "Funk Rock", "EE.UU"));
        BinaryTreeNode<PlataformLevel> ledZeppelin = new BinaryTreeNode<>(new Artista("Led Zeppelin", "Hard Rock", "Reino Unido"));
        BinaryTreeNode<PlataformLevel> metallica = new BinaryTreeNode<>(new Artista("Metallica", "Heavy Metal", "EE.UU"));
        BinaryTreeNode<PlataformLevel> lanaDelRey = new BinaryTreeNode<>(new Artista("Lana Del Rey", "Indie Pop", "EE.UU"));

        // Insertar artistas en sus sellos reales
        arbol.insertNode(beatles, emi);           // EMI
        arbol.insertNode(taylor, bigMachine);     // Big Machine Records
        arbol.insertNode(queen, parlophone);      // Parlophone
        arbol.insertNode(soda, warner);           // Warner Music
        arbol.insertNode(nirvana, capitol);       // Capitol Records
        arbol.insertNode(radiohead, emi);         // EMI
        arbol.insertNode(rhcp, warner);           // Warner Music
        arbol.insertNode(ledZeppelin, parlophone);// Parlophone
        arbol.insertNode(metallica, capitol);     // Capitol Records
        arbol.insertNode(lanaDelRey, bigMachine); // Big Machine Records

        // Crear álbumes
        BinaryTreeNode<PlataformLevel> abbey = new BinaryTreeNode<>(new Album("Abbey Road", 1969, "Rock"));
        BinaryTreeNode<PlataformLevel> folklore = new BinaryTreeNode<>(new Album("Folklore", 2020, "Indie Pop"));
        BinaryTreeNode<PlataformLevel> nightOpera = new BinaryTreeNode<>(new Album("A Night at the Opera", 1975, "Rock"));
        BinaryTreeNode<PlataformLevel> dynamo = new BinaryTreeNode<>(new Album("Dynamo", 1992, "Rock Alternativo"));
        BinaryTreeNode<PlataformLevel> nevermind = new BinaryTreeNode<>(new Album("Nevermind", 1991, "Grunge"));
        BinaryTreeNode<PlataformLevel> okComputer = new BinaryTreeNode<>(new Album("OK Computer", 1997, "Alternative Rock"));
        BinaryTreeNode<PlataformLevel> californication = new BinaryTreeNode<>(new Album("Californication", 1999, "Funk Rock"));
        BinaryTreeNode<PlataformLevel> iv = new BinaryTreeNode<>(new Album("IV", 1971, "Hard Rock"));
        BinaryTreeNode<PlataformLevel> blackAlbum = new BinaryTreeNode<>(new Album("Metallica (The Black Album)", 1991, "Heavy Metal"));
        BinaryTreeNode<PlataformLevel> bornToDie = new BinaryTreeNode<>(new Album("Born to Die", 2012, "Indie Pop"));

        // Insertar álbumes
        arbol.insertNode(abbey, beatles);
        arbol.insertNode(folklore, taylor);
        arbol.insertNode(nightOpera, queen);
        arbol.insertNode(dynamo, soda);
        arbol.insertNode(nevermind, nirvana);
        arbol.insertNode(okComputer, radiohead);
        arbol.insertNode(californication, rhcp);
        arbol.insertNode(iv, ledZeppelin);
        arbol.insertNode(blackAlbum, metallica);
        arbol.insertNode(bornToDie, lanaDelRey);

        // Crear y agregar canciones
        Cancion c1 = new Cancion("Come Together", 259, 1);
        Cancion c2 = new Cancion("Something", 182, 2);
        Cancion c3 = new Cancion("Cardigan", 239, 1);
        Cancion c4 = new Cancion("Exile", 275, 2);
        Cancion c5 = new Cancion("Bohemian Rhapsody", 354, 1);
        Cancion c6 = new Cancion("Love of My Life", 215, 2);
        Cancion c7 = new Cancion("En la Ciudad de la Furia", 345, 1);
        Cancion c8 = new Cancion("Claroscuro", 278, 2);
        Cancion c9 = new Cancion("Smells Like Teen Spirit", 301, 1);
        Cancion c10 = new Cancion("Come As You Are", 219, 2);
        Cancion c11 = new Cancion("I'm In Love With My Car", 265, 2);
        Cancion r1 = new Cancion("Paranoid Android", 387, 1);
        Cancion r2 = new Cancion("Karma Police", 262, 2);
        Cancion rhcp1 = new Cancion("Scar Tissue", 215, 1);
        Cancion rhcp2 = new Cancion("Otherside", 252, 2);
        Cancion lz1 = new Cancion("Stairway to Heaven", 482, 1);
        Cancion lz2 = new Cancion("Black Dog", 296, 2);
        Cancion m1 = new Cancion("Enter Sandman", 331, 1);
        Cancion m2 = new Cancion("Nothing Else Matters", 388, 2);
        Cancion ldr1 = new Cancion("Video Games", 300, 1);
        Cancion ldr2 = new Cancion("Summertime Sadness", 269, 2);

        // Agregar canciones a los álbumes
        ((Album) abbey.getInfo()).agregarCancion(c1);
        ((Album) abbey.getInfo()).agregarCancion(c2);
        ((Album) folklore.getInfo()).agregarCancion(c3);
        ((Album) folklore.getInfo()).agregarCancion(c4);
        ((Album) nightOpera.getInfo()).agregarCancion(c5);
        ((Album) nightOpera.getInfo()).agregarCancion(c6);
        ((Album) dynamo.getInfo()).agregarCancion(c7);
        ((Album) dynamo.getInfo()).agregarCancion(c8);
        ((Album) nevermind.getInfo()).agregarCancion(c9);
        ((Album) nevermind.getInfo()).agregarCancion(c10);
        ((Album) nightOpera.getInfo()).agregarCancion(c11);
        ((Album) okComputer.getInfo()).agregarCancion(r1);
        ((Album) okComputer.getInfo()).agregarCancion(r2);
        ((Album) californication.getInfo()).agregarCancion(rhcp1);
        ((Album) californication.getInfo()).agregarCancion(rhcp2);
        ((Album) iv.getInfo()).agregarCancion(lz1);
        ((Album) iv.getInfo()).agregarCancion(lz2);
        ((Album) blackAlbum.getInfo()).agregarCancion(m1);
        ((Album) blackAlbum.getInfo()).agregarCancion(m2);
        ((Album) bornToDie.getInfo()).agregarCancion(ldr1);
        ((Album) bornToDie.getInfo()).agregarCancion(ldr2);

        // Simular reproducciones
        c1.reproducir();
        c1.reproducir();
        c4.reproducir();
        c4.reproducir();
        c4.reproducir();


        System.out.println("Estructura de la plataforma " + spotify.getNombre() + ":");
        mostrarArbolConMarcoSimple(arbol, nodoPlataforma);

        Scanner sc = new Scanner(System.in);
        int opcion = -1;

        while (opcion != 0) {
            System.out.println("\n===== MENÚ DE LA PLATAFORMA =====");
            System.out.println("1. Mostrar canción más reproducida de la plataforma (INCISO B)");
            System.out.println("2. Transferir un artista de sello (INCISO C)");
            System.out.println("3. Reproducir una canción por nombre");
            System.out.println("4. Agregar canciones al album de un artista (INCISO D)");
            System.out.println("5. Hacer playlist (INCISO E)");
            System.out.println("6. Reproducir siguiente canción de la playlist");
            System.out.println("7. Mostrar árbol de la plataforma en pantalla");
            System.out.println("0. Salir");
            System.out.print("Seleccione una opción: ");

            try {
                opcion = Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("⛔ Opción inválida. Intente nuevamente.");
                continue;
            }

            switch (opcion) {
                case 1:
                    Cancion top = spotify.cancionMasReproducidaDeLaPlataforma();
                    if (top != null) {
                        System.out.println("\n🎵 La canción más reproducida de la plataforma es:");
                        System.out.println("   ► " + top.getTitulo() + " (" + top.getCantReproducciones() + " reproducciones)");
                    } else {
                        System.out.println("❌ No hay canciones en la plataforma aún.");
                    }
                    break;

                case 2:
                    System.out.print("\nIngrese el nombre del artista a transferir: ");
                    String nombreArtista = sc.nextLine().trim();
                    System.out.print("Ingrese el nombre del nuevo sello: ");
                    String nuevoSello = sc.nextLine().trim();

                    boolean exito = spotify.transferirArtistaDeSello(nombreArtista, nuevoSello);
                    if (exito) {
                        System.out.println("✅ Artista transferido correctamente.");
                    } else {
                        System.out.println("❌ No se pudo transferir el artista. Verifique los nombres ingresados.");
                    }
                    break;

                case 3:
                    System.out.print("\nIngrese el nombre de la canción a reproducir: ");
                    String nombreCancion = sc.nextLine().trim();
                    Cancion cancion = spotify.ObtenerCancionDadoElTitulo(nombreCancion);

                    if (cancion != null) {
                        cancion.reproducir();
                        System.out.println("🎧 Reproduciendo: " + cancion.getTitulo() +
                                " | Total reproducciones: " + cancion.getCantReproducciones());
                    } else {
                        System.out.println("❌ Canción no encontrada.");
                    }
                    break;

                case 4:
                    System.out.print("\nIngrese el nombre del artista: ");
                    String artista = sc.nextLine().trim();

                    System.out.print("Ingrese el nombre del álbum: ");
                    String album = sc.nextLine().trim();

                    List<Cancion> nuevasCanciones = new ArrayList<>();
                    System.out.println("Ingrese las canciones a agregar (deje vacío y presione Enter para terminar):");
                    String tituloCancion;
                    while (!(tituloCancion = sc.nextLine().trim()).isEmpty()) {
                        int duracion = 0;
                        while (true) {
                            System.out.print("Duración en segundos: ");
                            try {
                                duracion = Integer.parseInt(sc.nextLine().trim());
                                if (duracion > 0) break;
                                else System.out.println("⛔ La duración debe ser mayor a 0.");
                            } catch (NumberFormatException e) {
                                System.out.println("⛔ Valor inválido. Ingrese un número entero.");
                            }
                        }
                        nuevasCanciones.add(new Cancion(tituloCancion, duracion, 0));
                        System.out.print("Título de la siguiente canción (o Enter para terminar): ");
                    }

                    boolean agregado = spotify.agregarCancionesAUnAlbum(artista, album, nuevasCanciones);
                    if (agregado) {
                        System.out.println("✅ Canciones agregadas correctamente al álbum \"" + album + "\" de " + artista + ".");
                    } else {
                        System.out.println("❌ No se pudo encontrar el álbum o el artista indicado.");
                    }
                    break;

                case 5:
                    ArrayList<String> listaNombres = new ArrayList<>();
                    ArrayList<Integer> listaCantCanciones = new ArrayList<>();

                    System.out.println("\nIngrese los artistas y la cantidad de canciones a agregar a la lista de reproducción.");
                    System.out.println("Deje vacío el nombre del artista y presione Enter para terminar.");

                    String nameArtista;
                    while (!(nameArtista = sc.nextLine().trim()).isEmpty()) {
                        int cantCanciones;
                        while (true) {
                            System.out.print("Cantidad de canciones: ");
                            try {
                                cantCanciones = Integer.parseInt(sc.nextLine().trim());
                                if (cantCanciones > 0) break;
                                else System.out.println("⛔ La cantidad debe ser mayor a 0.");
                            } catch (NumberFormatException e) {
                                System.out.println("⛔ Valor inválido. Ingrese un número entero.");
                            }
                        }
                        listaNombres.add(nameArtista);
                        listaCantCanciones.add(cantCanciones);
                        System.out.print("Nombre del siguiente artista (o Enter para terminar): ");
                    }

                    spotify.setPlaylist(spotify.obtenerListaDeReproduccion(listaNombres, listaCantCanciones));
                    System.out.println("✅ Playlist creada correctamente.");
                    break;

                case 6:
                    if (spotify.getPlaylist() != null && !spotify.getPlaylist().isEmpty()) {
                        Cancion primera = spotify.getPlaylist().poll();
                        if (primera != null) {
                            primera.reproducir();
                        }
                        if (primera != null) {
                            System.out.println("▶ Reproduciendo: " + primera.getTitulo() +
                                    " (" + primera.getCantReproducciones() + " reproducciones hasta ahora)");
                        }
                    } else {
                        System.out.println("⚠ La playlist está vacía. Agrega canciones primero.");
                    }
                    break;

                case 7:
                    System.out.println("\nEstructura de la plataforma " + spotify.getNombre() + ":");
                    mostrarArbolConMarcoSimple(arbol, nodoPlataforma);
                    break;

                case 0:
                    System.out.println("Saliendo del sistema...");
                    break;

                default:
                    System.out.println("⚠ Opción no válida, intente nuevamente.");
                    break;
            }
        }


    }


    /*----------------------------------------------------------------------------------------------------------------*\
    |*                                      METODOS DE LA INTERFAZ DE CONSOLA                                         *|
    \*----------------------------------------------------------------------------------------------------------------*/

    private static void mostrarArbolConMarcoSimple(GeneralTree<PlataformLevel> arbol,
                                                   BinaryTreeNode<PlataformLevel> nodo) {
        if (nodo == null) return;

        // Construir todas las líneas del árbol en un ArrayList
        ArrayList<String> lineasArbol = new ArrayList<String>();
        construirLineasArbolSimple(arbol, nodo, "", true, 0, lineasArbol);

        // Determinar el ancho máximo de la caja (sin colores)
        int anchoMax = 0;
        for (String linea : lineasArbol) {
            String textoLimpio = linea.replaceAll("\u001B\\[[;\\d]*m", ""); // quitar colores
            if (textoLimpio.length() > anchoMax) {
                anchoMax = textoLimpio.length();
            }
        }

        // Línea superior
        System.out.print("╔");
        for (int i = 0; i < anchoMax + 2; i++) System.out.print("═");
        System.out.println("╗");

        // Imprimir cada línea con borde lateral
        for (String linea : lineasArbol) {
            String textoLimpio = linea.replaceAll("\u001B\\[[;\\d]*m", "");
            int padding = anchoMax - textoLimpio.length();
            System.out.print("║ ");
            System.out.print(linea);
            for (int i = 0; i < padding; i++) System.out.print(" ");
            System.out.println(" ║");
        }

        // Línea inferior
        System.out.print("╚");
        for (int i = 0; i < anchoMax + 2; i++) System.out.print("═");
        System.out.println("╝");
    }

    private static void construirLineasArbolSimple(GeneralTree<PlataformLevel> arbol,
                                                   BinaryTreeNode<PlataformLevel> nodo,
                                                   String prefijo,
                                                   boolean esUltimo,
                                                   int nivel,
                                                   ArrayList<String> lineas) {
        if (nodo == null) return;

        // Colores simples por nivel
        String[] colores = new String[] {
                "\u001B[92m", // nivel 0 → verde
                "\u001B[96m", // nivel 1 → cian
                "\u001B[94m", // nivel 2 → azul
                "\u001B[95m", // nivel 3 → magenta
                "\u001B[90m"  // nivel 4+ → gris
        };
        String reset = "\u001B[0m";
        String color = colores[Math.min(nivel, colores.length - 1)];

        String linea = "";
        if (nivel == 0) {
            linea = color + nodo.getInfo().getNombre() + reset;
        } else {
            linea = prefijo + (esUltimo ? "└── " : "├── ") + color + nodo.getInfo().getNombre() + reset;
        }
        lineas.add(linea);

        // Mostrar canciones si es un álbum
        if (nodo.getInfo() instanceof Album) {
            Album album = (Album) nodo.getInfo();
            ArrayList<Cancion> canciones = album.getCanciones();
            if (canciones != null && !canciones.isEmpty()) {
                for (int i = 0; i < canciones.size(); i++) {
                    Cancion c = canciones.get(i);
                    boolean ultimaCancion = (i == canciones.size() - 1);
                    String lineaCancion = prefijo + (esUltimo ? "    " : "│   ") +
                            (ultimaCancion ? "└── " : "├── ") +
                            "\u001B[37m" + c.getTitulo() + " (" + c.getDuracion() + "s)" + reset;
                    lineas.add(lineaCancion);
                }
            }
        }

        // Mostrar hijos
        ArrayList<BinaryTreeNode<PlataformLevel>> hijos = new ArrayList<>(arbol.getSons(nodo));
        for (int i = 0; i < hijos.size(); i++) {
            BinaryTreeNode<PlataformLevel> hijo = hijos.get(i);
            boolean ultimoHijo = (i == hijos.size() - 1);
            construirLineasArbolSimple(arbol, hijo,
                    prefijo + (nivel == 0 ? "" : (esUltimo ? "    " : "│  ")),
                    ultimoHijo, nivel + 1, lineas);
        }
    }
}
























































