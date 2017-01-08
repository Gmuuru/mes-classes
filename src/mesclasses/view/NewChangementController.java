/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mesclasses.view;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import mesclasses.MainApp;
import mesclasses.controller.PageController;
import mesclasses.model.ChangementClasse;
import mesclasses.model.Classe;
import mesclasses.model.Constants;
import mesclasses.model.Eleve;

/**
 * FXML Controller class
 *
 * @author Gilles
 */
public class NewChangementController extends PageController implements Initializable {

    @FXML AnchorPane anchor;
    @FXML Button okBtn;
    @FXML ComboBox<Classe> classeBox;
    @FXML ComboBox<Eleve> eleveBox;
    @FXML Label eleveStatusTitle;
    @FXML Label eleveStatus;
    @FXML HBox changeBox;
    @FXML Label eleveName;
    @FXML Label changementTitle;
    @FXML ComboBox<String> changes;
    @FXML DatePicker date;
    @FXML Label destination;
    @FXML ComboBox<Classe> destinationBox;
    @FXML Label resume;
    private Stage dialogStage;
    
    private BooleanProperty eleveSelected;
   
    private boolean okClicked;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        name = "Changement Dialog Ctrl";
        super.initialize(url, rb);
        eleveSelected = new SimpleBooleanProperty(false);
        eleveStatusTitle.visibleProperty().bind(eleveSelected);
        eleveStatus.visibleProperty().bind(eleveSelected);
        changementTitle.visibleProperty().bind(eleveSelected);
        changeBox.visibleProperty().bind(eleveSelected);
        resume.visibleProperty().bind(eleveSelected);
        
        classeBox.getItems().addAll(classes);
        classeBox.valueProperty().addListener(new ChangeListener<Classe>() {
            @Override public void changed(ObservableValue ov, Classe t, Classe t1) {
              if(t == null || !t1.getName().equals(t.getName())){
                  eleveBox.getItems().clear();
                  eleveBox.getItems().addAll(classeBox.getSelectionModel().getSelectedItem().getEleves());
              }
            }
        });
        
        eleveBox.valueProperty().addListener(new ChangeListener<Eleve>() {
            @Override public void changed(ObservableValue ov, Eleve t, Eleve t1) {
              eleveSelected.set(t1 != null);
              if(t1 != null){
                updateCurrentStatus(t1);
                updateChangeBox(t1);
              }
            }
        });
        
        changes.valueProperty().addListener(new ChangeListener<String>() {
            @Override public void changed(ObservableValue ov, String t, String t1) {
              updateDestinationBox();
            }
        });
        handleKeys();
    }
    
    private void handleKeys(){
        anchor.setOnKeyReleased((ev) -> {
            if(ev.getCode() == KeyCode.ESCAPE){
                handleCancel();
            }
            ev.consume(); 
        });
    }  
    
    
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
        this.dialogStage.getIcons().add(new Image(
            MainApp.class.getResourceAsStream( "/resources/package/windows/MesClasses.png" ))); 
    }
    
    private void updateCurrentStatus(Eleve eleve) {
        String text;
        if(eleve.getChangementsClasse().isEmpty()){
            text = eleve.getFirstName()+" est dans la "+eleve.getClasse()+" depuis la rentrée";
        } else {
            ChangementClasse last = eleve.getChangementsClasse().get(eleve.getChangementsClasse().size()-1);
            if(!isCurrentlyInClasse(eleve)){
                text = eleve.getFirstName()+" a quitté la "+eleve.getClasse()
                        +" le "+last.getDateAsDate().format(Constants.LONG_DATE_FORMATTER);
            } else {
                text = eleve.getFirstName()+" a rejoint la "+eleve.getClasse()
                        +" le "+last.getDateAsDate().format(Constants.LONG_DATE_FORMATTER);
            }
        }
        eleveStatus.setText(text);
    }
    
    private void updateChangeBox(Eleve t1) {
        eleveName.setText(t1.getFirstName());
        String depart = "quitte la "+t1.getClasse();
        String arrivee = "rejoint la "+t1.getClasse();
        changes.getItems().clear();
        if(isCurrentlyInClasse(t1)){
            changes.getItems().add(depart);
        }
        changes.getItems().add(arrivee);
    }
    
    private void updateDestinationBox() {
        if(changes.getSelectionModel().getSelectedItem() != null && 
                !changes.getSelectionModel().getSelectedItem().contains("quitte")){
            destination.setVisible(false);
            destinationBox.setVisible(false);
            return;
        }
        destination.setVisible(true);
        destinationBox.setVisible(true);
        Eleve selected = eleveBox.getSelectionModel().getSelectedItem();
        if(selected == null){
            return;
        }
        destinationBox.getItems().clear();
        destinationBox.getItems().add(new Classe("(aucune)"));
        destinationBox.getItems().addAll(classes.filtered(c -> !c.getName().equals(selected.getClasse().getName())));
        destinationBox.getSelectionModel().selectFirst();
    }
            
    private boolean isCurrentlyInClasse(Eleve eleve){
        if(eleve.getChangementsClasse().isEmpty()){
            return true;
        }
        ChangementClasse last = eleve.getChangementsClasse().get(eleve.getChangementsClasse().size()-1);
        return !last.getType().equals(Constants.CHANGEMENT_CLASSE_DEPART);
    }
    
    @FXML void handleOk() {
        buildChangement();
        okClicked = true;
        dialogStage.close();
    }

    private void buildChangement(){
        Eleve eleve = eleveBox.getValue();
        
        ChangementClasse cc = new ChangementClasse();
        eleve.getChangementsClasse().add(cc);
        cc.setClasse(classeBox.getValue());
        cc.setDate(date.getValue());
        if(changes.getSelectionModel().getSelectedItem().contains("rejoint")){
            cc.setType(Constants.CHANGEMENT_CLASSE_ARRIVEE);
        } else {
            cc.setType(Constants.CHANGEMENT_CLASSE_DEPART);
            if(destinationBox.getValue() != null && !"(aucune)".equalsIgnoreCase(destinationBox.getValue().getName())){
                Eleve eleveInNewClasse = model.moveEleveToClasse(eleve, destinationBox.getValue());
                ChangementClasse cc2 = new ChangementClasse();
                cc2.setClasse(destinationBox.getValue());
                cc2.setDate(date.getValue().plusDays(1));
                cc2.setType(Constants.CHANGEMENT_CLASSE_ARRIVEE);
                eleveInNewClasse.getChangementsClasse().add(cc2);
            }
        }
    }
    /**
     * Called when the user clicks cancel.
     */
    @FXML void handleCancel() {
        okClicked = false;
        dialogStage.close();
    }
    
    public boolean showAndWait(){
        dialogStage.showAndWait();
        return okClicked;
    }
}
