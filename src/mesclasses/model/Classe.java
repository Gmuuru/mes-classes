/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mesclasses.model;

import com.google.common.collect.Lists;
import java.util.List;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlTransient;
import mesclasses.objects.ClasseTab;
import mesclasses.util.Validators;
import mesclasses.util.validation.FError;

/**
 *
 * @author rrrt3491
 */

@XmlAccessorType(XmlAccessType.PROPERTY)
public class Classe extends MonitoredObject implements Comparable<Classe> {
    
    private StringProperty name;
    
    private StringProperty postIt;
    
    private BooleanProperty principale;
    
    private ObservableList<Eleve> eleves;

    private ClasseTab contentTab;
    private ClasseTab rapportTab;
    
    public Classe(){
        this.eleves = FXCollections.observableArrayList();
        this.name = new SimpleStringProperty();
        this.postIt = new SimpleStringProperty();
        this.principale = new SimpleBooleanProperty(Boolean.FALSE);
        
    }
    
    @Override
    public void startChangeDetection() {
        name.addListener(stringListener);
        postIt.addListener(stringListener);
        principale.addListener(booleanListener);
        eleves.addListener(listAddRemoveListener);
        eleves.forEach(e -> e.startChangeDetection());
    }
    
    @Override
    public void resetChange(){
        super.resetChange();
        eleves.forEach(c -> c.resetChange());
    }
    
    @Override
    public List<FError> validate() {
        List<FError> err = Lists.newArrayList();
        err.addAll(Validators.validate(this));
        return err;
    }
    
    public Classe(String name){
        this();
        this.name.set(name);
    }

    public StringProperty nameProperty(){
        return name;
    }
    
    public StringProperty postItProperty(){
        return postIt;
    }
    
    public BooleanProperty principaleProperty(){
        return principale;
    }
    
    
    @XmlID
    @XmlAttribute
    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    @XmlElement(name="postit")
    public String getPostIt() {
        return postIt.get();
    }

    public void setPostIt(String postIt) {
        this.postIt.set(postIt);
    }
    
    @XmlElement(name="principale")
    public Boolean isPrincipale() {
        return principale.get();
    }

    public void setPrincipale(Boolean principale) {
        this.principale.set(principale);
    }

    @XmlElement(name="eleve")
    @XmlElementWrapper(name="eleves")
    public List<Eleve> getEleves() {
        return eleves;
    }

    public void setEleves(ObservableList<Eleve> eleves) {
        this.eleves = eleves;
    }

    @Override
    public int compareTo(Classe t) {
        
        return getName()==null ?
         (t.getName()==null ? 0 : Integer.MIN_VALUE) :
         (t.getName()==null ? Integer.MAX_VALUE : getName().compareTo(t.getName()));
    }
    
    @XmlTransient
    public ClasseTab getContentTab() {
        return contentTab;
    }

    public void setContentTab(ClasseTab tab) {
        this.contentTab = tab;
    }

    @XmlTransient
    public ClasseTab getRapportTab() {
        return rapportTab;
    }

    public void setRapportTab(ClasseTab rapportTab) {
        this.rapportTab = rapportTab;
    }
    
    @Override
    public String toString(){
        return getName();
    }

}