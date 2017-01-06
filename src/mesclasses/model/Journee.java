/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mesclasses.model;

import com.google.common.collect.Lists;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlID;
import mesclasses.util.validation.FError;
import mesclasses.util.validation.ListValidators;
import mesclasses.util.validation.Validators;

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
    
    @Override
    public List<FError> validate() {
        List<FError> err = Lists.newArrayList();
        err.addAll(Validators.validate(this));
        err.addAll(ListValidators.validateCoursList(coursPonctuels));
        err.addAll(ListValidators.validateSeanceList(this));
        return err;
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

    public void setDate(String date) {
        this.date = LocalDate.parse(date, Constants.DATE_FORMATTER);
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
        if(date == null){
            return 1;
        }
        return date.compareTo(t.getDateAsDate());
    }
    
    @Override
    public String getDisplayName(){
        StringBuilder sb = new StringBuilder("Journée");
        if(date != null){
            sb.append(" ").append(getDate()).toString();
        }
        return sb.toString();
    }
    
}
