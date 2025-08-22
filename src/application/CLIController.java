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
	HasherBean hb;
	Scanner scanner;
	
	Path currentDiaryPath;
	String key;
	
	int year;
	int month;
	int day;
	
	//volute da sonarcloud. rende facile cambiare il nome dei field
	static String PWDHASH = "pwdHash";
	static String DIARYLIST = "diaryList";
		
	
	void start() {
		System.out.println(ANSI_CYAN + "Avvio della CLI..." + ANSI_RESET);
		
		ff = new FileFacade();
		System.out.println("FileFacade OK");
		fb = new FileBean();
		System.out.println("FileBean OK");
		mp = new MetadataParser();
		mp.ff = ff;
		System.out.println("MetadataParser OK");
		mb = new MetadataBean();
		System.out.println("MetadataBean OK");
		hasher = new Hasher();
		System.out.println("Hasher OK");
		hb = new HasherBean();
		System.out.println("HasherBean OK");
		scanner = new Scanner(System.in);
		System.out.println("Scanner Input OK");
		int choice = -1;
		
		
		while(true) {
			System.out.println(ANSI_CYAN_BG + ANSI_BLACK + "   t e r a j o u r n a l   " + ANSI_RESET);
			System.out.println(ANSI_CYAN + "Benvenuto/a! Inserisci il numero corrispondente all'opzione che vuoi scegliere." + ANSI_RESET);
			
			choice = getChoice("Apri un diario esistente", "Crea un nuovo diario", "Esci");
			System.out.println("Input: " + choice);
			
			if(choice == 1) {
				diaryPicker();
			}else if (choice == 2) {
				diaryCreator();
			}else if (choice == 3) {
				break;
			}else {
				System.out.println(ANSI_YELLOW + "Opzione invalida!" + ANSI_RESET);
			}
		}
		System.exit(0);
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
		
		List<String> diaries = mp.getFieldNames(DIARYLIST);
		int choice = getChoice(diaries.toArray(new String[0])); //mando la lista di stringhe come argomenti
		setDiary(diaries.get(choice));
		passwordPrompt();
		datePrompt();
		editPage();
	}
	
	void passwordPrompt() {
		//impacchetto bean e recupero risposta dal metadataParser
		mb.setFieldName(PWDHASH); //cerco il field pwdHash nel file nel path
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
		String filePath = currentDiaryPath.getParent().toString() + dateString + ".html";
		System.out.println(ANSI_CYAN_BG + ANSI_BLACK + dateString + ANSI_RESET);
		
		//se il file non esiste, lo creo
		if(ff.checkForFile(filePath) == false) {
			System.out.println("la pagina non esiste, la creo.");
				FileBean fb = new FileBean();
				fb.setPath(filePath);
				fb.setKey(null); //creo il file vuoto, non c'e' bisogno di cifrare
				fb.setData("");
			ff.encryptAndSaveBean(fb, false, false);
		}
		
		//carico i dati dal file sull'editor
		//caricamento dati nel bean ---------
		fb = new FileBean();
		fb.setPath(filePath);
		fb.setKey(key);
		fb = ff.loadAndDecryptBean(fb, true);
		//------------------------------
		
		while(true) {
			String data = fb.getData();
			System.out.println(ANSI_CYAN_BG + ANSI_BLACK + "Inserisci una riga da aggiungere alla pagina." + ANSI_RESET);
			System.out.println(ANSI_CYAN + "(Inserisci una riga vuota per uscire)" + ANSI_RESET);
			String clinput = scanner.nextLine();
			if(clinput.equals("")) break; //se si inserisce una riga vuota, torna alla schermata iniziale
			fb.setData(data + clinput + "<br>" + System.lineSeparator()); //br per l'html. lineseparator per rendere il file piu' ordinato
			ff.encryptAndSaveBean(fb, false, true);
		}
		
	}
	//----------------------------------------------------------------st---------------------------------
	
	public void createDiary(String name, String password, String path) {	
		
		String metadataFilePath = path + File.separator + name + File.separator + name + ".jm";
		
		//impacchettamento fileBean per creare directory e file metadati ------
		fb = new FileBean();
		fb.setPath(metadataFilePath);
		fb.setKey(null);
		fb.setData("");
		//--------------------------------
		
		
		if(ff.encryptAndSaveBean(fb, false, false) == 1) { //creo directory e file metadati per il diario
			System.out.println("Creando diario...");
			
			//aggiungo il diario alla lista dei diari
			System.out.println("aggiungo il diario alla lista dei diari");
				mb.setPath(DIARYLIST);
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
					mb.setFieldName(PWDHASH);
					mb.setFieldData("");
				mp.setFieldBean(mb);
			} else { //Se viene inserita una password, salvo l'hash
				mb.setFieldName(PWDHASH);
					hb.setString(password);
					hb.setAlgorithm("SHA-256");
				mb.setFieldData(hasher.getHashBean(hb).getString());
				mp.setFieldBean(mb);
				}
			
			//se e' tutto andato a buon fine, inizializzo e apro il calendario
			if(!password.equals("")) {
					hb.setString(password);
					hb.setAlgorithm("MD5");
				key = hasher.getHashBean(hb).getString();
				} //uso l'hash MD5 come key per decifrare. l'altro hash serve a farti entrare
			System.out.println("Diario creato!");
			
		}else {System.out.println("Diario NON creato.");}
	}
	
	
	
	int checkPassword(String password){
			hb.setString(password);
			hb.setAlgorithm("SHA-256");
		String hash = hasher.getHashBean(hb).getString(); //hash della password inserita
			mb.setFieldName(PWDHASH);
			mb.setPath(currentDiaryPath.toString());
		String pwdHash = mp.getFieldBean(mb).getFieldData(); //hash preso dal file, da confrontare a hash
		
		if(hash.equals(pwdHash)) { //se gli hash combaciano, la password inserita e' corretta
				hb.setString(password);
				hb.setAlgorithm("MD5");
			if(!password.equals("")) {key = hasher.getHashBean(hb).getString();} //un hash diverso come key, non quello immagazzinato
			System.out.println(ANSI_GREEN + "Password corretta!" + ANSI_RESET);
			return 1;
		}else {
			System.out.println(ANSI_YELLOW + "Password errata!" + ANSI_RESET);
			return 0;
		}
	}
	
	
	int getNumberChoice(int min, int max, Boolean useLimits) {
		if(Boolean.TRUE.equals(useLimits)) System.out.println("Inserisci un numero da " + min + " a " + max);
		else System.out.println("Inserisci un numero.");
		int clinput = Integer.parseInt(scanner.nextLine());
		if(Boolean.TRUE.equals(useLimits) && (clinput < min || clinput > max)) { //numero fuori dai limiti
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
		mb.setPath(DIARYLIST);
		mb = mp.getFieldBean(mb); //faccio inserire il fieldData corrispondente, da MetadataParser
		//-------------------------------------------
		
		currentDiaryPath = Paths.get(mb.getFieldData());
		System.out.printf("path: %s, cartella contenitore: %s%n", currentDiaryPath, currentDiaryPath.getParent());
	}
	
	void shutdown() {
		scanner.close();
	}
}


