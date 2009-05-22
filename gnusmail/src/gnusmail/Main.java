package gnusmail;

import gnu.getopt.Getopt;
import gnu.getopt.LongOpt;

public class Main {


    public static void main(String argv[]) throws Exception {
        LongOpt[] longopts = new LongOpt[3];
        StringBuffer sb = new StringBuffer();
        longopts[0] = new LongOpt("help", LongOpt.NO_ARGUMENT, null, 'h');
        longopts[1] = new LongOpt("connect", LongOpt.REQUIRED_ARGUMENT, sb, 'c');
        longopts[2] = new LongOpt("atrib", LongOpt.REQUIRED_ARGUMENT, sb, 'a');

        Getopt getopt = new Getopt("Clasificador", argv, "-:bdefgi::r::p::a::c::l::hx", longopts);
        getopt.setOpterr(false); 	// Disabling automatic handling of errors

        System.out.println("BIENVENIDO A GENUSMAIL!!!\n");

        String arg;
        int c;
        Options options = new Options();
        while ((c = getopt.getopt()) != -1) {
            switch (c) {
                case 0:
                    char car = (char) (new Integer(sb.toString())).intValue();
                    arg = getopt.getOptarg();
                    System.out.println("! " + arg);
                    if (arg != null) {
                        if (car == 'c') {
                        	options.setURL(arg);
                        } else if (car == 'a') {
                        	options.setShowAttributes(Integer.parseInt(arg));
                        }
                    } else {
                        System.out.println("Opcion no valida." +
                                "\nUse el comando --help (o -h) para ver las operaciones disponibles");
                    }
                    break;
                case 'a':	//Muestra x pantalla los atributos de un correo
                    arg = getopt.getOptarg();
                    System.out.println("! " + arg);
                	options.setShowAttributes(Integer.parseInt(arg));
                    break;

                case 'b':	//Extrae atribs de todos los correos del usuario
                	options.setAttributeExtraction(true);               	
                    break;
                    
                case 'c':	//Login
                    arg = getopt.getOptarg();
                    System.out.println("Arg es" + arg);
                    if (arg != null) {
                    	options.setURL(arg);
                    }
                    break;
                /*case 'd':	//Clasifica el correo n-esimo de la carpeta actual
                arg =  (g.getOptarg());
                if (miconexion==null) miconexion=new Conexion();
                MensajeInfo msj= new MensajeInfo(miconexion.getFolder().getMessage(Integer.parseInt(arg)));
                clasificarCorreo(msj);
                break;*/
                case 'e':	//Entrena el modelo
                	options.setModelTraining(true);
                    break;
                case 'f':	//Obtener lista de carpetas
                	options.setListFolders(true);
                    break;
                case 'g':	//Listar Correos de la carpeta actual
                	options.setListMailsInFolder(true);
                    break;
                case 'h':	//Muestra la ayuda
                    printMenu();
                    break;
                case 'i':	//Clasifica el Correo pasado por linea de comandos
                	options.setMailClassification(true);
                    break;
                case 'l':	//Clasifica el Correo pasado por linea de comandos
                    arg = getopt.getOptarg();
                	options.setListMails(true, Integer.parseInt(arg));
                    break;
                case 'p':	//Cambia una pareja de valores en el objeto Properties
                    String clave = getopt.getOptarg();
                    String valor = argv[getopt.getOptind()];
                	options.setProperties(clave, valor);
                    break;
                case 'r':	//Abrir Correo
                    arg = (getopt.getOptarg());
                    options.setOpenMail(Integer.parseInt(arg));
                    break;
                case '?':
                    System.out.println("Opcion no valida." +
                            "\nUse el comando --help o -h para ver las operaciones disponibles");
                    break;
            } //switch
            System.out.println();
        }//while
        System.out.println("Running options...");
        options.run();
        
        System.out.println("ADIOS!!!");

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


}
