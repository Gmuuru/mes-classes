/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mesclasses.model;

import com.google.common.collect.Lists;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import mesclasses.util.validation.FError;
import mesclasses.util.validation.Validators;

/**
 *
 * @author rrrt3491
 */
@XmlAccessorType(XmlAccessType.PROPERTY)
public class Trimestre extends MonitoredObject implements Comparable<Trimestre> {
    
    private final StringProperty name;
    
    private final ObjectProperty<LocalDate> start;
    
    private final ObjectProperty<LocalDate> end;

    public Trimestre(){
        name = new SimpleStringProperty();
        start = new SimpleObjectProperty<>();
        end = new SimpleObjectProperty<>();
        LocalDate now = LocalDate.now();
        start.set(now);
        end.set(now.plusMonths(3));
    }
    

    @Override
    public void startChangeDetection() {
        name.addListener(stringListener);
        start.addListener(dateListener);
        end.addListener(dateListener);
    }
    

    @Override
    public List<FError> validate() {
        List<FError> err = Lists.newArrayList();
        err.addAll(Validators.validate(this));
        return err;
    }
    
    public StringProperty nameProperty() {
        return name ;
    }
    
    public ObjectProperty<LocalDate> startProperty() {
        return start ;
    }
    
    public ObjectProperty<LocalDate> endProperty() {
        return end ;
    }
    
    @XmlID
    @XmlAttribute(name = "name")
    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    @XmlElement(name = "start")
    public String getStart() {
        if(start == null || start.get() == null){
            return null;
        }
        return start.get().format(Constants.DATE_FORMATTER);
    }
    
    public LocalDate getStartAsDate() {
        return start.get();
    }

    public void setStart(String start) {
        
        this.start.set(LocalDate.parse(start, Constants.DATE_FORMATTER));
    }

    public void setStart(LocalDate startDate) {
        this.start.set(startDate);
    }
    
    @XmlElement(name = "end")
    public String getEnd() {
        return end.get().format(DateTimeFormatter.ofPattern(Constants.DATE_FORMAT));
    }

    public LocalDate getEndAsDate() {
        return end.get();
    }
    
    public void setEnd(String end) {
        this.end.set(LocalDate.parse(end, DateTimeFormatter.ofPattern(Constants.DATE_FORMAT)));
    }
    
    public void setEnd(LocalDate endDate) {
        this.end.set(endDate);
    }

    @Override
    public int compareTo(Trimestre t) {
        return getStart()==null ?
         (t.getStart()==null ? 0 : Integer.MIN_VALUE) :
         (t.getStart()==null ? Integer.MAX_VALUE : getStart().compareTo(t.getStart()));
    }
    
    @Override
    public String getDisplayName(){
        return new StringBuilder("Trimestre ").append(getName()).toString();
    }
    
    @Override
    public String toString(){
        return new StringBuilder(getName()).append(" ").append(getStart()).append(" ").append(getEnd()).toString();
    }
}
