/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mesclasses.model;

import java.io.Serializable;
import java.time.LocalDate;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlIDREF;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author rrrt3491
 */
@XmlAccessorType(XmlAccessType.PROPERTY)
public class EleveData extends MonitoredObject implements Serializable, Comparable<EleveData>{
    
    private final String id;
    
    private Eleve eleve;
    
    private final ObjectProperty<LocalDate> date;
    
    private final IntegerProperty cours;
    
    private final BooleanProperty absent;
    
    private final IntegerProperty retard;
    
    private final BooleanProperty travailPasFait;
    
    private final BooleanProperty devoir;
    
    private final BooleanProperty motCarnet;
    
    private final BooleanProperty motSigne;
    
    private final StringProperty oubliMateriel;
    
    private final BooleanProperty exclus;
    
    private final StringProperty remarques;

    public EleveData(){
        super();
        this.id = RandomStringUtils.randomAlphanumeric(5);
        date = new SimpleObjectProperty<>();
        cours = new SimpleIntegerProperty();
        retard = new SimpleIntegerProperty();
        absent = new SimpleBooleanProperty();
        travailPasFait = new SimpleBooleanProperty();
        devoir = new SimpleBooleanProperty();
        motCarnet = new SimpleBooleanProperty();
        motSigne = new SimpleBooleanProperty();
        exclus = new SimpleBooleanProperty();
        oubliMateriel = new SimpleStringProperty();
        remarques = new SimpleStringProperty();
    }
    
    @Override
    public void startChangeDetection() {
        date.addListener(dateListener);
        cours.addListener(intListener);
        retard.addListener(intListener);
        absent.addListener(booleanListener);
        travailPasFait.addListener(booleanListener);
        devoir.addListener(booleanListener);
        motCarnet.addListener(booleanListener);
        motSigne.addListener(booleanListener);
        exclus.addListener(booleanListener);
        oubliMateriel.addListener(stringListener);
        remarques.addListener(stringListener);
    }
    
    public ObjectProperty<LocalDate> dateProperty(){
        return date;
    }
    
    public IntegerProperty coursProperty(){
        return cours;
    }
    
    public IntegerProperty retardProperty(){
        return retard;
    }
    
    public BooleanProperty absentProperty(){
        return absent;
    }
    
    public BooleanProperty travailPasFaitProperty(){
        return travailPasFait;
    }
    
    public BooleanProperty devoirProperty(){
        return devoir;
    }
    
    public BooleanProperty motCarnetProperty(){
        return motCarnet;
    }
    
    public BooleanProperty motSigneProperty(){
        return motSigne;
    }
    
    public BooleanProperty exclusProperty(){
        return exclus;
    }
    
    public StringProperty oubliMaterielProperty(){
        return oubliMateriel;
    }
    
    public StringProperty RemarquesProperty(){
        return remarques;
    }
    
    @XmlAttribute
    @XmlID
    public String getId(){
        return id;
    }
    
    @XmlAttribute
    @XmlIDREF
    public Eleve getEleve() {
        return eleve;
    }

    public void setEleve(Eleve eleve) {
        this.eleve = eleve;
    }

    @XmlElement
    public String getDate() {
        return date.get().format(Constants.DATE_FORMATTER);
    }
    
    public LocalDate getDateAsDate() {
        return date.get();
    }

    public void setDate(String date) {
        
        this.date.set(LocalDate.parse(date, Constants.DATE_FORMATTER));
    }

    public void setDate(LocalDate date) {
        this.date.set(date);
    }

    @XmlElement
    public int getCours() {
        return cours.get();
    }

    public void setCours(int cours) {
        this.cours.set(cours);
    }

    @XmlElement
    public boolean isAbsent() {
        return absent.get();
    }

    public void setAbsent(boolean absent) {
        this.absent.set(absent);
    }

    @XmlElement
    public int getRetard() {
        return retard.get();
    }

    public void setRetard(int retard) {
        this.retard.set(retard);
    }

    @XmlElement
    public boolean isTravailPasFait() {
        return travailPasFait.get();
    }

    public void setTravailPasFait(boolean travailPasFait) {
        this.travailPasFait.set(travailPasFait);
    }

    @XmlElement
    public boolean isDevoir() {
        return devoir.get();
    }

    public void setDevoir(boolean devoir) {
        this.devoir.set(devoir);
    }
    
    @XmlElement
    public boolean isMotCarnet() {
        return motCarnet.get();
    }

    public void setMotCarnet(boolean motCarnet) {
        this.motCarnet.set(motCarnet);
    }

    @XmlElement
    public boolean isMotSigne() {
        return motSigne.get();
    }

    public void setMotSigne(boolean motSigne) {
        this.motSigne.set(motSigne);
    }

    @XmlElement
    public String getOubliMateriel() {
        return oubliMateriel.get();
    }

    public void setOubliMateriel(String oubliMateriel) {
        this.oubliMateriel.set(oubliMateriel);
    }

    @XmlElement
    public boolean isExclus() {
        return exclus.get();
    }

    public void setExclus(boolean exclus) {
        this.exclus.set(exclus);
    }

    @XmlElement
    public String getRemarques() {
        return remarques.get();
    }

    public void setRemarques(String remarques) {
        this.remarques.set(remarques);
    }
    
    public boolean isEmpty(){
        return !isAbsent()
                && getRetard() == 0
                && !isTravailPasFait()
                && !isDevoir()
                && !isMotCarnet()
                && !isMotSigne()
                && StringUtils.isBlank(getOubliMateriel())
                && !isExclus()
                && StringUtils.isBlank(getRemarques());
    }

    @Override
    public int compareTo(EleveData t) {
        int compareDate = getDate().compareTo(t.getDate());
        if(compareDate != 0){
            return compareDate;
        }
        return getCours() - t.getCours();
    }

}
