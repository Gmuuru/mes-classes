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
import mesclasses.handlers.DonneesHandler;
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
        process();
        return null;
    }

    private void process() throws Exception {
        
        try {
            LocalDate day = handler.getTrimestres().get(0).getStartAsDate();
            long nbDays = ChronoUnit.DAYS.between(day, LocalDate.now());
            while(day.isBefore(LocalDate.now())){
                    // pas de journée créée pour ce jour
                    Journee journee = createJournee(day);
                    handler.declareJournee(journee);
                nbJournees++;
                day = day.plusDays(1);
                updateProgress(nbJournees, nbDays);
            }
        } catch(Exception e){
            log(e);
            setMsg("Impossible d'effectuer la conversion en séances : "+e.getMessage());
            throw e;
        }
        log("journees : "+nbJournees+", seances "+nbSeances+", cours additionnels : "+nbCoursPonctuels+", donnees : "+nbDonnees);
    }
    
    public Journee createJournee(LocalDate date){
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
        try {
            List<Cours> listeCours = handler.getCoursForDateAndClasse(date, classe);

            Map<Integer, List<EleveData>> donneesParCours = getDataParCoursForDate(classe, date);
            //cours normaux
            int index;
            for(index = 0; index < listeCours.size(); index++){
                Seance seance = handler.createSeance(journee, listeCours.get(index));
                if(donneesParCours.containsKey(index+1)){
                    nbDonnees+=donneesParCours.get(index+1).size();
                    donneesParCours.values().forEach(listeData -> {
                        listeData.forEach(data -> {
                            data.getEleve().getData().remove(data);
                            data.setSeance(seance);
                            DonneesHandler.getInstance().persistEleveData(data);
                        });
                    });
                }
                res.add(seance);
            }
            String jour = Constants.DAYMAP.get(date.getDayOfWeek());
            // cours ponctuels
            while(index < donneesParCours.size()){
                index++;
                assert donneesParCours.containsKey(index);
                Cours coursPonctuel = new Cours();
                coursPonctuel.setClasse(classe);
                coursPonctuel.setDay(jour);
                coursPonctuel.setWeek("ponctuel");
                coursPonctuel.setStartHour(16);
                coursPonctuel.setStartMin(00);
                coursPonctuel.setEndHour(17);
                coursPonctuel.setEndMin(00);
                journee.getCoursPonctuels().add(coursPonctuel);
                Seance seance = handler.createSeance(journee, coursPonctuel);
                seance.setCours(coursPonctuel);
                donneesParCours.values().forEach(liste -> {
                    liste.forEach(data -> {
                        data.setSeance(seance);
                        data.getEleve().getData().remove(data);
                        DonneesHandler.getInstance().persistEleveData(data);
                    });
                });
                nbDonnees+=seance.getDonnees().size();
                res.add(seance);
                nbCoursPonctuels++;
            }
            nbSeances += res.size();
        } catch(Exception e){
            log("erreur ici : ");
            log(e);
            setMsg(e.getMessage());
        }
        
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

