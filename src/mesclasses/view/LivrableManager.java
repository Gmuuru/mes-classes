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

    public LivrableManager(Classe classe, SmartGrid gridEnCours, SmartGrid gridFermes) {
        this.gridEnCours = gridEnCours;
        this.gridFermes = gridFermes;
        this.classe = classe;

        PunitionsController.INCLUDE_OLD_TRIMESTRES.addListener((ob, o, n) -> {
            init();
        });
    }

    public boolean init() {
        boolean ok = populate();
        if (ok) {
            draw();
        }
        return ok;
    }

    protected boolean getTrimestres() {
        if (model.getTrimestres() == null || model.getTrimestres().isEmpty()) {
            return false;
        }
        first = model.getTrimestres().get(0);
        current = model.getForDate(LocalDate.now());
        return current != null;
    }

    public abstract boolean populate();

    protected abstract String getEmptyTextEnCours();

    protected abstract String getEmptyTextFerme();

    public void draw() {
        gridEnCours.clear();
        if (enCours.isEmpty()) {
            gridEnCours.drawNoDataInGrid(getEmptyTextEnCours());
        } else {
            enCours.forEach(punition -> drawElement(punition, false));
        }
        gridFermes.clear();
        if (fermes.isEmpty()) {
            gridFermes.drawNoDataInGrid(getEmptyTextFerme());
        } else {
            fermes.forEach(punition -> drawElement(punition, true));
        }
    }

    protected abstract void drawElement(T obj, boolean closed);

}
