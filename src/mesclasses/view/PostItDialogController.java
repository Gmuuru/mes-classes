/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mesclasses.view;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import mesclasses.MainApp;
import mesclasses.controller.PageController;
import mesclasses.model.Classe;

/**
 * FXML Controller class
 *
 * @author rrrt3491
 */
public class PostItDialogController extends PageController implements Initializable {

    @FXML AnchorPane anchor;
    @FXML Label title;
    @FXML TextArea postIt;
    
    private Classe classe;
    private Stage dialogStage;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        name = "PostIt Ctrl";
        super.initialize(url, rb);
        handleKeys();
    }    
    
    private void handleKeys(){
        anchor.setOnKeyReleased((ev) -> {
            if (ev.getCode() == KeyCode.ESCAPE) { 
                dialogStage.close();
            }
            ev.consume(); 
        });
    }
    
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
        
        this.dialogStage.getIcons().add(new Image(
            MainApp.class.getResourceAsStream( "/resources/package/windows/MesClasses.png" ))); 
    }
    
    public void setClasse(Classe classe){
        this.classe = classe;
        if(classe == null || classe.getEleves().isEmpty()){
            
        } else {
            title.setText("Post It pour la "+classe.getName());
            postIt.setText(classe.getPostIt());
        }
    }
    
    @FXML public void onOK(){
        classe.setPostIt(postIt.getText());
        dialogStage.close();
    }
    @FXML public void onCancel(){
        dialogStage.close();
    }
}
