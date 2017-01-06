/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mesclasses.java.builders;

import mesclasses.model.Eleve;
import mesclasses.model.Punition;
import mesclasses.model.Seance;

/**
 *
 * @author rrrt3491
 */
public class PunitionBuilder {
    
    private final Punition punition = new Punition();
    
    public PunitionBuilder id(String id) {
        punition.setId(id);
        return this;
    }
    
    public PunitionBuilder texte(String texte) {
        punition.setTexte(texte);
        return this;
    }
    
    public PunitionBuilder eleve() {
        return eleve(new Eleve());
    }
    
    public PunitionBuilder eleve(Eleve eleve) {
        punition.setEleve(eleve);
        return this;
    }
    
    public PunitionBuilder closed(Boolean closed) {
        punition.setClosed(closed);
        return this;
    }
    
    public PunitionBuilder seance() {
        return seance(new Seance());
    }
    
    public PunitionBuilder seance(Seance seance) {
        punition.setSeance(seance);
        return this;
    }
    
    public Punition build(){
        return punition;
    }
    
}
