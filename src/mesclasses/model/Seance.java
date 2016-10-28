/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mesclasses.model;

import java.time.LocalDate;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

/**
 *
 * @author rrrt3491
 */
@XmlAccessorType(XmlAccessType.PROPERTY)
public class Seance extends MonitoredObject implements Comparable<Seance>{
    
    private LocalDate date;
    
    private Cours cours;
    
    private Classe classe;
    
    private ObservableMap<Eleve, EleveData> donnees  = FXCollections.observableHashMap();
    
    @Override
    public void startChangeDetection() {
        donnees.addListener(mapListener);
        donnees.values().forEach(d -> d.startChangeDetection());
    }
    
    @Override
    public void resetChange(){
        super.resetChange();
        donnees.values().forEach(c -> c.resetChange());
    }

    @XmlElement
    public Cours getCours() {
        return cours;
    }

    public void setCours(Cours cours) {
        this.cours = cours;
    }

    @XmlElement
    public ObservableMap<Eleve, EleveData> getDonnees() {
        return donnees;
    }

    public void setDonnees(ObservableMap<Eleve, EleveData> donnees) {
        this.donnees = donnees;
    }
    
    public void addDonnee(EleveData donnee){
        donnees.put(donnee.getEleve(), donnee);
    }

    public void addDonnees(List<EleveData> donnees){
        donnees.forEach(d -> this.donnees.put(d.getEleve(), d));
    }
    
    @XmlAttribute
    public String getDate() {
        return date.format(Constants.DATE_FORMATTER);
    }
    public LocalDate getDateAsDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Classe getClasse() {
        return classe;
    }

    public void setClasse(Classe classe) {
        this.classe = classe;
    }

    @Override
    public int compareTo(Seance t) {
        return cours.compareTo(t.getCours());
    }
    
    
}
