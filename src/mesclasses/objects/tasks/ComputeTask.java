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
import mesclasses.handlers.DonneesHandler;
import mesclasses.handlers.ModelHandler;
import mesclasses.model.Classe;
import mesclasses.model.Cours;
import mesclasses.model.Eleve;
import mesclasses.model.EleveData;
import mesclasses.model.Journee;
import mesclasses.model.Seance;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author rrrt3491
 */
public class ComputeTask extends AppTask<Object> {
    
    private static final Logger LOG = LogManager.getLogger(ComputeTask.class);
    
    private ModelHandler handler;
    
    private int nbJournees;
    private int nbCoursPonctuels;
    private int nbSeances;
    private int nbDonnees;
    private int nbPunitions;
    
    private int nbDonneesInitiales;
    private int nbPunitionsInitiales;
    
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
            if(handler.getTrimestres().isEmpty()){
                return;
            }
            getInitialStats();
            LocalDate day = handler.getTrimestres().get(0).getStartAsDate();
            long nbDays = ChronoUnit.DAYS.between(day, LocalDate.now());
            while(day.isBefore(LocalDate.now().plusDays(1))){
                    // pas de journée créée pour ce jour
                    Journee journee = createJournee(day);
                    handler.declareJournee(journee);
                nbJournees++;
                day = day.plusDays(1);
                updateProgress(nbJournees, nbDays);
            }
            updatePunitions();
        } catch(Exception e){
            LOG.error(e);
            setMsg("Impossible d'effectuer la conversion en séances : "+e.getMessage());
            throw e;
        }
        checkData();
        LOG.info("nbDonneesInitiales : "+nbDonneesInitiales+", nbPunitionsInitiales : "+nbPunitionsInitiales);
        LOG.info("journees : "+nbJournees+", seances "+nbSeances+", cours additionnels : "
                +nbCoursPonctuels+", donnees traitées : "+nbDonnees+", punitions traitées : "+nbPunitions);
    }
    
    public Journee createJournee(LocalDate date){
        if(date == null){
            return null;
        }
        Journee journee = new Journee();
        journee.setDate(date);
        handler.getClasses().forEach(c -> {
            buildSeancesForClasse(journee, c, date);
        });
        Collections.sort(journee.getCoursPonctuels());
        Collections.sort(journee.getSeances());
        return journee;
    }

    private void updatePunitions(){
        handler.getClasses().forEach(c -> {
            c.getEleves().forEach(e -> {
                e.getPunitions().forEach(p -> {
                    Journee journee = handler.getJournee(p.getDateAsDate());
                    if(journee == null){
                        LOG.error("erreur : punition "+p.getId()+" n'a pas de journée associée...");
                        return;
                    }
                    List<Seance> seances = journee.getSeances().filtered(s -> s.getClasse().getName().equals(p.getEleve().getClasse().getName()));
                    if(seances == null || seances.isEmpty()){
                        LOG.error("erreur : punition "+p.getId()+" n'a pas de séance associée...");
                        return;
                    }
                    p.setSeance(seances.get(0));
                    nbPunitions++;
                });
            });
        });
    }
    
    private void checkData() throws Exception {
        final List<String> errList = new ArrayList<>();
        handler.getJournees().entrySet().forEach(e -> {
            LocalDate key = e.getKey();
            if(e.getValue().getDate() == null || !e.getValue().getDateAsDate().isEqual(key)){
               errList.add("Journee incorrecte pour la date "+key);
            }
        });
        if(!errList.isEmpty()){
            throw new Exception(StringUtils.join(errList, " | "));
        }
    }
    
    private void addDonneesToSeance(List<EleveData> donnees, Seance seance){
        donnees.forEach(d -> {
            d.getEleve().getData().remove(d);
            d.setSeance(seance);
            DonneesHandler.getInstance().persistEleveData(d);
        });
    }
    
    private void buildSeancesForClasse(Journee journee, Classe classe, LocalDate date) {
        try {
            // liste des cours pour la classe à cette date
            List<Cours> listeCours = handler.getCoursForDateAndClasse(date, classe);
            // map des données élèves, réparties par cours
            Map<Integer, List<EleveData>> donneesParCours = getDataParCoursForDate(classe, date);
            
            LOG.info("Classe : "+classe+", date : "+date);
            LOG.info("nombre de cours prévus : "+listeCours.size());
            LOG.info("nombre de cours trouvés: "+donneesParCours.size());
            
            //cours normaux
            for(int index = 0; index < listeCours.size(); index++){
                Seance seance = handler.addSeanceWithCours(journee, listeCours.get(index));
                if(donneesParCours.containsKey(index+1)){
                    addDonneesToSeance(donneesParCours.get(index+1), seance);
                    nbDonnees+=donneesParCours.get(index+1).size();
                }
                LOG.info("nouvelle séance normale : "+seance);
            }
            //cours 0 ?
            if(donneesParCours.containsKey(0)){
                Seance seance = handler.addSeanceWithCoursPonctuel(journee, classe);
                addDonneesToSeance(donneesParCours.get(0), seance);
                    nbDonnees+=donneesParCours.get(0).size();
                LOG.info("nouvelle séance ponctuelle car cours 0: "+seance);
            }
            
            // cours ponctuels
            for(int index = listeCours.size(); index < donneesParCours.size(); index++){
                if(index == 0){
                    //cas 'cours 0' traité au dessus
                    continue;
                }
                if(!donneesParCours.containsKey(index)){
                    break;
                }
                Seance seance = handler.addSeanceWithCoursPonctuel(journee, classe);
                addDonneesToSeance(donneesParCours.get(index), seance);
                nbDonnees+=donneesParCours.get(index).size();
                LOG.info("nouvelle séance ponctuelle : "+seance);
            }
            nbSeances += journee.getSeances().size();
        } catch(Exception e){
            LOG.error(e);
            setMsg(e.getMessage());
        }
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

    private void getInitialStats() {
        handler.getClasses().forEach(c -> {
            c.getEleves().forEach(e -> {
                nbDonneesInitiales+=e.getData().size();
                nbPunitionsInitiales+=e.getPunitions().size();
            });
        });
    }
    
    
}

