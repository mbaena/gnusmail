package gnusmail;

import gnusmail.core.ClaseCSV;
import gnusmail.core.ConfigurationManager;
import gnusmail.core.cnx.Connection;
import gnusmail.core.cnx.MensajeInfo;
import gnusmail.filters.Filter;
import gnusmail.filters.WordFrequency;

import java.io.IOException;
import java.util.List;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.Vector;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;

import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;

public class FilterManager {

    ClaseCSV csvmanager;

    public FilterManager() {
        try {
            csvmanager = new ClaseCSV();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    /**
     * Almacena los atributos de todos los correos de la carpeta pasada en el
     * fichero CSV
     */
    public void saveAtributosCarpeta(Folder buzon) {
        String[] atributos;
        if (buzon != null) {
            try {
                if (!buzon.isOpen()) {
                    buzon.open(javax.mail.Folder.READ_WRITE);
                }

                for (int i = 1; i <= buzon.getMessageCount(); i++) {

                    MensajeInfo msj = new MensajeInfo(buzon.getMessage(i));
                    System.out.println("Extrayendo información de " + msj.getMessageId());
                    atributos = getAtributosCorreo(msj);

                    // el Vector filtros contiene todos los filtros activos
                    String[] filtros = ConfigurationManager.getFilters();

                    /* Y los escribimos en el fichero CSV */
                    csvmanager.addRegistro(atributos, expandirFiltros(filtros));
                }// for

            } catch (MessagingException e) {
                System.out.println("Folder " + buzon.getFullName() + " no encontrado");
                e.printStackTrace();
            } catch (IOException e) {
                System.out.println("Error al escribir en CSV");
                e.printStackTrace();
            }
        }// if

    }

    public void saveAtributos(Connection miconexion) {
        Folder[] carpetas;
        System.out.println("Extrayendo informacion de los correos...");
        try {
            carpetas = miconexion.getCarpetas();

            for (int i = 0; i < carpetas.length; i++) {
                if (!carpetas[i].getFullName().contains(".Sent")) {
                    System.out.println("Extrayendo informacion de " + carpetas[i].getFullName());
                    saveAtributosCarpeta(carpetas[i]);
                } else {
                    System.out.println("No es necesario extraer información de " +
                            carpetas[i].getFullName());
                }
            }

        } catch (MessagingException e) {
            System.out.println("Imposible obtener Carpetas de usuario");
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println();
            e.printStackTrace();
        }

    }

    public void saveAttributesInOrder(Connection connection, int limit) {
        MessageReader reader = new MessageReader(connection, limit);
        String[] atributos;
        for (Message msg : reader) {
            MensajeInfo msgInfo = new MensajeInfo(msg);
            try {
                System.out.println("Atributos de: " + msgInfo.getMessageId() + " " + msgInfo.getDateAsStr());
                atributos = getAtributosCorreo(msgInfo);

                    // el Vector filtros contiene todos los filtros activos
                    String[] filtros = ConfigurationManager.getFilters();

                    /* Y los escribimos en el fichero CSV */
                    csvmanager.addRegistro(atributos, expandirFiltros(filtros));
            } catch (IOException ex) {
                Logger.getLogger(FilterManager.class.getName()).log(Level.SEVERE, null, ex);
            } catch (MessagingException ex) {
                Logger.getLogger(FilterManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Este método crea una nueva instancia con los datos extraídos de msg para
     * poder clasificarla posteriormente
     */
    public Instance makeInstance(MensajeInfo msg, Instances dataSet)
            throws Exception {
        return makeInstance(msg, "INBOX", dataSet);
    }

    /**
     * Este método crea una nueva instancia con los datos extraídos de msg para
     * poder clasificarla posteriormente
     */
    public Instance makeInstance(MensajeInfo msg, String initialFolderName,
            Instances dataSet) throws Exception {
        String[] atribs = getAtributosCorreo(msg);
        // for (int j=0;j<atribs.length;j++) System.out.print(atribs[j]+',');
        // System.out.println();

        Instance inst = new Instance(atribs.length);

        String[] filtros = ConfigurationManager.getFilters();

        inst.setDataset(dataSet);

        for (int i = 0; i < atribs.length; i++) {
            // Este bucle toma el nombre del filtro, eliminando
            // la cadena "genusmail.filters." del pricipio
            StringTokenizer str = new StringTokenizer(filtros[i], ".");
            String p = "";
            int max = str.countTokens();
            for (int j = 1; j <= max; j++) {
                p = str.nextToken(".");
            }
            // System.out.println(p);

            Attribute messageAtt = dataSet.attribute(p);
            // System.out.print(i+" "+messageAtt.name()+" -> ");
            // System.out.println(messageAtt.indexOfValue(atribs[i]));

            if (messageAtt.indexOfValue(atribs[i]) == -1) {
                inst.setMissing(messageAtt);
            } else {
                inst.setValue(messageAtt, (double) messageAtt.indexOfValue(atribs[i]));
            }
        }

        return inst;
    }

    /**
     * Extrae los atributos del correo de la carpeta actual (según los filtros
     * activos en el Properties)
     * TODO: se abre y se cierra conexiones a las carpetas por cada correo. Esto se hace
     * porque si el numero posible de conexiones es limitado, y si leemos los
     * emails por orden de fecha, no podemos controlar cuantas conexiones tenemos
     * abiertas.
     */
    public String[] getAtributosCorreo(MensajeInfo msj) throws MessagingException {
        if (!msj.getFolder().isOpen()) {
                msj.getFolder().open(Folder.READ_ONLY);
        }
        System.out.println("   Analizando " + msj.getMessageId());
        // res es un vector q contendrá el contenido de todos los atributos
        // activos
        Vector<String> res = new Vector<String>();

        // filtros contiene todos los filtros activos
        String[] filtersName = ConfigurationManager.getFilters();
        Vector<Filter> filters = new Vector<Filter>();
        for (String sfiltro : filtersName) {
            try {
                filters.add((Filter) Class.forName(sfiltro).newInstance());
            } catch (InstantiationException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        // System.out.println("Procesando mensaje...");
        for (Filter filter : filters) {
            if (filter instanceof WordFrequency) {
                List<String> palabras = WordFrequency.leerPalabrasAAnalizar();
                try {
                    for (String palabra : palabras) {
                        WordFrequency filtroPalabras = (WordFrequency) filter;
                        filtroPalabras.setPalabraAMirar(palabra);
                        String elemento = filtroPalabras.applyTo(msj);
                        res.addElement(elemento);
                    }
                } catch (Exception e) {
                    System.out.println("Mensaje no encontrado");
                    e.printStackTrace();
                }
            } else {
                try {
                    String elemento = filter.applyTo(msj);
                    res.addElement(elemento);
                } catch (Exception e) {
                    System.out.println("Mensaje no encontrado");
                    e.printStackTrace();
                }
            }
        }

        // Interesa devolver un array de String, no un Vector
        String[] sres = new String[res.size()];
        msj.getFolder().close(false);
        return res.toArray(sres);

        
    }

    public void escribirFichero() {
        try {
            csvmanager.escribirFichero();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    /**
     * Esta clase rellena las palabras que se van a mirar en caso de tener el
     * filtro WordFrecuency
     *
     * @param filtros
     * @return
     */
    private static Vector<String> expandirFiltros(String[] filtros) {
        Vector<String> res = new Stack<String>();
        for (String s : filtros) {
            if (s.contains("WordFrequency")) {
                for (String palabra : WordFrequency.leerPalabrasAAnalizar()) {
                    res.add(palabra);
                }
            } else {
                res.add(s);
            }
        }
        // for (String s : res ) System.out.println(s);

        return res;
    }
}
