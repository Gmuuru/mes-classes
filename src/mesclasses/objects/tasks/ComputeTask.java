/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mesclasses.objects.tasks;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import mesclasses.handlers.ModelHandler;
import mesclasses.model.Classe;
import mesclasses.model.Constants;
import mesclasses.model.Cours;
import mesclasses.model.Eleve;
import mesclasses.model.EleveData;
import mesclasses.model.Journee;
import mesclasses.model.Seance;

/**
 *
 * @author rrrt3491
 */
public class ComputeTask extends AppTask<Object> {
    
    private ModelHandler handler;
    
    private int nbJournees;
    private int nbCoursPonctuels;
    private int nbSeances;
    private int nbDonnees;
    
    public ComputeTask(){
        super();
        updateProgress(0, 5.0);
    }
    @Override
    protected Object call() throws Exception {
        handler = ModelHandler.getInstance();
        if(handler.getJournees().isEmpty() && ! handler.getTrimestres().isEmpty()){
            process();
        }
        return null;
    }

    private void process() {
        try {
            LocalDate day = handler.getTrimestres().get(0).getStartAsDate();
            long nbDays = ChronoUnit.DAYS.between(day, LocalDate.now());
            while(day.isBefore(LocalDate.now())){
                Journee journee = createJournee(day);
                handler.getJournees().put(day, journee);
                day = day.plusDays(1);
                updateProgress(nbJournees, nbDays);
                handler.createJournee(journee);
            }
            log("journées ajoutées : "+handler.getJournees().size());
        } catch(Exception e){
            log(e);
        }
        log("journees : "+nbJournees+", seances "+nbSeances+", cours additionnels : "+nbCoursPonctuels+", donnees : "+nbDonnees);
    }
    
    public Journee createJournee(LocalDate date){
        log("Journee pour le "+date);
        if(date == null){
            return null;
        }
        Journee journee = new Journee();
        journee.setDate(date);
        handler.getClasses().forEach(c -> {
            journee.getSeances().addAll(buildSeancesForClasse(journee, c, date));
        });
        Collections.sort(journee.getCoursPonctuels());
        Collections.sort(journee.getSeances());
        return journee;
    }

    private ObservableList<Seance> buildSeancesForClasse(Journee journee, Classe classe, LocalDate date) {
        
        ObservableList<Seance> res = FXCollections.observableArrayList();
        String jour = Constants.DAYMAP.get(date.getDayOfWeek());
        List<Cours> listeCours = handler.getCoursForDayAndClasse(jour, classe);
        Map<Integer, List<EleveData>> donneesParCours = getDataParCoursForDate(classe, date);
        
        log("Classe "+classe+", cours prévus = "+listeCours.size()+", cours totaux = "+donneesParCours.size());
        //cours normaux
        int index;
        for(index = 0; index < listeCours.size(); index++){
            Seance seance = handler.createSeance(classe, date, listeCours.get(index));
            if(donneesParCours.containsKey(index+1)){
                nbDonnees+=donneesParCours.get(index+1).size();
                seance.addDonnees(donneesParCours.get(index+1));
            }
            res.add(seance);
        }
        // cours ponctuels
        while(index < donneesParCours.size()){
            index++;
            assert donneesParCours.containsKey(index);
            Cours coursPonctuel = new Cours();
            coursPonctuel.setClasse(classe);
            coursPonctuel.setDay(jour);
            coursPonctuel.setWeek(Constants.COURS_NORMAL);
            coursPonctuel.setStartHour(16);
            coursPonctuel.setStartMin(00);
            coursPonctuel.setEndHour(17);
            coursPonctuel.setEndMin(00);
            journee.getCoursPonctuels().add(coursPonctuel);
            Seance seance = handler.createSeance(classe, date, coursPonctuel);
            seance.setCours(coursPonctuel);
            seance.addDonnees(donneesParCours.get(index));
            nbDonnees+=seance.getDonnees().size();
            res.add(seance);
            nbCoursPonctuels++;
        }
        nbSeances += res.size();
        return res;
    }
    
    
    private Map<Integer, List<EleveData>> getDataParCoursForDate(Classe classe, LocalDate date){
        Map<Integer, List<EleveData>> res = new HashMap<>();
        if(classe == null || classe.getEleves() == null || classe.getEleves().isEmpty()){
            return null;
        }
        for(Eleve eleve : classe.getEleves()){
            for(EleveData eleveData : eleve.getData()){
                if(eleveData.getDateAsDate().isEqual(date)){
                    if(!res.containsKey(eleveData.getCours())){
                        res.put(eleveData.getCours(), new ArrayList<>());
                    }
                    res.get(eleveData.getCours()).add(eleveData);
                }
            }
        }
        return res;
    }
    @Override
    public String getName() {
        return "Construction des séances";
    }
    
    
}

