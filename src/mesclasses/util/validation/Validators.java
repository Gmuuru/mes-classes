/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mesclasses.util.validation;

import com.google.common.collect.Lists;
import java.util.List;
import mesclasses.model.ChangementClasse;
import mesclasses.model.Classe;
import mesclasses.model.Cours;
import mesclasses.model.Devoir;
import mesclasses.model.Eleve;
import mesclasses.model.EleveData;
import mesclasses.model.Journee;
import mesclasses.model.Mot;
import mesclasses.model.Punition;
import mesclasses.model.Seance;
import mesclasses.model.Trimestre;
import static mesclasses.util.validation.FErrorBuilder.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author rrrt3491
 */
public class Validators {
    
    private static final Logger LOG = LogManager.getLogger(Validators.class);
    
    /* TRIMESTRES */
    public static List<FError> validate(Trimestre t){
        List<FError> err = Lists.newArrayList();
        boolean datesNotNull = true;
        LOG.debug("validation du trimestre "+t.getName());
        if(StringUtils.isBlank(t.getName())){
            err.add(new FError(MANDATORY(t, "nom"), t));
        }
        if(t.getStartAsDate() == null){
            err.add(new FError(MANDATORY(t, "début"), t));
            datesNotNull = false;
        }
        if(t.getEndAsDate() == null){
            err.add(new FError(MANDATORY(t, "fin"), t));
            datesNotNull = false;
        }
        if(datesNotNull && !t.getStartAsDate().isEqual(t.getEndAsDate()) 
                && !t.getStartAsDate().isBefore(t.getEndAsDate())){
            err.add(new FError(INVALIDS(t, "dates"), t));
        }
        return err;
    }
    
    /* COURS */
    
    public static List<FError> validate(Cours c) {
        List<FError> err = Lists.newArrayList();
        LOG.debug("validation du cours "+c);
        if(c.getId() == null){
            err.add(new FError(NO_ID("Cours")));
            return err;
        }
        if(StringUtils.isBlank(c.getDay())){
            err.add(new FError(MANDATORY(c, "jour")));
        }
        if(StringUtils.isBlank(c.getWeek())){
            err.add(new FError(MANDATORY(c, "périodicité")));
        }
        if(c.getClasse()== null){
            err.add(new FError(MANDATORY(c, "classe")));
        }
        if(c.getStartHour()== null){
            err.add(new FError(MANDATORY(c, "heure de début")));
        }
        if(c.getEndHour()== null){
            err.add(new FError(MANDATORY(c, "heure de fin")));
        }
        return err;
    }
    /* CLASSES */
    
    public static List<FError> validate(Classe c){
        List<FError> err = Lists.newArrayList();
        LOG.debug("validation de la classe "+c);
        if(StringUtils.isBlank(c.getName())){
            err.add(new FError(MANDATORY(c, "nom")));
        }
        return err;
    }
    
    /* ELEVES */
    
    public static List<FError> validate(Eleve e){
        List<FError> err = Lists.newArrayList();
        if(e.getId() == null){
            err.add(new FError(NO_ID("Elève")));
            return err;
        }
        LOG.debug("validation de l'élève "+e);
        if(StringUtils.isBlank(e.getLastName())){
            err.add(new FError(MANDATORY(e, "nom")));
        }
        if(StringUtils.isBlank(e.getFirstName())){
            err.add(new FError(MANDATORY(e, "prénom")));
        }
        if(e.getClasse() == null){
            err.add(new FError(MANDATORY(e, "classe")));
        }
        return err;
    }
    
    /* PUNITIONS */
    
    public static List<FError> validate(Punition e){
        List<FError> err = Lists.newArrayList();
        if(e.getId() == null){
            err.add(new FError(NO_ID("Punition")));
            return err;
        }
        LOG.debug("validation de la punition "+e);
        if(StringUtils.isBlank(e.getTexte())){
            err.add(new FError(MANDATORY(e, "texte")));
        }
        if(e.getEleve()== null){
            err.add(new FError(MANDATORY(e, "élève")));
        }
        if(e.getSeance()== null){
            err.add(new FError(MANDATORY(e, "séance")));
        }
        return err;
    }
    
