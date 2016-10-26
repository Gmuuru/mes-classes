/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mesclasses.objects.events;

import mesclasses.model.Classe;

/**
 *
 * @author rrrt3491
 */
public class SelectClasseEvent implements Event {
    
    private final Classe classe;
    
    
    public SelectClasseEvent(Classe classe){
        this.classe = classe;
    }

    public Classe getClasse() {
        return classe;
    }
    
    @Override
    public String toString(){
        return "SelectClasseEvent("+classe+")";
    }
}
