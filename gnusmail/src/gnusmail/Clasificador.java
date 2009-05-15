package gnusmail;

import gnusmail.core.ClaseCSV;
import gnusmail.core.ConfigurationManager;
import gnusmail.core.WordStore;
import gnusmail.core.cnx.Conexion;
import gnusmail.core.cnx.MensajeInfo;
import gnusmail.filters.Filter;
import gnusmail.filters.WordFrequency;
import gnu.getopt.Getopt;
import gnu.getopt.LongOpt;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import weka.classifiers.Classifier;
import weka.classifiers.bayes.*;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.CSVLoader;

public class Clasificador {

    static ConfigurationManager cfg;
    static Conexion miconexion;
    static ClaseCSV csvmanager;
    //static Classifier model;
    static Instances dataSet;
    public final static String directorio = System.getProperty("user.home") + "/.genusmail/";
    public final static File fich_modelo = new File(directorio + "/model.conf");
    public final static File FICH_DATASET = new File(directorio + "/dataset.arff");
    public static WordStore wordStore;
    static List<String> palabrasAAnalizar;

    public static void main(String argv[]) throws Exception {
        int c;
        String arg;
        wordStore = new WordStore();
        LongOpt[] longopts = new LongOpt[3];
        System.out.println("BIENVENIDO A GENUSMAIL!!!\n");

        cfg = new ConfigurationManager();

        StringBuffer sb = new StringBuffer();
        longopts[0] = new LongOpt("help", LongOpt.NO_ARGUMENT, null, 'h');
        longopts[1] = new LongOpt("connect", LongOpt.REQUIRED_ARGUMENT, sb, 'c');
        longopts[2] = new LongOpt("atrib", LongOpt.REQUIRED_ARGUMENT, sb, 'a');

        Getopt g = new Getopt("Clasificador", argv, "-:efgibd::r::p::a::c::hx", longopts);
        g.setOpterr(false); 	//Desactivamos el manejo automatico de errores

        csvmanager = new ClaseCSV(); //Creamos el manejador de ficheros CSV



        while ((c = g.getopt()) != -1) {
            switch (c) {
                case 0:
                    char car = (char) (new Integer(sb.toString())).intValue();
                    arg = g.getOptarg();
                    System.out.println("! " + arg);
                    if (arg != null) {
                        if (car == 'c') {
                            conectar(arg);
                        } else if (car == 'a') {
                            miconexion.mostrarAtributos(Integer.parseInt(arg));
                        }
                    } else {
                        System.out.println("Opcion no valida." +
                                "\nUse el comando --help (o -h) para ver las operaciones disponibles");
                    }
                    break;
                case 'a':	//Muestra x pantalla los atributos de un correo
                    arg = g.getOptarg();
                    System.out.println("! " + arg);
                    if (miconexion == null) {
                        miconexion = new Conexion();
                    }
                    miconexion.mostrarAtributos(Integer.parseInt(arg));
                    break;

                case 'b':	//Extrae atribs de todos los correos del usuario
                    try {
                        if (miconexion == null) {
                            miconexion = new Conexion();
                        }
                        leerListaPalabras();
                        miconexion.logout();
                        miconexion = null;
                        System.out.println("Salvando atributos...");
                        saveAtributos();
                        System.out.println("Atributos salvados");
                    } catch (Exception e1) {
                        csvmanager.escribirFichero();
                        e1.printStackTrace();
                    }
                    break;
                case 'c':	//Login
                    arg = g.getOptarg();
                    if (arg != null) {
                        try {
                            conectar(arg);
                        } catch (Exception e) {
                            return;
                        }
                    }
                    break;
                /*case 'd':	//Clasifica el correo n-esimo de la carpeta actual
                arg =  (g.getOptarg());
                if (miconexion==null) miconexion=new Conexion();
                MensajeInfo msj= new MensajeInfo(miconexion.getFolder().getMessage(Integer.parseInt(arg)));
                clasificarCorreo(msj);
                break;*/
                case 'e':	//Entrena el modelo
                    entrenarModelo();
                    break;
                case 'f':	//Obtener lista de carpetas
                    if (miconexion == null) {
                        miconexion = new Conexion();
                    }
                    miconexion.listarCarpetas();
                    break;
                case 'g':	//Listar Correos de la carpeta actual
                    if (miconexion == null) {
                        miconexion = new Conexion();
                    }
                    miconexion.mostrarCorreos(miconexion.getFolder().getFullName());
                    break;
                case 'h':	//Muestra la ayuda
                    printMenu();
                    break;
                case 'i':	//Clasifica el Correo pasado por linea de comandos
                    MimeMessage msg = new MimeMessage(null, System.in);
                    //System.out.println("Mensaje creado a partir de entrada estándar");
                    MensajeInfo mi_mensaje = new MensajeInfo(msg);

                    clasificarCorreo(mi_mensaje);
                    break;
                case 'p':	//Cambia una pareja de valores en el objeto Properties
                    String clave = g.getOptarg();
                    String valor = argv[g.getOptind()];
                    cfg.añadirPropiedad("genusmail.filters." + clave, valor);
                    cfg.grabarFichero();
                    break;
                case 'r':	//Abrir Correo
                    arg = (g.getOptarg());
                    if (miconexion == null) {
                        miconexion = new Conexion();
                    }
                    miconexion.leerCorreo(Integer.parseInt(arg));
                    break;
                case 'x':	//Logout
                    if (miconexion.isLoggedIn()) {
                        miconexion.logout();
                    }
                    System.out.println("Logout terminado con exito!!");
                    break;
                case '?':
                    System.out.println("Opcion no valida." +
                            "\nUse el comando --help o -h para ver las operaciones disponibles");
                    break;
            } //switch
            System.out.println();
        }//while
        csvmanager.escribirFichero();

        if ((miconexion != null) && (miconexion.isLoggedIn())) {
            miconexion.logout();
        }
        System.out.println("ADIOS!!!");

    }

