/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mesclasses.handlers;

/**
 *
 * @author rrrt3491
 */
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import java.util.Set;
import mesclasses.model.Constants;
import mesclasses.model.FileConfigurationManager;
import mesclasses.util.AppLogger;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PropertiesCache {

    private static final Logger LOG = LogManager.getLogger(PropertiesCache.class);

    private final Properties configProp = new Properties();
    public static final String LAST_UPLOAD_DIR = "last.upload.dir";

    private static final String DEFAULT_PROPERTIES_FILE = "mesclasses.properties";

    private File pFile;

    private PropertiesCache() {

    }

    public void load() throws IOException {
        FileConfigurationManager conf = FileConfigurationManager.getInstance();
        File confFile = new File(conf.getPropertiesDir()+FileConfigurationManager.SEP+DEFAULT_PROPERTIES_FILE);
        if(!confFile.exists()){
            LOG.debug("Aucun fichier de properties trouvé, on va voir si il en existe un dans l'ancien path");
            File oldFile = new File("savedData/properties/mesclasses.properties");
            if(oldFile.exists()){
                LOG.debug("Fichier de properties trouvé à l'ancienne location, on le copie");
                FileUtils.copyFile(oldFile, confFile);
            } else {
                LOG.debug("Création d'un nouveau fichier de properties");
                confFile.createNewFile();
            }
        }
        
        load(confFile);

    }

    public void load(File file) throws IOException {
        pFile = file;
        LOG.info("Lecture du fichier de configuration " + pFile.getPath());
        if (!pFile.exists()) {
            LOG.error("Le fichier de configuration " + pFile.getAbsolutePath() + " n'existe pas");
            throw new IOException("Le fichier de configuration " + pFile.getAbsolutePath() + " n'existe pas");
        }
        FileReader reader = null;
        try {
            reader = new FileReader(pFile);
            LOG.info("Chargement de la configuration");
            configProp.load(reader);
            LOG.info("Chargement de la configuration effectué");
        } catch (Exception e) {
            throw new IOException(e);
        } finally {
            IOUtils.closeQuietly(reader);
        }

    }

    public void save() {
        LOG.info("Sauvegarde de la configuration");
        try (FileOutputStream out = new FileOutputStream(pFile.getPath())) {
            configProp.store(out, null);
        } catch (IOException e) {
            AppLogger.notif("Fichier de configuration", e);
        }
    }
    //Bill Pugh Solution for singleton pattern

    private static class LazyHolder {

        private static final PropertiesCache INSTANCE = new PropertiesCache();
    }

    public static PropertiesCache getInstance() {
        return LazyHolder.INSTANCE;
    }

    public String getProperty(String key) {
        return configProp.getProperty(key);
    }

    public int getIntegerProperty(String key) {
        try {
            return Integer.parseInt(configProp.getProperty(key));
        } catch (Exception e) {
            LOG.error(e);
            return 0;
        }
    }

    public int getIntegerProperty(String key, int defaultValue) {
        try {
            return Integer.parseInt(configProp.getProperty(key));
        } catch (Exception e) {
            LOG.error(e);
            return defaultValue;
        }
    }

    public Set<String> getAllPropertyNames() {
        return configProp.stringPropertyNames();
    }

    public boolean containsKey(String key) {
        return configProp.containsKey(key);
    }

    public void setProperty(String key, String value) {
        configProp.setProperty(key, value);
    }
    
    public static String version() {
        return LazyHolder.INSTANCE.getProperty(Constants.CONF_VERSION);
    }
}
