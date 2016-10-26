/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mesclasses.view;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import mesclasses.MainApp;
import mesclasses.controller.PageController;
import mesclasses.model.Classe;

/**
 *
 * @author rrrt3491
 */
public class NewClasseController extends PageController implements Initializable {
    
    @FXML AnchorPane anchor;
    
    @FXML TextField nameField;
    
    @FXML CheckBox principaleBox;
    
    @FXML Button okBtn;
    
    private Stage dialogStage;
    
    private Classe classe;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        name = "New classe Dialog Ctrl";
        super.initialize(url, rb);
        classe = new Classe();
        nameField.textProperty().bindBidirectional(classe.nameProperty());
        principaleBox.selectedProperty().bindBidirectional(classe.principaleProperty());
        
        nameField.setMaxWidth(200);
        markAsMandatory(nameField);
        markAsUnique(nameField);
        
        okBtn.disableProperty().bind(Bindings.or(nameField.textProperty().isEmpty(),hasErrors));
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
    
    public Classe getClasse(){
        return classe;
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
        classe = null;
        dialogStage.close();
    }
    
    @Override
    protected boolean isUnique(String classeName){
        return classes.stream().filter( c -> c.getName().equalsIgnoreCase(classeName) ).count() == 0;
    }
}
