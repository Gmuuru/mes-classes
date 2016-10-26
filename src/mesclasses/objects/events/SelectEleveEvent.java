/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mesclasses.objects.events;

import mesclasses.model.Eleve;

/**
 *
 * @author rrrt3491
 */
public class SelectEleveEvent implements Event {
    
    private final Eleve eleve;
    
    public SelectEleveEvent(Eleve eleve){
        this.eleve = eleve;
    }

    public Eleve getEleve() {
        return eleve;
    }
    
    @Override
    public String toString(){
        return "SelectEleveEvent("+eleve+")";
    }
}
