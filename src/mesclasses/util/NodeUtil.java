/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mesclasses.util;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.Locale;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Hyperlink;
import javafx.util.StringConverter;
import mesclasses.handlers.EventBusHandler;
import mesclasses.handlers.PropertiesCache;
import mesclasses.model.Constants;
import mesclasses.model.Cours;
import mesclasses.model.Eleve;
import mesclasses.model.Trimestre;
import mesclasses.objects.events.OpenMenuEvent;
import mesclasses.objects.events.SelectEleveEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author rrrt3491
 */
public class NodeUtil {
    
    private static final Logger LOG = LogManager.getLogger(EleveFileUtil.class);
    
    public static final StringConverter<LocalDate> DATE_WITH_DAY_NAME = new StringConverter<LocalDate>() {

        @Override 
        public String toString(LocalDate date) {
            if (date != null) {
                return Constants.LONG_DATE_FORMATTER.format(date);
            } else {
                return "";
            }
        }

        @Override 
        public LocalDate fromString(String string) {
            if (string != null && !string.isEmpty()) {
                return LocalDate.parse(string, Constants.LONG_DATE_FORMATTER);
            } else {
                return null;
            }
        }
    };
          
    public static String makeIntegerOnly(String str){
        if (str == null) {
            return "0";
        }
        int length = str.length();
        if (length == 0) {
            return "0";
        }
        StringBuilder sb = new StringBuilder("");
        for(int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c >= '0' && c <= '9') {
                sb.append(c);
            }
        }
        return sb.toString();
    }
    
    public static boolean isInteger(String str) {
        if (str == null) {
            return false;
        }
        int length = str.length();
        if (length == 0) {
            return false;
        }
        int i = 0;
        for (; i < length; i++) {
            char c = str.charAt(i);
            if (c < '0' || c > '9') {
                return false;
            }
        }
        return true;
    }
    
    public static String getStartTime(Cours cours){
        return formatTime(cours.getStartHour(), cours.getStartMin());
    }
    
    public static String getEndTime(Cours cours){
        return formatTime(cours.getEndHour(), cours.getEndMin());
    }
    
    public static String formatTime(int hours, int min){
        return hours+"h"+NodeUtil.formatMinutes(min);
    }
    
    public static String formatMinutes(int min){
        if(min == 0){
            return "";
        }
        if(min < 10){
            return "0"+min;
        }
        return ""+min;
    }
    
    public static String getJour(LocalDate date){
        
        return Constants.DAYMAP.get(date.getDayOfWeek());
    }
    
    public static int getWeekNumber(LocalDate date){
        WeekFields wf = WeekFields.of(Locale.FRANCE);
        return date.get(wf.weekOfWeekBasedYear());
    }
    
    public static boolean coursHappensThisDay(Cours cours, LocalDate date){
        
        PropertiesCache config = PropertiesCache.getInstance();
        if(cours.getWeek() == null){
            LOG.error("Erreur - cours sans semaine : "+cours.getId());
            return false;
        }
        if(cours.getWeek().equals(config.getProperty(Constants.CONF_WEEK_DEFAULT))){
            return true;
        }
        
        int weekInt = getWeekNumber(date);
        
        if(cours.getWeek().equals(config.getProperty(Constants.CONF_WEEK_P1_NAME))){
            // le cours est de type périodique 1
            return coursHappensThisDay(weekInt, config.getIntegerProperty(Constants.CONF_WEEK_P1_VAL));
        }
        if(cours.getWeek().equals(config.getProperty(Constants.CONF_WEEK_P2_NAME))){
            // le cours est de type périodique 1
            return coursHappensThisDay(weekInt, config.getIntegerProperty(Constants.CONF_WEEK_P2_VAL));
        }
        return false;
    }
    
    private static boolean coursHappensThisDay(int dateWeekNumber, int weekConfigValue){
        switch (weekConfigValue) {
            case 0:
                return dateWeekNumber % 2 == 0;
            case 1:
                return dateWeekNumber % 2 == 1;
            default:
                return false;
        }
    }
    
    /**
     * vérifie si une date est entre les dates fournies (incluses)
     * @param date
     * @param start
     * @param end
     * @return 
     */
    public static boolean isBetween(LocalDate date, LocalDate start, LocalDate end){
        if(date == null || start == null || end == null){
            return false;
        }
        return (date.isAfter(start) || date.isEqual(start))
            && (date.isBefore(end) || date.isEqual(end));
    }
    
    public static boolean datesOverlap(Trimestre t1, Trimestre t2){
        if(t1 == null || t2 == null){
            return false;
        }
        return isBetween(t1.getEndAsDate(), t2.getStartAsDate(), t2.getEndAsDate()) || 
            isBetween(t2.getEndAsDate(), t1.getStartAsDate(), t1.getEndAsDate());
    }
    
    public static Hyperlink buildEleveLink(Eleve eleve, StringProperty bindingProperty, String fromView){
        Hyperlink link = new Hyperlink();
        link.textProperty().bind(bindingProperty);
        link.setOnAction((event) -> {
            EventBusHandler.post(new OpenMenuEvent(Constants.ELEVE_RAPPORT_VIEW).setFromView(fromView));
            EventBusHandler.post(new SelectEleveEvent(eleve));
        });
        return link;
    }
}
