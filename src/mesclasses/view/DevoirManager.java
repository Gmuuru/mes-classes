/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mesclasses.view;

import javafx.geometry.HPos;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import mesclasses.model.Classe;
import mesclasses.model.Constants;
import mesclasses.model.Devoir;
import mesclasses.util.Btns;
import mesclasses.util.ModalUtil;
import mesclasses.util.NodeUtil;
import org.smartgrid.SmartGrid;

/**
 *
 * @author rrrt3491
 */
public class DevoirManager extends LivrableManager<Devoir> {

    public DevoirManager(Classe classe, String name, boolean fem, SmartGrid gridEnCours, SmartGrid gridFermes) {
        super(classe, name, fem, gridEnCours, gridFermes);
    }

    @Override
    public boolean populate() {
        enCours.clear();
        fermes.clear();

        if(!getTrimestres()){
            return false;
        }

        if (includeOldTrimestres) {
            // depuis la rentrée jusqu'à la fin du trimestre en cours
            classe.getEleves().forEach(eleve -> {
                model.filterDevoirsByTrimestre(eleve.getDevoirs(), first, current.getEndAsDate())
                        .forEach(d -> {
                            if (d.isClosed()) {
                                fermes.add(d);
                            } else {
                                enCours.add(d);
                            }
                        });
            });
        } else {
            // seulement le trimestre en cours
            classe.getEleves().forEach(eleve -> {
                model.filterDevoirsByTrimestre(eleve.getDevoirs(), current, null)
                    .forEach(d -> {
                        if (d.isClosed()) {
                            fermes.add(d);
                        } else {
                            enCours.add(d);
                        }
                    });
            });
        }
        return true;
    }

    private void closeDevoir(Devoir d) {
        d.setClosed(true);
        init();
    }

    private void openDevoir(Devoir d) {
        d.setClosed(false);
        init();
    }
    
    private void deleteDevoir(Devoir d) {
        if (ModalUtil.confirm("Supprimer le devoir", "Etes-vous sûr(e) ?")) {
            model.delete(d);
            init();
        }
    }
    
    @Override
    protected void drawElement(Devoir devoir, boolean isClosed) {
        SmartGrid grid = isClosed ? gridFermes : gridEnCours;
        Hyperlink lastName = NodeUtil.buildEleveLink(devoir.getEleve(), devoir.getEleve().lastNameProperty(), Constants.CLASSE_PUNITIONS_VIEW);
        int rowIndex = grid.addOnNewLineIfNecessary(lastName, 1, HPos.LEFT);
        Hyperlink firstName = NodeUtil.buildEleveLink(devoir.getEleve(), devoir.getEleve().firstNameProperty(), Constants.CLASSE_PUNITIONS_VIEW);
        grid.add(firstName, 2, rowIndex, HPos.LEFT);

        Label date = new Label(devoir.getSeance().getDateAsDate().format(Constants.DATE_FORMATTER_FR)
                + " (cours de " + NodeUtil.getStartTime(devoir.getSeance().getCours()) + ")");
        grid.add(date, 3, rowIndex, null);

        if (isClosed) {
            Button openBtn = Btns.arrowUpBtn();
            openBtn.setOnAction((event) -> {
                openDevoir(devoir);
            });
            grid.add(openBtn, 5, rowIndex, null);
        } else {
            Button closeBtn = Btns.arrowDownBtn();
            closeBtn.setOnAction((event) -> {
                closeDevoir(devoir);
            });
            grid.add(closeBtn, 5, rowIndex, null);
        }

        Button deleteBtn = Btns.deleteBtn();
        deleteBtn.setOnAction((event) -> {
            deleteDevoir(devoir);
        });
        grid.add(deleteBtn, 6, rowIndex, null);
    }
}
