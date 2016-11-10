/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mesclasses.model.datamodel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import mesclasses.model.Classe;
import mesclasses.model.Cours;
import mesclasses.model.Journee;
import mesclasses.model.Trimestre;

/**
 * Helper class to wrap a list of persons. This is used for saving the
 * list of persons to XML.
 * 
 * @author Marco Jakob
 */
@XmlRootElement(name = "xmldata")
public class XMLData {

    private List<Trimestre> trimestres = new ArrayList<>();
    
    private List<Classe> classes = new ArrayList<>();

    private List<Cours> cours = new ArrayList<>();
    
    private Map<String, Journee> journees = new HashMap<>();
    
    @XmlElement(name = "trimestre")
    @XmlElementWrapper(name="trimestres")
    public List<Trimestre> getTrimestres() {
        return trimestres;
    }

    public void setTrimestres(List<Trimestre> trimestres) {
        this.trimestres = trimestres;
    }
    
    
    @XmlElement(name = "classe")
    @XmlElementWrapper(name="classes")
    public List<Classe> getClasses() {
        return classes;
    }

    public void setClasses(List<Classe> classes) {
        this.classes = classes;
    }
    
    @XmlElement(name = "cours")
    @XmlElementWrapper(name="cours")
    public List<Cours> getCours() {
        return cours;
    }

    public void setCours(List<Cours> cours) {
        this.cours = cours;
    }

    @XmlElement(name = "journees")
    @XmlElementWrapper(name="journees")
    public Map<String, Journee> getJournees() {
        return journees;
    }

    public void setJournees(Map<String, Journee> journees) {
        this.journees = journees;
    }
    
    
}
