/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mesclasses.controller;

import com.google.common.eventbus.Subscribe;
import java.net.URL;
import java.util.HashSet;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Hyperlink;
import mesclasses.handlers.EventBusHandler;
import mesclasses.handlers.PropertiesCache;
import mesclasses.objects.events.Event;
import mesclasses.objects.events.IsAliveEvent;
import mesclasses.util.AppLogger;
import mesclasses.util.CssUtil;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author rrrt3491
 */
public abstract class BasicController implements Initializable {
    
    private static final Logger LOG = LogManager.getLogger(BasicController.class);
    
    public static final String ERROR_CLASS = "error";
    public static final String ERROR_MSG = "La page contient des erreurs";
    
    @FXML protected Hyperlink errorMessagesLabel = new Hyperlink();
    protected String name;
    protected String uniqueId;
    
    protected ObservableSet<Node> fieldsMissing = FXCollections.observableSet(new HashSet<Node>());
    protected ObservableSet<Node> fieldsNotUnique = FXCollections.observableSet(new HashSet<Node>());
    protected ObservableSet<Node> fieldsInvalid = FXCollections.observableSet(new HashSet<Node>());
    protected BooleanBinding hasErrors = Bindings.or(
            Bindings.size(fieldsMissing).isNotEqualTo(0),
            Bindings.or(
                    Bindings.size(fieldsNotUnique).isNotEqualTo(0),
                    Bindings.size(fieldsInvalid).isNotEqualTo(0)));
    
    protected PropertiesCache config = PropertiesCache.getInstance();
    
    public void initialize(URL url, ResourceBundle rb) {
        resetErrors();
        EventBusHandler.register(this);
        if(name == null){
            name = "Basic Ctrl";
        }
        uniqueId = RandomStringUtils.randomNumeric(10);
        LOG.debug("Creating controller "+getNameAndId());
        
    }  
    
    public final void notif(Throwable e){
            AppLogger.notif(name, e);
    }
    
    public final void notif(String e){
            AppLogger.notif(name, e);
    }
    
    public final void logEvent(Event event){
        LOG.info("MESSAGE "+event+" RECEIVED BY "+name+" ("+uniqueId+")");
    }
    
    public final void updateErrorMessages(){
        if(!fieldsMissing.isEmpty() || !fieldsNotUnique.isEmpty() || !fieldsInvalid.isEmpty()){
            errorMessagesLabel.setText(ERROR_MSG);
        } else {
            errorMessagesLabel.setText("");
        }
    }
    
    public final void addMissingError(Node node){
        fieldsMissing.add(node);
        addErrorCss(node);
        updateErrorMessages();
    }
    
    public final void removeMissingError(Node node){
        fieldsMissing.remove(node);
        removeErrorCssIfPossible(node);
        updateErrorMessages();
    }
    
    public final void addUnicityError(Node node){
        fieldsNotUnique.add(node);
        addErrorCss(node);
        updateErrorMessages();
    }
    
    public final void removeUnicityError(Node node){
        fieldsNotUnique.remove(node);
        removeErrorCssIfPossible(node);
        updateErrorMessages();
    }
    
    public final void addValidityError(Node node){
        fieldsInvalid.add(node);
        addErrorCss(node);
        updateErrorMessages();
    }
    
    public final void removeValidityError(Node node){
        fieldsInvalid.remove(node);
        removeErrorCssIfPossible(node);
        updateErrorMessages();
    }
    
    public final void addErrorCss(Node node){
        CssUtil.addClass(node, ERROR_CLASS);
    }
    
    public final void removeErrorCssIfPossible(Node node){
        if(!fieldsNotUnique.contains(node) && !fieldsMissing.contains(node) && !fieldsInvalid.contains(node)){
            CssUtil.removeClass(node, ERROR_CLASS);
        }
    }
    
    public final void removeErrors(Node... nodes){
        for(Node node : nodes){
            removeMissingError(node);
            removeUnicityError(node);
            removeErrorCssIfPossible(node);
        }
        updateErrorMessages();
    }
    
    public final void resetErrors(){
        fieldsMissing.stream().forEach((node) -> {
            CssUtil.removeClass(node, ERROR_CLASS);
        });
        fieldsNotUnique.stream().forEach((node) -> {
            CssUtil.removeClass(node, ERROR_CLASS);
        });
        fieldsInvalid.stream().forEach((node) -> {
            CssUtil.removeClass(node, ERROR_CLASS);
        });
        fieldsMissing.clear();
        fieldsNotUnique.clear();
        fieldsInvalid.clear();
        updateErrorMessages();
    }
    
    protected int getNbErrors(){
        return fieldsNotUnique.size() + fieldsMissing.size() + fieldsInvalid.size();
    }

    public String getName() {
        return name;
    }

    public String getUniqueId() {
        return uniqueId;
    }
    
    public String getNameAndId(){
        return name +" ("+uniqueId+")";
    }
    
    @Subscribe
    public void onIsAlive(IsAliveEvent event){
        LOG.info(getNameAndId()+" "+event);
    }
}
