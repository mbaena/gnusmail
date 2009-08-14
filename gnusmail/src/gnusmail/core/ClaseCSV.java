package gnusmail.core;

import com.Ostermiller.util.*;
import java.util.*;
import java.io.*;
import java.util.StringTokenizer;

import gnusmail.core.cnx.MensajeInfo;

/** Esta clase maneja la creacion, actualizacion y escritura del fichero CSV */
public class ClaseCSV {

    CSVParser reader;
    CSVPrinter writer;
    LabeledCSVParser lcp;
    String[][] todo;
    String[] cabeceras;
    int numRegistros = 0;
    public final static String FILE_CSV = System.getProperty("user.home") + "/.gnusmail/atributos.csv";

    public ClaseCSV() throws IOException {
        File fich = new File(FILE_CSV);

        if (!fich.exists()) {
            fich.createNewFile();
        }

        reader = new CSVParser(new FileInputStream(FILE_CSV));
        lcp = new LabeledCSVParser(reader);
        todo = lcp.getAllValues();
        cabeceras = getHeaders();
        //if (todo!=null) imprimir();
        reader.close();
    }

    /** Devuelve el valor del atributo consultado del mensaje pasado */
    public String getValue(String header, MensajeInfo msj) throws Exception {
        int index = buscarPosicion(header, cabeceras);
        int j = 0;
        while ((todo[j][0] != null) && (msj.getMessageId().compareTo(todo[j][0]) != 0) && (j < todo.length)) {
            j++;
        }
        if (j > todo.length) {
            return "";
        }
        return todo[j][index];
    }

    /** Busca un registro en el CSV en memoria. Si está nos devuelve
     * el número de fila en la estructura, si no, devuelve -1 */
    public int estaRegistro(String[] registro) throws IOException {
        if (todo != null) {
            int index = buscarPosicion("genusmail.filters.MessageId", cabeceras);
            if (index >= 0) {

                for (int i = 0; i < todo.length; i++) {
                    for (int j = 0; j < registro.length; j++) {
                        if (registro[j].compareTo(todo[i][index]) == 0) {
                            return i;
                        }
                    }
                }
            }
        }
        return -1;
    }

    /** Añade a la estructura CSV el registro pasado como parámetro */
    public void addRegistro(String[] nuevo, Vector<String> filtrosActivos) throws IOException {
        String[] nuevo2;

        if (cabeceras == null) {
            cabeceras = new String[nuevo.length];
            for (int i = 0; i < nuevo.length; i++) {
                cabeceras[i] = getNombreFiltro(filtrosActivos.get(i));
            }
            reordenarCabeceras();
        }
        //Si el nuevo registro no está, lo añadimos a la matriz q representa el CSV
        if (estaRegistro(nuevo) < 0) {
            nuevo2 = reordenarAtributos(nuevo, filtrosActivos);
            actualizarEstructura(nuevo2);
        }
    }

    /** Coloca el atributo Folder como primero en la lista de atributos */
    private void reordenarCabeceras() {
        int j = 0;
        String[] res = new String[cabeceras.length];

        res[0] = "Folder";
        for (int i = 1; i < res.length; i++) {
            if (cabeceras[j].compareTo("Folder") == 0) {
                j++;
            }
            res[i] = cabeceras[j];
            j++;
        }
        cabeceras = res.clone();
    }

    /** Elimina del nombre del filtro la subcadena "genusmail.filters." del inicio */
    private String getNombreFiltro(String nombreLargo) {
        StringTokenizer str = new StringTokenizer(nombreLargo, ".");
        String res = "";

        int max = str.countTokens();
        for (int j = 1; j <= max; j++) {
            res = str.nextToken(".");
        }

        return res;
    }

