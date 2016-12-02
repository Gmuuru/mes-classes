/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mesclasses.model;

import java.io.Serializable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.layout.VBox;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlTransient;
import org.apache.commons.lang3.RandomStringUtils;

/**
 *
 * @author rrrt3491
 */
@XmlAccessorType(XmlAccessType.PROPERTY)
public class Cours extends MonitoredObject implements Serializable, Comparable<Cours> {
    
    private String id;
    
    private final StringProperty day;
    
    private final ObjectProperty<Classe> classe;
    
    private final StringProperty room;
    
    private final StringProperty week;
    
    private final IntegerProperty startHour;
    
    private final IntegerProperty startMin;
    
    private final IntegerProperty endHour;
    
    private final IntegerProperty endMin;
    
    private final BooleanProperty ponctuel;
    
    private VBox event;

    public Cours(){
        this.id = RandomStringUtils.randomAlphanumeric(5);
        day = new SimpleStringProperty();
        classe = new SimpleObjectProperty<>();
        room = new SimpleStringProperty("");
        week = new SimpleStringProperty();
        startHour = new SimpleIntegerProperty(8);
        startMin = new SimpleIntegerProperty(0);
        endHour = new SimpleIntegerProperty(9);
        endMin = new SimpleIntegerProperty(0);
        ponctuel = new SimpleBooleanProperty(false);
    }
    
    @Override
    public void startChangeDetection() {
        day.addListener(stringListener);
        classe.addListener(objectListener);
        room.addListener(stringListener);
        week.addListener(stringListener);
        startHour.addListener(intListener);
        startMin.addListener(intListener);
        endHour.addListener(intListener);
        endMin.addListener(intListener);
        
    }
    
    public StringProperty dayProperty(){
        return day;
    }
    
    public StringProperty roomProperty(){
        return room;
    }
    
    public StringProperty weekProperty(){
        return week;
    }
    
    public IntegerProperty startHourProperty(){
        return startHour;
    }
    
    public IntegerProperty startMinProperty(){
        return startMin;
    }
    
    public IntegerProperty endHourProperty(){
        return endHour;
    }
    
    public IntegerProperty endMinProperty(){
        return endMin;
    }

    public BooleanProperty ponctuelProperty(){
        return ponctuel;
    }
    
    @XmlAttribute
    @XmlID
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    
    @XmlElement(name="day")
    public String getDay() {
        return day.get();
    }

    public void setDay(String Day) {
        this.day.set(Day);
    }

    public ObjectProperty<Classe> classeProperty(){
        return classe;
    }
    
    @XmlAttribute
    @XmlIDREF
    public Classe getClasse() {
        return classe.get();
    }

    public void setClasse(Classe classe) {
        this.classe.set(classe);
    }

    @XmlElement(name="room")
    public String getRoom() {
        return room.get();
    }

    public void setRoom(String room) {
        this.room.set(room);
    }

    @XmlElement(name="week")
    public String getWeek() {
        return week.get();
    }

    public void setWeek(String semaine) {
        this.week.set(semaine);
    }
    
    @XmlElement(name="startHour")
    public Integer getStartHour() {
        return startHour.get();
    }

    public void setStartHour(Integer startHour) {
        this.startHour.set(startHour);
    }

    @XmlElement(name="startMin")
    public Integer getStartMin() {
        return startMin.get();
    }

    public void setStartMin(Integer startMin) {
        this.startMin.set(startMin);
    }

    @XmlElement(name="endHour")
    public Integer getEndHour() {
        return endHour.get();
    }

    public void setEndHour(Integer endHour) {
        this.endHour.set(endHour);
    }

    @XmlElement(name="endMin")
    public Integer getEndMin() {
        return endMin.get();
    }

    public void setEndMin(Integer endMin) {
        this.endMin.set(endMin);
    }

    @XmlElement(name="ponctuel")
    public Boolean isPonctuel() {
        return ponctuel.get();
    }

    public void setPonctuel(Boolean ponctuel) {
        this.ponctuel.set(ponctuel);
    }
    
    @XmlTransient
    public VBox getEvent() {
        return event;
    }

    public void setEvent(VBox event) {
        this.event = event;
    }
    
    @Override
    public int compareTo(Cours t) {
        String cmp = (getStartHour() * 60 + getStartMin())+classe.getName();
        String cmpt = (t.getStartHour() * 60 + t.getStartMin())+t.getClasse().getName();
        return classe.getName()==null ?
         (t.classe.getName()==null ? 0 : Integer.MIN_VALUE) :
         (t.classe.getName()==null ? Integer.MAX_VALUE : cmp.compareTo(cmpt));
    }
    
    @Override
    public String toString(){
        return "Cours ("+getClasse()+", "+getDay()+", "+getStartHour()+"->"+getEndHour()+", "+getRoom()+", "+getWeek()+")";
    }
    
}
