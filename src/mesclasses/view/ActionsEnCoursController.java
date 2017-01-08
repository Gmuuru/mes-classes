/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mesclasses.view;

import java.net.URL;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import mesclasses.MainApp;
import mesclasses.controller.PageController;
import mesclasses.model.Classe;
import mesclasses.model.Constants;
import mesclasses.model.Devoir;
import mesclasses.model.Eleve;
import mesclasses.model.Mot;
import mesclasses.model.Punition;
import mesclasses.util.Btns;
import mesclasses.util.CssUtil;
import mesclasses.util.NodeUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.smartgrid.SmartGrid;

/**
 *
 * @author Gilles
 */
public class ActionsEnCoursController extends PageController implements Initializable {

    private static final Logger LOG = LogManager.getLogger(ActionsEnCoursController.class);
    private Stage dialogStage;

    @FXML
    AnchorPane anchor;
    @FXML
    Button closeBtn;
    @FXML
    SmartGrid grid;

    private Classe classe;

    private final Set<Devoir> aSupprimer = new HashSet<>();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        name = "Actions en cours Ctrl";
        super.initialize(url, rb);
        // TODO
        handleKeys();
    }

    private void handleKeys() {
        anchor.setOnKeyReleased((ev) -> {
            if (ev.getCode() == KeyCode.ENTER) {
                close();
            }
            ev.consume();
        });
    }

    void setClasse(Classe classe) {
        this.classe = classe;
        drawGrid();
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
        this.dialogStage.getIcons().add(new Image(
                MainApp.class.getResourceAsStream("/resources/package/windows/MesClasses.png")));
    }

    @FXML
    void close() {
        aSupprimer.forEach(d -> {
            model.delete(d);
        });
        dialogStage.close();
    }

    private void drawGrid() {
        classe.getEleves().forEach(e -> {
            drawRow(e);
        });
    }

    private void drawRow(Eleve e) {
        List<Punition> punitions = model.getOpenPunitions(e);
        List<Devoir> devoirs = model.getOpenDevoirs(e);
        List<Mot> mots = model.getOpenMots(e);
        if (punitions.isEmpty() && devoirs.isEmpty() && mots.isEmpty()) {
            return;
        }
        LOG.debug("Drawing line for {} : {} punitions, {} devoirs, {}/{} mots", e, punitions.size(), devoirs.size(), mots.size(), e.getMots().size());
        VBox lnBox = new VBox(2);
        lnBox.getChildren().add(NodeUtil.buildEleveLink(e, e.lastNameProperty(), Constants.JOURNEE_VIEW));
        CssUtil.addClass(lnBox, "bordered-cell");
        lnBox.setPadding(new Insets(0, 2, 5, 2));
        int rowIndex = grid.addOnNewLineIfNecessary(lnBox, 1, HPos.LEFT);

        VBox fnBox = new VBox(2);
        fnBox.getChildren().add(NodeUtil.buildEleveLink(e, e.firstNameProperty(), Constants.JOURNEE_VIEW));
        CssUtil.addClass(fnBox, "bordered-cell");
        fnBox.setPadding(new Insets(0, 2, 5, 2));
        grid.add(fnBox, 2, rowIndex, HPos.LEFT);

        VBox punitionsBox = new VBox(2);
        CssUtil.addClass(punitionsBox, "bordered-cell");
        punitionsBox.setPadding(new Insets(0, 2, 5, 2));
        punitions.forEach(p -> {
            HBox box = new HBox(5);
            TextField punitionTextField = new TextField();
            Bindings.bindBidirectional(punitionTextField.textProperty(), p.texteProperty());
            box.getChildren().add(punitionTextField);
            Button close = Btns.okBtn();
            close.setOnAction(ev -> {
                p.setClosed(!p.isClosed());
                if (p.isClosed()) {
                    CssUtil.switchClass(close, "button-done", "button-ok");
                } else {
                    CssUtil.switchClass(close, "button-ok", "button-done");
                }
            });
            box.getChildren().add(close);
            punitionsBox.getChildren().add(box);
        });
        grid.add(punitionsBox, 3, rowIndex, HPos.CENTER);

        VBox devoirsBox = new VBox(2);
        CssUtil.addClass(devoirsBox, "bordered-cell");
        devoirsBox.setPadding(new Insets(0, 2, 5, 2));
        devoirs.forEach(d -> {
            HBox box = new HBox(5);
            box.setAlignment(Pos.CENTER);
            Button rendu = new Button();
            Button tropTard = new Button();

            rendu.setText("Rendu");
            CssUtil.addClass(rendu, "button-ok");
            rendu.setOnAction(ev -> {
                CssUtil.switchClass(tropTard, "button-delete", "button-to-delete");
                d.setClosed(false);
                if (aSupprimer.contains(d)) {
                    LOG.debug("Non rendu");
                    CssUtil.switchClass(rendu, "button-ok", "button-done");
                    aSupprimer.remove(d);
                } else {
                    CssUtil.switchClass(rendu, "button-done", "button-ok");
                    LOG.debug("rendu");
                    aSupprimer.add(d);
                }
            });
            box.getChildren().add(rendu);

            tropTard.setText("Trop tard");
            CssUtil.addClass(tropTard, "button-delete");
            tropTard.setOnAction(ev -> {
                CssUtil.switchClass(rendu, "button-ok", "button-done");
                aSupprimer.remove(d);
                d.setClosed(!d.isClosed());
                if (d.isClosed()) {
                    CssUtil.switchClass(tropTard, "button-to-delete", "button-delete");
                } else {
                    CssUtil.switchClass(tropTard, "button-delete", "button-to-delete");
                }
            });
            box.getChildren().add(tropTard);
            devoirsBox.getChildren().add(box);
        });
        grid.add(devoirsBox, 4, rowIndex, HPos.CENTER);
        
        VBox motsBox = new VBox(2);
        CssUtil.addClass(motsBox, "bordered-cell");
        motsBox.setPadding(new Insets(0, 2, 5, 2));
        motsBox.setAlignment(Pos.CENTER);
        mots.forEach(p -> {
            Button close = Btns.okBtn();
            close.setOnAction(ev -> {
                if (p.getDateCloture() == null) {
                    p.setDateCloture(LocalDate.now());
                    CssUtil.switchClass(close, "button-done", "button-ok");
                } else {
                    LocalDate nullDate = null;
                    p.setDateCloture(nullDate);
                    CssUtil.switchClass(close, "button-ok", "button-done");
                }
            });
            motsBox.getChildren().add(close);
        });
        grid.add(motsBox, 5, rowIndex, HPos.CENTER);
    }
}
