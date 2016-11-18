/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mesclasses.util;

import java.util.logging.Level;
import java.util.logging.Logger;
import mesclasses.handlers.EventBusHandler;
import mesclasses.objects.events.MessageEvent;
import mesclasses.view.RootLayoutController;

/**
 *
 * @author rrrt3491
 */
public class AppLogger {
    
    public static void notif(String controller, Throwable e){
            log(e);
            Throwable error = e;
            while(error != null && error.getCause() != null){
                error = error.getCause();
            }
            String message = error.getMessage();
            if(message == null){
                message = error.getClass().toString();
            }
            EventBusHandler.post(new MessageEvent(MessageEvent.ERROR, controller+" : "+message));
    }
    
    public static void notif(String controller, String error){
            log(error);
            EventBusHandler.post(new MessageEvent(MessageEvent.ERROR, controller+" : "+error));
    }
    
    public static void log(Throwable e){
        Logger.getLogger(RootLayoutController.class.getName()).log(Level.SEVERE, null, e);
    }
    
    public static void log(Object o){
        log(o.toString());
    }
    
    public static void log(String e){
        System.out.println(e);
    }
}
