package application;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class CLIController {
	public static final String ANSI_RESET = "\u001B[0m";
	public static final String ANSI_BLACK = "\u001B[30m";
	public static final String ANSI_RED = "\u001B[31m";
	public static final String ANSI_GREEN = "\u001B[32m";
	public static final String ANSI_YELLOW = "\u001B[33m";
	public static final String ANSI_BLUE = "\u001B[34m";
	public static final String ANSI_PURPLE = "\u001B[35m";
	public static final String ANSI_CYAN = "\u001B[36m";
	public static final String ANSI_WHITE = "\u001B[37m";
	
	public static final String ANSI_BLACK_BG = "\u001B[40m";
	public static final String ANSI_RED_BG = "\u001B[41m";
	public static final String ANSI_GREEN_BG = "\u001B[42m";
	public static final String ANSI_YELLOW_BG = "\u001B[43m";
	public static final String ANSI_BLUE_BG = "\u001B[44m";
	public static final String ANSI_PURPLE_BG = "\u001B[45m";
	public static final String ANSI_CYAN_BG = "\u001B[46m";
	public static final String ANSI_WHITE_BG = "\u001B[47m";

	FileFacade ff;
	FileBean fb;
	MetadataParser mp;
	MetadataBean mb;
	String beanAnswer;
	Hasher hasher;
	Scanner scanner;
	
	Path currentDiaryPath;
	String key;
	
	int year,month,day;
	
	void start() {
		System.out.println(ANSI_CYAN + "Avvio della CLI..." + ANSI_RESET);
		
		ff = new FileFacade();
		System.out.println("Facade File OK");
		fb = new FileBean();
		System.out.println("Bean File OK");
		mp = new MetadataParser();
		mp.ff = ff;
		System.out.println("Parser metadati OK");
		mb = new MetadataBean();
		System.out.println("Bean metadati OK");
		hasher = new Hasher();
		System.out.println("Generatore hash OK");
		scanner = new Scanner(System.in);
		System.out.println("Scanner Input OK");
		int choice = -1;
		
		
		while(true) {
			System.out.println(ANSI_CYAN_BG + ANSI_BLACK + "   t e r a j o u r n a l   " + ANSI_RESET);
			System.out.println(ANSI_CYAN + "Benvenuto/a! Inserisci il numero corrispondente all'opzione che vuoi scegliere." + ANSI_RESET);
			
			choice = getChoice("Apri un diario esistente", "Crea un nuovo diario");
			System.out.println("Input: " + choice);
			
			if(choice == 1) {
				diaryPicker();
			}else if (choice == 2) {
				diaryCreator();
			}else {
				System.out.println(ANSI_YELLOW + "Opzione invalida!" + ANSI_RESET);
			}
		}
	}
	
	//"schermate" --------------------------------------------------------------------------------------
	void diaryCreator() {
		System.out.println(ANSI_CYAN_BG + ANSI_BLACK + "   Creazione Diario   " + ANSI_RESET);
		System.out.println(ANSI_CYAN + "Nome: " + ANSI_RESET);
		String name = scanner.nextLine();
		String password;
		while(true) {
			System.out.println(ANSI_CYAN + "Password: " + ANSI_RESET);
			password = scanner.nextLine();
			System.out.println(ANSI_CYAN + "Conferma Password: " + ANSI_RESET);
			String cpassword = scanner.nextLine();
			if(password.equals(cpassword)) break;
			else System.out.println(ANSI_YELLOW + "Le password non combaciano!" + ANSI_RESET);
		}
		System.out.println(ANSI_CYAN + "Percorso: " + ANSI_RESET);
		String newPath = scanner.nextLine();
		createDiary(name, password, newPath);
	}
	
	void diaryPicker() {
		System.out.println(ANSI_CYAN_BG + ANSI_BLACK + "   Seleziona il diario...   " + ANSI_RESET);
		
		List<String> diaries = mp.getFieldNames("diaryList");
		int choice = getChoice(diaries.toArray(new String[0])); //mando la lista di stringhe come argomenti
		setDiary(diaries.get(choice));
		passwordPrompt();
		datePrompt();
		editPage();
	}
	
	void passwordPrompt() {
		//impacchetto bean e recupero risposta
		mb.setFieldName("pwdHash"); //cerco il field pwdHash nel file nel path
		mb.setPath(currentDiaryPath.toString());
		beanAnswer = mp.getFieldBean(mb).getFieldData();
		//-------------------
		if(beanAnswer.equals("notFound")) { //se non c'è password, apro direttamente il calendario
			System.out.println(ANSI_GREEN + "Il diario non ha password." + ANSI_RESET);
		}else {
			while(true) { //controllo password
				System.out.println(ANSI_CYAN_BG + ANSI_BLACK + "   Inserisci la password per il diario.   "+ ANSI_RESET);
				String clinput = scanner.nextLine();
				if(checkPassword(clinput) == 1) {
					break;
				}
			}
		}
	}
	
	void datePrompt() {
		System.out.println(ANSI_CYAN_BG + ANSI_BLACK + "   Inserisci la data.   "+ ANSI_RESET);
		System.out.println(ANSI_CYAN + "Anno:" + ANSI_RESET);
		year = getNumberChoice(0,0,false);
		System.out.println(ANSI_CYAN + "Mese :" + ANSI_RESET);
		month = getNumberChoice(1,12,true);
		System.out.println(ANSI_CYAN + "Giorno:" + ANSI_RESET);
		day = getNumberChoice(1,LocalDate.of(year, month, 1).lengthOfMonth(),true);
	}
	
	void editPage() {
		String dateString = File.separator + year + File.separator + month + File.separator + day; 
		String diaryFolder = currentDiaryPath.getParent().toString();
		System.out.println(ANSI_CYAN_BG + ANSI_BLACK + dateString + ANSI_RESET);
		
		//carico i dati dal file sull'editor
		//caricamento dati nel bean ---------
		FileBean fb = new FileBean();
		fb.setPath(diaryFolder + dateString + ".html");
		fb.setKey(key);
		fb = ff.loadAndDecryptBean(fb, true);
		//------------------------------
		String data = fb.getData();
		
		while(true) {
			System.out.println(ANSI_CYAN + "Inserisci una riga da aggiungere alla pagina." + ANSI_RESET);
			System.out.println(ANSI_CYAN + "(Inserisci una riga vuota per uscire)" + ANSI_RESET);
			String clinput = scanner.nextLine();
			if(clinput.equals("")) break;
			fb.setData(data + clinput);
			ff.encryptAndSaveBean(fb, false, true);
		}
		
	}
	//----------------------------------------------------------------st---------------------------------
	
	public void createDiary(String name, String password, String path) {	
		
		String metadataFilePath = path + File.separator + name + File.separator + name + ".jm";
		
		//impacchettamento fileBean per creare directory e file metadati ------
		FileBean fb = new FileBean();
		fb.setPath(metadataFilePath);
		fb.setKey(null);
		fb.setData("");
		//--------------------------------
		
		//metadataBean per modificare il file metadati ------
		MetadataBean mb = new MetadataBean();
		//--------------------------------
		
		if(ff.encryptAndSaveBean(fb, false, false) == 1) { //creo directory e file metadati per il diario
			System.out.println("Creando diario...");
			
			//aggiungo il diario alla lista dei diari
			System.out.println("aggiungo il diario alla lista dei diari");
			mb.setPath("diaryList");
			mb.setFieldName(name);
			mb.setFieldData(metadataFilePath);
			mp.setFieldBean(mb);
			
			//adesso opero sul file metadati del diario
			mb.setPath(metadataFilePath);
			
			//riempio metadati
			mb.setFieldName("name");
			mb.setFieldData(name);
			mp.setFieldBean(mb);
			
			mb.setFieldName("folder");
			mb.setFieldData(path + "/" + name);
			mp.setFieldBean(mb);
			
			if(password.equals("")) { //Se non inserisco una password, il diario non avrà password.
				mb.setFieldName("pwdHash");
				mb.setFieldData("");
				mp.setFieldBean(mb);
			} else { //Se viene inserita una password, salvo l'hash
				mb.setFieldName("pwdHash");
				mb.setFieldData(hasher.getHash(password, "SHA-256"));
				mp.setFieldBean(mb);
				}
			
			//se e' tutto andato a buon fine, inizializzo e apro il calendario
			if(!password.equals("")) {
				
				key = hasher.getHash(password, "MD5");
				} //uso l'hash MD5 come key per decifrare. l'altro hash serve a farti entrare
			System.out.println("Diario creato!");
			
		}else {System.out.println("Diario NON creato.");}
	}
	
	
	
	int checkPassword(String password){
		String hash = hasher.getHash(password, "SHA-256"); //hash della password inserita
		mb.setFieldName("pwdHash");
		mb.setPath(currentDiaryPath.toString());
		String pwdHash = mp.getFieldBean(mb).getFieldData(); //hash preso dal file, da confrontare a hash
		
		if(hash.equals(pwdHash)) { //se gli hash combaciano, la password inserita e' corretta
			if(!password.equals("")) {key = hasher.getHash(password, "MD5");} //un hash diverso come key, non quello immagazzinato
			System.out.println(ANSI_GREEN + "Password corretta!" + ANSI_RESET);
			return 1;
		}else {
			System.out.println(ANSI_YELLOW + "Password errata!" + ANSI_RESET);
			return 0;
		}
	}
	
	
	int getNumberChoice(int min, int max, Boolean useLimits) {
		if(useLimits) System.out.println("Inserisci un numero da " + min + " a " + max);
		else System.out.println("Inserisci un numero.");
		int clinput = Integer.parseInt(scanner.nextLine());
		if(useLimits && (clinput < min || clinput > max)) { //numero fuori dai limiti
			System.out.println("Scelta invalida!");
			return getNumberChoice(min, max, useLimits); //rifai
		}else {
			return clinput;
		}
	}
	
	int getChoice(String... n) {
		int argc = 0;
		for(String i : n) {
			if(i.equals("")) continue; //passo alla prossima iterazione se l'opzione è vuota
			argc++;
			System.out.println(ANSI_CYAN + argc + ". " + i + ANSI_RESET);
		}
		System.out.println(ANSI_CYAN + "Cosa vuoi fare?" + ANSI_RESET);
		
		int clinput = Integer.parseInt(scanner.nextLine());
		if(clinput < 1 || clinput > argc) clinput = -1;
		
		return clinput;
	}
	
	void setDiary(String selection) {
		//Impacchettamento bean da mandare a getField
		mb.setFieldName(selection);
		mb.setPath("diaryList");
		mb = mp.getFieldBean(mb); //faccio inserire il fieldData corrispondente, da MetadataParser
		//-------------------------------------------
		
		currentDiaryPath = Paths.get(mb.getFieldData());
		System.out.printf("path: %s, cartella contenitore: %s\n", currentDiaryPath, currentDiaryPath.getParent());
	}
	
	void shutdown() {
		scanner.close();
	}
}


