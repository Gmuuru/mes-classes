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
public class CreateCoursEvent implements Event {
    
    private final String day;
    
    private final Classe classe;
    
    public CreateCoursEvent(String day, Classe classe){
        
        this.day = day;
        this.classe = classe;
    }

    public String getDay() {
        return day;
    }

    public Classe getClasse() {
        return classe;
    }
    
    @Override
    public String toString(){
        return "CreateCoursEvent("+day+", "+classe+")";
    }
}
