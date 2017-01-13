/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mesclasses.view;

import java.util.Comparator;
import javafx.geometry.HPos;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import mesclasses.model.Classe;
import mesclasses.model.Constants;
import mesclasses.model.Punition;
import mesclasses.util.Btns;
import mesclasses.util.ModalUtil;
import mesclasses.util.NodeUtil;
import org.smartgrid.SmartGrid;

/**
 *
 * @author rrrt3491
 */
public class PunitionManager extends LivrableManager<Punition> {

    
    private final Comparator<Punition> comparator = (Punition t, Punition t1) -> {
        if(t == null){
            return t1 == null ? 0 : -1;
        }
        if(t1 == null){        
            return 1;
        }
        return t1.getSeance().getDateAsDate().compareTo(t.getSeance().getDateAsDate());
    };
    
    public PunitionManager(Classe classe, SmartGrid gridEnCours, SmartGrid gridFermes) {
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
                model.filterPunitionsByTrimestre(eleve.getPunitions(), first, current.getEndAsDate())
                        .forEach(punition -> {
                            if (punition.isClosed()) {
                                fermes.add(punition);
                            } else {
                                enCours.add(punition);
                            }
                        });
            });
        } else {
            // seulement le trimestre en cours
            classe.getEleves().forEach(eleve -> {
                model.filterPunitionsByTrimestre(eleve.getPunitions(), current, null)
                        .forEach(punition -> {
                            if (punition.isClosed()) {
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

            
    private void closePunition(Punition punition) {
        punition.setClosed(true);
        init();
    }

    private void openPunition(Punition punition) {
        punition.setClosed(false);
        init();
    }

    private void deletePunition(Punition punition) {
        if (ModalUtil.confirm("Supprimer la punition", "Etes-vous sûr(e) ?")) {
            model.delete(punition);
            init();
        }
    }
    
    @Override
    protected void drawElement(Punition punition, boolean isClosed) {
        SmartGrid grid = isClosed ? gridFermes : gridEnCours;
        Hyperlink lastName = NodeUtil.buildEleveLink(punition.getEleve(), punition.getEleve().lastNameProperty(), Constants.CLASSE_PUNITIONS_VIEW);
        int rowIndex = grid.addOnNewLineIfNecessary(lastName, 1, HPos.LEFT);
        Hyperlink firstName = NodeUtil.buildEleveLink(punition.getEleve(), punition.getEleve().firstNameProperty(), Constants.CLASSE_PUNITIONS_VIEW);
        grid.add(firstName, 2, rowIndex, HPos.LEFT);

        Label date = new Label(punition.getSeance().getDateAsDate().format(Constants.DATE_FORMATTER_FR)
                + " (cours de " + NodeUtil.getStartTime(punition.getSeance().getCours()) + ")");
        grid.add(date, 3, rowIndex, null);

        if (isClosed) {
            grid.add(new Label(punition.getTexte()), 4, rowIndex, null);
            Button openBtn = Btns.arrowUpBtn();
            openBtn.setOnAction((event) -> {
                openPunition(punition);
            });
            grid.add(openBtn, 5, rowIndex, null);
        } else {
            TextField texte = new TextField();
            texte.textProperty().bindBidirectional(punition.texteProperty());
            texte.setMaxWidth(200);
            grid.add(texte, 4, rowIndex, null);

            Button closeBtn = Btns.arrowDownBtn();
            closeBtn.setOnAction((event) -> {
                closePunition(punition);
            });
            grid.add(closeBtn, 5, rowIndex, null);
        }

        Button deleteBtn = Btns.deleteBtn();
        deleteBtn.setOnAction((event) -> {
            deletePunition(punition);
        });
        grid.add(deleteBtn, 6, rowIndex, null);
    }

    @Override
    protected String getEmptyTextEnCours() {
        return "Aucune punition en cours";
    }

    @Override
    protected String getEmptyTextFerme() {
        return "Aucune punition fermée";
    }
}
