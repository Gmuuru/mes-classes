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
import mesclasses.model.FileConfigurationManager;
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

    public static File archive() throws IOException {

        FileConfigurationManager conf = FileConfigurationManager.getInstance();

        File archiveFile = new File(new StringBuilder(conf.getArchivesDir())
                .append(File.separator)
                .append("mesclasses_archive_")
                .append(LocalDate.now().format(Constants.DATE_FORMATTER))
                .append(".zip")
                .toString());
        if (archiveFile.exists()) {
            archiveFile.delete();
        }
        ZipUtil.pack(new File(conf.getSaveDir()), archiveFile);
        LOG.info("Archive créée : " + archiveFile.getPath());
        return archiveFile;
    }

    public static void restoreArchive(File archiveFile) throws IOException {
        FileConfigurationManager conf = FileConfigurationManager.getInstance();
        if (!ZipUtil.containsEntry(archiveFile, conf.getSaveFile())) {
            AppLogger.notif("Archivage : ", "Le fichier " + archiveFile.getName() + " n'est pas une archive valide");
            return;
        }
        FileUtils.cleanDirectory(new File(conf.getSaveDir()));
        ZipUtil.unpack(archiveFile, new File(conf.getSaveDir()));
    }

    public static File getSaveFile() {
        return new File(FileConfigurationManager.getInstance().getSaveFile());
    }

    public static boolean saveFileExists() {
        return getSaveFile().exists();
    }

    public static File createNewSaveFile(File file) {
        try {
            file.createNewFile();
            return file;
        } catch (IOException ex) {
            LOG.error("Le fichier " + file.getPath() + " ne peut pas être créé");
            return null;
        }
    }

    public static File createBackupFile() {
        FileConfigurationManager conf = FileConfigurationManager.getInstance();
        if (!saveFileExists()) {
            return null;
        }
        File bckFile = new File(conf.getBackupDir() + File.separator + FileConfigurationManager.DEFAULT_SAVE_FILE_NAME 
                + "_" + LocalDate.now().format(Constants.DATE_FORMATTER) + ".xml");
        try {
            if (bckFile.exists()) {
                bckFile.delete();
            }
            FileUtils.copyFile(getSaveFile(), bckFile);
            LOG.info("Fichier de sauvegarde " + bckFile.getName() + " créé");
        } catch (Exception e) {
            AppLogger.notif("Impossible de créer le fichier de sauvegarde", e);
        }
        File[] bckFiles = new File(conf.getBackupDir()).listFiles();
        if (bckFiles.length >= 8) {
            bckFiles[0].delete();
        }
        return bckFile;
    }

    public static void restoreBackupFile(File file) {
        FileConfigurationManager conf = FileConfigurationManager.getInstance();
        if (!file.exists()) {
            AppLogger.notif("Chargement impossible", "le fichier " + file.getName() + " n'existe pas");
            return;
        }
        File currentFile = getSaveFile();
        File tmpFile = new File(conf.getSaveDir() + File.separator + FileConfigurationManager.DEFAULT_SAVE_FILE_NAME + "tmp.xml");
        try {
            FileUtils.copyFile(currentFile, tmpFile);
        } catch (IOException ex) {
            AppLogger.notif("Impossible de charger le fichier de sauvegarde", ex);
            return;
        }
        try {
            if (currentFile.exists()) {
                LOG.info("deleting " + currentFile.getName());
                currentFile.delete();
            }
            LOG.info("copying " + file.getName() + " to " + currentFile.getName());
            FileUtils.copyFile(file, currentFile);
            LOG.info("Fichier de sauvegarde " + file.getName() + " chargé");
        } catch (Exception e) {
            AppLogger.notif("Impossible de charger le fichier de sauvegarde", e);
            if (currentFile.exists()) {
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
