/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mesclasses.model;

import com.google.common.collect.Lists;
import java.io.Serializable;
import java.util.List;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlIDREF;
import mesclasses.util.validation.FError;
import mesclasses.util.validation.Validators;
import org.apache.commons.lang3.RandomStringUtils;

/**
 *
 * @author rrrt3491
 */
@XmlAccessorType(XmlAccessType.PROPERTY)
public class Punition extends MonitoredObject implements Serializable, Comparable<Punition> {
    
    private String id;
    
    private Eleve eleve;
    
    private final StringProperty texte;
    
    private final BooleanProperty closed;
    
    private Seance seance;

    public Punition(){
        super();
        this.id = RandomStringUtils.randomAlphanumeric(5);
        this.texte = new SimpleStringProperty();
        this.closed = new SimpleBooleanProperty(false);
    }
    

    @Override
    public void startChangeDetection() {
        this.closed.addListener(booleanListener);
        this.texte.addListener(stringListener);
    }
    
    @Override
    public List<FError> validate() {
        List<FError> err = Lists.newArrayList();
        err.addAll(Validators.validate(this));
        return err;
    }
    
    public Punition(Eleve eleve, Seance seance, String texte){
        this();
        this.eleve = eleve;
        this.texte.set(texte);
        this.seance = seance;
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
        if(t == null){
            return 1;
        }
        if(eleve != null){
            compareEleve = eleve.compareTo(t.getEleve());
        }
        if(compareEleve != 0){
            return compareEleve;
        }
        if(seance == null){
            return t.getSeance() != null ? -1 : 0;
        }
        return getSeance().getDateAsDate().compareTo(t.getSeance().getDateAsDate());
    }
    
    @Override
    public String getDisplayName(){
        return new StringBuilder("Punition ").append(id).toString();
    }
}
