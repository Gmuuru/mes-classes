/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mesclasses.model;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import mesclasses.handlers.EventBusHandler;
import mesclasses.objects.events.ChangeEvent;
import mesclasses.util.validation.FError;

/**
 *
 * @author rrrt3491
 */
@XmlAccessorType(XmlAccessType.PROPERTY)
public abstract class MonitoredObject {
    
    protected final BooleanProperty changed;

    @XmlTransient
    public boolean isChanged(){
        return changed.get();
    }
    
    public BooleanProperty changedProperty() {
        return changed;
    }

    public void setChanged(boolean changed) {
        this.changed.set(changed);
    }
    
    public MonitoredObject(){
        changed = new SimpleBooleanProperty(false);
        changed.addListener((observable, oldValue, newValue) -> {
            if(!oldValue && newValue){
                EventBusHandler.post(new ChangeEvent(this.toString()));
            }
        });
    }
    
    public abstract void startChangeDetection();
    
    protected ListChangeListener<MonitoredObject> listAddRemoveSortListener = new ListChangeListener<MonitoredObject>() {
        @Override
        public void onChanged(ListChangeListener.Change<? extends MonitoredObject> c) {
            while(c.next()){
            }
            changed.set(true);
        }
    };
    
    protected ListChangeListener<MonitoredObject> listAddRemoveListener = new ListChangeListener<MonitoredObject>() {
        @Override
        public void onChanged(ListChangeListener.Change<? extends MonitoredObject> c) {
            while(c.next()){
                if(c.wasAdded() || c.wasRemoved()){
                    changed.set(true);
                }
            }
        }
    };
    
    protected MapChangeListener<Object, Object> mapListener = new MapChangeListener<Object, Object>() {
        @Override
        public void onChanged(MapChangeListener.Change<? extends Object, ? extends Object> c) {
            changed.set(true);
        }
    };
    
    protected ChangeListener<String> stringListener = (ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
        if(!newValue.equals(oldValue)){
            setChanged(true);
        }
    };
            
    protected ChangeListener<Number> intListener = (ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
        if(!Objects.equals(newValue, oldValue)){
            setChanged(true);
        }
    };
    
    protected ChangeListener<Boolean> booleanListener = (ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
        if(!Objects.equals(oldValue, newValue)){
            setChanged(true);
        }
    };

    protected ChangeListener<LocalDate> dateListener = 
            (ObservableValue<? extends LocalDate> observable, LocalDate oldValue, LocalDate newValue) -> {
        if(!newValue.isEqual(oldValue)){
            setChanged(true);
        }
    };
    
    protected ChangeListener<MonitoredObject> objectListener = 
            (ObservableValue<? extends MonitoredObject> observable, MonitoredObject oldValue, MonitoredObject newValue) -> {
        if(oldValue == newValue){
        } else {
            setChanged(true);
        }
    };
    
    public void resetChange() {
        this.setChanged(false);
    }
    
    protected abstract List<FError> validate();
    
}
