package com.tera13.application.backend;

import com.tera13.application.backend.file.FileFacade;
import com.tera13.application.backend.file.Hasher;
import com.tera13.application.bean.*;
import com.tera13.application.exception.CreateDiaryException;
import com.tera13.application.exception.FileFacadeException;
import com.tera13.application.exception.MetadataParserException;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DiaryFacade {

    public String diaryList = "diaryList";

    public static final String PWDHASH = "pwdHash";

    private static final FileFacade ff = new FileFacade(); //questo fileFacade deve essere l'unica istanza di fileFacade, altrimenti in modalità demo verranno creati più DB separati
    private static final MetadataParser mp = new MetadataParser();
    private static final Hasher hasher = new Hasher();

    Logger logger = Logger.getLogger("diaryFacade");

    public DiaryFacade() {
        mp.setFF(ff);
    }

    public void setDiaryList(LoginBean lb){
        diaryList = lb.getDiaryListPath();
    }

    public void createDiaryBean(CreateDiaryBean cd) throws CreateDiaryException{
        createDiary(cd.getName(), cd.getPath(), cd.getPassword());
    }

    //creazione di un nuovo diario
    public void createDiary(String name, String path, String password) throws CreateDiaryException {
        String metadataFilePath = path + File.separator + name + File.separator + name + ".jm";

        int s = 0;
        try {
            s = ff.encryptAndSave(metadataFilePath, "", null, false);
        } catch (FileFacadeException e) {
            throw new CreateDiaryException(e.getMessage(), e.getCause());
        }

        if(s == 1) { //creo directory e file metadati per il diario

            try{
                //aggiungo il diario alla lista dei diari
                mp.setField(name, diaryList, metadataFilePath);
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

            }catch(MetadataParserException e){
                throw new CreateDiaryException(e.getMessage(), e.getCause());
            }

        }else {logger.log(Level.INFO, "Diario NON creato.");}
    }

    public void savePage(PageBean fb) throws FileFacadeException {
        //combina i dati in arrivo dalla view con quelli che conosce questa classe (key e path del diario attuale)
        String p = fb.getPath();
        logger.log(Level.INFO, "Salvo la pagina in: {0}", p);
        ff.encryptAndSave(p, fb.getData(), fb.getKey(), true);
    }

    public PageBean loadPageBean(PageBean fb) throws FileFacadeException {
        return loadPage(fb.getPath(),fb.getKey());
    }

    PageBean loadPage(String path, String key) throws FileFacadeException {
        PageBean fb = new PageBean();
        fb.setData(ff.loadAndDecrypt(path, key, true));
        fb.setPath(path);
        fb.setKey(key);
        return fb;
    }

    public void checkForDiaryList() throws FileFacadeException{
        if(Boolean.FALSE.equals(ff.checkForFile(diaryList))) {
            logger.log(Level.INFO, "diaryList non esiste, lo creo.");
            ff.encryptAndSave(diaryList, "", null, false);
        }
    }

    public DiaryNamesBean getDiaryNames() throws MetadataParserException{
        DiaryNamesBean dn = new DiaryNamesBean();
        dn.setNames(mp.getFieldNames(diaryList));
        return dn;
    }

    public DiaryBean getDiaryMetadata(DiaryBean db) throws MetadataParserException{
        String p = mp.getField(db.getName(), diaryList);

        db.setName(mp.getField("name", p));
        db.setFolder(mp.getField("folder", p));
        db.setPwdHash(mp.getField(PWDHASH, p));
        return db;
    }

    public Boolean checkPasswordBean(PasswordBean pb, FilePathBean fp) throws MetadataParserException{
        return checkPassword(fp.getPath(), pb.getPassword());
    }

    Boolean checkPassword(String path, String password) throws MetadataParserException {
        String hash = hasher.getHash(password, "SHA-256");
        String storedHash = mp.getField(PWDHASH, path);
        return hash.equals(storedHash); //se gli hash combaciano, la password inserita e' corretta
    }

    public PasswordBean generateKeyBean(PasswordBean pb){
        String k = generateKey(pb.getPassword());
        pb.setKey(k);
        return pb;
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

