/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mesclasses.util.validation;

import com.google.common.collect.Lists;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import mesclasses.model.Classe;
import mesclasses.model.Cours;
import mesclasses.model.Eleve;
import mesclasses.model.Journee;
import mesclasses.model.Trimestre;
import mesclasses.util.NodeUtil;
import static mesclasses.util.validation.FErrorBuilder.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
/**
 *
 * @author rrrt3491
 */
public class ListValidators {
    private static final Logger LOG = LogManager.getLogger(ListValidators.class);
    
    /* TRIMESTRES */
    
    public static List<FError> validateTrimestreList(List<Trimestre> list){
        List<FError> err = Lists.newArrayList();
        if(list == null){
            err.add(new FError(TRIMESTRES_NULL));
            return err;
        }
        list.forEach(t -> {
            if(t == null){
                err.add(new FError(IS_NULL("Trimestre")));
            } else {
                err.addAll(t.validate());
            }
        });
        
        for (int i = 0; i < list.size(); i++) {
            for (int k = i + 1; k < list.size(); k++) {
                if (NodeUtil.datesOverlap(list.get(i), list.get(k))) {
                    err.add(new FError(OVERLAP(list.get(i), list.get(k))));
                }
            }
        }
        Set<String> dupes = findDuplicates(list.stream().map( t -> t.getName() ).collect(Collectors.toList()));
        dupes.forEach(s -> {
            err.add(new FError(TRIMESTRE_DUPE(s)));
        });
        return err;
    }
    /* COURS */
    
    public static List<FError> validateCoursList(List<Cours> list){
        List<FError> err = Lists.newArrayList();
        if(list == null){
            err.add(new FError(COURS_NULL));
            return err;
        }
        list.forEach(c -> {
            if(c == null){
                err.add(new FError(IS_NULL("Cours")));
            } else {
                err.addAll(c.validate());
            }
        });
        return err;
    }
    
    /* CLASSES */
    
    public static List<FError> validateClasseList(List<Classe> list){
        List<FError> err = Lists.newArrayList();
        if(list == null){
            err.add(new FError(CLASSES_NULL));
            return err;
        }
        
        list.forEach(c -> {
            if(c == null){
                err.add(new FError(IS_NULL("Classe")));
            } else {
                err.addAll(c.validate());
            }
        });
        Set<String> dupes = findDuplicates(list.stream().filter(e -> e != null).map( e -> e.getName() ).collect(Collectors.toList()));
        dupes.forEach(s -> {
            err.add(new FError(CLASSE_DUPE(s)));
        });
        return err;
    }
    
    /* ELEVES */
    
    public static List<FError> validateEleveList(Classe c){
        List<FError> err = Lists.newArrayList();
        if(c.getEleves() == null){
            err.add(new FError(ELEVES_NULL(c)));
            return err;
        }
        c.getEleves().stream().forEach(e -> {
            if(e == null){
                err.add(new FError(IS_NULL_WITH_SOURCE(c, "Elève")));
            } else {
                if(!c.equals(e.getClasse())){
                    err.add(new FError(ELEVE_WRONG_CLASSE(c,e)));
                }
                err.addAll(e.validate());
            }
        });
        Set<String> dupes = findDuplicates(c.getEleves().stream().filter(e -> e != null).map( e -> e.getDisplayName()).collect(Collectors.toList()));
        dupes.forEach(s -> {
            err.add(new FError(ELEVE_DUPE(c, s)));
        });
        return err;
    }
    
    /* PUNITIONS */
    
    public static List<FError> validatePunitionList(Eleve e){
        List<FError> err = Lists.newArrayList();
        if(e.getPunitions() == null){
            err.add(new FError(PUNITIONS_NULL(e)));
            return err;
        }
        e.getPunitions().stream().forEach(p -> {
            if(p == null){
                err.add(new FError(IS_NULL_WITH_SOURCE(e, "punition")));
            } else {
                if(p.getEleve() != null && !p.getEleve().equals(e)){
                    err.add(new FError(PUNITION_WRONG_ELEVE(e,p)));
                }
                err.addAll(p.validate());
            }
        });
        return err;
    }
    
    /* DEVOIRS */
    
