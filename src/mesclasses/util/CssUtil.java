/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mesclasses.util;

import javafx.css.Styleable;

/**
 *
 * @author rrrt3491
 */
public class CssUtil {
    
    public static void addClass(Styleable obj, String cssClass){
        if(!obj.getStyleClass().contains(cssClass)){
            obj.getStyleClass().add(cssClass);
        }
    }
    
    public static void removeClass(Styleable obj, String cssClass){
        obj.getStyleClass().removeIf(c -> c.equals(cssClass));
    }
    
    public static void switchClass(Styleable obj, String cssClassToAdd, String removeClass){
        removeClass(obj, removeClass);
        addClass(obj, cssClassToAdd);
    }
}
