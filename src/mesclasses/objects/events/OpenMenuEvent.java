/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mesclasses.objects.events;

/**
 *
 * @author rrrt3491
 */
public class OpenMenuEvent implements Event {
    
    private String view;
    private String fromView;
    private boolean reload = true;

    public OpenMenuEvent(String view){
        this.view = view;
    }
    
    public String getView() {
        return view;
    }

    public OpenMenuEvent setView(String view) {
        this.view = view;
        return this;
    }

    public String getFromView() {
        return fromView;
    }

    public OpenMenuEvent setFromView(String fromView) {
        this.fromView = fromView;
        return this;
    }

    public boolean isReload() {
        return reload;
    }
    
    public OpenMenuEvent setReload(boolean reload) {
        this.reload = reload;
        return this;
    }
    
    public String toString(){
        return "OpenMenuEvent(\""+view+"\")"+ (fromView != null ? " from "+fromView : "")+ "reload="+reload;
    }
}
