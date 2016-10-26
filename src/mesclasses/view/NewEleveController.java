/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mesclasses.view;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import mesclasses.MainApp;
import mesclasses.controller.PageController;
import mesclasses.model.Classe;
import mesclasses.model.Eleve;
import org.smartgrid.SmartGrid;

/**
 *
 * @author rrrt3491
 */
public class NewEleveController extends PageController implements Initializable {
    
    @FXML AnchorPane anchor;
    @FXML TextField lastnameField;
    @FXML TextField firstnameField;
    @FXML SmartGrid grid;
    
    @FXML Button okBtn;
    
    private Stage dialogStage;
    
    private Eleve eleve;
    
    private final ChangeListener<String> unicityChecker = new ChangeListener<String>() {
        @Override
        public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
            checkUnicity(lastnameField, firstnameField);
        }
    };
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        name = "New classe Dialog Ctrl";
        super.initialize(url, rb);
        eleve = new Eleve();
        lastnameField.textProperty().bindBidirectional(eleve.lastNameProperty());
        firstnameField.textProperty().bindBidirectional(eleve.firstNameProperty());
        
        lastnameField.setMaxWidth(200);
        firstnameField.setMaxWidth(200);
        markAsMandatory(lastnameField);
        markAsMandatory(firstnameField);
        lastnameField.textProperty().addListener(unicityChecker);
        firstnameField.textProperty().addListener(unicityChecker);
        
        okBtn.disableProperty().bind(Bindings.or(
                lastnameField.textProperty().isEmpty(),
                Bindings.or(
                firstnameField.textProperty().isEmpty(),
                hasErrors)));
        handleKeys();
    }
    
    private void handleKeys(){
        anchor.setOnKeyReleased((ev) -> {
            if (ev.getCode() == KeyCode.ENTER && !okBtn.isDisable()) { 
                okBtn.fire();
            } else if(ev.getCode() == KeyCode.ESCAPE){
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
    
    public Eleve getEleve(){
        return eleve;
    }

    public void setCurrentClasse(Classe classe) {
        eleve.setClasse(classe);
    }
    
    
    @FXML
    private void handleOk() {
        dialogStage.close();
    }

    /**
     * Called when the user clicks cancel.
     */
    @FXML
    private void handleCancel() {
        eleve = null;
        dialogStage.close();
    }
    
    private void checkUnicity(TextField lastNameField, TextField firstNameField){
        if(!isUnique(firstNameField.getText(), lastNameField.getText())){
            addUnicityError(lastNameField);
            addUnicityError(firstNameField);
        } else {
            removeUnicityError(lastNameField);
            removeUnicityError(firstNameField);
        }
    }
    
    private boolean isUnique(String firstName, String lastName){
        if(firstName == null || lastName == null){
            return true;
        }
        return eleve.getClasse().getEleves().stream().noneMatch((eleveExistant) -> 
                (firstName.equalsIgnoreCase(eleveExistant.getFirstName()) &&
                        lastName.equalsIgnoreCase(eleveExistant.getLastName())));
    }
}
