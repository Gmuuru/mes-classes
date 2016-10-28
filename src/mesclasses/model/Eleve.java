/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mesclasses.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlIDREF;
import org.apache.commons.lang3.RandomStringUtils;

/**
 *
 * @author rrrt3491
 */
@XmlAccessorType(XmlAccessType.PROPERTY)
public class Eleve extends MonitoredObject implements Serializable, Comparable<Eleve> {
    
    private String id;
    
    private final StringProperty firstName;
    
    private final StringProperty lastName;
    
    private Classe classe;
    
    private ObservableList<EleveData> data;
    
    private ObservableList<Punition> punitions;
    
    private ObservableList<ChangementClasse> changementsClasse;
    
    private final BooleanProperty actif;

    public Eleve(){
        super();
        this.id = RandomStringUtils.randomAlphanumeric(5);
        this.firstName = new SimpleStringProperty();
        this.lastName = new SimpleStringProperty();
        this.actif = new SimpleBooleanProperty(true);
        this.data = FXCollections.observableArrayList();
        this.punitions = FXCollections.observableArrayList();
        this.changementsClasse = FXCollections.observableArrayList();
    }
    

    @Override
    public void startChangeDetection() {
        
        firstName.addListener(stringListener);
        lastName.addListener(stringListener);
        actif.addListener(booleanListener);
        //pas de listener sur la taille des data, pour le cas ou on rajoute des data vides
        // seule la modification des data doit declencher le saveNeeded
        //data.addListener(listAddRemoveListener);
        punitions.addListener(listAddRemoveListener);
        changementsClasse.addListener(listAddRemoveListener);
        data.forEach(d -> d.startChangeDetection());
        punitions.forEach(p -> p.startChangeDetection());
        changementsClasse.forEach(p -> p.startChangeDetection());
    }
    
    @Override
    public void resetChange(){
        super.resetChange();
        data.forEach(c -> c.resetChange());
        punitions.forEach(c -> c.resetChange());
        changementsClasse.forEach(c -> c.resetChange());
    }
    
    @XmlAttribute
    @XmlID
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public StringProperty firstNameProperty(){
        return firstName;
    }
    
    public StringProperty lastNameProperty(){
        return lastName;
    }
    
    public BooleanProperty actifProperty(){
        return actif;
    }
    
    @XmlAttribute
    public String getFirstName() {
        if(firstName == null){
            return "";
        }
        return firstName.get();
    }

    public void setFirstName(String firstName) {
        this.firstName.set(firstName);
    }

    @XmlAttribute
    public String getLastName() {
        if(lastName == null){
            return "";
        }
        return lastName.get();
    }

    public void setLastName(String lastName) {
        this.lastName.set(lastName);
    }

    @XmlAttribute
    @XmlIDREF
    public Classe getClasse() {
        return classe;
    }

    public void setClasse(Classe classe) {
        this.classe = classe;
    }

    @XmlElement(name="eleveData")
    @XmlElementWrapper(name="data")
    public List<EleveData> getData() {
        return data;
    }

    public void setData(ObservableList<EleveData> data) {
        this.data = data;
    }
    
    @XmlAttribute
    public boolean isActif() {
        return actif.get();
    }

    public void setActif(boolean actif) {
        this.actif.set(actif);
    }

    @XmlElement(name="punition")
    @XmlElementWrapper(name="punitions")
    public List<Punition> getPunitions() {
        return punitions;
    }

    public void setPunitions(ObservableList<Punition> punitions) {
        this.punitions = punitions;
    }
    
    @XmlElement(name="changementClasse")
    @XmlElementWrapper(name="changementsClasse")
    public List<ChangementClasse> getChangementsClasse() {
        return changementsClasse;
    }

    public void setChangementsClasse(ObservableList<ChangementClasse> changementsClasse) {
        this.changementsClasse = changementsClasse;
    }
    
    public String getFullName(){
        return new StringBuilder(getLastName()).append(" ").append(getFirstName()).toString();
    }

    public String getDisplayName(){
        return new StringBuilder(getFirstName()).append(" ").append(getLastName()).toString();
    }
    
    @Override
    public int compareTo(Eleve t) {
        return getFullName()==null ?
         (t.getFullName()==null ? 0 : Integer.MIN_VALUE) :
         (t.getFullName()==null ? Integer.MAX_VALUE : getFullName().compareTo(t.getFullName()));
    }
    
    @Override
    public String toString(){
        return getFirstName()+" "+getLastName();
    }
    
    public boolean isInClasse(LocalDate date){
        if(changementsClasse == null || changementsClasse.isEmpty()){
            return true;
        }
        boolean ok = false;
        for(ChangementClasse cc : changementsClasse){
            if(cc.getType().equals(Constants.CHANGEMENT_CLASSE_ARRIVEE)){
                ok = ok || cc.getDateAsDate().isBefore(date) || cc.getDateAsDate().isEqual(date);
            } else {
                ok = ok && (cc.getDateAsDate().isAfter(date) || cc.getDateAsDate().isEqual(date));
            }
        }
        return ok;
    }
    
}
