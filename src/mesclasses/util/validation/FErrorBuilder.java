/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mesclasses.util.validation;

import java.util.List;
import mesclasses.model.ChangementClasse;
import mesclasses.model.Classe;
import mesclasses.model.Cours;
import mesclasses.model.Eleve;
import mesclasses.model.EleveData;
import mesclasses.model.Journee;
import mesclasses.model.MonitoredObject;
import mesclasses.model.Punition;
import mesclasses.model.Seance;
import mesclasses.model.Trimestre;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author rrrt3491
 */
public class FErrorBuilder {
    
    // PLACEHOLDERS
    public static final String TRIMESTRE = "%TRIMESTRE%";
    public static final String CLASSE = "%CLASSE%";
    public static final String COURS = "%COURS%";
    public static final String ELEVE = "%ELEVE%";
    public static final String PUNITION = "%PUNITION%";
    public static final String CHANGEMENT = "%CHANGEMENT%";
    public static final String DONNEE = "%DONNEE%";
    public static final String JOURNEE = "%JOURNEE%";
    public static final String SEANCE = "%SEANCE%";
    
    public static final String SOURCE = "%SOURCE%";
    public static final String OBJET = "%OBJET%";
    public static final String CHAMP = "%CHAMP%";
    
    // LISTES VIDES
    public static final String TRIMESTRES_NULL = "Liste trimestres null";
    public static final String CLASSES_NULL = "Liste classes null";
    public static final String COURS_NULL = "Liste cours null";
    public static final String JOURNEES_NULL = "Liste journées null";
    public static final String JOURNEES_MAP_ERROR = "Erreur : date vide dans le calendrier des journées";
    private static final String SEANCE_MAP_ERROR = SEANCE+" : données présentes pour élève inexistant";
    public static final String SEANCE_MAP_ERROR(Seance seance){
        return seance(SEANCE_MAP_ERROR, seance);
    }
    private static final String ELEVES_NULL = CLASSE+" : liste élèves null";
    public static final String ELEVES_NULL(Classe classe){
        return classe(ELEVES_NULL, classe);
    }
    private static final String DONNEES_NULL = ELEVE+" : Liste données null";
    public static final String DONNEES_NULL(Eleve eleve){
        return eleve(DONNEES_NULL, eleve);
    }
    private static final String DONNEES_SEANCE_NULL = SEANCE+" : Liste données null";
    public static final String DONNEES_SEANCE_NULL(Seance seance){
        return seance(DONNEES_SEANCE_NULL, seance);
    }
    private static final String PUNITIONS_NULL = ELEVE+" : Liste punitions null";
    public static final String PUNITIONS_NULL(Eleve eleve){
        return eleve(PUNITIONS_NULL, eleve);
    }
    private static final String CHANGEMENTS_NULL = ELEVE+" : Liste changements null";
    public static final String CHANGEMENTS_NULL(Eleve eleve){
        return eleve(CHANGEMENTS_NULL, eleve);
    }
    private static final String SEANCES_NULL = JOURNEE+" : Liste séances null";
    public static final String SEANCES_NULL(Journee journee){
        return journee(SEANCES_NULL, journee);
    }
    // OBJETS INCORRECTS
    private static final String IS_NULL = OBJET+" null";
    public static final String IS_NULL(String object){
        return object(IS_NULL, object);
    }
    private static final String IS_NULL_WITH_SOURCE = SOURCE+" : "+OBJET+" null";
    public static final <T extends MonitoredObject> String IS_NULL_WITH_SOURCE(T source, String object){
        return source(object(IS_NULL_WITH_SOURCE, object), source);
    }
    private static final String NO_ID = OBJET+" null ou sans Id";
    public static final String NO_ID(String object){
        return object(NO_ID, object);
    }
    private static final String MANDATORY = SOURCE+" : "+CHAMP+" obligatoire";
    public static final <T extends MonitoredObject> String MANDATORY(T source, String champ){
        return source(champ(MANDATORY, champ), source);
    }
    private static final String INVALID = SOURCE+" : "+CHAMP+" invalide";
    public static final <T extends MonitoredObject> String INVALID(T source, String champ){
        return source(champ(INVALID, champ), source);
    }
    private static final String INVALIDS = SOURCE+" : "+CHAMP+" invalides";
    public static final <T extends MonitoredObject> String INVALIDS(T source, String champ){
        return source(champ(INVALIDS, champ), source);
    }
    private static final String OVERLAP = SOURCE+" et "+OBJET+" ont des périodes superposées";
    public static final String OVERLAP(Trimestre t1, Trimestre t2){
        return source(object(OVERLAP, t2.getDisplayName()), t1);
    }
    
