/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mesclasses.view;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import mesclasses.controller.PageController;
import mesclasses.model.Constants;
import mesclasses.model.Cours;
import mesclasses.util.ModalUtil;

/**
 * FXML Controller class
 *
 * @author rrrt3491
 */
public class ConfigSemainesController extends PageController implements Initializable {

    /**
     * ComboBoxes
     * 1 = semaines paires
     * 2 = semaines impaires
     */
    @FXML ComboBox<Integer> p1ComboBox;
    @FXML ComboBox<Integer> p2ComboBox;
    
    @FXML TextField standardNameField;
    @FXML TextField p1NameField;
    @FXML TextField p2NameField;
    
    @FXML Button saveBtn;
    
    private BooleanProperty pristine = new SimpleBooleanProperty(true);
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        name = "Config Semaine Ctrl";
        // TODO
        standardNameField.textProperty().set(config.getProperty(Constants.CONF_WEEK_DEFAULT));
        standardNameField.textProperty().addListener((observable, oldValue, newValue) -> {
            pristine.set(false);
        });
        markAsMandatory(standardNameField);
        checkMandatory(standardNameField);
        p1NameField.textProperty().set(config.getProperty(Constants.CONF_WEEK_P1));
        p1NameField.textProperty().addListener((observable, oldValue, newValue) -> {
            pristine.set(false);
        });
        markAsMandatory(p1NameField);
        checkMandatory(p1NameField);
        p2NameField.textProperty().set(config.getProperty(Constants.CONF_WEEK_P2));
        markAsMandatory(p2NameField);
        checkMandatory(p2NameField);
        p2NameField.textProperty().addListener((observable, oldValue, newValue) -> {
            pristine.set(false);
        });
        
        saveBtn.disableProperty().bind(Bindings.or(pristine, hasErrors));
        super.initialize(url, rb);
    }    
    
    @FXML void onSave(){
        //update des cours
        log("nb de cours : "+modelHandler.getCours());
        modelHandler.getCours().forEach(c -> updateWeek(c));
        config.setProperty(Constants.CONF_WEEK_DEFAULT, standardNameField.getText());
        config.setProperty(Constants.CONF_WEEK_P1, p1NameField.getText());
        config.setProperty(Constants.CONF_WEEK_P2, p2NameField.getText());
        config.save();
        pristine.set(true);
    }
    
    private void updateWeek(Cours c){
        if(c.getWeek().equals(config.getProperty(Constants.CONF_WEEK_P1))){
            c.setWeek(p1NameField.getText());
        } else if(c.getWeek().equals(config.getProperty(Constants.CONF_WEEK_P2))){
            c.setWeek(p2NameField.getText());
        } else {
            c.setWeek(standardNameField.getText());
        }
    }
    
    @Override
    public boolean notifyExit(){
        Boolean res = super.notifyExit();
        if(!res){
            return false;
        }
        res = pristine.get() || ModalUtil.confirm("Confirmation", "Changer de page sans appliquer les changements ?");
        return res;
    }
    
    @Override
    public void reload(){
        super.reload();
    }
}
