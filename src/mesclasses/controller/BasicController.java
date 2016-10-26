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
import javafx.scene.control.Label;
import mesclasses.handlers.EventBusHandler;
import mesclasses.objects.events.Event;
import mesclasses.objects.events.IsAliveEvent;
import mesclasses.util.AppLogger;
import mesclasses.util.CssUtil;
import org.apache.commons.lang3.RandomStringUtils;

/**
 *
 * @author rrrt3491
 */
public abstract class BasicController implements Initializable {
    
    @FXML
    protected Label errorMessagesLabel = new Label();
    
    protected String name;
    protected String uniqueId;
    protected ObservableSet<Node> fieldsMissing = FXCollections.observableSet(new HashSet<Node>());
    protected ObservableSet<Node> fieldsNotUnique = FXCollections.observableSet(new HashSet<Node>());
    protected BooleanBinding hasErrors = Bindings.or(
            Bindings.size(fieldsMissing).isNotEqualTo(0),
            Bindings.size(fieldsNotUnique).isNotEqualTo(0));
    
    public void initialize(URL url, ResourceBundle rb) {
        resetErrors();
        EventBusHandler.register(this);
        if(name == null){
            name = "Basic Ctrl";
        }
        uniqueId = RandomStringUtils.randomNumeric(10);
        log("Creating controller "+getNameAndId());
    }  
    
    public final void notif(Throwable e){
            AppLogger.notif(name, e);
    }
    
    public final void notif(String e){
            AppLogger.notif(name, e);
    }
    
    public final void log(Throwable e){
            AppLogger.log(e);
    }
    
    public final void log(String e){
            AppLogger.log(e);
    }
    
    public final void logEvent(Event event){
        log("MESSAGE "+event+" RECEIVED BY "+name+" ("+uniqueId+")");
    }
    
    public final void updateErrorMessages(){
        String errorMessages = "";
        if(!fieldsMissing.isEmpty()){
            errorMessages = "Remplissez les champs obligatoires";
        }
        if(!fieldsNotUnique.isEmpty()){
            errorMessages += errorMessages.equals("") ? "" : "\n";
            errorMessages += "Certaines valeurs doivent être uniques";
        }
        errorMessagesLabel.setText(errorMessages);
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
    
    public final void addErrorCss(Node node){
        CssUtil.addClass(node, "error");
    }
    
    public final void removeErrorCssIfPossible(Node node){
        if(!fieldsNotUnique.contains(node) && !fieldsMissing.contains(node)){
            CssUtil.removeClass(node, "error");
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
            CssUtil.removeClass(node, "error");
        });
        fieldsNotUnique.stream().forEach((node) -> {
            CssUtil.removeClass(node, "error");
        });
        fieldsMissing.clear();
        fieldsNotUnique.clear();
        updateErrorMessages();
    }
    
    protected int getNbErrors(){
        return fieldsNotUnique.size() + fieldsMissing.size();
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
        log(getNameAndId()+" "+event);
    }
}
