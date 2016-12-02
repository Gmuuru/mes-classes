/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mesclasses.controller;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import mesclasses.handlers.DonneesHandler;
import mesclasses.handlers.ModelHandler;
import mesclasses.handlers.StatsHandler;
import mesclasses.model.Classe;
import mesclasses.model.Cours;
import mesclasses.model.Journee;
import mesclasses.model.Trimestre;
import mesclasses.util.ModalUtil;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author rrrt3491
 */
public abstract class PageController extends BasicController {
    
    protected Stage primaryStage;
    
    protected ModelHandler modelHandler;
    protected DonneesHandler donneesHandler;
    protected StatsHandler stats;
    protected ObservableList<Trimestre> trimestres;
    protected ObservableList<Classe> classes;
    protected ObservableList<Cours> cours;
    protected ObservableMap<LocalDate, Journee> journees;
    
    protected String previousPage;
    
    @Override
    @FXML
    public void initialize(URL url, ResourceBundle rb) {
        name = name == null ? "Page Ctrl" : name;
        super.initialize(url ,rb);
        modelHandler = ModelHandler.getInstance();
        donneesHandler = DonneesHandler.getInstance();
        stats = StatsHandler.getInstance();
        trimestres = modelHandler.getTrimestres();
        classes = modelHandler.getClasses();
        cours = modelHandler.getCours();
        journees = modelHandler.getJournees();
    }    
    
    public final void markAsMandatory(TextField field){
        field.textProperty().addListener((observable, oldValue, newValue) -> {
            checkMandatory(field);
        });
    }
    
    public void checkMandatory(TextField field){
        if(StringUtils.isBlank(field.getText())){
            addMissingError(field);
        } else {
            removeMissingError(field);
        }
    }
    
    public static final void markAsHour(TextField field){
        field.textProperty().addListener((observable, oldValue, newValue) -> {
            if(StringUtils.isBlank(newValue)){
                return;
            }
            if(!StringUtils.isNumeric(newValue.substring(newValue.length()-1))){
                field.setText(newValue.substring(0, newValue.length()-1));
                return;
            }
            if(newValue.length() > 2){
                field.setText(oldValue);
                return;
            }
            if(Integer.parseInt(newValue) > 23){
                field.setText("23");
            }
            
        });
    }
    
    public static final void markAsMin(TextField field){
        field.textProperty().addListener((observable, oldValue, newValue) -> {
            if(StringUtils.isBlank(newValue)){
                return;
            }
            if(!StringUtils.isNumeric(newValue.substring(newValue.length()-1))){
                field.setText(newValue.substring(0, newValue.length()-1));
                return;
            }
            if(newValue.length() > 2){
                field.setText(oldValue);
                return;
            }
            if(Integer.parseInt(newValue) > 59){
                field.setText("59");
            }
            
        });
    }
    
    public void markAsInteger(TextField field){
        field.textProperty().addListener((observable, oldValue, newValue) -> {
            if(StringUtils.isBlank(newValue)){
                return;
            }
            if(!StringUtils.isNumeric(newValue.substring(newValue.length()-1))){
                field.setText(newValue.substring(0, newValue.length()-1));
            }
        });
    }
    
    public final void markAsMandatory(ComboBox box){
        box.selectionModelProperty().addListener((observable, oldValue, newValue) -> {
            checkMandatory(box);
        });
    }
    
    public void checkMandatory(ComboBox box){
        if(box.getSelectionModel().getSelectedItem() == null){
            addMissingError(box);
        } else {
            removeMissingError(box);
        }
    }
    
    public final void markAsMandatory(DatePicker field){
        field.valueProperty().addListener((observable, oldValue, newValue) -> {
            checkMandatory(field);
        });
    }
    
    public void checkMandatory(DatePicker field){
        if(field.getValue() == null){
            addMissingError(field);
        } else {
            removeMissingError(field);
        }
    }
    
    public void markAsUnique(TextField field){
        field.textProperty().addListener((observable, oldValue, newValue) -> {
            if(!isUnique(newValue)){
                addUnicityError(field);
            } else {
                removeUnicityError(field);
            }
        });
    }
    
    public void checkUnique(TextField field){
        if(!isUnique(field.getText())){
            addUnicityError(field);
        } else {
            removeUnicityError(field);
        }
    }
    
    protected boolean isUnique(String val){
        return true;
    }
    
    public boolean notifySave() {
        if(getNbErrors() > 0){
            ModalUtil.alert("Il existe des erreurs sur la page", "Vérifiez la page avant de sauvegarder");
            return false;
        }
        return true;
    }
    
    public void reload(){
        log("Reloading "+getNameAndId());
    }
    
    public boolean notifyExit(){
        if(getNbErrors() > 0){
            ModalUtil.alert("Il existe des erreurs sur la page", "Vérifiez la page avant de la quitter");
            return false;
        }
        return true;
    };

    public String getPreviousPage() {
        return previousPage;
    }

    public void setPreviousPage(String previousPage) {
        this.previousPage = previousPage;
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }
    
}
