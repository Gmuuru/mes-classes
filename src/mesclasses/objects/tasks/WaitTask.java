/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mesclasses.objects.tasks;

/**
 *
 * @author rrrt3491
 */
public class WaitTask extends AppTask<Object> {
    
    long millis;
    public WaitTask(long millis){
        super();
        this.millis = millis;
    }
    @Override
    protected Object call() throws Exception {
        Thread.sleep(millis);
        return null;
    }

    @Override
    public String getName() {
        return null;
    }
    
    
}