    /* DEVOIRS */
    
    public static List<FError> validate(Devoir e){
        List<FError> err = Lists.newArrayList();
        if(e.getId() == null){
            err.add(new FError(NO_ID("Devoir")));
            return err;
        }
        LOG.debug("validation du devoir "+e);
        if(e.getEleve()== null){
            err.add(new FError(MANDATORY(e, "élève")));
        }
        if(e.getSeance()== null){
            err.add(new FError(MANDATORY(e, "séance")));
        }
        return err;
    }
    

    public static List<FError> validate(Mot e) {
        List<FError> err = Lists.newArrayList();
        if(e.getId() == null){
            err.add(new FError(NO_ID("Mot carnet")));
            return err;
        }
        LOG.debug("validation du mot "+e);
        if(e.getEleve()== null){
            err.add(new FError(MANDATORY(e, "élève")));
        }
        if(e.getSeance()== null){
            err.add(new FError(MANDATORY(e, "séance")));
        }
        return err;
    }
    
    /* CHANGEMENTS CLASSE */
    public static List<FError> validate(ChangementClasse e){
        List<FError> err = Lists.newArrayList();
        LOG.debug("validation du changement classe "+e);
        if(StringUtils.isBlank(e.getType())){
            err.add(new FError(MANDATORY(e, "type")));
        }
        if(e.getClasse()== null){
            err.add(new FError(MANDATORY(e, "classe")));
        }
        if(e.getDateAsDate() == null){
            err.add(new FError(MANDATORY(e, "date")));
        }
        return err;
    }

    /* DONNEES ELEVE */
    
    public static List<FError> validate(EleveData d) {
        List<FError> err = Lists.newArrayList();
        if(d.getId() == null){
            err.add(new FError(NO_ID("Donnée")));
            return err;
        }
        if(d.getEleve() == null){
            err.add(new FError(MANDATORY(d, "élève")));
        }
        if(d.getSeance() == null){
            err.add(new FError(MANDATORY(d, "séance")));
        }
        if(d.getSeance().getDonnees() == null ||
                !d.getSeance().getDonnees().containsKey(d.getEleve())){
            LOG.error("donnée {} non reliée à sa séance {}, réparation", d.getId(), d.getSeance().getId());
            d.getSeance().getDonnees().put(d.getEleve(), d);
        }
        return err;
    }
    
    /* JOURNEES */
    public static List<FError> validate(Journee j) {
        List<FError> err = Lists.newArrayList();
        if(j.getDateAsDate() == null){
            err.add(new FError(MANDATORY(j, "date")));
        }
        return err;
    }

    /* SEANCES */
    public static List<FError> validate(Seance s) {
        List<FError> err = Lists.newArrayList();
        if(s.getId() == null){
            err.add(new FError(NO_ID("Séance")));
            return err;
        }
        if(s.getJournee() == null){
            err.add(new FError(MANDATORY(s, "journée")));
        }
        if(s.getClasse() == null){
            err.add(new FError(MANDATORY(s, "classe")));
        }
        if(s.getCours()== null){
            err.add(new FError(MANDATORY(s, "cours")));
        }
        if(s.getDonnees()== null){
            err.add(new FError(DONNEES_SEANCE_NULL(s)));
        }
        s.getDonnees().entrySet().forEach(e -> {
            if(e == null || e.getKey() == null){
                err.add(new FError(SEANCE_MAP_ERROR(s)));
            } else {
                Eleve eleve = e.getKey();
                if(s.getClasse() == null || s.getClasse().getEleves() == null || !s.getClasse().getEleves().contains(eleve)){
                    // l'élève dans la map n'est pas dans la classe de la séance
                    err.add(new FError(SEANCE_WRONG_ELEVE(s, eleve, s.getClasse())));
                } else if(!eleve.getData().contains(e.getValue())){
                    // la donnée stockée ne correspond pas à l'élève
                    err.add(new FError(SEANCE_WRONG_DONNEE(s, eleve, e.getValue())));
                }
            }
        });
        return err;
    }
    

}
