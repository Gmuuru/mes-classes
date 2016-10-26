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
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Set;
import mesclasses.util.AppLogger;
import mesclasses.util.FileSaveUtil;

public class PropertiesCache
{
   private final Properties configProp = new Properties();
   
   public static final String LAST_UPLOAD_DIR = "last.upload.dir";
   
   private static final String PROPERTIES_FILE = FileSaveUtil.PROPERTIES_DIR+File.separator+"mesclasses.properties";
   private PropertiesCache()
   {
      //Private constructor to restrict new instances
      
   }

   private void load(){
      AppLogger.log("Read all properties from file");
      try {
        InputStream in = this.getClass().getClassLoader().getResourceAsStream(PROPERTIES_FILE);
          configProp.load(in);
      } catch (IOException e) {
          AppLogger.notif("Fichier de configuration", e);
      }
      
   }
   
   public void save(){
      AppLogger.log("Saving properties into file");
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
