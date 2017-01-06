/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mesclasses.java.builders;

import mesclasses.model.Eleve;
import mesclasses.model.EleveData;
import mesclasses.model.Seance;

/**
 *
 * @author rrrt3491
 */
public class DonneeBuilder {
    
    private final EleveData donnee = new EleveData();
    
    public DonneeBuilder id(String id) {
        donnee.setId(id);
        return this;
    }
    
    public DonneeBuilder eleve() {
        return eleve(new Eleve());
    }
    
    public DonneeBuilder eleve(Eleve eleve) {
        donnee.setEleve(eleve);
        return this;
    }
    
    public DonneeBuilder seance() {
        return seance(new Seance());
    }
    
    public DonneeBuilder seance(Seance seance) {
        donnee.setSeance(seance);
        return this;
    }
    
    public EleveData build(){
        return donnee;
    }
    
}
