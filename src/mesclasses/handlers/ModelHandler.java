/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mesclasses.handlers;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import mesclasses.model.Classe;
import mesclasses.model.Constants;
import mesclasses.model.Cours;
import mesclasses.model.Eleve;
import mesclasses.model.EleveData;
import mesclasses.model.Journee;
import mesclasses.model.Punition;
import mesclasses.model.Seance;
import mesclasses.model.Trimestre;
import mesclasses.model.datamodel.ObservableData;
import mesclasses.util.EleveFileUtil;
import mesclasses.util.NodeUtil;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author rrrt3491
 */
public class ModelHandler {
    
    private ObservableData data;
    private static ModelHandler handler;
    
    public static ModelHandler getInstance(){
        if(handler == null){
            handler = new ModelHandler();
        }
        return handler;
    }
    
    public void injectData(ObservableData data){
        this.data = data;
    }

    public ObservableData getData() {
        return data;
    }
    
    public ObservableList<Trimestre> getTrimestres(){
        return data.getTrimestres();
    }
    
    public ObservableList<Classe> getClasses(){
        return data.getClasses();
    }
    
    public ObservableList<Cours> getCours(){
        return data.getCours();
    }
    
    public ObservableMap<LocalDate, Journee> getJournees(){
        return data.getJournees();
    }
    
    /* TRIMESTRES */
    
    public Trimestre createTrimestre(){
        Trimestre trimestre = new Trimestre();
        trimestre.setName("Nom trimestre");
        if(!data.getTrimestres().isEmpty()){
            Trimestre last = data.getTrimestres().get(data.getTrimestres().size()-1);
            trimestre.setStart(last.getEndAsDate().plusDays(1));
            trimestre.setEnd(trimestre.getStartAsDate().plusMonths(3));
        }
        data.getTrimestres().add(trimestre);
        trimestre.startChangeDetection();
        Collections.sort(data.getTrimestres());
        return trimestre;
    }
    
    public void cleanupTrimestres(){
        data.getTrimestres().removeIf(t -> StringUtils.isEmpty(t.getName()));
    }
    
    public void delete(Trimestre trimestre){
        data.getTrimestres().remove(trimestre);
    }
    
    public Trimestre getForDate(LocalDate date){
        for(Trimestre trimestre : data.getTrimestres()){
            if(NodeUtil.isBetween(date, trimestre.getStartAsDate(), trimestre.getEndAsDate())){
             return trimestre;
            }
        }
        return null;
    }
    
    /* CLASSES */
    
    public Classe createClasse(Classe classe){
        data.getClasses().add(classe);
        classe.startChangeDetection();
        return classe;
    }
    
    public void cleanupClasses(){
        data.getClasses().removeIf(t -> StringUtils.isEmpty(t.getName()));
        data.getClasses().forEach(classe -> {
            cleanupElevesForClasse(classe);
        });
    }
    
    public void delete(Classe classe){
        // eleves
        while(!classe.getEleves().isEmpty()){
            delete(classe.getEleves().get(0));
        }
        
        //cours
        data.getCours().removeIf((c) -> c.getClasse().getName().equals(classe.getName()));
        
        data.getClasses().remove(classe);
    }
    
    
    /* ELEVES */
    
    public Eleve createEleve(Eleve eleve){
        eleve.getClasse().getEleves().add(eleve);
        Collections.sort(eleve.getClasse().getEleves());
        eleve.startChangeDetection();
        return eleve;
    }
    
    public void cleanupElevesForClasse(Classe classe){
        if(classe == null){
            return;
        }
        classe.getEleves().removeIf(t -> StringUtils.isEmpty(t.getFirstName()) || StringUtils.isEmpty(t.getLastName()));
        Collections.sort(classe.getEleves());
         classe.getEleves().stream().forEach(eleve -> {
            cleanupDataForEleve(eleve);
         });
    }
    
    public void delete(Eleve eleve){
        eleve.getClasse().getEleves().remove(eleve);
        EleveFileUtil.deleteFilesForEleve(eleve);
    }
    
    public List<Eleve> getOnlyActive(List<Eleve> list){
        return list.stream().filter(e -> e.isActif()).collect(Collectors.toList());
    }
    
