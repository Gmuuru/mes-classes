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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * FXML Controller class
 *
 * @author rrrt3491
 */
public class ConfigSemainesController extends PageController implements Initializable {

    private static final Logger LOG = LogManager.getLogger(ConfigSemainesController.class);
    /**
     * ComboBoxes
     * 1 = semaines paires
     * 2 = semaines impaires
     */
    @FXML ComboBox<String> p1ComboBox;
    @FXML ComboBox<String> p2ComboBox;
    
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
        
        p1NameField.textProperty().set(config.getProperty(Constants.CONF_WEEK_P1_NAME));
        p1NameField.textProperty().addListener((observable, oldValue, newValue) -> {
            pristine.set(false);
        });
        markAsMandatory(p1NameField);
        checkMandatory(p1NameField);
        p1ComboBox.getItems().add("Semaines paires");
        p1ComboBox.getItems().add("Semaines impaires");
        p1ComboBox.getSelectionModel().select(config.getIntegerProperty(Constants.CONF_WEEK_P1_VAL, 0));
        
        p2NameField.textProperty().set(config.getProperty(Constants.CONF_WEEK_P2_NAME));
        markAsMandatory(p2NameField);
        checkMandatory(p2NameField);
        p2NameField.textProperty().addListener((observable, oldValue, newValue) -> {
            pristine.set(false);
        });
        p2ComboBox.getItems().add("Semaines paires");
        p2ComboBox.getItems().add("Semaines impaires");
        p2ComboBox.getSelectionModel().select(config.getIntegerProperty(Constants.CONF_WEEK_P2_VAL, 1));
        
        saveBtn.disableProperty().bind(Bindings.or(pristine, hasErrors));
        super.initialize(url, rb);
        
        p1ComboBox.getSelectionModel().selectedIndexProperty().addListener((ob, o, n) -> {
            p2ComboBox.getSelectionModel().clearAndSelect(1-n.intValue());
            pristine.set(false);
        });
        
        p2ComboBox.getSelectionModel().selectedIndexProperty().addListener((ob, o, n) -> {
            p1ComboBox.getSelectionModel().clearAndSelect(1-n.intValue());
            pristine.set(false);
        });
    }    
    
    @FXML void onSave(){
        //update des cours
        model.getCours().forEach(c -> updateWeek(c));
        config.setProperty(Constants.CONF_WEEK_DEFAULT, standardNameField.getText());
        config.setProperty(Constants.CONF_WEEK_P1_NAME, p1NameField.getText());
        config.setProperty(Constants.CONF_WEEK_P2_NAME, p2NameField.getText());
        config.setProperty(Constants.CONF_WEEK_P1_VAL, ""+p1ComboBox.getSelectionModel().getSelectedIndex());
        config.setProperty(Constants.CONF_WEEK_P2_VAL, ""+p2ComboBox.getSelectionModel().getSelectedIndex());
        config.save();
        pristine.set(true);
    }
    
    private void updateWeek(Cours c){
        if(c.getWeek().equals(config.getProperty(Constants.CONF_WEEK_P1_NAME))){
            c.setWeek(p1NameField.getText());
        } else if(c.getWeek().equals(config.getProperty(Constants.CONF_WEEK_P2_NAME))){
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
