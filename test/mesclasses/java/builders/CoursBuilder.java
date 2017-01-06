/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mesclasses.java.builders;

import mesclasses.model.Classe;
import mesclasses.model.Cours;

/**
 *
 * @author rrrt3491
 */
public class CoursBuilder {
    
    private final Cours cours = new Cours();
    
    public CoursBuilder id(String id) {
        cours.setId(id);
        return this;
    }
    
    public CoursBuilder day(String day) {
        cours.setDay(day);
        return this;
    }
    
    public CoursBuilder week(String week) {
        cours.setWeek(week);
        return this;
    }
    
    public CoursBuilder room(String room) {
        cours.setRoom(room);
        return this;
    }
    
    public CoursBuilder classe() {
        return classe(new Classe());
    }
    
    public CoursBuilder classe(Classe classe) {
        cours.setClasse(classe);
        return this;
    }
    
    public CoursBuilder ponctuel(Boolean ponctuel) {
        cours.setPonctuel(ponctuel);
        return this;
    }
    
    public CoursBuilder start(String start) {
        if(start == null){
            cours.setStartHour(null);
            cours.setStartMin(null);
        } else {
            cours.setStartHour(Integer.parseInt(start.split("h")[0]));
            cours.setStartMin(start.split("h").length == 2 ? Integer.parseInt(start.split("h")[1]) : 0);
        }
        return this;
    } 
    
    public CoursBuilder end(String end) {
        if(end == null){
            cours.setEndHour(null);
            cours.setEndMin(null);
        } else {
            cours.setEndHour(Integer.parseInt(end.split("h")[0]));
            cours.setEndMin(end.split("h").length == 2 ? Integer.parseInt(end.split("h")[1]) : 0);
        }
        return this;
    }
    
    public Cours build(){
        return cours;
    }
    
}
