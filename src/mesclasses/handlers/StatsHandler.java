/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mesclasses.handlers;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import mesclasses.model.Eleve;
import mesclasses.model.EleveData;
import mesclasses.model.Mot;
import mesclasses.model.Seance;
import mesclasses.model.Trimestre;
import mesclasses.util.NodeUtil;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author rrrt3491
 */
public class StatsHandler {

    
    private final ModelHandler model;
    
    private static StatsHandler handler;
    
    public static void init(){
            handler = new StatsHandler();
    }
    
    public static StatsHandler getInstance(){
        if(handler == null){
            init();
        }
        return handler;
    }
    
    private StatsHandler(){
        model = ModelHandler.getInstance();
    }
    
    /**
     * retourne les données d'un élèves sur la période donnée
     * @param eleve
     * @param start
     * @param end
     * @return 
     */
    public Stream<EleveData> getDonneesForPeriode(Eleve eleve, LocalDate start, LocalDate end){
        return eleve.getData().stream()
                .filter(d -> NodeUtil.isBetween(d.getDateAsDate(), start, end));
    }
    
    /****************************   RAPPORTS    ********************************/
    
    /**
     * retourne toutes les seances d'un élève avec absence, sur un trimestre
     * @param eleve
     * @param trimestre
     * @return 
     */
    public List<Seance> getSeancesWithAbsenceOnTrimestre(Eleve eleve, Trimestre trimestre){
        return getDonneesForPeriode(eleve, trimestre.getStartAsDate(), trimestre.getEndAsDate())
                .filter(d -> d.isAbsent())
                .map(d -> d.getSeance())
                .collect(Collectors.toList());
    }
    
    /**
     * retourne toutes les seances d'un élève avec retard, sur un trimestre
     * @param eleve
     * @param trimestre
     * @return 
     */
    public List<Seance> getSeancesWithRetardOnTrimestre(Eleve eleve, Trimestre trimestre){
        return getDonneesForPeriode(eleve, trimestre.getStartAsDate(), trimestre.getEndAsDate())
                .filter(d -> d.getRetard() > 0)
                .map(d -> d.getSeance())
                .collect(Collectors.toList());
    }
    
    /**
     * retourne toutes les seances d'un élève avec travail non fait, sur un trimestre
     * @param eleve
     * @param trimestre
     * @return 
     */
    public List<Seance> getSeancesWithTravailOnTrimestre(Eleve eleve, Trimestre trimestre){
        return getDonneesForPeriode(eleve, trimestre.getStartAsDate(), trimestre.getEndAsDate())
                .filter(d -> d.isTravailPasFait())
                .map(d -> d.getSeance())
                .collect(Collectors.toList());
    }
    
    /**
     * retourne toutes les seances d'un élève avec oubli de matériel d'un élève, sur un trimestre
     * @param eleve
     * @param trimestre
     * @return 
     */
    public List<Seance> getSeancesWithOubliOnTrimestre(Eleve eleve, Trimestre trimestre){
        return getDonneesForPeriode(eleve, trimestre.getStartAsDate(), trimestre.getEndAsDate())
                .filter(d -> StringUtils.isNotBlank(d.getOubliMateriel()))
                .map(d -> d.getSeance())
                .collect(Collectors.toList());
    }
    
    /**
     * retourne toutes les seances d'un élève avec exclusion, sur un trimestre
     * @param eleve
     * @param trimestre
     * @return 
     */
    public List<Seance> getSeancesWithExclusionOnTrimestre(Eleve eleve, Trimestre trimestre){
        return getDonneesForPeriode(eleve, trimestre.getStartAsDate(), trimestre.getEndAsDate())
                .filter(d -> d.isExclus())
                .map(d -> d.getSeance())
                .collect(Collectors.toList());
    }
    
    /****************************   SEANCES     ********************************/
    /**
     * retourne toutes les seances d'un élève avec travail non fait, du debut du trimestre à la date fournie
     * @param eleve
     * @param date
     * @return 
     */
    public List<Seance> getSeancesWithTravailUntil(Eleve eleve, LocalDate date){
        Trimestre trim = model.getForDate(date);
        return getDonneesForPeriode(eleve, trim.getStartAsDate(), date)
                .filter(d -> d.isTravailPasFait())
                .map(d -> d.getSeance())
                .collect(Collectors.toList());
    }
    
    
    /**
     * retourne toutes les seances d'un élève avec retard, du debut du trimestre à la date fournie
     * @param eleve
     * @param date
     * @return 
     */
    public List<Seance> getSeancesWithRetardUntil(Eleve eleve, LocalDate date){
        Trimestre trim = model.getForDate(date);
        return getDonneesForPeriode(eleve, trim.getStartAsDate(), date)
                .filter(d -> d.getRetard() > 0)
                .map(d -> d.getSeance())
                .collect(Collectors.toList());
    }
    
    /**
     * retourne toutes les seances d'un élève avec oubli de matériel d'un élève, du debut du trimestre à la date fournie
     * @param eleve
     * @param date
     * @return 
     */
    public List<Seance> getSeancesWithOubliUntil(Eleve eleve, LocalDate date){
        Trimestre trim = model.getForDate(date);
        return getDonneesForPeriode(eleve, trim.getStartAsDate(), date)
                .filter(d -> StringUtils.isNotBlank(d.getOubliMateriel()))
                .map(d -> d.getSeance())
                .collect(Collectors.toList());
    }
    
    
    /**
     * retourne tous les mots d'un élève, du début du trimestre à la date fournie
     * @param eleve
     * @param date
     * @return 
     */
    public List<Mot> getMotsUntil(Eleve eleve, LocalDate date){
        Trimestre trim = model.getForDate(date);
        return eleve.getMots().filtered(m -> {
            return NodeUtil.isBetween(m.getDate(), trim.getStartAsDate(), trim.getEndAsDate()); 
        });
    }
    
    /**
     * retourne le nombre de séances avec travail non fait d'un élève, du début du trimestre à la date fournie
     * @param eleve
     * @param date
     * @return 
     */
    public int getNbTravailUntil(Eleve eleve, LocalDate date){
        return getSeancesWithTravailUntil(eleve, date).size();
    }
    
    /**
     * retourne le nombre de séances avec retard d'un élève, du début du trimestre à la date fournie
     * @param eleve
     * @param date
     * @return 
     */
    public int getNbRetardsUntil(Eleve eleve, LocalDate date){
        return getSeancesWithRetardUntil(eleve, date).size();
    }
    
    /**
     * retourne le nombre de séances avec oubli de matériel d'un élève, du début du trimestre à la date fournie
     * @param eleve
     * @param date
     * @return 
     */
    public int getNbOublisUntil(Eleve eleve, LocalDate date){
        return getSeancesWithOubliUntil(eleve, date).size();
    }
}
