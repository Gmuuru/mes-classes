/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mesclasses.java.builders;

import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import mesclasses.model.Classe;
import mesclasses.model.Cours;
import mesclasses.model.Eleve;
import mesclasses.model.EleveData;
import mesclasses.model.Journee;
import mesclasses.model.Seance;

/**
 *
 * @author rrrt3491
 */
public class SeanceBuilder {
    
    private final Seance seance = new Seance();
    
    public SeanceBuilder id(String id) {
        seance.setId(id);
        return this;
    }
    
    public SeanceBuilder classe() {
        return classe(new Classe());
    }
    
    public SeanceBuilder classe(Classe classe) {
        seance.setClasse(classe);
        return this;
    }
    
    public SeanceBuilder cours() {
        return cours(new Cours());
    }
    
    public SeanceBuilder cours(Cours cours) {
        seance.setCours(cours);
        return this;
    }
    
    public SeanceBuilder journee() {
        return journee(new Journee());
    }
    
    public SeanceBuilder journee(Journee journee) {
        seance.setJournee(journee);
        return this;
    }
    
    public SeanceBuilder donnees(ObservableMap<Eleve, EleveData> map) {
        seance.setDonnees(map);
        return this;
    }
    
    public SeanceBuilder donnee(Eleve eleve, EleveData data) {
        if(seance.getDonnees() == null){
            donnees(FXCollections.observableHashMap());
        }
        seance.getDonnees().put(eleve, data);
        return this;
    }
    
    public Seance build(){
        return seance;
    }
    
}
