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
import mesclasses.util.AppLogger;
import mesclasses.util.FileSaveUtil;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PropertiesCache
{
    private static final Logger LOG = LogManager.getLogger(PropertiesCache.class);
    
   private final Properties configProp = new Properties();
   public static final String LAST_UPLOAD_DIR = "last.upload.dir";
   
   private static final String PROPERTIES_FILE = FileSaveUtil.PROPERTIES_DIR+File.separator+"mesclasses.properties";
   
   private PropertiesCache()
   {
      
   }

   public void load() throws Exception {
      LOG.info("Lecture du fichier de configuration "+PROPERTIES_FILE);
      File pfile = new File(PROPERTIES_FILE);
      if(!pfile.exists()){
          LOG.error("Le fichier de configuration "+ pfile.getAbsolutePath()+" n'existe pas");
          throw new Exception ("Le fichier de configuration "+ pfile.getAbsolutePath()+" n'existe pas");
      }
      FileReader reader = null;
      try {
          reader = new FileReader(pfile);
          LOG.info("Chargement de la configuration");
          configProp.load(reader);
          LOG.info("Chargement de la configuration effectué");
      } catch (Exception e) {
          throw new Exception (e);
      } finally {
          IOUtils.closeQuietly(reader);
      }
      
   }
   
   public void save(){
      LOG.info("Sauvegarde de la configuration");
      try (FileOutputStream out = new FileOutputStream(PROPERTIES_FILE)) {
        configProp.store(out, null);
        } catch (IOException e) {
          AppLogger.notif("Fichier de configuration", e);
      }
   }
   //Bill Pugh Solution for singleton pattern
   private static class LazyHolder
   {
      private static final PropertiesCache INSTANCE = new PropertiesCache();
   }

   public static PropertiesCache getInstance()
   {
      return LazyHolder.INSTANCE;
   }
   
   public String getProperty(String key){
      return configProp.getProperty(key);
   }
   
   public int getIntegerProperty(String key){
        try {
            return Integer.parseInt(configProp.getProperty(key));
        } catch(Exception e){
            LOG.error(e);
            return 0;
        }
   }
   
   public int getIntegerProperty(String key, int defaultValue){
        try {
            return Integer.parseInt(configProp.getProperty(key));
        } catch(Exception e){
            LOG.error(e);
            return defaultValue;
        }
   }
   
   public Set<String> getAllPropertyNames(){
      return configProp.stringPropertyNames();
   }
   
   public boolean containsKey(String key){
      return configProp.containsKey(key);
   }
   
   public void setProperty(String key, String value){
        configProp.setProperty(key, value);
   }
}
