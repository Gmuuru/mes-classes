/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mesclasses.util.validation;

import mesclasses.model.MonitoredObject;

/**
 *
 * @author rrrt3491
 */
public class FError {
    
    String message;
    
    MonitoredObject item;

    public FError(){}
    
    public FError(String message){
        this.message = message;
    }
    
    public FError(String message, MonitoredObject item ){
        this.message = message;
        this.item = item;
    }
    
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public MonitoredObject getItem() {
        return item;
    }

    public void setItem(MonitoredObject item) {
        this.item = item;
    }
    
    @Override
    public String toString(){
        return message;
    }
}
