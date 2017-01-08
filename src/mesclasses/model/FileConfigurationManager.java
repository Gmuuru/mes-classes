/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mesclasses.model;

import java.io.File;
import java.io.FileNotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Gilles
 */
public class FileConfigurationManager {

    private static final FileConfigurationManager CONF = new FileConfigurationManager();

    private final static String ROOT_DIR = "mesClasses";
    private final static String LOG_DIR = "logs";
    private final static String SAVE_DIR = "savedData";
    private final static String BCK_DIR = "bck";
    private final static String ARCHIVE_DIR = "archives";
    private final static String PROPERTIES_DIR = "properties";
    private final static String UPLOAD_DIR = "uploadedFiles";
    private final static String SAVE_FILE = "mesClassesData.xml";
    public final static String SEP = File.separator;
    public final static String DEFAULT_SAVE_FILE_NAME = "mesClassesData";

    private String rootDir;
    private String propertiesDir;
    private String archivesDir;
    private String logDir;
    private String saveDir;
    private String backupDir;
    private String uploadDir;
    private String saveFile;

    private FileConfigurationManager() {}

    private static Logger LOG;

    public static FileConfigurationManager getInstance() {
        return CONF;
    }

    public static void setUserHome(String home) {
        CONF.rootDir = home + SEP + ROOT_DIR;
        CONF.logDir = CONF.rootDir + SEP + LOG_DIR;
        CONF.saveDir = CONF.rootDir + SEP + SAVE_DIR;
        CONF.archivesDir = CONF.rootDir + SEP + ARCHIVE_DIR;
        CONF.propertiesDir = CONF.rootDir + SEP + PROPERTIES_DIR;
        CONF.backupDir = CONF.saveDir + SEP + BCK_DIR;
        CONF.uploadDir = CONF.saveDir + SEP + UPLOAD_DIR;
        CONF.saveFile = CONF.saveDir + SEP + SAVE_FILE;

        new File(CONF.rootDir).mkdirs();
        new File(CONF.propertiesDir).mkdirs();
        new File(CONF.logDir).mkdirs();
        new File(CONF.archivesDir).mkdirs();
        new File(CONF.saveDir).mkdirs();
        new File(CONF.backupDir).mkdirs();
        new File(CONF.uploadDir).mkdirs();
        setLog4JConf();
        LOG = LogManager.getLogger(FileConfigurationManager.class);
        LOG.info("Local root directory : " + CONF.rootDir);
    }

    public static void setLog4JConf() {
        System.setProperty("logDir", CONF.logDir);
        reloadLog4JContext();
    }
    
    public static void setSaveFileName(String name) {
        CONF.saveFile = CONF.rootDir+SEP+name;
    }
    
    public static void reloadLog4JContext(){
    org.apache.logging.log4j.core.LoggerContext ctx =
        (org.apache.logging.log4j.core.LoggerContext) LogManager.getContext(false);
    ctx.reconfigure();
    }
    public static void autoDetect() throws FileNotFoundException {

        String workingDirectory;
        //here, we assign the name of the OS, according to Java, to a variable...
        String OS = (System.getProperty("os.name")).toUpperCase();
        //to determine what the workingDirectory is.
        //if it is some version of Windows
        if (OS.contains(
                "WIN")) {
            //it is simply the location of the "AppData" folder
            workingDirectory = System.getenv("AppData");
        } //Otherwise, we assume Linux or Mac
        else {
            //in either case, we would start in the user's home directory
            workingDirectory = System.getProperty("user.home");
            //if we are on a Mac, we are not done, we look for "Application Support"
            workingDirectory += "/Library/Application Support";
        }

        if (workingDirectory == null) {
            throw new FileNotFoundException("No default configuration directory found");
        }
        setUserHome(workingDirectory);
    }

    public String getRootDir() {
        return rootDir;
    }

    public String getSaveDir() {
        return saveDir;
    }

    public String getSaveFile() {
        return saveFile;
    }

    public String getBackupDir() {
        return backupDir;
    }

    public String getPropertiesDir() {
        return propertiesDir;
    }

    public String getUploadDir() {
        return uploadDir;
    }

    public String getArchivesDir() {
        return archivesDir;
    }

}
