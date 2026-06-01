package application;

import application.bean.*;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DiaryFacade {

    public static final String DIARYLIST = "diaryList";

    FileFacade ff = new FileFacade(); //questo fileFacade deve essere l'unica istanza di fileFacade, altrimenti in modalità demo verranno creati più DB separati
    MetadataParser mp = new MetadataParser();
    Hasher hasher = new Hasher();

    Logger logger = Logger.getLogger("diaryFacade");

    public DiaryFacade() {
        mp.setFF(ff);
    }

    public DiaryBean createDiaryBean(CreateDiaryBean cd){
        String k = createDiary(cd.getName(), cd.getPath(), cd.getPassword(), cd.getConfirmPassword());
        DiaryBean db = new DiaryBean();
        db.setKey(k);
        return db; //restituisco la chiave attraverso un diaryBean
    }

    //creazione di un nuovo diario
    public String createDiary(String name, String path, String password, String confirmPassword) {
        try {
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

                //uso l'hash MD5 come key per decifrare. l'altro hash serve a farti entrare
                if(!password.equals("")) {
                    return hasher.getHash(password, "MD5");
                } else {
                    return "";
                }


            }else {logger.log(Level.INFO, "Diario NON creato.");}
        }catch(IllegalArgumentException e) {
            logger.log(Level.SEVERE, "Errore nell'impostazione di un bean");
            e.printStackTrace();
        }
        return ""; //TODO temporaneo, leva sti return provenienti da filefacade e usa eccezioni
    }

    public void savePage(FileBean fb) {
        //combina i dati in arrivo dalla view con quelli che conosce questa classe (key e path del diario attuale)
        String p = fb.getPath();
        logger.log(Level.INFO, "Salvo la pagina in: " + p);
        ff.encryptAndSave(p, fb.getData(), fb.getKey(), true, true);
    }

    public FileBean loadPageBean(FileBean fb) throws Exception {
        return loadPage(fb.getPath(),fb.getKey());
    }

    FileBean loadPage(String path, String key) throws Exception {
        FileBean fb = new FileBean();
        fb.setData(ff.loadAndDecrypt(path, key, true));
        fb.setPath(path);
        fb.setKey(key);
        return fb;
    }

    public void checkForDiaryList(){
        if(Boolean.FALSE.equals(ff.checkForFile(DIARYLIST))) {
            logger.log(Level.INFO, "diaryList non esiste, lo creo.");
            ff.encryptAndSave("diaryList", "", null, true, false);
        }
    }

    public List<String> getDiaryNames(){
        return mp.getFieldNames(DIARYLIST);
    }

    public DiaryBean getDiaryMetadata(String name){
        String p = mp.getField(name, DIARYLIST);
        DiaryBean b = new DiaryBean();

        b.setName(mp.getField("name", p));
        b.setFolder(mp.getField("folder", p));
        b.setPwdHash(mp.getField("pwdHash", p));
        return b;
    }

    public Boolean checkPasswordBean(PasswordBean pb, FilePathBean fp){
        return checkPassword(fp.getPath(), pb.getPassword());
    }

    Boolean checkPassword(String path, String password){
        String hash = hasher.getHash(password, "SHA-256");
        String storedHash = mp.getField("pwdHash", path);
        return hash.equals(storedHash); //se gli hash combaciano, la password inserita e' corretta
    }

    public String generateKeyBean(PasswordBean pb){
        return generateKey(pb.getPassword()); //TODO neamozza pure la key?
    }

    String generateKey(String password){ //genera key per cifrare/decifrare le pagine di diario
        if(!password.equals("")) {
            return hasher.getHash(password, "MD5");
        }else{return "";}
    }

    public Boolean isPageWrittenBean(DateBean db, FilePathBean fp){
        return isPageWritten(db.getYear(), db.getMonth(), db.getDay(), fp.getPath());
    }

    Boolean isPageWritten(int year, int month, int day, String folder){
        return ff.checkForFile(folder + File.separator + year + File.separator + month + File.separator + day + ".html");
    }

}

