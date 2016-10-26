/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mesclasses.model;

import java.io.Serializable;
import java.time.LocalDate;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author rrrt3491
 */
@XmlAccessorType(XmlAccessType.PROPERTY)
public class ChangementClasse extends MonitoredObject implements Serializable, Comparable<ChangementClasse>{
    
    private LocalDate date;
    
    private String type;
    
    private Classe classe;

    public ChangementClasse(){
        super();
        date = LocalDate.now();
    }
    
    @XmlElement(name="date")
    public String getDate() {
        return date.format(Constants.DATE_FORMATTER);
    }
    
    public LocalDate getDateAsDate() {
        return date;
    }

    public void setDate(String date) {
        
        this.date = LocalDate.parse(date, Constants.DATE_FORMATTER);
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    @XmlAttribute(name="type")
    public String getType() {
        return type;
    }

    public void setType(String type) {
        
        this.type = type;
    }

    @XmlTransient
    public Classe getClasse() {
        return classe;
    }

    public void setClasse(Classe classe) {
        this.classe = classe;
    }
    
    
    @Override
    public int compareTo(ChangementClasse t) {
        if(getDateAsDate().isEqual(t.getDateAsDate())){
            // depart avant arrivee
            return t.getType().compareTo(getType());
        }
        return getDateAsDate().compareTo(t.getDateAsDate());
    }
    
    @Override
    public String toString(){
        return "Classe "+classe+", "+type+" le "+date.format(Constants.LONG_DATE_FORMATTER);
    }

    @Override
    public void startChangeDetection() {
    }
    
}
