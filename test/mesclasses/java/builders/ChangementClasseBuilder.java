/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mesclasses.java.builders;

import java.time.LocalDate;
import mesclasses.model.ChangementClasse;
import mesclasses.model.Classe;

/**
 *
 * @author rrrt3491
 */
public class ChangementClasseBuilder {
    
    private final ChangementClasse changementClasse = new ChangementClasse();
    
    
    public ChangementClasseBuilder classe() {
        return classe(new Classe());
    }
    
    public ChangementClasseBuilder classe(Classe classe) {
        changementClasse.setClasse(classe);
        return this;
    }
    
    public ChangementClasseBuilder date(LocalDate date) {
        changementClasse.setDate(date);
        return this;
    }
    
    public ChangementClasseBuilder type(String type) {
        changementClasse.setType(type);
        return this;
    }
    
    public ChangementClasse build(){
        return changementClasse;
    }
    
}
