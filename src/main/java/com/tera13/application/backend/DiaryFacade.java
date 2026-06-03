package com.tera13.application.backend;

import com.tera13.application.backend.file.FileFacade;
import com.tera13.application.backend.file.Hasher;
import com.tera13.application.bean.*;
import com.tera13.application.exception.CreateDiaryException;
import com.tera13.application.exception.FileFacadeException;
import com.tera13.application.exception.MetadataParserException;

import java.io.File;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DiaryFacade {

    public static final String DIARYLIST = "diaryList";

    public static final String PWDHASH = "pwdHash";

    FileFacade ff = new FileFacade(); //questo fileFacade deve essere l'unica istanza di fileFacade, altrimenti in modalità demo verranno creati più DB separati
    MetadataParser mp = new MetadataParser();
    Hasher hasher = new Hasher();

    Logger logger = Logger.getLogger("diaryFacade");

    public DiaryFacade() {
        mp.setFF(ff);
    }

    public DiaryBean createDiaryBean(CreateDiaryBean cd) throws CreateDiaryException{
        String k = createDiary(cd.getName(), cd.getPath(), cd.getPassword());
        DiaryBean db = new DiaryBean();
        db.setKey(k);
        return db; //restituisco la chiave attraverso un diaryBean
    }

    //creazione di un nuovo diario
    public String createDiary(String name, String path, String password) throws CreateDiaryException {
        String metadataFilePath = path + File.separator + name + File.separator + name + ".jm";

        int s = 0;
        try {
            s = ff.encryptAndSave(metadataFilePath, "", null, false, false);
        } catch (FileFacadeException e) {
            throw new CreateDiaryException(e.getMessage(), e.getCause());
        }

        if(s == 1) { //creo directory e file metadati per il diario

            try{
                //aggiungo il diario alla lista dei diari
                mp.setField(name, DIARYLIST, metadataFilePath);
                logger.log(Level.INFO, "Diario aggiunto alla lista dei diari");

                //riempio metadati
                mp.setField("name", metadataFilePath, name);
                mp.setField("folder", metadataFilePath, path + File.separator + name);

                if(password.equals("")) { //Se non inserisco una password, il diario non avrà password.
                    mp.setField(PWDHASH, metadataFilePath, "");
                } else { //Se viene inserita una password, salvo l'hash
                    mp.setField(PWDHASH, metadataFilePath, hasher.getHash(password, "SHA-256"));
                }

                logger.log(Level.INFO, "Diario creato.");

                //uso l'hash MD5 come key per decifrare. l'altro hash serve a farti entrare
                if(!password.equals("")) {
                    return hasher.getHash(password, "MD5");
                } else {
                    return "";
                }

            }catch(MetadataParserException e){
                throw new CreateDiaryException(e.getMessage(), e.getCause());
            }

        }else {logger.log(Level.INFO, "Diario NON creato.");}
        return ""; //TODO leva?
    }

    public void savePage(FileBean fb) throws FileFacadeException {
        //combina i dati in arrivo dalla view con quelli che conosce questa classe (key e path del diario attuale)
        String p = fb.getPath();
        logger.log(Level.INFO, "Salvo la pagina in: {0}", p);
        ff.encryptAndSave(p, fb.getData(), fb.getKey(), true, true);
    }

    public FileBean loadPageBean(FileBean fb) throws FileFacadeException {
        return loadPage(fb.getPath(),fb.getKey());
    }

    FileBean loadPage(String path, String key) throws FileFacadeException {
        FileBean fb = new FileBean();
        fb.setData(ff.loadAndDecrypt(path, key, true));
        fb.setPath(path);
        fb.setKey(key);
        return fb;
    }

    public void checkForDiaryList() throws FileFacadeException{
        if(Boolean.FALSE.equals(ff.checkForFile(DIARYLIST))) {
            logger.log(Level.INFO, "diaryList non esiste, lo creo.");
            ff.encryptAndSave(DIARYLIST, "", null, true, false);
        }
    }

    public List<String> getDiaryNames() throws MetadataParserException{
        return mp.getFieldNames(DIARYLIST);
    }

    public DiaryBean getDiaryMetadata(String name) throws MetadataParserException{
        String p = mp.getField(name, DIARYLIST);
        DiaryBean b = new DiaryBean();

        b.setName(mp.getField("name", p));
        b.setFolder(mp.getField("folder", p));
        b.setPwdHash(mp.getField(PWDHASH, p));
        return b;
    }

    public Boolean checkPasswordBean(PasswordBean pb, FilePathBean fp) throws MetadataParserException{
        return checkPassword(fp.getPath(), pb.getPassword());
    }

    Boolean checkPassword(String path, String password) throws MetadataParserException {
        String hash = hasher.getHash(password, "SHA-256");
        String storedHash = mp.getField(PWDHASH, path);
        return hash.equals(storedHash); //se gli hash combaciano, la password inserita e' corretta
    }

    public String generateKeyBean(PasswordBean pb){
        return generateKey(pb.getPassword());
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

