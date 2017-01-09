/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mesclasses.model;

import com.google.common.collect.Lists;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
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
import mesclasses.util.validation.FError;
import mesclasses.util.validation.Validators;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author rrrt3491
 */
@XmlAccessorType(XmlAccessType.PROPERTY)
public class EleveData extends MonitoredObject implements Serializable, Comparable<EleveData>{
    
    private String id;
    
    private Eleve eleve;
    
    private Seance seance;
    
    private final ObjectProperty<LocalDate> date;
    
    private final BooleanProperty absent;
    
    private final IntegerProperty retard;
    
    private final BooleanProperty travailPasFait;
    
    private final BooleanProperty devoir;
    
    private final BooleanProperty motCarnet;
    
    private final BooleanProperty motSigne;
    
    private final StringProperty oubliMateriel;
    
    private final BooleanProperty exclus;
    
    private final StringProperty motif;

    public EleveData(){
        super();
        this.id = "donnee_"+RandomStringUtils.randomAlphanumeric(10);
        date = new SimpleObjectProperty<>();
        retard = new SimpleIntegerProperty();
        absent = new SimpleBooleanProperty();
        travailPasFait = new SimpleBooleanProperty();
        devoir = new SimpleBooleanProperty();
        motCarnet = new SimpleBooleanProperty();
        motSigne = new SimpleBooleanProperty();
        exclus = new SimpleBooleanProperty();
        oubliMateriel = new SimpleStringProperty();
        motif = new SimpleStringProperty();
    }
    
    @Override
    public void startChangeDetection() {
        date.addListener(dateListener);
        retard.addListener(intListener);
        absent.addListener(booleanListener);
        travailPasFait.addListener(booleanListener);
        devoir.addListener(booleanListener);
        motCarnet.addListener(booleanListener);
        motSigne.addListener(booleanListener);
        exclus.addListener(booleanListener);
        oubliMateriel.addListener(stringListener);
        motif.addListener(stringListener);
    }

    @Override
    public List<FError> validate() {
        List<FError> err = Lists.newArrayList();
        err.addAll(Validators.validate(this));
        return err;
    }
    
    public ObjectProperty<LocalDate> dateProperty(){
        return date;
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
    
    public StringProperty MotifProperty(){
        return motif;
    }
    
    @XmlAttribute
    @XmlID
    public String getId(){
        return id;
    }
    
    public void setId(String id){
        this.id = id;
    }
    
    @XmlAttribute
    @XmlIDREF
    public Eleve getEleve() {
        return eleve;
    }

    public void setEleve(Eleve eleve) {
        this.eleve = eleve;
    }

    @XmlAttribute
    @XmlIDREF
    public Seance getSeance() {
        return seance;
    }

    public void setSeance(Seance seance) {
        this.seance = seance;
    }
    
    @XmlElement
    public String getDate() {
        if(date.get() != null){
            return date.get().format(Constants.DATE_FORMATTER);
        }
        return "";
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
    public String getMotif() {
        return motif.get();
    }

    public void setMotif(String motif) {
        this.motif.set(motif);
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
                && StringUtils.isBlank(getMotif());
    }

    @Override
    public int compareTo(EleveData t) {
        if(seance == null || getSeance().getDateAsDate() == null){
            return 1;
        }
        if(t.getSeance() == null){
            return -1;
        }
        return getSeance().getDateAsDate().compareTo(t.getSeance().getDateAsDate());
    }

    @Override
    public String getDisplayName(){
        StringBuilder sb =  new StringBuilder("Donnée");
        if(eleve != null){
            sb.append(" ").append(eleve.getDisplayName());
        }
        sb.append(" (").append(id).append(")");
        return sb.toString();
    }
}
