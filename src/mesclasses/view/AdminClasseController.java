/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mesclasses.view;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import mesclasses.MainApp;
import mesclasses.controller.PageController;
import mesclasses.handlers.EventBusHandler;
import mesclasses.model.Classe;
import mesclasses.model.Constants;
import mesclasses.objects.events.ClassesChangeEvent;
import mesclasses.objects.events.OpenMenuEvent;
import mesclasses.util.AppLogger;
import mesclasses.util.Btns;
import mesclasses.util.ModalUtil;
import mesclasses.util.validation.ListValidators;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.smartgrid.SmartGrid;
import org.smartgrid.elements.MoveButtons;

/**
 * FXML Controller class
 *
 * @author rrrt3491
 */
public class AdminClasseController extends PageController implements Initializable {

    private static final Logger LOG = LogManager.getLogger(AdminClasseController.class);
    
    @FXML AnchorPane anchor;
    @FXML SmartGrid grid;
    @FXML Button addClasseBtn;
    
    
    
    /**
     * Initializes the controller class.
     */
    @Override
    @FXML
    public void initialize(URL url, ResourceBundle rb) {
        name = "Admin Classe Ctrl";
        super.initialize(url, rb);
        LOG.info("Loading AdminClasseController with "+ classes.size() +" classes");
        handleKeys();
        
        errorMessagesLabel.setOnAction((e) -> {
            openErrorDialog();
        });
        
        init();
    } 
    
    private void handleKeys(){
        try {
        anchor.setOnKeyReleased((ev) -> {
            if (ev.getCode() == KeyCode.ENTER && !addClasseBtn.isDisable()) { 
                addClasseBtn.fire();
            }
            ev.consume(); 
        });
        } catch (Exception e){
            notif(e);
        }
    }
    public void init(){
        grid.clear();
        classes.stream().forEach((classe) -> {
            addClasseToGrid(classe);
        });
    }
    
    @FXML public void openEleves(){
        EventBusHandler.post(new OpenMenuEvent(Constants.ADMIN_ELEVE_VIEW));
    }
    
    @FXML public void openCours(){
        EventBusHandler.post(new OpenMenuEvent(Constants.EMPLOI_DU_TEMPS_VIEW));
    }
    
    @FXML
    public void handleNewClasse(){
        try {
            // Load the fxml file and create a new stage for the popup dialog.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource(Constants.NEW_CLASSE_VIEW));
            AnchorPane page = (AnchorPane) loader.load();

            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Nouvelle classe");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            // Set the person into the controller.
            NewClasseController controller = loader.getController();
            controller.setDialogStage(dialogStage);

            // Show the dialog and wait until the user closes it
            dialogStage.showAndWait();
            Classe newClasse = controller.getClasse();
            if(newClasse != null){
                modelHandler.createClasse(newClasse);
                EventBusHandler.post(new ClassesChangeEvent());
                addClasseToGrid(newClasse);
            }

        } catch (IOException e) {
            AppLogger.notif(name, e);
        }
    
    }
    
    private void addClasseToGrid(Classe classe){
        int rowIndex = grid.getGridHeight();
        TextField nameField = new TextField(classe.getName());
        Bindings.bindBidirectional(nameField.textProperty(), classe.nameProperty());
        nameField.setMaxWidth(200);
        markAsMandatory(nameField);
        markAsUnique(nameField);
        checkMandatory(nameField);
        checkUnique(nameField);
        grid.add(nameField, 1, rowIndex, HPos.LEFT);
        
        CheckBox box = new CheckBox();
        Bindings.bindBidirectional(box.selectedProperty(), classe.principaleProperty());
        grid.add(box, 2, rowIndex, null);
        
        MoveButtons moveButtons = new MoveButtons();
        Btns.makeUp(moveButtons.getButtonUp());
        Btns.makeDown(moveButtons.getButtonDown());
        grid.add(moveButtons, 3, rowIndex, null);
        
        moveButtons.setOnMove((event) -> {
            Collections.swap(classes, event.getSourceRow()-1, event.getDestRow()-1);
            EventBusHandler.post(new ClassesChangeEvent());
        });
        
        Button delete = Btns.deleteBtn();
        delete.setOnAction((ActionEvent e) -> {
            if (ModalUtil.confirm("Suppression de la classe", "Etes vous sûr ?")) {
                grid.deleteRow(SmartGrid.row(delete));
                removeErrors(nameField, box);
                this.modelHandler.delete(classe);
                 EventBusHandler.post(new ClassesChangeEvent());
            }
        });
        grid.add(delete, 4, rowIndex, null);
    }

    @Override
    public boolean notifyExit() {
        if(!super.notifyExit()){
            return false;
        }
        LOG.info("Unload of AdminClasseController");
        this.modelHandler.cleanupClasses();
        return true;
    }
    
    @Override
    public void reload(){
        LOG.info("reloading classes with "+classes.size()+" classes"); 
        init();
    }
    
    
    @Override
    protected boolean isUnique(String classeName){
        List<String> names = classes.stream().map( c -> c.getName() ).collect(Collectors.toList());
        return Collections.frequency(names, classeName) == 1;
    }
    
    private void openErrorDialog() {
        ModalUtil.showErrors(ListValidators.validateClasseList(classes));
    }
}
