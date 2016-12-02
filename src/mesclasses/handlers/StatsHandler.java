/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mesclasses.handlers;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import java.util.List;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import mesclasses.model.Eleve;
import mesclasses.model.EleveData;
import mesclasses.model.Seance;
import mesclasses.model.Trimestre;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author rrrt3491
 */
public class StatsHandler {

    
    private final ModelHandler model;
    
    private static StatsHandler handler;
    
    private Table<String, Trimestre, ObservableList<Seance>> cumulTravail;
    private Table<String, Trimestre, ObservableList<Seance>> cumulRetard;
    private Table<String, Trimestre, ObservableList<Seance>> cumulOubli;
    
    public static void init(){
            handler = new StatsHandler();
            handler.initializeCumuls();
    }
    
    public static StatsHandler getInstance(){
        if(handler == null){
            init();
        }
        return handler;
    }
    
    private StatsHandler(){
        model = ModelHandler.getInstance();
        cumulTravail = HashBasedTable.create();
        cumulRetard = HashBasedTable.create();
        cumulOubli = HashBasedTable.create();
    }
    
    
    /**
     * retourne le nombre de séances avec travail non fait pour un élève, sur un trimestre
     * @param eleve
     * @param trimestre
     * @return 
     */
    /*public int getCumulTravail(Eleve eleve, Trimestre trimestre){
        return getSeancesWithTravailNonFait(eleve, trimestre).size();
    }*/
    
    /**
     * recalcule toutes les seances d'un élève avec oubli de matériel, sur un trimestre
     * @param eleve
     * @param trimestre
     * @return 
     */
    public ObservableList<Seance> computeSeancesWithRetard(Eleve eleve, Trimestre trimestre){
        List<Seance> seances =  eleve.getData().stream()
                .filter(d -> d.getRetard() > 0)
                .map(d -> d.getSeance())
                .collect(Collectors.toList());
        cumulOubli.get(eleve, trimestre).clear();
        cumulOubli.get(eleve, trimestre).addAll(seances);
        
        return getSeancesWithOubli(eleve, trimestre);
    }
    /**
     * retourne toutes les seances d'un élève où il a été en retard, sur un trimestre
     * @param eleve
     * @param trimestre
     * @return 
     */
    public ObservableList<Seance> getSeancesWithRetard(Eleve eleve, Trimestre trimestre){
       return cumulRetard.get(eleve, trimestre);
    }
    
    /**
     * retourne le nombre de séances avec retard pour un élève, sur un trimestre
     * @param eleve
     * @param trimestre
     * @return 
     */
    public int getCumulRetard(Eleve eleve, Trimestre trimestre){
        return getSeancesWithRetard(eleve, trimestre).size();
    }
    
    /**
     * recalcule toutes les seances d'un élève avec oubli de matériel, sur un trimestre
     * @param eleve
     * @param trimestre
     * @return 
     */
    public ObservableList<Seance> computeSeancesWithOubli(Eleve eleve, Trimestre trimestre){
        List<Seance> seances =  eleve.getData().stream()
                .filter(d -> StringUtils.isNotBlank(d.getOubliMateriel()))
                .map(d -> d.getSeance())
                .collect(Collectors.toList());
        cumulOubli.get(eleve, trimestre).clear();
        cumulOubli.get(eleve, trimestre).addAll(seances);
        
        return getSeancesWithOubli(eleve, trimestre);
    }
    
    /**
     * retourne toutes les seances d'un élève avec oubli de matériel, sur un trimestre
     * @param eleve
     * @param trimestre
     * @return 
     */
    public ObservableList<Seance> getSeancesWithOubli(Eleve eleve, Trimestre trimestre){
        return cumulOubli.get(eleve, trimestre);
    }
    
    /**
     * retourne le nombre de séances avec oubli de matériel pour un élève, sur un trimestre
     * @param eleve
     * @param trimestre
     * @return 
     */
    public int getCumulOubli(Eleve eleve, Trimestre trimestre){
        return getSeancesWithOubli(eleve, trimestre).size();
    }

    private void initializeCumuls() {
        
        model.getClasses().forEach(c -> {
            c.getEleves().forEach(e -> {
                e.getData().forEach(d -> {
                    if(d.isTravailPasFait()){
                        addOrCreateCumulList(cumulTravail, d);
                    }
                    if(d.getRetard() > 0){
                        addOrCreateCumulList(cumulRetard, d);
                    }
                    if(StringUtils.isNotBlank(d.getOubliMateriel())){
                        addOrCreateCumulList(cumulOubli, d);
                    }
                });
            });
        });
    }
    
    private void addOrCreateCumulList(Table<String, Trimestre, ObservableList<Seance>> table, EleveData data){
        Eleve e = data.getEleve();
        Seance s = data.getSeance();
        Trimestre t = model.getForDate(s.getDateAsDate());
        if(table.get(e.getId(), t) == null){
            table.put(e.getId(), t, FXCollections.observableArrayList());
        }
        table.get(e.getId(), t).add(s);
    }
}