    /**
     * Esta clase rellena las palabras que se van a mirar en caso de tener el filtro WordFrecuency
     * @param filtros
     * @return
     */
    private static Vector<String> expandirFiltros(Vector<String> filtros) {
        Vector<String> res = new Stack<String>();
        for (String s : filtros) {
            if (s.contains("WordFrequency")) {
                for (String palabra : leerPalabrasAAnalizar()) {
                    res.add(palabra);
                }
            } else {
                res.add(s);
            }
        }
//        for (String s : res ) System.out.println(s);

        return res;
    }

    /**
     * Esta funcion lee una lista de palabras que deben ser usadas como filtro en el cuerpo
     * @return
     */
    private static List<String> leerPalabrasAAnalizar() {
        List<String> res = new ArrayList<String>();
        if (palabrasAAnalizar == null) {
            try {
                // Open the file that is the first
                // command line parameter
                FileInputStream fstream = new FileInputStream(WordStore.FICH_WORDS);
                // Get the object of DataInputStream
                DataInputStream in = new DataInputStream(fstream);
                BufferedReader br = new BufferedReader(new InputStreamReader(in));
                String strLine;
                //Read File Line By Line
                while ((strLine = br.readLine()) != null) {
                    // Print the content on the console
                    res.add(strLine);
                }
                //Close the input stream
                in.close();
            } catch (Exception e) {//Catch exception if any
                System.err.println("Error: " + e.getMessage());
            }
            palabrasAAnalizar = res;
        } else {
            res = palabrasAAnalizar;
        }
        return res;
    }

    /* Imprime por pantalla el menu de ayuda */
    private static void printMenu() {
        System.out.println("Estas son las operaciones a su disposicion:");
        System.out.println("----------------------------------------------");
        System.out.println("-h/--help\n	Muestra este menú de ayuda\n");
        System.out.println("-a/--atrib numCorreo\n	Obtener atributos de un Correo\n");
        System.out.println("-b	Extrae atributos de todos los correos del usuario y los almacena en el CSV\n");
        System.out.println("-c/--connect url\n	Se conecta, donde url es " +
                "protocolo://usuario:contraseña@servidor[/rutaCarpeta.subCarpeta]\n");
        //System.out.println("-d numCorreo\n	Clasifica con el modelo el correo numCorreo-esimo" +
        //		" de la carpeta actual\n");
        System.out.println("-e	Entrena el modelo\n");
        System.out.println("-f	Obtener lista de carpetas\n");
        System.out.println("-g	Listar Correos de la carpeta actual\n");
        System.out.println("-i	Clasifica el correo pasado por la entrada estandar\n");
        System.out.println("-p Clave Valor\n	Cambiar par en Properties\n");
        System.out.println("-r numCorreo\n	Muestra el contenido del Correo indicado\n");
        System.out.println("-x	Desconectar");
        System.out.println("----------------------------------------------");
    }

    private static void leerListaPalabras() {
        Folder[] carpetas;
        System.out.println("Extrayendo informacion de palabras de los correos...");
        try {
            carpetas = miconexion.getCarpetas();

            for (int i = 0; i < carpetas.length; i++) {
                System.out.println("Extrayendo informacion de palabres de  " + carpetas[i].getFullName());
                leerListaPalabrasCarpeta(carpetas[i]);
            }
            System.out.println("Extraida la info de las palabras");
            wordStore.writeToFile();

        } catch (MessagingException e) {
            System.out.println("Imposible obtener Carpetas de usuario");
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println();
            e.printStackTrace();
        }
    }

