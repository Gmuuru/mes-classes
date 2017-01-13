/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mesclasses.view;

import java.time.LocalDate;
import java.util.Comparator;
import javafx.geometry.HPos;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import mesclasses.model.Classe;
import mesclasses.model.Constants;
import mesclasses.model.Mot;
import mesclasses.util.Btns;
import mesclasses.util.ModalUtil;
import mesclasses.util.NodeUtil;
import org.smartgrid.SmartGrid;

/**
 *
 * @author rrrt3491
 */
public class MotManager extends LivrableManager<Mot> {

    private final Comparator<Mot> comparator = (Mot t, Mot t1) -> {
        if(t == null){
            return t1 == null ? 0 : -1;
        }
        if(t1 == null){        
            return 1;
        }
        return t1.getSeance().getDateAsDate().compareTo(t.getSeance().getDateAsDate());
    };
    
    public MotManager(Classe classe, SmartGrid gridEnCours, SmartGrid gridFermes) {
        super(classe, gridEnCours, gridFermes);
    }

    @Override
    public boolean populate() {
        enCours.clear();
        fermes.clear();

        if(!getTrimestres()){
            return false;
        }

        if (PunitionsController.INCLUDE_OLD_TRIMESTRES.get()) {
            // depuis la rentrée jusqu'à la fin du trimestre en cours
            classe.getEleves().forEach(eleve -> {
                model.filterMotsByTrimestre(eleve.getMots(), first, current.getEndAsDate())
                        .forEach(m -> {
                            if (m.getDateClotureAsDate() != null) {
                                fermes.add(m);
                            } else {
                                enCours.add(m);
                            }
                        });
            });
        } else {
            // seulement le trimestre en cours
            classe.getEleves().forEach(eleve -> {
                model.filterMotsByTrimestre(eleve.getMots(), current, null)
                        .forEach(punition -> {
                            if (punition.getDateClotureAsDate() != null) {
                                fermes.add(punition);
                            } else {
                                enCours.add(punition);
                            }
                        });
            });
        }
        enCours.sort(comparator);
        fermes.sort(comparator);
        return true;
    }

    private void closeMot(Mot m) {
        model.close(m, LocalDate.now());
        init();
    }

    private void openMot(Mot m) {
        model.reopen(m);
        init();
    }

    private void deleteMot(Mot m) {
        if (ModalUtil.confirm("Supprimer le mot", "Etes-vous sûr(e) ?")) {
            model.delete(m);
            init();
        }
    }
    
    @Override
    protected void drawElement(Mot mot, boolean isClosed) {
        SmartGrid grid = isClosed ? gridFermes : gridEnCours;
        Hyperlink lastName = NodeUtil.buildEleveLink(mot.getEleve(), mot.getEleve().lastNameProperty(), Constants.CLASSE_PUNITIONS_VIEW);
        int rowIndex = grid.addOnNewLineIfNecessary(lastName, 1, HPos.LEFT);
        Hyperlink firstName = NodeUtil.buildEleveLink(mot.getEleve(), mot.getEleve().firstNameProperty(), Constants.CLASSE_PUNITIONS_VIEW);
        grid.add(firstName, 2, rowIndex, HPos.LEFT);

        Label date = new Label(mot.getSeance().getDateAsDate().format(Constants.DATE_FORMATTER_FR)
                + " (cours de " + NodeUtil.getStartTime(mot.getSeance().getCours()) + ")");
        grid.add(date, 3, rowIndex, null);

        if (isClosed) {
            Button openBtn = Btns.arrowUpBtn();
            openBtn.setOnAction((event) -> {
                openMot(mot);
            });
            grid.add(openBtn, 4, rowIndex, null);
        } else {
            Button closeBtn = Btns.arrowDownBtn();
            closeBtn.setOnAction((event) -> {
                closeMot(mot);
            });
            grid.add(closeBtn, 4, rowIndex, null);
        }

        Button deleteBtn = Btns.deleteBtn();
        deleteBtn.setOnAction((event) -> {
            deleteMot(mot);
        });
        grid.add(deleteBtn, 5, rowIndex, null);
    }

    @Override
    protected String getEmptyTextEnCours() {
        return "Aucun mot en cours";
    }

    @Override
    protected String getEmptyTextFerme() {
        return "Aucun mot fermé";
    }
}
