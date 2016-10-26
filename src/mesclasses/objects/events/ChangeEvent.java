/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mesclasses.objects.events;

/**
 *
 * @author rrrt3491
 */
public class ChangeEvent implements Event {
    
    private String objectName;

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }
    
    public ChangeEvent(){}
    
    public ChangeEvent(String objectName){
        this.objectName = objectName;
    }
    
    @Override
    public String toString(){
        return objectName+" changed";
    }
}
