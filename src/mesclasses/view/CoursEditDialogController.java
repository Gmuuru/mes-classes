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
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import mesclasses.MainApp;
import mesclasses.controller.PageController;
import mesclasses.model.Classe;
import mesclasses.model.Constants;
import mesclasses.model.Cours;
import mesclasses.objects.IntegerOnlyConverter;
import mesclasses.util.ModalUtil;

/**
 * FXML Controller class
 *
 * @author rrrt3491
 */
public class CoursEditDialogController extends PageController implements Initializable {

    
    @FXML
    private AnchorPane anchor;
    @FXML
    private ComboBox<String> day;
    @FXML
    private ComboBox<Classe> classe;
    @FXML
    private ComboBox<String> semaine;
    @FXML
    private TextField room;
    @FXML
    private TextField startHour;
    @FXML
    private TextField startMin;
    @FXML
    private TextField endHour;
    @FXML
    private TextField endMin;
    
    @FXML Button deleteBtn;
    
    @FXML
    Button okBtn;
        
    private Stage dialogStage;
    
    private Cours newCours;
    
    private int status;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        name = "Cours Init Dialog Ctrl";
        super.initialize(url, rb);
        // TODO
        day.getItems().addAll(Constants.DAYS);
        semaine.getItems().add(config.getProperty(Constants.CONF_WEEK_DEFAULT));
        semaine.getItems().add(config.getProperty(Constants.CONF_WEEK_P1));
        semaine.getItems().add(config.getProperty(Constants.CONF_WEEK_P2));
        classe.getItems().addAll(classes);
        okBtn.disableProperty().bind(this.hasErrors);
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
    
    @FXML
    private void handleOk() {
        status = 1;
        dialogStage.close();
    }

    /**
     * Called when the user clicks cancel.
     */
    @FXML
    private void handleCancel() {
        status = 0;
        newCours = null;
        dialogStage.close();
    }
       
    @FXML public void handleDelete(){
    
        if(ModalUtil.confirm("Supprimer ce cours", "Etes-vous sûr(e) ?")){
            status = -1;
            dialogStage.close();
        }
    }
    
    public void setCours(Cours newCours){
        this.newCours = newCours;
        if(newCours == null){
            dialogStage.close();
        }
        if(newCours.getEvent() == null){
            deleteBtn.setDisable(true);
        }
        day.valueProperty().bindBidirectional(this.newCours.dayProperty());
        room.textProperty().bindBidirectional(this.newCours.roomProperty());
        semaine.valueProperty().bindBidirectional(this.newCours.weekProperty());
        classe.valueProperty().bindBidirectional(this.newCours.classeProperty());
        startHour.textProperty().bindBidirectional(this.newCours.startHourProperty(), new IntegerOnlyConverter());
        startMin.textProperty().bindBidirectional(this.newCours.startMinProperty(), new IntegerOnlyConverter());
        endHour.textProperty().bindBidirectional(this.newCours.endHourProperty(), new IntegerOnlyConverter());
        endMin.textProperty().bindBidirectional(this.newCours.endMinProperty(), new IntegerOnlyConverter());
        
        markAsMandatory(day);
        markAsMandatory(semaine);
        markAsMandatory(classe);
        markAsMandatory(startHour);
        markAsHour(startHour);
        markAsMin(startMin);
        markAsMandatory(endHour);
        markAsHour(endHour);
        markAsMin(endMin);
        
    }
    
    public Cours getCours() {
        return newCours;
    }

    public int getStatus() {
        return status;
    }
    
}
