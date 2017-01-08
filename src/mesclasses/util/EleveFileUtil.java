/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mesclasses.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import mesclasses.model.Eleve;
import mesclasses.model.FileConfigurationManager;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author rrrt3491
 */
public class EleveFileUtil {
    
    private static final Logger LOG = LogManager.getLogger(EleveFileUtil.class);
    
    public static File getEleveDir(Eleve eleve){
        return new File(FileConfigurationManager.getInstance().getUploadDir() + File.separator + sanitize(eleve.getId()));
    }
    
    public static File getEleveDirWithType(Eleve eleve, String type){
        return new File(FileConfigurationManager.getInstance().getUploadDir() + File.separator + sanitize(eleve.getId()) + File.separator + type);
    }
    
    public static List<File> getEleveFilesOfType(Eleve eleve, String type){
        LOG.info("Recuperation des fichiers "+type+" pour "+eleve.getDisplayName());
        File[] files = getEleveDirWithType(eleve, type).listFiles();
        if(files == null){
            return new ArrayList<>();
        }
        return Arrays.asList(getEleveDirWithType(eleve, type).listFiles());
    }
    
    public static String copyFileForEleve(Eleve eleve, File file, String type) throws IOException {
        File eleveDir = getEleveDirWithType(eleve, type);
        eleveDir.mkdirs();
        String fileName = FilenameUtils.getName(file.getPath());
        File newFile = new File( eleveDir.getPath() + File.separator + fileName );
        if(newFile.exists()){
            throw new IOException("Le fichier "+fileName+" de type "+type+" existe déjà pour "+eleve.getFirstName());
        }
        FileUtils.copyFile(file, newFile);
        return newFile.getPath();
    }
    
    public static String moveFileForEleve(Eleve eleve, File file, String type) throws IOException {
        File eleveDir = getEleveDirWithType(eleve, type);
        eleveDir.mkdirs();
        String fileName = FilenameUtils.getName(file.getPath());
        File newFile = new File( eleveDir.getPath() + File.separator + fileName );
        if(newFile.exists()){
            throw new IOException("Le fichier "+fileName+" de type "+type+" existe déjà pour "+eleve.getFirstName());
        }
        FileUtils.moveFile(file, newFile);
        return newFile.getPath();
    }
    
    public static void deleteFilesForEleve(Eleve eleve) {
        try {
            FileUtils.deleteDirectory(getEleveDir(eleve));
        } catch (IOException ex) {
            AppLogger.notif("Suppression de fichiers", ex);
        }
    }
    
    public static String sanitize(String str){
        return  StringUtils.stripAccents(str).replaceAll("[^\\w.-]", "_");
    }
    
    public static void purge() throws IOException {
        File dir = new File(FileConfigurationManager.getInstance().getUploadDir());
        FileUtils.deleteDirectory(dir);
        new File(FileConfigurationManager.getInstance().getUploadDir()).mkdirs();
    }
}
