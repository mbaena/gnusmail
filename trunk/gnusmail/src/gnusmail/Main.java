package gnusmail;

import gnu.getopt.Getopt;
import gnu.getopt.LongOpt;

public class Main {

	public static void main(String argv[]) throws Exception {
		LongOpt[] longopts = new LongOpt[5];
		StringBuffer sb = new StringBuffer();
		longopts[0] = new LongOpt("help", LongOpt.NO_ARGUMENT, null, 'h');
		longopts[1] = new LongOpt("connect", LongOpt.REQUIRED_ARGUMENT, sb, 'c');
		longopts[2] = new LongOpt("atrib", LongOpt.REQUIRED_ARGUMENT, sb, 'a');
		longopts[3] = new LongOpt("moa-classifier", LongOpt.REQUIRED_ARGUMENT, sb, 243);
		longopts[4] = new LongOpt("weka-classifier", LongOpt.REQUIRED_ARGUMENT, sb, 244);
		Getopt getopt = new Getopt("Clasificador", argv, "-:b::defgi::kz::m::n::r::p::a::c:::l::hx", longopts);
		getopt.setOpterr(false); 	// Disabling automatic handling of errors

		System.out.println("WELCOME TO GENUSMAIL!!!");
		for (String s : argv) {
			System.out.println(s);
		}

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
						} else if (car == 244) {
							options.setWekaClassifier(arg);
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
					options.setDatasetFileName(arg);
					options.setAttributeExtraction(true);
					break;

				case 'c':
					System.out.println(arg);
					if (arg != null) {
						options.setURL(arg);
					}
					break;
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
					options.setTasasFileName(arg);					
					options.setIncrementallyTraining(true);
					break;
				case 'k':
					options.setUpdateModelWithMail();
					break;
				case 'l':
					System.out.println("Arg es" + arg);
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
		System.out.println("-b	Extracts attributes for every mail\n");
		System.out.println("-e	Trains the model\n");
		System.out.println("-i	Classifies a mail read from the sdtin\n");
		System.out.println("-k	Updates the classification model with a mail read from the sdtin\n");
		System.out.println("-z	Reads mail from filesystem (~/.gnusmail/maildir) instead of IMAP connection \n");
		System.out.println("-l[N]\tLists messages chronologically \n" +
				"\t(limitar a N mensajes por carpeta).\n");
		System.out.println("----------------------------------------------");
	}
}
