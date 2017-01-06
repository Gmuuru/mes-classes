/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mesclasses.view;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import mesclasses.MainApp;
import mesclasses.util.validation.FError;

/**
 * FXML Controller class
 *
 * @author rrrt3491
 */
public class ErrorDialogController implements Initializable {

    private Stage dialogStage;
    private List<FError> errors = new ArrayList<>();
    @FXML VBox errorContainer;
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        errorContainer.getChildren().clear();
        errors.clear();
    }    

    public Stage getDialogStage() {
        return dialogStage;
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
        this.dialogStage.getIcons().add(new Image(
            MainApp.class.getResourceAsStream( "/resources/package/windows/MesClasses.png" ))); 
    }

    public List<FError> getErrors() {
        return errors;
    }

    public void setErrors(List<FError> errors) {
        this.errors = errors;
        this.errors.forEach(e -> {
            displayError(e);
        });
    }
    
    
    @FXML public void close(){
        dialogStage.close();
    }

    private void displayError(FError e) {
        errorContainer.getChildren().add(new Label(e.getMessage()));
    }
    
}
