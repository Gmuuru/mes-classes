/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mesclasses.java.builders;

import java.time.LocalDate;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import mesclasses.model.Journee;
import mesclasses.model.Seance;

/**
 *
 * @author rrrt3491
 */
public class JourneeBuilder {
    
    private final Journee journee = new Journee();
    
    public JourneeBuilder date(LocalDate date) {
        journee.setDate(date);
        return this;
    }
    
    public JourneeBuilder seances(ObservableList<Seance> seances) {
        journee.setSeances(seances);
        return this;
    }
    
    public JourneeBuilder seance(Seance seance) {
        if(journee.getSeances() == null){
            seances(FXCollections.emptyObservableList());
        }
        journee.getSeances().add(seance);
        return this;
    }
    
    public Journee build(){
        return journee;
    }
    
}
