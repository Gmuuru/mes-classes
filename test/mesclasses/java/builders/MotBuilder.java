/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mesclasses.java.builders;

import java.time.LocalDate;
import mesclasses.model.Eleve;
import mesclasses.model.Mot;
import mesclasses.model.Seance;

/**
 *
 * @author rrrt3491
 */
public class MotBuilder {
    
    private final Mot mot = new Mot();
    
    public MotBuilder id(String id) {
        mot.setId(id);
        return this;
    }
    
    public MotBuilder eleve() {
        return eleve(new Eleve());
    }
    
    public MotBuilder eleve(Eleve eleve) {
        mot.setEleve(eleve);
        return this;
    }
    
    public MotBuilder cloture(LocalDate date) {
        mot.setDateCloture(date);
        return this;
    }
    
    public MotBuilder seance() {
        return seance(new Seance());
    }
    
    public MotBuilder seance(Seance seance) {
        mot.setSeance(seance);
        return this;
    }
    
    public Mot build(){
        return mot;
    }
    
}