    public Eleve moveEleveToClasse(Eleve source, Classe classe){
        Eleve clone = new Eleve();
        clone.setActif(true);
        clone.setClasse(classe);
        clone.setId(source.getId());
        clone.setFirstName(source.getFirstName());
        clone.setLastName(source.getLastName());
        return createEleve(clone);
    }
    
    /* COURS */
    
    /**
     * utilisé par l'emploi du temps
     * @param day
     * @return 
     */
    public ObservableList<Cours> getCoursForDay(String day){
        return data.getCours().stream().filter(c -> c.getDay().equalsIgnoreCase(day)).collect(
                Collectors.toCollection(FXCollections::observableArrayList));
    }
    
    public ObservableList<Cours> getCoursForClasse(Classe classe){
        return data.getCours().stream().filter(c -> 
                c.getClasse().getName().equals(classe.getName())
        ).collect(Collectors.toCollection(FXCollections::observableArrayList));
    }
    
    public ObservableList<Cours> getCoursForDate(LocalDate date){
        
        String day = NodeUtil.getJour(date);
        return data.getCours().stream().filter(c -> 
                c.getDay().equals(day) && NodeUtil.coursHappensThisDay(c, date)
        ).collect(Collectors.toCollection(FXCollections::observableArrayList));
    }
    
    public ObservableList<Cours> getCoursForDateAndClasse(LocalDate date, Classe classe){
        
        String day = NodeUtil.getJour(date);
        return data.getCours().stream().filter(c -> 
                c.getDay().equals(day) 
                && c.getClasse().getName().equals(classe.getName())
                && NodeUtil.coursHappensThisDay(c, date)
        ).collect(Collectors.toCollection(FXCollections::observableArrayList));
    }
    
    public ObservableList<Cours> getCoursForDayAndClasse(String day, Classe classe){
        return data.getCours().stream().filter(c -> 
                c.getDay().equals(day) && c.getClasse().getName().equals(classe.getName())
        ).collect(Collectors.toCollection(FXCollections::observableArrayList));
    }
    
    /**
     * Utilisé par la page content
     * @param date
     * @param classe
     * @return 
     */
    public int getNbCoursForDay(LocalDate date, Classe classe){
        final String formattedDay = Constants.DAYMAP.get(date.getDayOfWeek());
        int nbCoursPrevus = getCoursForDayAndClasse(formattedDay, classe).size();
        for(Eleve eleve : classe.getEleves()){
            for(EleveData eleveData : eleve.getData()){
                if(eleveData.getDateAsDate().isEqual(date) ){
                    nbCoursPrevus = Math.max(nbCoursPrevus, eleveData.getCours());
                }
            }
        }
        return nbCoursPrevus;
    }
    
    
    public Cours createCours(Cours newCours){
        data.getCours().add(newCours);
        newCours.startChangeDetection();
        return newCours;
    }
    
    public void cleanupCours(){
        data.getCours().removeIf(t -> t.getClasse() == null);
    }
    
    public void delete(Cours cours){
        data.getCours().remove(cours);
    }
    
    public Cours cloneCours(Cours sourceCours){
        Cours clone = new Cours();
        clone.setClasse(sourceCours.getClasse());
        clone.setDay(sourceCours.getDay());
        clone.setEndHour(sourceCours.getEndHour());
        clone.setEndMin(sourceCours.getEndMin());
        clone.setStartHour(sourceCours.getStartHour());
        clone.setStartMin(sourceCours.getStartMin());
        clone.setWeek(sourceCours.getWeek());
        clone.setRoom(sourceCours.getRoom());
        clone.setEvent(sourceCours.getEvent());
        return clone;
    }
    
    public void updateCours(Cours dest, Cours source){
        int index = getCours().indexOf(dest);
        getCours().set(index, source);
        getCours().get(index).setChanged(true);
    }
    
    //DATA
    public EleveData createEleveData(Eleve eleve, int cours, LocalDate date){
        EleveData newData = new EleveData();
        newData.setEleve(eleve);
        newData.setCours(cours);
        newData.setDate(date);
        eleve.getData().add(newData);
        newData.startChangeDetection();
        return newData;
    }
    
