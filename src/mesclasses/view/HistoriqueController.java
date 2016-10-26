/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mesclasses.view;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import mesclasses.MainApp;
import mesclasses.controller.PageController;
import mesclasses.model.ChangementClasse;
import mesclasses.model.Constants;
import mesclasses.model.Eleve;
import mesclasses.util.AppLogger;
import mesclasses.util.Btns;
import mesclasses.util.CssUtil;
import org.smartgrid.SmartGrid;

/**
 * FXML Controller class
 *
 * @author rrrt3491
 */
public class HistoriqueController  extends PageController implements Initializable {

    @FXML AnchorPane anchor;
    @FXML SmartGrid grid;
    
    private Map<String, Eleve> changes;
    private Map<String, List<Eleve>> eleves;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        name = "Historique Ctrl";
        super.initialize(url, rb);
        log("Loading HistoriqueController");
        
        refreshGrid();
    }    
    
    private void consolidateData(){
        
        changes = new HashMap<>();
        eleves = new HashMap<>();
        
        classes.forEach(c -> {
            c.getEleves().forEach(e -> {
                if(!e.getChangementsClasse().isEmpty()){
                    String id = e.getId();
                    if(!changes.containsKey(id)){
                        Eleve eleve = new Eleve();
                        eleve.setFirstName(e.getFirstName());
                        eleve.setLastName(e.getLastName());
                        eleve.setChangementsClasse(FXCollections.observableArrayList());
                        eleve.setId(id);
                        changes.put(id, eleve);
                    }
                    if(!eleves.containsKey(id)){
                        eleves.put(id, new ArrayList<>());
                    }
                    eleves.get(id).add(e);
                    e.getChangementsClasse().forEach(cc -> {cc.setClasse(c);});
                    changes.get(id).getChangementsClasse().addAll(e.getChangementsClasse());
                    Collections.sort(changes.get(id).getChangementsClasse());
                }
            });
        });
    }
    
    private void refreshGrid(){
        consolidateData();
        grid.clear();
        changes.keySet().forEach((id) -> {
            drawGrid(changes.get(id));
        });
    }
    
    private void drawGrid(Eleve eleve){
        Label textLabel = new Label(eleve.getDisplayName());
        textLabel.setMaxHeight(Double.MAX_VALUE);
        textLabel.setMaxWidth(Double.MAX_VALUE);
        CssUtil.addClass(textLabel, "bordered-cell");
        int rowIndex = grid.addOnNewLineIfNecessary(textLabel, 1, HPos.LEFT);
        VBox detailBox = new VBox();
        detailBox.setAlignment(Pos.CENTER_LEFT);
        CssUtil.addClass(detailBox, "bordered-cell");
        grid.add(detailBox, 2, rowIndex, HPos.LEFT);
        eleve.getChangementsClasse().forEach(cc -> {
            detailBox.getChildren().add(new Label(getText(cc)));
        });
        Button delete = Btns.deleteBtn();
        delete.setDisable(true);
        HBox container = new HBox();
        container.setAlignment(Pos.CENTER);
        CssUtil.addClass(container, "bordered-cell");
        container.getChildren().add(delete);
        grid.add(container, 3, rowIndex, HPos.LEFT);
        delete.setOnAction(e -> {
        });
    }
    
    private String getText(ChangementClasse cc){
        if(cc.getType().equals(Constants.CHANGEMENT_CLASSE_ARRIVEE)){
            return "a intégré la "+cc.getClasse()+" le "+cc.getDateAsDate().format(Constants.LONG_DATE_FORMATTER);
        } else {
            return "a quitté la "+cc.getClasse()+" le "+cc.getDateAsDate().format(Constants.LONG_DATE_FORMATTER);
        }
    }
    
    @FXML void handleNewChange(){
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource(Constants.NEW_CHANGEMENT_VIEW));
            AnchorPane page = (AnchorPane) loader.load();

            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Nouveau changement de classe");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            // Set the person into the controller.
            NewChangementController controller = loader.getController();
            controller.setDialogStage(dialogStage);

            // Show the dialog and wait until the user closes it
            if(controller.showAndWait()){
                refreshGrid();
            }
        } catch (IOException e) {
            AppLogger.notif(name, e);
        }
    }
    
}
