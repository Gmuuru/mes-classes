/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mesclasses.objects.events;

import mesclasses.model.Seance;

/**
 *
 * @author rrrt3491
 */
public class SelectSeanceEvent implements Event {
    
    private final Seance seance;
    
    
    public SelectSeanceEvent(Seance seance){
        this.seance = seance;
    }

    public Seance getSeance() {
        return seance;
    }
    
    @Override
    public String toString(){
        return "SelectSeanceEvent("+seance+")";
    }
}
