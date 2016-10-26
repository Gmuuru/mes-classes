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
import javafx.geometry.HPos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import mesclasses.MainApp;
import mesclasses.controller.PageController;
import mesclasses.model.Classe;
import mesclasses.model.Eleve;
import mesclasses.util.CssUtil;
import org.smartgrid.SmartGrid;

/**
 * FXML Controller class
 *
 * @author rrrt3491
 */
public class CumulMotDialogController extends PageController implements Initializable {

    @FXML AnchorPane anchor;
    @FXML SmartGrid grid;
    
    private Stage dialogStage;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        name = "Mots Dialog Ctrl";
        super.initialize(url, rb);
        handleKeys();
    }    
    
    private void handleKeys(){
        anchor.setOnKeyReleased((ev) -> {
            if (ev.getCode() == KeyCode.ENTER || ev.getCode() == KeyCode.ESCAPE) { 
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
        if(classe == null || classe.getEleves().isEmpty()){
            
        } else {
            classe.getEleves().stream().forEach((eleve) -> {
                drawGrid(eleve);
            });
        }
    }
    
    private void drawGrid(Eleve eleve){
        long motsCarnets = eleve.getData().stream().filter(d -> d.isMotCarnet()).count();
        long motsSignes = eleve.getData().stream().filter(d -> d.isMotSigne()).count();
        long val = motsCarnets - motsSignes;
        if(val != 0){
            int rowIndex = grid.getGridHeight();
            Label lastName = new Label(eleve.getLastName());

            Label firstName = new Label(eleve.getFirstName());

            
            Label mots = new Label(String.valueOf(val));
            if(val >=3){
                CssUtil.addClass(lastName, "text-red");
                CssUtil.addClass(firstName, "text-red");
                CssUtil.addClass(mots, "text-red");
            }
            grid.add(lastName, 1, rowIndex, HPos.LEFT);
            grid.add(firstName, 2, rowIndex, HPos.LEFT);
            grid.add(mots, 3, rowIndex, null);
        }
    }
    
    @FXML public void onOK(){
        dialogStage.close();
    }
}
