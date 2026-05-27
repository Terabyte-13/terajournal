package application;

import application.bean.CreateDiaryBean;
import application.bean.DateBean;
import application.bean.DiaryBean;
import application.bean.PasswordBean;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DiaryFacade {

    public static final String DIARYLIST = "diaryList";

    String currentKey = "";
    String currentDiaryPath = ""; //path del file metadati
    String currentDiaryFolder; //path della cartella

    FileFacade ff = new FileFacade(); //questo fileFacade deve essere l'unica istanza di fileFacade, altrimenti in modalità demo verranno creati più DB separati
    MetadataParser mp = new MetadataParser();
    Hasher hasher = new Hasher();

    Logger logger = Logger.getLogger("diaryFacade");

    DiaryFacade() {
        mp.setFF(ff);
    }

    void switchDiaryBean(DiaryBean db){
        switchDiary(db.getName(), db.getKey());
    }

    void switchDiary(String diaryName, String key){
        currentDiaryPath = mp.getField(diaryName, DIARYLIST);

        Path p = Paths.get(currentDiaryPath).getParent();
        if(p != null){
            currentDiaryFolder = p.toString(); //prendo la directory parente
        } else {currentDiaryFolder = "";}

        currentKey = key;
    }

    void setKeyBean(DiaryBean db){
        currentKey = db.getKey();
    }

    void setKey(String k){
        currentKey = k;
    }

    public void createDiaryBean(CreateDiaryBean cd){
        createDiary(cd.getName(), cd.getPath(), cd.getPassword(), cd.getConfirmPassword());
    }

    //creazione di un nuovo diario
    public void createDiary(String name, String path, String password, String confirmPassword) {
        printStatus(); //TODO leva
        try {
            if(name.equals("") || path.equals("")) {
                logger.log(Level.INFO, "Ci sono dei campi vuoti.");
                return;
            }
            if(!password.equals(confirmPassword)) {
                logger.log(Level.INFO, "password e confirmPassword non combaciano.");
                return;
            }

            String metadataFilePath = path + File.separator + name + File.separator + name + ".jm";

            if(ff.encryptAndSave(metadataFilePath, "", null, false, false) == 1) { //creo directory e file metadati per il diario
                logger.log(Level.INFO, "Diario creato.");

                //aggiungo il diario alla lista dei diari
                mp.setField(name, "diaryList", metadataFilePath);
                logger.log(Level.INFO, "Diario aggiunto alla lista dei diari");

                //riempio metadati
                mp.setField("name", metadataFilePath, name);
                mp.setField("folder", metadataFilePath, path + File.separator + name);

                if(password.equals("")) { //Se non inserisco una password, il diario non avrà password.
                    mp.setField("pwdHash", metadataFilePath, "");
                } else { //Se viene inserita una password, salvo l'hash
                    mp.setField("pwdHash", metadataFilePath, hasher.getHash(password, "SHA-256"));
                }

                //se e' tutto andato a buon fine, inizializzo e apro il calendario
                //uso l'hash MD5 come key per decifrare. l'altro hash serve a farti entrare
                if(!password.equals("")) {
                    switchDiary(name, hasher.getHash(password, "MD5"));
                } else {
                    switchDiary(name, "");
                }


            }else {logger.log(Level.INFO, "Diario NON creato.");}
        }catch(IllegalArgumentException e) {
            logger.log(Level.SEVERE, "Errore nell'impostazione di un bean");
            e.printStackTrace();
        }

    }

    public void savePage(FileBean fb) {
        //combina i dati in arrivo dalla view con quelli che conosce questa classe (key e path del diario attuale)
        String p = currentDiaryFolder + fb.getPath();
        logger.log(Level.INFO, "Salvo la pagina in: " + p);
        ff.encryptAndSave(p, fb.getData(), currentKey, false, true);
    }

    FileBean loadPageBean(DateBean db){
        return loadPage(db.getYear(), db.getMonth(), db.getDay());
    }

    FileBean loadPage(int year, int month, int day){
        String p = currentDiaryFolder + File.separator + year + File.separator + month + File.separator + day + ".html";
        FileBean fb = new FileBean();
        fb.setData(ff.loadAndDecrypt(p, currentKey, true));
        fb.setPath(p);
        return fb;
    }

    void checkForDiaryList(){
        if(Boolean.FALSE.equals(ff.checkForFile(DIARYLIST))) {
            logger.log(Level.INFO, "diaryList non esiste, lo creo.");
            ff.encryptAndSave("diaryList", "", null, false, false);
        }
    }

    List<String> getDiaryNames(){
        return mp.getFieldNames(DIARYLIST);
    }

    DiaryBean getDiaryMetadata(String name){
        String p = "";
        DiaryBean b = new DiaryBean();

        if(!name.equals("")) {
            p = mp.getField(name, DIARYLIST);
        } else { //se l'argomento è vuoto restituisco i metadati del diario attuale
            p = currentDiaryPath;
        }

        b.setName(mp.getField("name", p));
        b.setFolder(mp.getField("folder", p));
        b.setPwdHash(mp.getField("pwdHash", p));
        return b;
    }

    Boolean checkPasswordBean(PasswordBean pb){
        return checkPassword(pb.getPassword());
    }

    Boolean checkPassword(String password){
        String hash = hasher.getHash(password, "SHA-256");
        String storedHash = mp.getField("pwdHash", currentDiaryPath);
        return hash.equals(storedHash); //se gli hash combaciano, la password inserita e' corretta
    }

    String generateKeyBean(PasswordBean pb){
        return generateKey(pb.getPassword()); //TODO neamozza pure la key?
    }

    String generateKey(String password){ //genera key per cifrare/decifrare le pagine di diario
        if(!password.equals("")) {
            return hasher.getHash(password, "MD5");
        }else{return "";}
    }

    Boolean isPageWrittenBean(DateBean db){
        return isPageWritten(db.getYear(), db.getMonth(), db.getDay());
    }

    Boolean isPageWritten(int year, int month, int day){
        return ff.checkForFile(currentDiaryFolder + File.separator + year + File.separator + month + File.separator + day + ".html");
    }

    void printStatus(){
        System.out.println("currentDiaryPath: " + currentDiaryPath);
        System.out.println("currentKey: " + currentKey);
    }
}

