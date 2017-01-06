/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mesclasses.util;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Map;
import mesclasses.model.Constants;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zeroturnaround.zip.ZipUtil;

/**
 *
 * @author rrrt3491
 */
public class FileSaveUtil {
    
    private static final Logger LOG = LogManager.getLogger(FileSaveUtil.class);
    
    public static String SAVE_DIR = "savedData";
    public static String BCK_DIR = SAVE_DIR+File.separator+"bck";
    public static String ARCHIVE_DIR = "archives";
    public static String PROPERTIES_DIR = SAVE_DIR+File.separator+"properties";
    public static String FILE_DIR = SAVE_DIR+File.separator+"uploadedFiles";
    public static String SAVE_FILE = "mesClassesData";
    
    static {
        reset();
        new File(SAVE_DIR).mkdirs();
        new File(ARCHIVE_DIR).mkdirs();
        new File(FILE_DIR).mkdirs();
        new File(BCK_DIR).mkdirs();
        new File(PROPERTIES_DIR).mkdirs();
    }
    
    public static void reset(){
        SAVE_DIR = "savedData";
        SAVE_FILE = "mesClassesData";
        BCK_DIR = SAVE_DIR+File.separator+"bck";
        ARCHIVE_DIR = "archives";
        PROPERTIES_DIR = SAVE_DIR+File.separator+"properties";
        FILE_DIR = SAVE_DIR+File.separator+"uploadedFiles";
    }
    
    public static void set(String saveDir, String saveFile){
        try {
            SAVE_DIR = saveDir;
            SAVE_FILE = saveFile;
            BCK_DIR = SAVE_DIR+File.separator+"bck";
            ARCHIVE_DIR = "archives";
            PROPERTIES_DIR = SAVE_DIR+File.separator+"properties";
            FILE_DIR = SAVE_DIR+File.separator+"uploadedFiles";
            new File(SAVE_DIR).mkdirs();
            new File(ARCHIVE_DIR).mkdirs();
            new File(FILE_DIR).mkdirs();
            new File(BCK_DIR).mkdirs();
            new File(PROPERTIES_DIR).mkdirs();
        } catch(Exception e){
            LOG.error("Impossible de charger la configuration demandée : ",e);
        }
    }
    
    public static Map<String, String> getConf(){
        return ImmutableMap.<String, String>builder()
            .put("SAVE_DIR", SAVE_DIR) 
            .put("SAVE_FILE", SAVE_FILE) 
            .put("BCK_DIR", BCK_DIR) 
            .put("ARCHIVE_DIR", ARCHIVE_DIR) 
            .put("PROPERTIES_DIR", PROPERTIES_DIR) 
            .put("FILE_DIR", FILE_DIR) 
            .build();
    }
    
    public static String displayConf(){
        return Joiner.on("\n").withKeyValueSeparator("=").join(getConf());
    }
    
    public static File archive() throws IOException {
        File archiveFile = new File(new StringBuilder(ARCHIVE_DIR)
            .append(File.separator)
            .append("mesclasses_archive_")
            .append(LocalDate.now().format(Constants.DATE_FORMATTER))
            .append(".zip")
            .toString());
        if(archiveFile.exists()){
            archiveFile.delete();
        }
        ZipUtil.pack(new File(SAVE_DIR), archiveFile);
        LOG.info("Archive créée : "+archiveFile.getPath());
        return archiveFile;
    }
    
    public static void restoreArchive(File archiveFile) throws IOException {
        if(!ZipUtil.containsEntry(archiveFile, SAVE_FILE+".xml")){
            AppLogger.notif("Archivage : ", "Le fichier "+archiveFile.getName()+" n'est pas une archive valide");
            return;
        }
        FileUtils.cleanDirectory(new File(SAVE_DIR));
        ZipUtil.unpack(archiveFile, new File(SAVE_DIR));
    }
    
    public static File getSaveFile(){
        return new File(SAVE_DIR+File.separator+SAVE_FILE+".xml");
    }
    public static boolean saveFileExists(){
        return getSaveFile().exists();
    }
    public static File createNewSaveFile(File file){
        try {
            file.createNewFile();
            return file;
        } catch (IOException ex) {
            LOG.error("Le fichier "+file.getPath()+" ne peut pas être créé");
            return null;
        }
    }

    public static File createBackupFile() {
        if(!saveFileExists()){
            return null;
        }
        File bckFile = new File(BCK_DIR+File.separator+SAVE_FILE+"_"+LocalDate.now().format(Constants.DATE_FORMATTER)+".xml");
        try {
            if(bckFile.exists()){
                bckFile.delete();
            }
            FileUtils.copyFile(getSaveFile(), bckFile);
            LOG.info("Fichier de sauvegarde "+bckFile.getName()+" créé");
        } catch(Exception e){
            AppLogger.notif("Impossible de créer le fichier de sauvegarde", e);
        }
        File[] bckFiles = new File(BCK_DIR).listFiles();
        if(bckFiles.length >=8){
            bckFiles[0].delete();
        }
        return bckFile;
    }
    
    public static void restoreBackupFile(File file){
        if(!file.exists()){
            AppLogger.notif("Chargement impossible", "le fichier "+file.getName()+" n'existe pas");
            return;
        }
        File currentFile = getSaveFile();
        File tmpFile = new File(SAVE_DIR+File.separator+SAVE_FILE+"tmp.xml");
        try {
            FileUtils.copyFile(currentFile, tmpFile);
        } catch (IOException ex) {
            AppLogger.notif("Impossible de charger le fichier de sauvegarde", ex);
            return;
        }
        try {
            if(currentFile.exists()){
                LOG.info("deleting "+currentFile.getName());
                currentFile.delete();
            }
            LOG.info("copying "+file.getName()+" to "+currentFile.getName());
            FileUtils.copyFile(file, currentFile);
            LOG.info("Fichier de sauvegarde "+file.getName()+" chargé");
        } catch(Exception e){
            AppLogger.notif("Impossible de charger le fichier de sauvegarde", e);
            if(currentFile.exists()){
                currentFile.delete();
            }
            try {
                FileUtils.copyFile(tmpFile, currentFile);
            } catch (IOException ex) {
                AppLogger.notif("Impossible de restaurer le fichier temporaire", e);
            }
        } finally {
            tmpFile.delete();
        }
    }
}
