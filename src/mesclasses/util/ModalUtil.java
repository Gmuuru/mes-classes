/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mesclasses.util;

import java.util.Optional;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;

/**
 *
 * @author rrrt3491
 */
public class ModalUtil {
    
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
}
