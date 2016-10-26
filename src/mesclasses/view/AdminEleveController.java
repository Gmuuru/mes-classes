/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mesclasses.view;

import com.google.common.eventbus.Subscribe;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.binding.Bindings;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
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
import mesclasses.model.Eleve;
import mesclasses.objects.events.OpenMenuEvent;
import mesclasses.objects.events.SelectClasseEvent;
import mesclasses.objects.events.SelectEleveEvent;
import mesclasses.util.Btns;
import mesclasses.util.ModalUtil;
import org.smartgrid.SmartGrid;

/**
 * FXML Controller class
 *
 * @author rrrt3491
 */
public class AdminEleveController extends PageController implements Initializable {
    
    @FXML AnchorPane anchor;
            
    @FXML
    private ScrollPane scroll;
    
    @FXML
    private SmartGrid grid;
    
    @FXML
    private Button addEleveBtn;
    
    @FXML
    private Button previousClasseBtn;
    
    @FXML
    private Button nextClasseBtn;
    
    @FXML
    private Label currentClasseLabel;
    
    private Classe currentClasse;
    
    
    
    private final ChangeListener<String> unicityChecker = new ChangeListener<String>() {
        @Override
        public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
            StringProperty textProperty = (StringProperty) observable ;
            Node source = (Node) textProperty.getBean();
            checkUnicity((TextField)grid.get(1, SmartGrid.row(source)), (TextField)grid.get(2, SmartGrid.row(source)));
        }
    };

    
    /**
     * Initializes the controller class.
     */
    @Override
    @FXML
    public void initialize(URL url, ResourceBundle rb) {
        name = "Admin Eleve Ctrl";
        super.initialize(url, rb);
        log("Loading AdminEleveController with "+classes.size()+" classes");
        addEleveBtn.disableProperty().bind(Bindings.size(classes).isEqualTo(0));
        previousClasseBtn.disableProperty().bind(Bindings.size(classes).lessThanOrEqualTo(1));
        nextClasseBtn.disableProperty().bind(Bindings.size(classes).lessThanOrEqualTo(1));
        Btns.makeLeft(previousClasseBtn);
        Btns.makeRight(nextClasseBtn);
        handleKeys();
        init();
    }
    
    private void handleKeys(){
        anchor.setOnKeyReleased((ev) -> {
            if (ev.getCode() == KeyCode.ENTER && !addEleveBtn.isDisable()) { 
                addEleveBtn.fire();
            }
            else if(ev.getCode() == KeyCode.RIGHT && !nextClasseBtn.isDisable()) { 
                nextClasseBtn.fire();
            }
            else if(ev.getCode() == KeyCode.LEFT && !previousClasseBtn.isDisable()) { 
                previousClasseBtn.fire();
            }
            ev.consume(); 
        });
    }
    public void init(){
        resetErrors();
        if(!classes.isEmpty()){
            changeClasse(classes.get(0));
            addEleveBtn.requestFocus();
        } else {
            grid.drawNoClasseAlert((event) -> 
                    EventBusHandler.post(new OpenMenuEvent(Constants.ADMIN_CLASSE_VIEW))
            );
        }
    }
    
    @FXML
    public void previousClasse(){
        int curId = classes.indexOf(currentClasse);
        curId--;
        if(curId == -1){
            curId = classes.size() - 1 ;
        }
        changeClasse(classes.get(curId));
    }
    
    @FXML
    public void nextClasse(){
        int curId = classes.indexOf(currentClasse);
        curId++;
        if(curId == classes.size()){
            curId = 0;
        }
        changeClasse(classes.get(curId));
    }
    
    @FXML public void openClasses(){
        EventBusHandler.post(new OpenMenuEvent(Constants.ADMIN_CLASSE_VIEW));
    }
    
    @FXML public void openCours(){
        EventBusHandler.post(new OpenMenuEvent(Constants.EMPLOI_DU_TEMPS_VIEW));
    }
    
    private void changeClasse(Classe classe){
        if(!notifyExit()){
            return;
        }
        this.modelHandler.cleanupElevesForClasse(currentClasse);
        loadClasse(classe);
    }
    
    private void loadClasse(Classe classe){
        if(classe == null){
            return;
        }
        this.currentClasse = classe;
        this.currentClasseLabel.setText(classe.getName());
        log("Loading classe "+currentClasse.getName()+" avec "+currentClasse.getEleves().size()+" élèves");
        
        if(classe.getEleves().isEmpty()){
            grid.drawNoDataInGrid("Aucun élève dans cette classe");
        } else {
            grid.clear();
            int count = 1;
            for(Eleve eleve : classe.getEleves()){
                addEleveToGrid(eleve, count);
                count++;
            }
        }
    }
    
    @Subscribe
    public void onSelectClasse(SelectClasseEvent event){
        if(event.getClasse() != null && event.getClasse() != currentClasse){
            loadClasse(event.getClasse());
        }
    }
    
    @FXML
    public void handleNewEleve(){
        try {
            // Load the fxml file and create a new stage for the popup dialog.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource(Constants.NEW_ELEVE_VIEW));
            AnchorPane page = (AnchorPane) loader.load();

            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Nouvel élève");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            // Set the person into the controller.
            NewEleveController controller = loader.getController();
            
            controller.setDialogStage(dialogStage);
            controller.setCurrentClasse(currentClasse);

            // Show the dialog and wait until the user closes it
            dialogStage.showAndWait();
            Eleve newEleve = controller.getEleve();
            if(newEleve != null){
                modelHandler.createEleve(newEleve);
                loadClasse(currentClasse);
            }
        } catch (IOException e) {
            Logger.getLogger(RootLayoutController.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    
    private void addEleveToGrid(Eleve eleve, int rowIndex){
        
        TextField lastNameField = new TextField(eleve.getFirstName());
        Bindings.bindBidirectional(lastNameField.textProperty(), eleve.lastNameProperty());
        markAsMandatory(lastNameField);
        checkMandatory(lastNameField);
        lastNameField.setMaxWidth(200);
        grid.add(lastNameField, 1, rowIndex, HPos.LEFT);
        
        TextField firstNameField = new TextField(eleve.getFirstName());
        firstNameField.setMaxWidth(200);
        Bindings.bindBidirectional(firstNameField.textProperty(), eleve.firstNameProperty());
        markAsMandatory(firstNameField);
        checkMandatory(firstNameField);
        grid.add(firstNameField, 2, rowIndex, HPos.LEFT);
        
        lastNameField.textProperty().addListener(unicityChecker);
        firstNameField.textProperty().addListener(unicityChecker);
        checkUnicity(lastNameField, firstNameField);
        CheckBox box = new CheckBox();
        Bindings.bindBidirectional(box.selectedProperty(), eleve.actifProperty());
        grid.add(box, 3, rowIndex, null);
        
        Button rapport = Btns.rapportBtn();
        rapport.setOnAction(e -> {
            EventBusHandler.post(new OpenMenuEvent(Constants.ELEVE_RAPPORT_VIEW).setFromView(Constants.ADMIN_ELEVE_VIEW));
            EventBusHandler.post(new SelectEleveEvent(eleve));
        });
        grid.add(rapport, 4, rowIndex, null);
        Button delete = Btns.deleteBtn();
        delete.setOnAction((ActionEvent e) -> {
            if (ModalUtil.confirm("Suppression de l'eleve", "Etes vous sûr ?")) {
                grid.deleteRow(SmartGrid.row(delete));
                removeErrors(firstNameField, lastNameField, box);
                this.modelHandler.delete(eleve);
            }
        });
        grid.add(delete, 5, rowIndex, null);
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
        boolean foundFirst = false;
        if(firstName == null || lastName == null){
            return true;
        }
        for(Eleve eleve : currentClasse.getEleves()){
            if(firstName.equalsIgnoreCase(eleve.getFirstName()) && 
                    lastName.equalsIgnoreCase(eleve.getLastName())){
                if(!foundFirst){
                    foundFirst = true;
                } else {
                    return false;
                }
            }
        }
        return true;
    }
    
    @Override
    public boolean notifyExit() {
        if(!super.notifyExit()){
            return false;
        }
        log("Unload/ClasseLoad in AdminEleveController");
        this.modelHandler.cleanupElevesForClasse(currentClasse);
        resetErrors();
        return true;
    }
    
    @Override
    public void reload(){
        super.reload();
        if(currentClasse == null && !classes.isEmpty()){
            loadClasse(classes.get(0));
        } else {
            loadClasse(currentClasse);
        }
    }
}
