/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mesclasses.objects.tasks;

import javafx.concurrent.Task;
import mesclasses.util.AppLogger;

/**
 *
 * @author rrrt3491
 */
public abstract class AppTask<T> extends Task<T> {
    
    public abstract String getName();
    
    public void log(String e){
        AppLogger.log(e);
    }
    public void log(Exception e){
        AppLogger.log(e);
    }
}
