/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mesclasses.util;

import java.time.LocalDate;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Hyperlink;
import javafx.util.StringConverter;
import mesclasses.handlers.EventBusHandler;
import mesclasses.model.Constants;
import mesclasses.model.Eleve;
import mesclasses.objects.events.OpenMenuEvent;
import mesclasses.objects.events.SelectEleveEvent;

/**
 *
 * @author rrrt3491
 */
public class NodeUtil {
    
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
    
    public static boolean isBetween(LocalDate date, LocalDate start, LocalDate end){
        return (date.isAfter(start) || date.isEqual(start))
            && (date.isBefore(end) || date.isEqual(end));
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
