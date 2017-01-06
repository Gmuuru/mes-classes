/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mesclasses.model;

import java.time.DayOfWeek;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.collections.FXCollections;

/**
 *
 * @author rrrt3491
 */
public class Constants {
    
    public static final String APPLICATION_TITLE = "Mes Classes";
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String DATE_FORMAT_FR = "dd-MM-yyyy";
    public static final String LONG_DATE_FORMAT = "EEEE dd MMMM yyyy";
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(Constants.DATE_FORMAT);
    public static final DateTimeFormatter DATE_FORMATTER_FR = DateTimeFormatter.ofPattern(Constants.DATE_FORMAT_FR);
    public static final DateTimeFormatter LONG_DATE_FORMATTER = DateTimeFormatter.ofPattern(LONG_DATE_FORMAT);
    
    public static final List<String> DAYS = Arrays.asList(new String[]{"Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi", "Samedi"});
    public static final List<String> OBSERVABLE_DAYS = FXCollections.observableArrayList(DAYS);
    
    public static final Map<DayOfWeek, String> DAYMAP = new HashMap<>();
    
    static {
        DAYMAP.put(DayOfWeek.MONDAY, DAYS.get(0));
        DAYMAP.put(DayOfWeek.TUESDAY, DAYS.get(1));
        DAYMAP.put(DayOfWeek.WEDNESDAY, DAYS.get(2));
        DAYMAP.put(DayOfWeek.THURSDAY, DAYS.get(3));
        DAYMAP.put(DayOfWeek.FRIDAY, DAYS.get(4));
        DAYMAP.put(DayOfWeek.SATURDAY, DAYS.get(5));
    }
    
    public static final String ACCUEIL_VIEW = "view/Accueil.fxml";
    public static final String ADMIN_TRIMESTRE_VIEW = "view/AdminTrimestre.fxml";
    public static final String ADMIN_CLASSE_VIEW = "view/AdminClasse.fxml";
    public static final String NEW_CLASSE_VIEW = "view/NewClasseDialog.fxml";
    public static final String ADMIN_ELEVE_VIEW = "view/AdminEleve.fxml";
    public static final String NEW_ELEVE_VIEW = "view/NewEleveDialog.fxml";
    public static final String EMPLOI_DU_TEMPS_VIEW = "view/Timetable.fxml";
    public static final String CLASSE_TABS_VIEW = "view/ClasseTabs.fxml";
    public static final String JOURNEE_VIEW = "view/Journee.fxml";
    public static final String CLASSE_RAPPORT_TABS_VIEW = "view/RapportClasseTabs.fxml";
    public static final String CLASSE_RAPPORT_VIEW = "view/RapportClasse.fxml";
    public static final String COURS_EDIT_DIALOG = "view/CoursEditDialog.fxml";
    public static final String CLASSE_MOTS_DIALOG = "view/CumulMotDialog.fxml";
    public static final String CLASSE_PUNITIONS_VIEW = "view/Punitions.fxml";
    public static final String ELEVE_RAPPORT_VIEW = "view/RapportEleve.fxml";
    public static final String HISTORIQUE_VIEW = "view/Historique.fxml";
    public static final String NEW_CHANGEMENT_VIEW = "view/NewChangementDialog.fxml";
    public static final String POSTIT_DIALOG = "view/PostItDialog.fxml";
    public static final String CONFIGURATION_VIEW = "view/Configuration.fxml";
    public static final String ERREURS_VIEW = "view/ErrorDialog.fxml";
    
    public static final String CONF_WEEK_DEFAULT = "semaines.normal.nom";
    public static final String CONF_WEEK_P1_NAME = "semaines.periodique1.nom";
    public static final String CONF_WEEK_P1_VAL = "semaines.periodique1.valeur";
    public static final String CONF_WEEK_P2_NAME = "semaines.periodique2.nom";
    public static final String CONF_WEEK_P2_VAL = "semaines.periodique2.valeur";
    
    public static final String FILE_TYPE_BULLETIN = "Bulletin";
    public static final String FILE_TYPE_VIE_SCOLAIRE = "Vie scolaire";
    public static final String FILE_TYPE_ORIENTATION = "Orientation";
    public static final List<String> FILE_TYPES = Arrays.asList(new String[]{FILE_TYPE_BULLETIN, FILE_TYPE_VIE_SCOLAIRE, FILE_TYPE_ORIENTATION});
    
    
    public static final String DEFAULT_EVENT_CSS = "/resources/css/defaultEvents.css";
    
    public static final String CHANGEMENT_CLASSE_DEPART = "départ";
    public static final String CHANGEMENT_CLASSE_ARRIVEE = "arrivée";
}
