/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mesclasses.objects.tasks;

import java.io.File;
import mesclasses.handlers.PropertiesCache;
import mesclasses.model.Constants;
import mesclasses.model.FileConfigurationManager;
import mesclasses.objects.LoadWindow;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zeroturnaround.zip.commons.FileUtils;

/**
 *
 * @author rrrt3491
 */
public class ValidateConfigTask extends AppTask<Void> {
    
    private static final Logger LOG = LogManager.getLogger(LoadWindow.class);
    
    public ValidateConfigTask(){
        super();
        updateProgress(0, 100.0);
    }
    
    @Override 
    public String getName(){
        return "Configuration";
    }
    
    @Override
    protected Void call() throws Exception {
        try {
            FileConfigurationManager conf = FileConfigurationManager.getInstance();
            if(!new File(conf.getSaveFile()).exists()){
                LOG.debug("Aucun fichier de données trouvé, on va voir si il en existe un dans l'ancien path");
                File oldFile = new File("savedData/mesClassesData.xml");
                if(oldFile.exists()){
                    LOG.debug("Fichier de données trouvé à l'ancienne location, on le copie");
                    FileUtils.copyFile(oldFile, new File(conf.getSaveFile()));
                } else {
                    LOG.debug("Création d'un nouveau fichier de sauvegaOrde");
                }
            }
            PropertiesCache.getInstance().load();
            initConfig();
            
        } catch (Exception e) { // catches ANY exception
            LOG.error(e);
            setMsg("Impossible de charger la configuration");
            throw e;
        }
        return null;
    }
    
    private void initConfig(){
        LOG.info("Initialisation de la configuration par défaut si besoin");
        PropertiesCache config = PropertiesCache.getInstance();
        if(!config.containsKey(Constants.CONF_VERSION)){
            config.setProperty(Constants.CONF_VERSION, "1.0.0");
        } else {
            LOG.info("Version détectée : "+config.getProperty(Constants.CONF_VERSION));
        }
        if(!config.containsKey(Constants.CONF_WEEK_DEFAULT)){
            config.setProperty(Constants.CONF_WEEK_DEFAULT, "normale");
        }
        if(!config.containsKey(Constants.CONF_WEEK_P1_NAME)){
            config.setProperty(Constants.CONF_WEEK_P1_NAME, "Q1");
        }
        if(!config.containsKey(Constants.CONF_WEEK_P1_VAL)){
            config.setProperty(Constants.CONF_WEEK_P1_VAL, "0");
        }
        if(!config.containsKey(Constants.CONF_WEEK_P2_NAME)){
            config.setProperty(Constants.CONF_WEEK_P2_NAME, "Q2");
        }
        if(!config.containsKey(Constants.CONF_WEEK_P2_VAL)){
            config.setProperty(Constants.CONF_WEEK_P2_VAL, "1");
        }
        config.save();
    }
}
