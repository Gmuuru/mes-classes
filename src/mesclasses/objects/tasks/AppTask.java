/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mesclasses.objects.tasks;

import javafx.concurrent.Task;

/**
 *
 * @author rrrt3491
 * @param <T>
 */
public abstract class AppTask<T> extends Task<T> {
    
    protected String msg;
    
    public abstract String getName();

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
