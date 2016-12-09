/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mesclasses.objects.tasks;

import mesclasses.handlers.PropertiesCache;
import mesclasses.model.Constants;
import mesclasses.objects.LoadWindow;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author rrrt3491
 */
public class FetchConfigTask extends AppTask<Void> {
    
    private static final Logger LOG = LogManager.getLogger(LoadWindow.class);
    
    public FetchConfigTask(){
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
            PropertiesCache.getInstance().load();
            
            LOG.info("Initialisation de la configuration par défaut si besoin");
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
