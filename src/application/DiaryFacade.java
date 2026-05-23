package application;

import application.bean.DiaryBean;

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

    void switchDiary(String diaryName, String key){
        currentDiaryPath = mp.getField(diaryName, DIARYLIST);

        Path p = Paths.get(currentDiaryPath).getParent();
        if(p != null){
            currentDiaryFolder = p.toString(); //prendo la directory parente
        } else {currentDiaryFolder = "";}

        currentKey = key;
    }

    void setKey(String k){
        currentKey = k;
    }

    //creazione di un nuovo diario TODO debeanizza
    public void createDiary(String name, String path, String password, String confirmPassword) {
        printStatus();
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

            //impacchettamento fileBean per creare directory e file metadati ------
            FileBean fb = new FileBean();
            fb.setPath(metadataFilePath);
            fb.setKey(null);
            fb.setData("");
            //--------------------------------

            MetadataBean mb = new MetadataBean();
            HasherBean hb = new HasherBean();

            if(ff.encryptAndSaveBean(fb, false, false) == 1) { //creo directory e file metadati per il diario
                logger.log(Level.INFO, "Diario creato.");

                //aggiungo il diario alla lista dei diari
                mb.setPath("diaryList");
                mb.setFieldName(name);
                mb.setFieldData(metadataFilePath);
                mp.setFieldBean(mb);
                logger.log(Level.INFO, "Diario aggiunto alla lista dei diari");

                //adesso opero sul file metadati del diario
                mb.setPath(metadataFilePath);

                //riempio metadati
                mb.setFieldName("name");
                mb.setFieldData(name);
                mp.setFieldBean(mb);

                mb.setFieldName("folder");
                mb.setFieldData(path + File.separator + name);
                mp.setFieldBean(mb);

                if(password.equals("")) { //Se non inserisco una password, il diario non avrà password.
                    mb.setFieldName("pwdHash");
                    mb.setFieldData("");
                    mp.setFieldBean(mb);
                } else { //Se viene inserita una password, salvo l'hash
                    mb.setFieldName("pwdHash");
                    hb.setString(password);
                    hb.setAlgorithm("SHA-256");
                    mb.setFieldData(hasher.getHashBean(hb).getString());
                    mp.setFieldBean(mb);
                }

                //se e' tutto andato a buon fine, inizializzo e apro il calendario
                //uso l'hash MD5 come key per decifrare. l'altro hash serve a farti entrare
                if(!password.equals("")) {
                    hb.setString(password);
                    hb.setAlgorithm("MD5");
                    switchDiary(name, hasher.getHashBean(hb).getString());
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
        fb.setKey(currentKey);
        String p = currentDiaryFolder + fb.getPath();
        fb.setPath(p);
        logger.log(Level.INFO, "Salvo la pagina in: " + p);
        ff.encryptAndSaveBean(fb, false, true);
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
        String n = "";
        String p = "";
        String h = "";
        DiaryBean b = new DiaryBean();

        if(!name.equals("")) {
            n = name;
            p = mp.getField(name, DIARYLIST);
        } else { //se l'argomento è vuoto restituisco il diario attuale
            n = mp.getField("name", currentDiaryPath);
            p = currentDiaryPath;
        }
        h = mp.getField("pwdHash", p);

        b.setName(mp.getField("name", p));
        b.setFolder(mp.getField("folder", p));
        b.setPwdHash(mp.getField("pwdHash", p));
        return b;
    }

    Boolean checkPassword(String password){
        String hash = hasher.getHash(password, "SHA-256");
        String storedHash = mp.getField("pwdHash", currentDiaryPath);
        return hash.equals(storedHash); //se gli hash combaciano, la password inserita e' corretta
    }

    String generateKey(String password){ //genera key per cifrare/decifrare le pagine di diario
        if(!password.equals("")) {
            return hasher.getHash(password, "MD5");
        }else{return "";}
    }

    Boolean isPageWritten(int year, int month, int day){
        List<Integer> l; //TODO vedi se il path qua è quello della cartella o quello del jm
        return ff.checkForFile(currentDiaryFolder + File.separator + year + File.separator + month + File.separator + day + ".html");
    }

    void printStatus(){
        System.out.println("currentDiaryPath: " + currentDiaryPath);
        System.out.println("currentKey: " + currentKey);
    }
}

