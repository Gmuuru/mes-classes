/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mesclasses.java.builders;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import mesclasses.model.ChangementClasse;
import mesclasses.model.Classe;
import mesclasses.model.Eleve;
import mesclasses.model.EleveData;
import mesclasses.model.Punition;

/**
 *
 * @author rrrt3491
 */
public class EleveBuilder {
    
    private final Eleve eleve = new Eleve();
    
    public EleveBuilder id(String id) {
        eleve.setId(id);
        return this;
    }
    
    public EleveBuilder firstName(String name) {
        eleve.setFirstName(name);
        return this;
    }
    
    public EleveBuilder lastName(String name) {
        eleve.setLastName(name);
        return this;
    }
    
    public EleveBuilder actif(boolean actif) {
        eleve.setActif(actif);
        return this;
    }
    
    public EleveBuilder classe(Classe classe) {
        eleve.setClasse(classe);
        return this;
    }
    
    public EleveBuilder donnees(ObservableList<EleveData> list) {
        eleve.setData(list);
        return this;
    }
    
    public EleveBuilder donnee(EleveData donnee) {
        if(eleve.getData() == null){
            eleve.setData(FXCollections.observableArrayList());
        }
        eleve.getData().add(donnee);
        return this;
    }
    
    public EleveBuilder cleanDonnees() {
        eleve.setData(FXCollections.observableArrayList());
        return this;
    }
    
    public EleveBuilder changements(ObservableList<ChangementClasse> list) {
        eleve.setChangementsClasse(list);
        return this;
    }
    
    public EleveBuilder changement(ChangementClasse p) {
        if(eleve.getChangementsClasse()== null){
            changements(FXCollections.observableArrayList());
        }
        eleve.getChangementsClasse().add(p);
        return this;
    }
    
    public EleveBuilder punitions(ObservableList<Punition> list) {
        eleve.setPunitions(list);
        return this;
    }
    
    public EleveBuilder punition(Punition p) {
        if(eleve.getPunitions() == null){
            punitions(FXCollections.observableArrayList());
        }
        eleve.getPunitions().add(p);
        return this;
    }
    
    public Eleve build(){
        return eleve;
    }
    
}
