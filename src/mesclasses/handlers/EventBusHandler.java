/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mesclasses.handlers;

import com.google.common.eventbus.EventBus;
import mesclasses.controller.BasicController;
import mesclasses.objects.events.ChangeEvent;
import mesclasses.objects.events.ClassesChangeEvent;
import mesclasses.objects.events.CreateCoursEvent;
import mesclasses.objects.events.IsAliveEvent;
import mesclasses.objects.events.MessageEvent;
import mesclasses.objects.events.OpenMenuEvent;
import mesclasses.objects.events.SelectClasseEvent;
import mesclasses.objects.events.SelectDateEvent;
import mesclasses.objects.events.SelectEleveEvent;
import mesclasses.objects.events.SelectSeanceEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author rrrt3491
 */
public class EventBusHandler {
    
    private static final Logger LOG = LogManager.getLogger(EventBusHandler.class);
    
    private static final EventBus EVENT_BUS = new EventBus();
    
    public static void register(BasicController ctrl){
         EVENT_BUS.register(ctrl);
    }
    
    public static void unregister(BasicController ctrl){
        try {
            EVENT_BUS.unregister(ctrl);
        } catch(IllegalArgumentException e){
            LOG.error("Controller "+ctrl.getName()+" was not registered. Can't unregister");
        }
    }
    
    public static void post(OpenMenuEvent event){
        LOG.info("EVENT BUS MESSAGE : "+event);
        EVENT_BUS.post(event);
    }
    
    public static void post(ChangeEvent event){
        LOG.info("EVENT BUS MESSAGE : "+event);
        EVENT_BUS.post(event);
    }
    
    public static void post(SelectClasseEvent event){
        LOG.info("EVENT BUS MESSAGE : "+event);
        EVENT_BUS.post(event);
    }
    
    public static void post(SelectDateEvent event){
        LOG.info("EVENT BUS MESSAGE : "+event);
        EVENT_BUS.post(event);
    }
    
    public static void post(SelectSeanceEvent event){
        LOG.info("EVENT BUS MESSAGE : "+event);
        EVENT_BUS.post(event);
    }
    
    public static void post(SelectEleveEvent event){
        LOG.info("EVENT BUS MESSAGE : "+event);
        EVENT_BUS.post(event);
    }
    
    public static void post(ClassesChangeEvent event){
        LOG.info("EVENT BUS MESSAGE : "+event);
        EVENT_BUS.post(event);
    }
    
    public static void post(CreateCoursEvent event){
        LOG.info("EVENT BUS MESSAGE : "+event);
        EVENT_BUS.post(event);
    }
    
    public static void post(MessageEvent event){
        LOG.info("EVENT BUS MESSAGE : "+event);
        EVENT_BUS.post(event);
    }
    
    public static void post(IsAliveEvent event){
        LOG.info("EVENT BUS MESSAGE : "+event);
        EVENT_BUS.post(event);
    }
    
    private EventBusHandler(){};
}
