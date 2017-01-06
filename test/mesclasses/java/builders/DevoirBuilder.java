/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mesclasses.java.builders;

import mesclasses.model.Devoir;
import mesclasses.model.Eleve;
import mesclasses.model.Seance;

/**
 *
 * @author rrrt3491
 */
public class DevoirBuilder {
    
    private final Devoir devoir = new Devoir();
    
    public DevoirBuilder id(String id) {
        devoir.setId(id);
        return this;
    }
    
    public DevoirBuilder eleve() {
        return eleve(new Eleve());
    }
    
    public DevoirBuilder eleve(Eleve eleve) {
        devoir.setEleve(eleve);
        return this;
    }
    
    public DevoirBuilder closed(Boolean closed) {
        devoir.setClosed(closed);
        return this;
    }
    
    public DevoirBuilder seance() {
        return seance(new Seance());
    }
    
    public DevoirBuilder seance(Seance seance) {
        devoir.setSeance(seance);
        return this;
    }
    
    public Devoir build(){
        return devoir;
    }
    
}
