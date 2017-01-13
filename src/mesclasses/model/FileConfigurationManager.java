/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mesclasses.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Gilles
 */
public class FileConfigurationManager {

    private static final FileConfigurationManager CONF = new FileConfigurationManager();

    private static File CUSTOM_INSTALL_FILE;
    private static final Properties customProperties = new Properties();
    private final static String STORAGE_PATH_PROP = "storage.path";
    
    private final static String ROOT_DIR = "mesClasses";
    private final static String LOG_DIR = "logs";
    private final static String SAVE_DIR = "savedData";
    private final static String BCK_DIR = "bck";
    private final static String ARCHIVE_DIR = "archives";
    private final static String PROPERTIES_DIR = "properties";
    private final static String UPLOAD_DIR = "uploadedFiles";
    public final static String SAVE_FILE = "mesClassesData.xml";
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

    private FileConfigurationManager() {
    }

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
        System.out.println("Auto détection de la configuration");
        CUSTOM_INSTALL_FILE = new File("../mesclasses_install.properties");
        try {
            if(!CUSTOM_INSTALL_FILE.exists()){
                CUSTOM_INSTALL_FILE.createNewFile();
            } else {
                readCustomProperties(CUSTOM_INSTALL_FILE);
            }
        } catch(IOException e){
            System.out.println(e);
        }
        System.out.println("Détection des paths de stockage");
        String workingDirectory;
        String customStoragePath = customProperties.getProperty(STORAGE_PATH_PROP);
        if(customStoragePath == null){
            System.out.println("Pas de configuration customisée");
            workingDirectory = getDefaultLocalDirectory();
            customProperties.setProperty(STORAGE_PATH_PROP, workingDirectory);
            writeCustomProperties(CUSTOM_INSTALL_FILE);
        } else {
            workingDirectory = customStoragePath;
        }

        if (workingDirectory == null) {
            throw new FileNotFoundException("No default configuration directory found");
        }
        setUserHome(workingDirectory);
    }
    
    private static String getDefaultLocalDirectory(){
        
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
        return workingDirectory;
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

    public String getLogDir() {
        return logDir;
    }

    private static void readCustomProperties(File pFile) throws IOException {
        FileReader reader = null;
        try {
            reader = new FileReader(pFile);
            customProperties.load(reader);
        } catch (Exception e) {
            throw new IOException(e);
        } finally {
            IOUtils.closeQuietly(reader);
        }
    }
    
    public static void setCustomStoragePath(String path){
        customProperties.setProperty(STORAGE_PATH_PROP, path);
        writeCustomProperties(CUSTOM_INSTALL_FILE);
        setUserHome(path);
    }
    
    private static void writeCustomProperties(File pFile) {
        try (FileOutputStream out = new FileOutputStream(pFile.getPath())) {
            customProperties.store(out, null);
        } catch (IOException e) {
        }
    }
}
