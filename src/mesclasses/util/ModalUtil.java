/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mesclasses.util;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import mesclasses.MainApp;
import mesclasses.model.Constants;
import mesclasses.util.validation.FError;
import mesclasses.view.ErrorDialogController;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author rrrt3491
 */
public class ModalUtil {
    
    private static final Logger LOG = LogManager.getLogger(ModalUtil.class);
    
    public static boolean confirm(String header, String text){
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Requise");
        alert.setHeaderText(header);
        alert.setContentText(text);

        Optional<ButtonType> result = alert.showAndWait();
        return (result.get() == ButtonType.OK);
    }
    
    public static void alert(String header, String text){
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(header);
        alert.setContentText(text);
        alert.showAndWait();
    }
    
    public static void info(String header, String text){
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(header);
        alert.setContentText(text);
        alert.showAndWait();
    }
    public static String prompt(String header, String text){
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Entrez une valeur");
        dialog.setHeaderText(header);
        dialog.setContentText(text);
        dialog.getDialogPane().lookupButton(ButtonType.OK).disableProperty()
                .bind(dialog.getEditor().textProperty().isEmpty());
        // Traditional way to get the response value.
        Optional<String> res = dialog.showAndWait();
        if(res.isPresent()){
            return res.get();
        }
        return null;
    }
    
    public static String yesNoCancel(String header, String content){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Requise");
        alert.setHeaderText(header);
        alert.setContentText(content);
        ButtonType okButton = new ButtonType("Oui", ButtonBar.ButtonData.YES);
        ButtonType noButton = new ButtonType("Non", ButtonBar.ButtonData.NO);
        ButtonType cancelButton = new ButtonType("Annuler", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(okButton, noButton, cancelButton);
        Optional<ButtonType> result = alert.showAndWait();
        if(result.get() == okButton){
            return "yes";
        } else if(result.get() == noButton){
            return "no";
        } else {
            return "cancel";
        }
    }
    
    public static void showErrors(List<FError> list) {
        try {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(MainApp.class.getResource(Constants.ERREURS_VIEW));
        BorderPane page = (BorderPane) loader.load();

        // Create the dialog Stage.
        Stage dialogStage = new Stage();
        dialogStage.setTitle("Erreurs sur la page");
        dialogStage.initModality(Modality.WINDOW_MODAL);
        Scene scene = new Scene(page);
        dialogStage.setScene(scene);

        // Set the person into the controller.
        ErrorDialogController controller = loader.getController();
        controller.setDialogStage(dialogStage);
        controller.setErrors(list);

        // Show the dialog and wait until the user closes it
        dialogStage.showAndWait();
        } catch(IOException e){
            LOG.error("Erreur d'affichage : ", e);
        }
    }
}
