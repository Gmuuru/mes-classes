/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mesclasses.view;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import mesclasses.handlers.ModelHandler;
import mesclasses.model.Classe;
import mesclasses.model.Trimestre;
import org.smartgrid.SmartGrid;

/**
 *
 * @author rrrt3491
 * @param <T>
 */
public abstract class LivrableManager<T> {
    
    protected ModelHandler model = ModelHandler.getInstance();
    protected SmartGrid gridEnCours;
    protected SmartGrid gridFermes;
    
    protected List<T> enCours = new ArrayList<>();
    protected List<T> fermes = new ArrayList<>();
    protected String name;
    protected boolean fem;
    protected Trimestre first;
    protected Trimestre current;
    protected Classe classe;
    
    protected Boolean includeOldTrimestres = false;

    public LivrableManager(Classe classe, String name, boolean fem, SmartGrid gridEnCours, SmartGrid gridFermes) {
        this.gridEnCours = gridEnCours;
        this.gridFermes = gridFermes;
        this.classe = classe;
        this.name = name;
    }
    
    public boolean init(){
        boolean ok = populate();
        if(ok){
            draw();
        }
        return ok;
    }
    
    public void switchInclusion() {
        includeOldTrimestres = !includeOldTrimestres;
        populate();
        draw();
    }
    
    protected boolean getTrimestres(){
        if (model.getTrimestres() == null || model.getTrimestres().isEmpty()) {
            return false;
        }
        first = model.getTrimestres().get(0);
        current = model.getForDate(LocalDate.now());
        return current != null;
    }
    
    public abstract boolean populate();
    
    public void draw(){
        gridEnCours.clear();
        String nameStr = fem?"Aucune "+name:"Aucun "+name;
        String closed = fem?" fermée":" fermé";
        if(enCours.isEmpty()) {
            gridEnCours.drawNoDataInGrid(nameStr+" en cours");
        } else {
            enCours.forEach(punition -> drawElement(punition, false));
        }
        gridFermes.clear();
        if (fermes.isEmpty()) {
            gridFermes.drawNoDataInGrid(nameStr+closed);
        } else {
            fermes.forEach(punition -> drawElement(punition, true));
        }
    }

    protected abstract void drawElement(T obj, boolean closed);

    public Boolean getIncludeOldTrimestres() {
        return includeOldTrimestres;
    }

    public void setIncludeOldTrimestres(Boolean includeOldTrimestres) {
        this.includeOldTrimestres = includeOldTrimestres;
    }
    
    
}
