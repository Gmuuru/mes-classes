/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mesclasses.view;

import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import mesclasses.controller.PageController;
import mesclasses.handlers.EventBusHandler;
import mesclasses.model.Constants;
import mesclasses.model.Trimestre;
import mesclasses.objects.TunedDayCellFactory;
import mesclasses.objects.events.OpenMenuEvent;
import mesclasses.util.AppLogger;
import mesclasses.util.Btns;
import mesclasses.util.ModalUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.smartgrid.SmartGrid;

/**
 * FXML Controller class
 *
 * @author rrrt3491
 */
public class AdminTrimestreController extends PageController implements Initializable {
    
    private static final Logger LOG = LogManager.getLogger(AdminTrimestreController.class);
    
    @FXML SmartGrid grid;
    
    /**
     * Initializes the controller class.
     */
    @Override
    @FXML
    public void initialize(URL url, ResourceBundle rb) {
        name = "Admin Trimestre Ctrl";
        super.initialize(url, rb);
        LOG.info("Loading AdminTrimestreController with "+ trimestres.size() +" trimestres");
        
        init();
    }  
    
    public void init(){
        try {
            resetErrors();

            int count = 1;
            grid.clear();
            for(Trimestre trimestre : trimestres){
                addTrimestreToGrid(trimestre, count);
                count++;
            }
        } catch (Exception e){
            notif(e);
        }
    }
    
    @FXML
    public void handleNewTrimestre(){
        try {
            Trimestre newTrimestre = this.modelHandler.createTrimestre();
            addTrimestreToGrid(newTrimestre, trimestres.size());
        } catch (Exception e) {
            notif(e);
        }
    }
    
    private void addTrimestreToGrid(Trimestre trimestre, int rowIndex){
        try {
            
            TextField nameField = new TextField();
            Bindings.bindBidirectional(nameField.textProperty(), trimestre.nameProperty());
            nameField.setMaxWidth(200);
            markAsMandatory(nameField);
            checkMandatory(nameField);
            markAsUnique(nameField);
            checkUnique(nameField);
            grid.add(nameField, 1, rowIndex, HPos.LEFT);
            
            DatePicker datePickerStart = new DatePicker();
            Bindings.bindBidirectional(datePickerStart.valueProperty(), trimestre.startProperty());
            markAsMandatory(datePickerStart);
            checkMandatory(datePickerStart);
            grid.add(datePickerStart, 2, rowIndex, null);
            
            DatePicker datePickerEnd = new DatePicker();
            Bindings.bindBidirectional(datePickerEnd.valueProperty(), trimestre.endProperty());
            markAsMandatory(datePickerEnd);
            checkMandatory(datePickerEnd);
            grid.add(datePickerEnd, 3, rowIndex, null);
            
            // restricteur pour que end soit égal à ou après start
            datePickerEnd.setDayCellFactory(new TunedDayCellFactory().setPreviousPicker(datePickerStart));
            
            Button close = Btns.deleteBtn();
            close.setOnAction((ActionEvent e) -> {
                if (ModalUtil.confirm("Suppression du trimestre", "Etes vous sûr ?")) {
                    grid.deleteRow(SmartGrid.row(close));
                    removeErrors(nameField, datePickerStart, datePickerEnd);
                    this.modelHandler.delete(trimestre);
                }
            });
            grid.add(close, 4, rowIndex, null);
        } catch (Exception e) {
            notif(e);
        }
    }

    @FXML public void openClasses(){
        EventBusHandler.post(new OpenMenuEvent(Constants.ADMIN_CLASSE_VIEW));
    }
    
    @Override
    public boolean notifyExit() {
        try {
            if (!super.notifyExit()) {
                return false;
            }
            LOG.info("Unload in AdminTrimestreController");
            this.modelHandler.cleanupTrimestres();
            return true;
        } catch (Exception e) {
            AppLogger.notif(name, e);
            return false;
        }
    }
    
    @Override
    public void reload(){
        init();
    }
    
    @Override
    protected boolean isUnique(String trimestreName){
        List<String> names = trimestres.stream().map( t -> t.getName() ).collect(Collectors.toList());
        return Collections.frequency(names, trimestreName) == 1;
    }
}