    public EleveData createEleveData(Eleve eleve, Seance seance){
        EleveData newData = new EleveData();
        newData.setEleve(eleve);
        newData.setCours(0);
        newData.setDate(seance.getDate());
        seance.addDonnee(newData);
        newData.startChangeDetection();
        return newData;
    }
    
    
    public EleveData getDataForCoursAndDate(Eleve eleve, int cours, LocalDate date){
        if(eleve == null || eleve.getData() == null){
            return null;
        }
        for(EleveData eleveData : eleve.getData()){
            if(eleveData.getDateAsDate().isEqual(date) && eleveData.getCours() == cours){
                return eleveData;
            }
        }
        return createEleveData(eleve, cours, date);
    }
    
    public List<EleveData> filterByTrimestre(List<EleveData> liste, Trimestre trimestre, LocalDate optionalEnDate){
        LocalDate tmp = trimestre.getEndAsDate();
        if(optionalEnDate != null){
            tmp = optionalEnDate;
        }
        final LocalDate endDate = tmp;
        return liste.stream().filter(d -> NodeUtil.isBetween(d.getDateAsDate(), trimestre.getStartAsDate(), endDate))
                .collect(Collectors.toList());
    }
    
    public List<Punition> filterByTrimestre(List<Punition> liste, Trimestre trimestre){
        LocalDate tmp = trimestre.getEndAsDate();
        final LocalDate endDate = tmp;
        return liste.stream().filter(d -> NodeUtil.isBetween(d.getDateAsDate(), trimestre.getStartAsDate(), endDate))
                .collect(Collectors.toList());
    }
    
    public int countCumulTravail(List<EleveData> liste){
        return (int)liste.stream().filter(d -> d.isTravailPasFait()).count();
    }
    
    public int countCumulRetard(List<EleveData> liste){
        return (int)liste.stream().filter(d -> d.getRetard() > 0).count();
    }
    
    public int countCumulOubli(List<EleveData> liste){
        return (int)liste.stream().filter(d -> StringUtils.isNotBlank(d.getOubliMateriel())).count();
    }

    public void cleanupDataForEleve(Eleve eleve){
        eleve.getData().removeIf(eleveData -> eleveData.isEmpty());
    }
    // PUNITIONS
    
    public Punition createPunition(Eleve eleve, LocalDate date, Cours cours, String texte) {
        //TODO
        Punition punition = new Punition(eleve, date, 0, texte);
        eleve.getPunitions().add(punition);
        punition.startChangeDetection();
        return punition;
    }
    
    public void deletePunition(Punition punition){
        punition.getEleve().getPunitions().remove(punition);
    }
    
    // JOURNEES
    
    public Journee getJournee(LocalDate date){
        if(getJournees().containsKey(date)){
            return getJournees().get(date);
        }
        return buildJournee(date);
    }
    
    public Journee buildJournee(LocalDate date){
        Journee journee = new Journee();
        List<Cours> cours = getCoursForDate(date);
        cours.forEach(c -> {
            journee.getSeances().add(createSeance(c.getClasse(), date, c));
        });
        journee.setDate(date);
        return journee;
    } 
    
    public void addCoursPonctuel(Journee journee, Classe classe){
        Cours coursPonctuel = new Cours();
        coursPonctuel.setClasse(classe);
        coursPonctuel.setDay(NodeUtil.getJour(journee.getDateAsDate()));
        coursPonctuel.setWeek("ponctuel");
        coursPonctuel.setStartHour(16);
        coursPonctuel.setStartMin(00);
        coursPonctuel.setEndHour(17);
        coursPonctuel.setEndMin(00);
        journee.getCoursPonctuels().add(coursPonctuel);
        journee.getSeances().add(createSeance(classe, journee.getDateAsDate(), coursPonctuel));
    }
    
    public void createJournee(Journee journee){
        data.getJournees().put(journee.getDateAsDate(), journee);
    }
    // SEANCES
    
    public Seance createSeance(Classe classe, LocalDate date, Cours cours){
        Seance seance = new Seance();
        seance.setCours(cours);
        seance.setDate(date);
        return seance;
    }
}
