/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mesclasses.model;

import java.io.Serializable;
import java.time.LocalDate;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlIDREF;
import org.apache.commons.lang3.RandomStringUtils;

/**
 *
 * @author rrrt3491
 */
@XmlAccessorType(XmlAccessType.PROPERTY)
public class Punition extends MonitoredObject implements Serializable, Comparable<Punition> {
    
    private String id;
    
    private Eleve eleve;
    
    private ObjectProperty<LocalDate> date;
    
    private int cours;
    
    private final StringProperty texte;
    
    private final BooleanProperty closed;
    
    private Seance seance;

    public Punition(){
        super();
        this.id = RandomStringUtils.randomAlphanumeric(5);
        this.texte = new SimpleStringProperty();
        this.date = new SimpleObjectProperty<>();
        this.closed = new SimpleBooleanProperty(false);
    }
    

    @Override
    public void startChangeDetection() {
    }
    
    public Punition(Eleve eleve, Seance seance, String texte){
        this();
        this.date = new SimpleObjectProperty<>(seance.getDateAsDate());
        this.eleve = eleve;
        this.texte.set(texte);
        this.seance = seance;
    }
    
    public Punition(Eleve eleve, LocalDate date, int cours, String texte){
        this();
        this.eleve = eleve;
        this.date = new SimpleObjectProperty<>(date);
        this.cours = cours;
        this.texte.set(texte);
    }

    @XmlAttribute
    @XmlID
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    
    @XmlElement
    @XmlIDREF
    public Eleve getEleve() {
        return eleve;
    }

    public void setEleve(Eleve eleve) {
        this.eleve = eleve;
    }

    @XmlElement(name="date")
    public String getDate() {
        if(date != null){
            return date.get().format(Constants.DATE_FORMATTER);
        }
        return null;
    }
    
    public LocalDate getDateAsDate() {
        return date.get();
    }

    public void setDate(String date) {
        this.date.set(LocalDate.parse(date, Constants.DATE_FORMATTER));
    }

    public void setDate(LocalDate date) {
        this.date.set(date);
    }
    
    @XmlElement(name="cours")
    public int getCours() {
        return cours;
    }

    public void setCours(int cours) {
        this.cours = cours;
    }

    @XmlElement
    @XmlIDREF
    public Seance getSeance() {
        return seance;
    }

    public void setSeance(Seance seance) {
        this.seance = seance;
    }

    public StringProperty texteProperty(){
        return texte;
    }
    
    @XmlElement(name="texte")
    public String getTexte() {
        return texte.get();
    }

    public void setTexte(String texte) {
        this.texte.set(texte);
    }

    public BooleanProperty closedProperty(){
        return closed;
    }
    
    @XmlElement(name="closed")
    public boolean isClosed() {
        return closed.get();
    }

    public void setClosed(boolean closed) {
        this.closed.set(closed);
    }

    @Override
    public int compareTo(Punition t) {
        int compareEleve = -1;
        if(eleve != null){
            compareEleve = eleve.compareTo(t.getEleve());
        }
        if(compareEleve != 0){
            return compareEleve;
        }
        return date.get().compareTo(t.getDateAsDate());
    }
    
}
