/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mesclasses.model;

import java.time.LocalDate;
import java.util.Collections;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlID;

/**
 *
 * @author rrrt3491
 */
@XmlAccessorType(XmlAccessType.PROPERTY)
public class Journee extends MonitoredObject implements Comparable<Journee> {
    
    private LocalDate date;
    private ObservableList<Cours> coursPonctuels = FXCollections.observableArrayList();
    private ObservableList<Seance> seances = FXCollections.observableArrayList();

    @Override
    public void startChangeDetection() {
        coursPonctuels.addListener(listAddRemoveListener);
        coursPonctuels.forEach(c -> c.startChangeDetection());
        seances.addListener(listAddRemoveListener);
        seances.forEach(c -> c.startChangeDetection());
    }

    @XmlID
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

    @XmlElement(name="coursPonctuel")
    @XmlElementWrapper(name="coursPonctuels")
    public ObservableList<Cours> getCoursPonctuels() {
        return coursPonctuels;
    }

    public void setCoursPonctuels(ObservableList<Cours> coursPonctuels) {
        this.coursPonctuels = coursPonctuels;
    }

    @XmlElement(name="seance")
    @XmlElementWrapper(name="seances")
    public ObservableList<Seance> getSeances() {
        return seances;
    }

    public void setSeances(ObservableList<Seance> seances) {
        this.seances = seances;
        if(seances != null){
            Collections.sort(seances);
        }
    }
    
    @Override
    public int compareTo(Journee t) {
        return date.compareTo(t.getDateAsDate());
    }
    
    
}
