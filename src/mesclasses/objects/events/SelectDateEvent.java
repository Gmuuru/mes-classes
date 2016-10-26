/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mesclasses.objects.events;

import java.time.LocalDate;

/**
 *
 * @author rrrt3491
 */
public class SelectDateEvent implements Event {
    
    private final LocalDate date;
    
    
    public SelectDateEvent(LocalDate date){
        this.date = date;
    }

    public LocalDate getDate() {
        return date;
    }
    
    @Override
    public String toString(){
        return "SelectDateEvent("+date+")";
    }
}
