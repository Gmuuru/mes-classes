/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mesclasses.util;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import mesclasses.model.Constants;
import org.apache.commons.io.FileUtils;
import org.zeroturnaround.zip.ZipUtil;

/**
 *
 * @author rrrt3491
 */
public class FileSaveUtil {
    
    public static final String SAVEDIR = "savedData";
    public static final String BCK_DIR = SAVEDIR+File.separator+"bck";
    public static final String ARCHIVE_DIR = "archives";
    public static final String PROPERTIES_DIR = SAVEDIR+File.separator+"properties";
    public static final String FILE_DIR = SAVEDIR+File.separator+"uploadedFiles";
    public static final String SAVE_FILE = "mesClassesData";
    
    static {
        new File(SAVEDIR).mkdirs();
        new File(ARCHIVE_DIR).mkdirs();
        new File(FILE_DIR).mkdirs();
        new File(BCK_DIR).mkdirs();
        new File(PROPERTIES_DIR).mkdirs();
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
        ZipUtil.pack(new File(SAVEDIR), archiveFile);
        AppLogger.log("Archive créée : "+archiveFile.getPath());
        return archiveFile;
    }
    
    public static void restoreArchive(File archiveFile) throws IOException {
        if(!ZipUtil.containsEntry(archiveFile, SAVE_FILE+".xml")){
            AppLogger.notif("Archivage : ", "Le fichier "+archiveFile.getName()+" n'est pas une archive valide");
            return;
        }
        FileUtils.cleanDirectory(new File(SAVEDIR));
        ZipUtil.unpack(archiveFile, new File(SAVEDIR));
    }
    
    public static File getSaveFile(){
        return new File(SAVEDIR+File.separator+SAVE_FILE+".xml");
    }
    public static boolean saveFileExists(){
        return getSaveFile().exists();
    }
    public static File createNewSaveFile(){
        File file = getSaveFile();
        try {
            file.createNewFile();
            return file;
        } catch (IOException ex) {
            AppLogger.log("Le fichier "+file.getPath()+" ne peut pas être créé");
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
            AppLogger.log("Fichier de sauvegarde "+bckFile.getName()+" créé");
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
        File tmpFile = new File(SAVEDIR+File.separator+SAVE_FILE+"tmp.xml");
        try {
            FileUtils.copyFile(currentFile, tmpFile);
        } catch (IOException ex) {
            AppLogger.notif("Impossible de charger le fichier de sauvegarde", ex);
            return;
        }
        try {
            if(currentFile.exists()){
                AppLogger.log("deleting "+currentFile.getName());
                currentFile.delete();
            }
            AppLogger.log("copying "+file.getName()+" to "+currentFile.getName());
            FileUtils.copyFile(file, currentFile);
            AppLogger.log("Fichier de sauvegarde "+file.getName()+" chargé");
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
