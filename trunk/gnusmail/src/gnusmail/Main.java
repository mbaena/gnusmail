package gnusmail;

import gnu.getopt.Getopt;
import gnu.getopt.LongOpt;

public class Main {

	public static void main(String argv[]) throws Exception {
		LongOpt[] longopts = new LongOpt[4];
		StringBuffer sb = new StringBuffer();
		longopts[0] = new LongOpt("help", LongOpt.NO_ARGUMENT, null, 'h');
		longopts[1] = new LongOpt("connect", LongOpt.REQUIRED_ARGUMENT, sb, 'c');
		longopts[2] = new LongOpt("atrib", LongOpt.REQUIRED_ARGUMENT, sb, 'a');
		longopts[3] = new LongOpt("moa-classifier", LongOpt.REQUIRED_ARGUMENT, sb, 243);
		Getopt getopt = new Getopt("Clasificador", argv, "-:bdefgi::kz::m::n::r::p::a::c:::l::hx", longopts);
		getopt.setOpterr(false); 	// Disabling automatic handling of errors

		System.out.println("WELCOME TO GENUSMAIL!!!");

		String arg;
		int c;
		Options options = Options.getInstance();
		c = getopt.getopt();
		arg = getopt.getOptarg();
		while (c != -1) {
			switch (c) {
				case 0:
					char car = (char) (new Integer(sb.toString())).intValue();
					if (arg != null) {
						if (car == 'c') {
							options.setURL(arg);
						} else if (car == 'a') {
							options.setShowAttributes(Integer.parseInt(arg));
						} else if (car == 243) {
							options.setMoaClassifier(arg);
						}
					} else {
						System.out.println("Invalid option" +
								"\nUse command --help (or -h) to see available options");
					}
					break;
				case 'a':
					options.setShowAttributes(Integer.parseInt(arg));
					break;

				case 'b':
					options.setAttributeExtraction(true);
					break;

				case 'c':
					System.out.println(arg);
					if (arg != null) {
						options.setURL(arg);
					}
					break;
				/*case 'w':
					options.setExtractWords(true);
					break;*/
				/*case 'd':	//Clasifica el correo n-esimo de la carpeta actual
				arg =  (g.getOptarg());
				if (miconexion==null) miconexion=new Conexion();
				MensajeInfo msj= new MensajeInfo(miconexion.getFolder().getMessage(Integer.parseInt(arg)));
				clasificarCorreo(msj);
				break;*/
				case 'e':
					options.setModelTraining(true);
					break;
				case 'f':
					options.setListFolders(true);
					break;
				case 'g':
					options.setListMailsInFolder(true);
					break;
				case 'h':
					printMenu();
					return;
				case 'i':
					//options.setMailClassification(true);
					options.setTasasFileName(arg);					
					options.setIncrementallyTraining(true);
					break;
				case 'k':
					options.setUpdateModelWithMail();
					break;
				case 'l':
					options.setListMails(true, Integer.parseInt(arg));
					break;
				case 'm':
					options.setTasasFileName(arg);
					options.setMoaTraining(true);
					break;					
				case 'n':
					options.setStudyHeaders(true);
					break;
				case 'r':
					options.setOpenMail(Integer.parseInt(arg));
					break;
				case 'z':
					options.setReadMailsFromFileSystem(arg);
					System.out.println("Mails will be read from filesystem..." + arg);
					break;
				case '?':
					System.out.println("Invalid option" +
							"\nUse command --help (or -h) to see available options");
					break;
			} //switch
			c = getopt.getopt();
			arg = getopt.getOptarg();
		}//while
		System.out.println("Running options...");
		options.run();
		System.out.println("End of execution");
	}

	private static void printMenu() {
		System.out.println("These are the available options:\n");
		System.out.println("----------------------------------------------");
		System.out.println("-h/--help\n	Shows this menu \n");
		//System.out.println("-a/--atrib numMail\n	Gets the attributes of a message\n");
		System.out.println("-b	Extracts attributes for every mail\n");
		//System.out.println("-c/--connect url\n\tSpecifies a connection url: \n " +
		//		"\tprotocol://user:passwd@server[/folder.subFolder]\n");
		//System.out.println("-d numCorreo\n	Clasifica con el modelo el correo numCorreo-esimo" +
		//		" de la carpeta actual\n");
		System.out.println("-e	Trains the model\n");
		//System.out.println("-f	Lists folders\n");
		//System.out.println("-g	Lists messages in actual folder\n");
		System.out.println("-i	Classifies a mail read from the sdtin\n");
		System.out.println("-k	Updates the classification model with a mail read from the sdtin\n");
		System.out.println("-z	Reads mail from filesystem (~/.gnusmail/maildir) instead of IMAP connection \n");
		System.out.println("-l[N]\tLists messages chronologically \n" +
				"\t(limitar a N mensajes por carpeta).\n");
		//System.out.println("-r numCorreo\n\tShows message content\n");
		System.out.println("----------------------------------------------");
	}
}