    //DOUBLONS
    private static final String TRIMESTRE_DUPE = "Trimestre "+ TRIMESTRE+" en double";
    public static final String TRIMESTRE_DUPE(String name){
        return inject(TRIMESTRE_DUPE, TRIMESTRE, name);
    }
    private static final String CLASSE_DUPE = "Classe "+ CLASSE+" en double";
    public static final String CLASSE_DUPE(String name){
        return inject(CLASSE_DUPE, CLASSE, name);
    }
    private static final String ELEVE_DUPE = CLASSE+" : "+ELEVE+" en double";
    public static final String ELEVE_DUPE(Classe classe, String name){
        return classe(inject(ELEVE_DUPE, ELEVE, name), classe);
    }
    
    //LIENS BIDIRECTIONNELS FAUX
    private static final String ELEVE_WRONG_CLASSE = CLASSE+" : l'"+ELEVE+" n'a pas la bonne classe";
    public static final String ELEVE_WRONG_CLASSE(Classe classe, Eleve eleve){
        return classe(eleve(ELEVE_WRONG_CLASSE, eleve), classe);
    }
    private static final String PUNITION_WRONG_ELEVE = ELEVE+" : la "+PUNITION+" n'a pas le bon élève";
    public static final String PUNITION_WRONG_ELEVE(Eleve eleve, Punition punition){
        return punition(eleve(PUNITION_WRONG_ELEVE, eleve), punition);
    }
    private static final String DONNEE_WRONG_ELEVE = ELEVE+" : la "+DONNEE+" n'a pas le bon élève";
    public static final String DONNEE_WRONG_ELEVE(Eleve eleve, EleveData donnee){
        return donnee(eleve(DONNEE_WRONG_ELEVE, eleve), donnee);
    }
    private static final String JOURNEE_WRONG_DATE = JOURNEE+" : mauvaise date";
    public static final String JOURNEE_WRONG_DATE(Journee journee){
        return journee(JOURNEE_WRONG_DATE, journee);
    }
    private static final String SEANCE_WRONG_JOURNEE = JOURNEE+" : la "+SEANCE+" n'a pas la bonne journée";
    public static final String SEANCE_WRONG_JOURNEE(Journee journee, Seance seance){
        return seance(journee(SEANCE_WRONG_JOURNEE, journee), seance);
    }
    private static final String SEANCE_WRONG_ELEVE = SEANCE+" : l'"+ELEVE+" ne fait pas partie de la "+CLASSE;
    public static final String SEANCE_WRONG_ELEVE(Seance seance, Eleve eleve, Classe classe){
        return seance(classe(eleve(SEANCE_WRONG_ELEVE, eleve), classe), seance);
    }
    private static final String SEANCE_WRONG_DONNEE = SEANCE+" : la "+DONNEE+" n'appartient pas à l'"+ELEVE;
    public static final String SEANCE_WRONG_DONNEE(Seance seance, Eleve eleve, EleveData donnee){
        return seance(donnee(eleve(SEANCE_WRONG_DONNEE, eleve), donnee), seance);
    }
    
    private static String inject(String msg, String pattern, String value){
        String val = value == null ? "null":value;
        return msg.replaceAll(pattern, val);
    }
    
    private static String classe(String msg, Classe c){
        String id = c == null ? "" : c.getDisplayName();
        return inject(msg, CLASSE, id);
    }
    private static String eleve(String msg, Eleve e){
        String id = e == null ? "" : e.getDisplayName();
        return inject(msg, ELEVE, id);
    }
    
    private static String cours(String msg, Cours c){
        String id = c == null ? "" : c.getDisplayName();
        return inject(msg, COURS, id);
    }
    
    private static String punition(String msg, Punition p){
        String id = p == null ? "" : p.getDisplayName();
        return inject(msg, PUNITION, id);
    }
    
    private static String changement(String msg, ChangementClasse p){
        String id = p == null ? "" : p.getDisplayName();
        return inject(msg, CHANGEMENT, id);
    }
    
    private static String donnee(String msg, EleveData d){
        String id = d == null ? "" : d.getDisplayName();
        return inject(msg, DONNEE, id);
    }
    
    private static String journee(String msg, Journee j){
        String id = j == null ? "" : j.getDisplayName();
        return inject(msg, JOURNEE, id);
    }
    
    private static String seance(String msg, Seance s){
        String id = s == null ? "" : s.getDisplayName();
        return inject(msg, SEANCE, id);
    }
    
    private static String champ(String msg, String o){
        return inject(msg, CHAMP, o);
    }
    
    private static String object(String msg, String o){
        return inject(msg, OBJET, o);
    }
    
    private static String source(String msg, MonitoredObject o){
        return inject(msg, SOURCE, o.getDisplayName());
    }
    
    public static void showListInConsole(List<FError> e){
        System.out.println(" -> LISTE ERREURS ("+e.size()+") <-");
        System.out.println("[ "+StringUtils.join(e, " ]\n[ ")+" ]");
    }
}
