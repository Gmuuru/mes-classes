/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mesclasses.java.builders;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import mesclasses.model.Classe;
import mesclasses.model.Eleve;

/**
 *
 * @author rrrt3491
 */
public class ClasseBuilder {
    
    private final Classe classe = new Classe();
    
    public ClasseBuilder name(String name) {
        classe.setName(name);
        return this;
    }
    
    public ClasseBuilder eleves(ObservableList<Eleve> list) {
        classe.setEleves(list);
        return this;
    }
    
    public ClasseBuilder eleve(Eleve eleve) {
        if(classe.getEleves() == null){
            classe.setEleves(FXCollections.observableArrayList());
        }
        classe.getEleves().add(eleve);
        return this;
    }
    
    public ClasseBuilder cleanEleves() {
        classe.setEleves(FXCollections.observableArrayList());
        return this;
    }
    
    public Classe build(){
        return classe;
    }
    
}
