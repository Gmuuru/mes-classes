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
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
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
public class Mot extends MonitoredObject implements Serializable, Comparable<Mot> {
    
    private String id;
    
    private Eleve eleve;
    
    private final ObjectProperty<LocalDate> dateCloture;
    
    private Seance seance;

    public Mot(){
        super();
        this.id = RandomStringUtils.randomAlphanumeric(5);
        this.dateCloture = new SimpleObjectProperty<>();
    }
    

    @Override
    public void startChangeDetection() {
        this.dateCloture.addListener(dateListener);
    }
    
    @Override
    public List<FError> validate() {
        List<FError> err = Lists.newArrayList();
        err.addAll(Validators.validate(this));
        return err;
    }
    
    public Mot(Eleve eleve, Seance seance){
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

    @XmlElement(name="dateCloture")
    public String getDateCloture() {
        if(dateCloture != null && dateCloture.get() != null){
            return dateCloture.get().format(Constants.DATE_FORMATTER);
        }
        return null;
    }
    
    public LocalDate getDateClotureAsDate() {
        return dateCloture.get();
    }

    public void setDateCloture(String date) {
        this.dateCloture.set(LocalDate.parse(date, Constants.DATE_FORMATTER));
    }

    public void setDateCloture(LocalDate date) {
        this.dateCloture.set(date);
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

    @Override
    public int compareTo(Mot t) {
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
        return new StringBuilder("Mot carnet ").append(id).toString();
    }
}