    /** Almacena la informacion en memoria al fichero CSV en disco */
    public void escribirFichero() throws IOException {
        FileWriter fw = new FileWriter(FILE_CSV);
        writer = new CSVPrinter(fw, false, true);
        writer.setAlwaysQuote(true);

        if (todo != null) {
//			System.out.println("Escribiendo en fichero CSV...");
            writer.writeln(cabeceras);
            writer.writeln(todo);
        }
    }

    /** Le da al nuevo registro el formato adecuado para escribrlo en el CSV */
    private String[] reordenarAtributos(String[] registro, Vector<String> filtros) throws IOException {
        String[] res;
        int j;

        if ((cabeceras != null) && (registro.length < cabeceras.length)) {
            res = new String[cabeceras.length];
        } else {
            res = new String[registro.length];
        }

        for (int z = 0; z < res.length; z++) {
            res[z] = "?";
        }


        for (int i = 0; i < registro.length; i++) {
            String atrib = filtros.get(i);
            if (cabeceras != null) {
                j = buscarPosicion(atrib, cabeceras);
            } else {
                j = i;
            }

            if (j == -1) {
                addHeader(atrib);
                j = cabeceras.length - 1;
            }

            res[j] = registro[i];

        }
        return res;
    }

    /** Devuelve la posicion de cadena en el array de Strings, -1 si no está */
    private int buscarPosicion(String cadena, String[] array) {
        int pos = 0;
        while ((pos < array.length) && (pos >= 0) && (cadena.compareTo("gnusmail.filters." + array[pos]) != 0) && (cadena.compareTo(array[pos]) != 0)) { //Por las palabras sueltas
            pos++;
        }

        if (pos >= array.length) {
            return -1;
        }
        return pos;
    }

    /** Añade una cabecera al final de this.cabeceras */
    private void addHeader(String nombre) {
        String[] res = new String[cabeceras.length + 1];

        for (int i = 0; i < cabeceras.length; i++) {
            res[i] = cabeceras[i];
        }
        try {
            res[cabeceras.length] = ((gnusmail.filters.Filter) Class.forName(nombre).newInstance()).getName();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            res[cabeceras.length] = nombre;
        }
        this.cabeceras = res.clone();
    }

    /** Actualiza el contenido en memoria del CSV */
    private void actualizarEstructura(String[] nuevo) {
        int i, j;
        if (cabeceras != null) {
            j = cabeceras.length;
        } else {
            j = nuevo.length;
        }

        if (todo != null) {
            i = todo.length + 1;
        } else {
            i = 1;
        }

        String[][] estructura = new String[i][j];
        if (todo != null) {
            for (i = 0; i < todo.length; i++) {
                for (j = 0; j < todo[0].length; j++) {
                    estructura[i][j] = todo[i][j];
                }
                while (j < estructura[0].length) {
                    estructura[i][j] = "?";
                    j++;
                }//while
            }//for
        }
        i = estructura.length - 1;
        for (j = 0; j < estructura[0].length; j++) {
            //TODO: la asignación de abajo da a veces problemas,
            //hay que quitarle la excepcion
            estructura[i][j] = nuevo[j];

        }
        todo = estructura.clone();
    }

    /** Devuelve un array conteniendo las cabeceras del fichero CSV */
    private String[] getHeaders() throws IOException {
        return lcp.getLabels();
    }

    /** Imprime las cabeceras del CSV */
    private void imprimirCabeceras() throws IOException {
        for (int i = 0; i < cabeceras.length; i++) {
            System.out.print(cabeceras[i]);
            if (i < cabeceras.length - 1) {
                System.out.print(",");
            }
        }
        System.out.println();
    }

    /** Imprime toda la información del CSV */
    @SuppressWarnings("unused")
    private void imprimir() throws IOException {
        imprimirCabeceras();
        for (int i = 0; i < todo.length; i++) {
            for (int j = 0; j < todo[i].length; j++) {
                System.out.print(todo[i][j]);
                if (j < todo[i].length - 1) {
                    System.out.print(",");
                }
            }
            System.out.println();
        }
        System.out.println();
    }
}
