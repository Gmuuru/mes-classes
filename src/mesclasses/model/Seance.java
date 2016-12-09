/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mesclasses.model;

import java.time.LocalDate;
import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlTransient;
import mesclasses.handlers.PropertiesCache;
import mesclasses.util.NodeUtil;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author rrrt3491
 */
@XmlAccessorType(XmlAccessType.PROPERTY)
public class Seance extends MonitoredObject implements Comparable<Seance>{
    
    private String id;
    
    private Journee journee;
    
    private Cours cours;
    
    private Classe classe;
    
    private ObservableMap<Eleve, EleveData> donnees;
    
    public Seance(){
        id = "seance_"+RandomStringUtils.randomAlphanumeric(5);
        donnees = FXCollections.observableHashMap();
    }
    
    @Override
    public void startChangeDetection() {
        donnees.addListener(mapListener);
    }
    
    @Override
    public void resetChange(){
        super.resetChange();
    }

    @XmlAttribute
    @XmlID
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @XmlAttribute
    @XmlIDREF
    public Cours getCours() {
        return cours;
    }

    public void setCours(Cours cours) {
        this.cours = cours;
    }

    @XmlIDREF
    @XmlElement(name="donnee")
    @XmlElementWrapper(name="donnees")
    public ObservableList<EleveData> getDonnees() {
        return FXCollections.observableList(new ArrayList<>(donnees.values()));
    }

    public void setDonnees(ObservableList<EleveData> donnees) {
        donnees.forEach(d -> this.donnees.put(d.getEleve(), d));
    }
    
    public ObservableMap<Eleve, EleveData> getDonneesAsMap() {
        return donnees;
    }
    
    public void setDonneesFromMap(ObservableMap<Eleve, EleveData> donnees) {
        this.donnees = donnees;
    }
    
    @XmlTransient
    public LocalDate getDateAsDate() {
        return journee.getDateAsDate();
    }

    @XmlAttribute
    @XmlIDREF
    public Classe getClasse() {
        return classe;
    }

    public void setClasse(Classe classe) {
        this.classe = classe;
    }
    
    @XmlAttribute
    @XmlIDREF
    public Journee getJournee() {
        return journee;
    }

    public void setJournee(Journee journee) {
        this.journee = journee;
    }
    public int index(){
        return journee.getSeances().indexOf(this);
    }
    
    @XmlTransient
    public boolean isFirst(){
        return (journee.getSeances().indexOf(this) == 0);
    }
    
    @XmlTransient
    public boolean isLast(){
        return (journee.getSeances().indexOf(this) == journee.getSeances().size() -1);
    }
    
    public Seance next(){
        if(isLast() || index() < 0){
            return null;
        }
        return journee.getSeances().get(index() + 1);
    }
    
    public Seance previous(){
        if(isFirst() || index() < 0){
            return null;
        }
        return journee.getSeances().get(index() - 1);
    }
    @Override
    public int compareTo(Seance t) {
        return cours.compareTo(t.getCours());
    }
    @Override
    public String toString(){
        return "seance "+id+" : "+display();
    }
    
    public String display(){
        StringBuilder sb = new StringBuilder();
        sb.append(NodeUtil.formatTime(getCours().getStartHour(), getCours().getStartMin()))
        .append("   ")
        .append(getClasse().getName());
        if(StringUtils.isNotBlank(getCours().getRoom())){
            sb.append(" en ").append(getCours().getRoom());
        }
        PropertiesCache config = PropertiesCache.getInstance();
        if(!getCours().getWeek().equals(config.getProperty(Constants.CONF_WEEK_DEFAULT))){
            sb.append(" (").append(getCours().getWeek()).append(")");
        }
        sb.append("   ")
        .append(NodeUtil.formatTime(getCours().getEndHour(), getCours().getEndMin()));
        return sb.toString();
    }

    
}