    public static List<FError> validateDevoirList(Eleve e){
        List<FError> err = Lists.newArrayList();
        if(e.getDevoirs() == null){
            err.add(new FError(DEVOIRS_NULL(e)));
            return err;
        }
        e.getDevoirs().stream().forEach(p -> {
            if(p == null){
                err.add(new FError(IS_NULL_WITH_SOURCE(e, "devoir")));
            } else {
                if(p.getEleve() != null && !p.getEleve().equals(e)){
                    err.add(new FError(DEVOIR_WRONG_ELEVE(e,p)));
                }
                err.addAll(p.validate());
            }
        });
        return err;
    }
    
    /* DEVOIRS */
    
    public static List<FError> validateMotList(Eleve e){
        List<FError> err = Lists.newArrayList();
        if(e.getDevoirs() == null){
            err.add(new FError(MOTS_NULL(e)));
            return err;
        }
        e.getMots().stream().forEach(p -> {
            if(p == null){
                err.add(new FError(IS_NULL_WITH_SOURCE(e, "mot carnet")));
            } else {
                if(p.getEleve() != null && !p.getEleve().equals(e)){
                    err.add(new FError(MOT_WRONG_ELEVE(e,p)));
                }
                err.addAll(p.validate());
            }
        });
        return err;
    }
    
    /* MOTS */
    
    public static List<FError> validateChangementList(Eleve e){
        List<FError> err = Lists.newArrayList();
        if(e.getChangementsClasse()== null){
            err.add(new FError(CHANGEMENTS_NULL(e)));
            return err;
        }
        e.getChangementsClasse().stream().forEach(p -> {
            if(p == null){
                err.add(new FError(IS_NULL_WITH_SOURCE(e, "Changement")));
            } else {
                err.addAll(p.validate());
            }
        });
        return err;
    }
    /* DONNEES ELEVE */
    
    public static List<FError> validateDonneeList(Eleve e) {
        List<FError> err = Lists.newArrayList();
        if(e.getData() == null){
            err.add(new FError(DONNEES_NULL(e)));
            return err;
        }
        e.getData().stream().forEach(d -> {
            if(d == null){
                err.add(new FError(IS_NULL_WITH_SOURCE(e, "Donnée")));
            } else {
                if(d.getEleve() != null && !d.getEleve().equals(e)){
                    err.add(new FError(DONNEE_WRONG_ELEVE(e,d)));
                }
                err.addAll(d.validate());
            }
        });
        return err;
    }
    
    /* JOURNEES */
    
    public static List<FError> validateJourneeMap(Map<LocalDate, Journee> list) {
        List<FError> err = Lists.newArrayList();
        if(list == null){
            err.add(new FError(JOURNEES_NULL));
            return err;
        }
        list.entrySet().forEach(e -> {
            if(e == null || e.getKey() == null){
                err.add(new FError(JOURNEES_MAP_ERROR));
            } else {
                if(e.getValue().getDateAsDate() != null && 
                        !e.getKey().isEqual(e.getValue().getDateAsDate())){
                    err.add(new FError(JOURNEE_WRONG_DATE(e.getValue())));
                }
                err.addAll(e.getValue().validate());
            }
        });
        
        return err;
    }
    /* JOURNEES */
    
    public static List<FError> validateSeanceList(Journee j) {
        List<FError> err = Lists.newArrayList();
        if(j.getSeances() == null){
            err.add(new FError(SEANCES_NULL(j)));
            return err;
        }
        j.getSeances().forEach(s -> {
            if(s == null){
                err.add(new FError(IS_NULL_WITH_SOURCE(j, "Séance")));
            } else {
                if(s.getJournee()!= null && !s.getJournee().equals(j)){
                    err.add(new FError(SEANCE_WRONG_JOURNEE(j, s)));
                }
                err.addAll(s.validate());
            }
        });
        
        return err;
    }
    
    /* UTILS */
    
    private static <T> Set<T> findDuplicates(Collection<T> list) {

        Set<T> duplicates = new HashSet<>();
        Set<T> uniques = new HashSet<>();

        list.stream().filter((t) -> (!uniques.add(t))).forEach((t) -> {
            duplicates.add(t);
        });

        return duplicates;
    }
}
