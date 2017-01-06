/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mesclasses.model;

import com.google.common.collect.Lists;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
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
public class Devoir extends MonitoredObject implements Serializable, Comparable<Devoir> {
    
    public static final String STATUS_OPEN = "en cours";
    
    private String id;
    
    private Eleve eleve;
    
    private final BooleanProperty closed;
    
    private Seance seance;

    public Devoir(){
        super();
        this.id = "devoir_"+RandomStringUtils.randomAlphanumeric(5);
        this.closed = new SimpleBooleanProperty(false);
    }
    

    @Override
    public void startChangeDetection() {
        this.closed.addListener(booleanListener);
    }
    
    @Override
    public List<FError> validate() {
        List<FError> err = Lists.newArrayList();
        err.addAll(Validators.validate(this));
        return err;
    }
    
    public Devoir(Eleve eleve, Seance seance){
        this();
        this.eleve = eleve;
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

    public LocalDate getDate(){
        if(seance != null){
            return seance.getDateAsDate();
        }
        return null;
    }

    @XmlElement
    @XmlIDREF
    public Seance getSeance() {
        return seance;
    }

    public void setSeance(Seance seance) {
        this.seance = seance;
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
    public int compareTo(Devoir t) {
        int compareEleve = -1;
        if(eleve != null){
            compareEleve = eleve.compareTo(t.getEleve());
        }
        if(compareEleve != 0){
            return compareEleve;
        }
        return getDate().compareTo(t.getDate());
    }
    
    @Override
    public String getDisplayName(){
        return new StringBuilder("Devoir ").append(id).toString();
    }
}
