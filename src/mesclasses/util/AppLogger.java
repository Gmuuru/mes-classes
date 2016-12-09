/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mesclasses.util;

import mesclasses.handlers.EventBusHandler;
import mesclasses.objects.events.MessageEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author rrrt3491
 */
public class AppLogger {
    
    public static void logStart(Class clazz){
        Logger LOG = LogManager.getLogger(clazz);
        LOG.info("*******************************************************************************************");
        LOG.info("******************************  Démarrage de l'application  *******************************");
        LOG.info("*******************************************************************************************");
    }
    
    public static void logExit(Class clazz){
        Logger LOG = LogManager.getLogger(clazz);
        LOG.info("*******************************************************************************************");
        LOG.info("******************************  Clôture de l'application    *******************************");
        LOG.info("*******************************************************************************************");
        
    }
    
    public static void notif(String name, Throwable e){
            Throwable error = e;
            while(error != null && error.getCause() != null){
                error = error.getCause();
            }
            String message = error.getMessage();
            if(message == null){
                message = error.getClass().toString();
            }
            EventBusHandler.post(new MessageEvent(MessageEvent.ERROR, name+" : "+message));
    }
    
    public static void notif(String name, String error){
            EventBusHandler.post(new MessageEvent(MessageEvent.ERROR, name+" : "+error));
    }
}