    private static void saveAtributos() {
        if (miconexion == null) {
            miconexion = new Conexion();
        }
        Folder[] carpetas;
        System.out.println("Extrayendo informacion de los correos...");
        try {
            carpetas = miconexion.getCarpetas();

            for (int i = 0; i < carpetas.length; i++) {
                System.out.println("Extrayendo informacion de " + carpetas[i].getFullName());
                saveAtributosCarpeta(carpetas[i]);
            }

        } catch (MessagingException e) {
            System.out.println("Imposible obtener Carpetas de usuario");
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println();
            e.printStackTrace();
        }



    }

    /** Extrae los atributos del correo de la carpeta actual
     * (según los filtros activos en el Properties) */
    private static String[] getAtributosCorreo(MensajeInfo msj) {
        System.out.println("   Analizando " + msj.getMessageId());
        // res es un vector q contendrá el contenido de todos los atributos activos
        Vector<String> res = new Vector<String>();

        //filtros contiene todos los filtros activos
        Vector<String> filtros = new Vector<String>();
        cfg.getFiltrosActivos(filtros);



        try {
            //System.out.println("Procesando mensaje...");
            for (String sfiltro : filtros) {
                Filter f1 = (Filter) Class.forName(sfiltro).newInstance();
                if (f1 instanceof WordFrequency) {
                    List<String> palabras = leerPalabrasAAnalizar();
                    try {
                        for (String palabra : palabras) {
                            WordFrequency filtroPalabras = (WordFrequency) f1;
                            filtroPalabras.setPalabraAMirar(palabra);
                            String elemento = filtroPalabras.aplicarFiltro(msj);
                            res.addElement(elemento);
                        }
                    } catch (Exception e) {
                        System.out.println("Mensaje no encontrado");
                        e.printStackTrace();
                    }
                } else {
                    try {
                        String elemento = f1.aplicarFiltro(msj);
                        System.out.println("Elemento = " + elemento);
                        res.addElement(elemento);
                    } catch (Exception e) {
                        System.out.println("Mensaje no encontrado");
                        e.printStackTrace();
                    }
                }
            }
        } catch (ClassNotFoundException cnfe) {
            System.out.println("Clase no encontrada: " + cnfe.getMessage());
        } catch (InstantiationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        //Interesa devolver un array de String, no un Vector
        String[] sres = new String[res.size()];
        return res.toArray(sres);
    }

    private static void leerListaPalabrasCarpeta(Folder buzon) {
        if (buzon != null) {
            try {
                if (!buzon.isOpen()) {
                    buzon.open(javax.mail.Folder.READ_WRITE);
                }

                for (int i = 1; i <= buzon.getMessageCount(); i++) {
                    MensajeInfo msj = new MensajeInfo(buzon.getMessage(i));
                    String body = msj.getBody();
                    wordStore.addTokenizedString(body);
                }//for

            } catch (MessagingException e) {
                System.out.println("Folder " + buzon.getFullName() + " no encontrado al leer palabras");
            //e.printStackTrace();
            } catch (IOException e) {
                System.out.println("Error al escribir en CSV");
                e.printStackTrace();
            }
        }//if
    }

    /** Almacena los atributos de todos los correos de
     * la carpeta pasada en el fichero CSV */
    private static void saveAtributosCarpeta(Folder buzon) {
        String[] atributos;
        if (buzon != null) {
            try {
                if (!buzon.isOpen()) {
                    buzon.open(javax.mail.Folder.READ_WRITE);
                }

                for (int i = 1; i <= buzon.getMessageCount(); i++) {
                    MensajeInfo msj = new MensajeInfo(buzon.getMessage(i));
                    atributos = getAtributosCorreo(msj);

                    //el Vector filtros contiene todos los filtros activos
                    Vector<String> filtros = new Vector<String>();
                    cfg.getFiltrosActivos(filtros);

                    /* Y los escribimos en el fichero CSV */
                    csvmanager.addRegistro(atributos, expandirFiltros(filtros));
                }//for

            } catch (MessagingException e) {
                System.out.println("Folder " + buzon.getFullName() + " no encontrado");
                e.printStackTrace();
            } catch (IOException e) {
                System.out.println("Error al escribir en CSV");
                e.printStackTrace();
            }
        }//if

    }

    /** Se conecta a la URL dada 
     * @throws Exception */
    private static void conectar(String url) throws MessagingException {
        if (url != null) {
            try {
                miconexion = new Conexion(url);
                miconexion.login(url);
                System.out.println("conectado");
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Imposible conectar al host solicitado");
            }
            if (miconexion.getFolder() != null) {
                miconexion.mostrar_mens();
            }
        }
    }

    private static void entrenarModelo() {
        Classifier model = new BayesNet();
        //J48 j48 = new J48();
        //j48.setBinarySplits(true);
        //Classifier model = j48;
        //Classifier model = new weka.classifiers.functions.RBFNetwork();

        System.out.println("Entrenando modelo...");

        CSVLoader csvdata = new CSVLoader();
        try {
            // MANOLO: ¿Por qué la llamada a escribirFichero aquí?
            // MIGUE: Por si hay información en memoria q aún no esté en disco,
            // aunq ya no tiene sentido xq hacemos llamadas con un solo parametro.
            //csvmanager.escribirFichero();

            File f = new File(ClaseCSV.FILE_CSV);
            System.out.println("La ruta es " + f.getAbsolutePath());
            csvdata.setSource(new File(ClaseCSV.FILE_CSV));
            dataSet = csvdata.getDataSet();
            dataSet.setClass(dataSet.attribute("Folder"));
            model.buildClassifier(dataSet);

        } catch (Exception e) {
            System.out.println("Imposible entrenar modelo");
            e.printStackTrace();
            return;
        }
        System.out.println(model);
        try {
            FileOutputStream f = new FileOutputStream(fich_modelo);
            ObjectOutputStream fis = new ObjectOutputStream(f);
            fis.writeObject(model);
            fis.close();


            Writer w = new BufferedWriter(new FileWriter(FICH_DATASET));
            Instances h = new Instances(dataSet);
            w.write(h.toString());
            w.write("\n");
            w.close();

        } catch (FileNotFoundException e) {
            System.out.println("Fichero " + fich_modelo.getAbsolutePath() + " no encontrado");
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /** Este método crea una nueva instancia con los datos extraídos de msg para
    poder clasificarla posteriormente */
    private static Instance makeInstance(MensajeInfo msg, Instances dataSet) throws Exception {
        String[] atribs = getAtributosCorreo(msg);
        //for (int j=0;j<atribs.length;j++) System.out.print(atribs[j]+',');
        //System.out.println();

        Instance inst = new Instance(atribs.length);

        Vector<String> filtros = new Vector<String>();
        cfg.getFiltrosActivos(filtros);

        inst.setDataset(dataSet);

        for (int i = 0; i < atribs.length; i++) {
            //Este bucle toma el nombre del filtro, eliminando
            // la cadena "genusmail.filters." del pricipio
            StringTokenizer str = new StringTokenizer(filtros.get(i), ".");
            String p = "";
            int max = str.countTokens();
            for (int j = 1; j <= max; j++) {
                p = str.nextToken(".");
            }
            //System.out.println(p);

            Attribute messageAtt = dataSet.attribute(p);
            //System.out.print(i+" "+messageAtt.name()+" -> ");
            //System.out.println(messageAtt.indexOfValue(atribs[i]));

            if (messageAtt.indexOfValue(atribs[i]) == -1) {
                inst.setMissing(messageAtt);
            } else {
                inst.setValue(messageAtt, (double) messageAtt.indexOfValue(atribs[i]));
            }
        }

        return inst;
    }

    private static void clasificarCorreo(MensajeInfo msg) throws Exception {
        Reader r = new BufferedReader(new FileReader(FICH_DATASET));
        dataSet = new Instances(r, 0); // Sólo necesitamos las cabeceras de los atributos
        dataSet.setClass(dataSet.attribute("Folder"));
        r.close();

        Instance inst = makeInstance(msg, dataSet);
        /*dataSet.add(inst);

        Writer w = new BufferedWriter(new FileWriter(FICH_DATASET));
        Instances h = new Instances(dataSet,0);
        w.write(h.toString());
        w.write("\n");
        w.close();*/

        Classifier model;
        //System.out.println(inst);

        if (!fich_modelo.exists()) {
            entrenarModelo();
        }

        FileInputStream fe = new FileInputStream(fich_modelo);
        ObjectInputStream fie = new ObjectInputStream(fe);
        model = (Classifier) fie.readObject();

        System.out.println("\nClasificando...\n");
        //distributionForInstance: da la predicción...
        double[] res = model.distributionForInstance(inst);
        Attribute att = dataSet.attribute("Folder");

        double mayor = 0;
        int indice_mayor = 0;
        for (int i = 0; i < res.length; i++) {
            System.out.println("\nLa carpeta destino sería: " + att.value(i) +
                    " con probabilidad: " + res[i]);
            if (res[i] > mayor) {
                indice_mayor = i;
                mayor = res[i];
            }
        }
        System.out.println("\nLa carpeta con mayor probabilidad es: " + att.value(indice_mayor));
    //msg.crearCabecera("Genusmail", att.value(indice_mayor));
    //msg.imprimir(System.out);
    }
}
