package gnusmail;

import gnu.getopt.Getopt;
import gnu.getopt.LongOpt;
import gnusmail.core.ConfigurationManager;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Main {

	public static void main(String argv[]) throws Exception {
		LongOpt[] longopts = new LongOpt[3];
		StringBuffer sb = new StringBuffer();
		longopts[0] = new LongOpt("help", LongOpt.NO_ARGUMENT, null, 'h');
		longopts[1] = new LongOpt("connect", LongOpt.REQUIRED_ARGUMENT, sb, 'c');
		longopts[2] = new LongOpt("atrib", LongOpt.REQUIRED_ARGUMENT, sb, 'a');

		Getopt getopt = new Getopt("Clasificador", argv, "-:bdefgik::r::p::a::c:::l::w::hx", longopts);

		getopt.setOpterr(false); 	// Disabling automatic handling of errors

		System.out.println("BIENVENIDO A GENUSMAIL!!!");


		String arg;
		int c;
		Options options = new Options();
		c = getopt.getopt();
		arg = getopt.getOptarg();
		while (c != -1) {
			switch (c) {
				case 0:
					char car = (char) (new Integer(sb.toString())).intValue();
					//arg = getopt.getOptarg();
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
					//arg = getopt.getOptarg();
					options.setShowAttributes(Integer.parseInt(arg));
					break;

				case 'b':	//Extrae atribs de todos los correos del usuario
					options.setAttributeExtraction(true);
					break;

				case 'c':	//Login
					System.out.println("Vamos a conectar");
					System.out.println(arg);
					//arg = getopt.getOptarg();
					if (arg != null) {
						options.setURL(arg);
						System.out.println("Conectados a " + arg);
					}
					break;
				case 'w':	//Extraer lista de palabras frecuentes
					options.setExtractWords(true);
					System.out.println("SetExtractWords");
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
					return;
				case 'i':	//Clasifica el Correo pasado por linea de comandos
					options.setMailClassification(true);
					break;
				case 'k': //Modifica el modelo, con el correo (como String) pasado por linea de comandos
					//arg = (getopt.getOptarg());
					options.setUpdateModelWithMail();
					break;
				case 'l':	//Clasifica el Correo pasado por linea de comandos
					//arg = getopt.getOptarg();
					options.setListMails(true, Integer.parseInt(arg));
					break;
				case 'r':	//Abrir Correo
					//arg = (getopt.getOptarg());
					options.setOpenMail(Integer.parseInt(arg));
					break;

				case '?':
					System.out.println("Opcion no valida." +
							"\nUse el comando --help o -h para ver las operaciones disponibles");
					break;
			} //switch
			System.out.println("Running options...");
			options.run();
			printMenu();
			System.out.println("Entre nuevo comando...");
			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
			String inputLine = in.readLine();
			String args[] = inputLine.split(" ");
			c = args[0].charAt(0);
			if (args.length > 1) {
				arg = args[1];
			}
		}//while
	}

	/* Imprime por pantalla el menu de ayuda */
	private static void printMenu() {
		System.out.println("Estas son las operaciones a su disposicion:");
		System.out.println("----------------------------------------------");
		System.out.println("-h/--help\n	Muestra este menú de ayuda\n");
		System.out.println("-a/--atrib numCorreo\n	Obtener atributos de un Correo\n");
		System.out.println("-b	Extrae atributos de todos los correos del usuario y los almacena en\n" +
				"\tel fichero CSV " + ConfigurationManager.DATASET_FILE);
		System.out.println("-c/--connect url\n\tEspecifica una url de conexión:\n " +
				"\tprotocolo://usuario:contraseña@servidor[/rutaCarpeta.subCarpeta]\n");
		//System.out.println("-d numCorreo\n	Clasifica con el modelo el correo numCorreo-esimo" +
		//		" de la carpeta actual\n");
		System.out.println("-e	Entrena el modelo\n");
		System.out.println("-f	Obtener lista de carpetas\n");
		System.out.println("-g	Listar Correos de la carpeta actual\n");
		System.out.println("-i	Clasifica el correo pasado por la entrada estandar\n");
		System.out.println("-l[N]\tLista mensajes en todas las carpetas ordenados por fecha de recepcción \n" +
				"\t(limitar a N mensajes por carpeta).\n");
		System.out.println("-r numCorreo\n\tMuestra el contenido del Correo indicado\n");
		System.out.println("----------------------------------------------");
	}
}
