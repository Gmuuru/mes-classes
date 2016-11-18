/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mesclasses.model;

import java.time.LocalDate;
import java.util.List;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author rrrt3491
 */
@XmlAccessorType(XmlAccessType.PROPERTY)
public class Seance extends MonitoredObject implements Comparable<Seance>{
    
    private Journee journee;
    
    private LocalDate date;
    
    private Cours cours;
    
    private Classe classe;
    
    private final StringProperty remarques;
    
    private BooleanProperty isFirst;
    
    private BooleanProperty isLast;
    
    private ObservableMap<Eleve, EleveData> donnees;
    
    
    public Seance(){
        remarques = new SimpleStringProperty();
        donnees  = FXCollections.observableHashMap();
    }
    
    @Override
    public void startChangeDetection() {
        remarques.addListener(stringListener);
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
    
    public EleveData getDonneesForEleve(Eleve eleve) {
        if(donnees.containsKey(eleve)){
            return donnees.get(eleve);
        }
        return addNewDonnee(eleve);
    }
    
    public void addDonnee(EleveData donnee){
        donnees.put(donnee.getEleve(), donnee);
    }
    
    public EleveData addNewDonnee(Eleve eleve){
        EleveData newData = new EleveData();
        newData.setEleve(eleve);
        newData.setCours(0);
        newData.setDate(getDate());
        addDonnee(newData);
        newData.startChangeDetection();
        return newData;
    }

    public void addDonnees(List<EleveData> donnees){
        donnees.forEach(d -> this.donnees.put(d.getEleve(), d));
    }
    
    @XmlAttribute
    public String getDate() {
        if(date != null){
            return date.format(Constants.DATE_FORMATTER);
        }
        return "";
    }
    public LocalDate getDateAsDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    @XmlElement
    public Classe getClasse() {
        return classe;
    }

    public void setClasse(Classe classe) {
        this.classe = classe;
    }

    public StringProperty remarquesProperty() {
        return remarques;
    }

    @XmlElement
    public String getRemarques() {
        return remarques.get();
    }
    
    public void setRemarques(String remarques) {
        this.remarques.set(remarques);
    }
    
    @XmlAttribute
    @XmlIDREF
    public Journee getJournee() {
        return journee;
    }

    public void setJournee(Journee journee) {
        this.journee = journee;
    }
    @XmlTransient
    public int index(){
        return journee.getSeances().indexOf(this);
    }
    
    @XmlTransient
    public BooleanProperty isFirst(){
        if(isFirst == null){
            isFirst = new SimpleBooleanProperty(journee.getSeances().indexOf(this) == 0);
        }
        return isFirst;
    }
    
    @XmlTransient
    public BooleanProperty isLast(){
        if(isLast == null){
            isLast = new SimpleBooleanProperty(journee.getSeances().indexOf(this) == journee.getSeances().size() -1);
        }
        return isLast;
    }
    
    @XmlTransient
    public Seance next(){
        if(isLast.get() || index() < 0){
            return null;
        }
        return journee.getSeances().get(index() + 1);
    }
    
    @XmlTransient
    public Seance previous(){
        if(isFirst.get() || index() < 0){
            return null;
        }
        return journee.getSeances().get(index() - 1);
    }
    @Override
    public int compareTo(Seance t) {
        return cours.compareTo(t.getCours());
    }

    
}
