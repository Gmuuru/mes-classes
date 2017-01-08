/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mesclasses.model;

import com.google.common.collect.Lists;
import java.time.LocalDate;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import mesclasses.handlers.EleveDataMapXmlAdapter;
import mesclasses.handlers.PropertiesCache;
import mesclasses.util.NodeUtil;
import mesclasses.util.validation.FError;
import mesclasses.util.validation.Validators;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author rrrt3491
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class Seance extends MonitoredObject implements Comparable<Seance>{
    
    @XmlAttribute
    @XmlID
    private String id;
    
    @XmlAttribute
    @XmlIDREF
    private Journee journee;
    
    @XmlAttribute
    @XmlIDREF
    private Cours cours;
    
    @XmlAttribute
    @XmlIDREF
    private Classe classe;
    
    @XmlJavaTypeAdapter(EleveDataMapXmlAdapter.class)
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
    public List<FError> validate() {
        List<FError> err = Lists.newArrayList();
        err.addAll(Validators.validate(this));
        return err;
    }
    
    @Override
    public void resetChange(){
        super.resetChange();
    }
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Cours getCours() {
        return cours;
    }

    public void setCours(Cours cours) {
        this.cours = cours;
    }

    public ObservableMap<Eleve, EleveData> getDonnees() {
        return donnees;
    }

    public void setDonnees(ObservableMap<Eleve, EleveData> donnees) {
        this.donnees = donnees;
    }
    
    @XmlTransient
    public LocalDate getDateAsDate() {
        if(journee == null){
            return null;
        }
        return journee.getDateAsDate();
    }

    public Classe getClasse() {
        return classe;
    }

    public void setClasse(Classe classe) {
        this.classe = classe;
    }
    
    public Journee getJournee() {
        return journee;
    }

    public void setJournee(Journee journee) {
        this.journee = journee;
    }
    public int index(){
        return journee.getSeances().indexOf(this);
    }
    
    @Override
    public int compareTo(Seance t) {
        return cours.compareTo(t.getCours());
    }
    @Override
    public String toString(){
        return display();
    }
    
    public String display(){
        StringBuilder sb = new StringBuilder();
        sb.append(NodeUtil.getStartTime(getCours()))
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
        .append(NodeUtil.getEndTime(getCours()));
        return sb.toString();
    }

    @Override
    public String getDisplayName(){
        return new StringBuilder("Séance ").append(id).toString();
    }
    
}
