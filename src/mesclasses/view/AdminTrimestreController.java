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
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.util.Pair;
import mesclasses.controller.PageController;
import mesclasses.handlers.EventBusHandler;
import mesclasses.model.Constants;
import mesclasses.model.Trimestre;
import mesclasses.objects.TunedDayCellFactory;
import mesclasses.objects.events.OpenMenuEvent;
import mesclasses.util.AppLogger;
import mesclasses.util.Btns;
import mesclasses.util.ModalUtil;
import mesclasses.util.NodeUtil;
import mesclasses.util.validation.ListValidators;
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
    
    @FXML SmartGrid trimestreGrid;
    
    /**
     * Initializes the controller class.
     */
    @Override
    @FXML
    public void initialize(URL url, ResourceBundle rb) {
        name = "Admin Trimestre Ctrl";
        super.initialize(url, rb);
        LOG.info("Loading AdminTrimestreController with "+ trimestres.size() +" trimestres");
        
        errorMessagesLabel.setOnAction((e) -> {
            openErrorDialog();
        });
        init();
    }  
    
    public void init(){
        try {
            resetErrors();

            int count = 1;
            trimestreGrid.clear();
            for(Trimestre trimestre : trimestres){
                addTrimestreToGrid(trimestre, count);
                count++;
            }
            validateDates();
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
            trimestreGrid.add(nameField, 1, rowIndex, HPos.LEFT);
            
            DatePicker datePickerStart = new DatePicker();
            Bindings.bindBidirectional(datePickerStart.valueProperty(), trimestre.startProperty());
            markAsMandatory(datePickerStart);
            checkMandatory(datePickerStart);
            trimestreGrid.add(datePickerStart, 2, rowIndex, null);
            
            DatePicker datePickerEnd = new DatePicker();
            Bindings.bindBidirectional(datePickerEnd.valueProperty(), trimestre.endProperty());
            markAsMandatory(datePickerEnd);
            checkMandatory(datePickerEnd);
            trimestreGrid.add(datePickerEnd, 3, rowIndex, null);
            
            addDateValidator(trimestre, datePickerStart, datePickerEnd);
            // restricteur pour que end soit égal à ou après start
            datePickerEnd.setDayCellFactory(new TunedDayCellFactory().setPreviousPicker(datePickerStart));
            
            Button close = Btns.deleteBtn();
            close.setOnAction((ActionEvent e) -> {
                if (ModalUtil.confirm("Suppression du trimestre", "Etes vous sûr ?")) {
                    trimestreGrid.deleteRow(SmartGrid.row(close));
                    removeErrors(nameField, datePickerStart, datePickerEnd);
                    this.modelHandler.delete(trimestre);
                }
            });
            trimestreGrid.add(close, 4, rowIndex, null);
        } catch (Exception e) {
            notif(e);
        }
    }

    @FXML public void openClasses(){
        EventBusHandler.post(new OpenMenuEvent(Constants.ADMIN_CLASSE_VIEW));
    }
    
    @Override
    public boolean notifyExit() {
        ListValidators.validateTrimestreList(trimestres);
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
    

    private void addDateValidator(Trimestre t, DatePicker datePickerStart, DatePicker datePickerEnd) {
        ChangeListener cl = (ChangeListener) (ObservableValue observable, Object oldValue, Object newValue) -> {
            LOG.info("change !");
            validateDates();
        };
        datePickerStart.valueProperty().addListener(cl);
        datePickerEnd.valueProperty().addListener(cl);
    }
    
    private void validateDates(){
        List<List<Node>> rows = trimestreGrid.getDataRows();
        for(int i = 0;i< rows.size(); i++){
            if(datesAreInvalid(modelHandler.getTrimestres().get(i))){
                LOG.info(modelHandler.getTrimestres().get(i).toString()+" : dates invalides");
                addValidityError((DatePicker)rows.get(i).get(1));
                addValidityError((DatePicker)rows.get(i).get(2));
            } else {
                LOG.info(modelHandler.getTrimestres().get(i).toString()+" : dates ok");
                removeValidityError((DatePicker)rows.get(i).get(1));
                removeValidityError((DatePicker)rows.get(i).get(2));
            }
        }
    }
    
    private boolean datesAreInvalid(Trimestre t){
    
        if(t == null || t.getStartAsDate() == null || t.getEndAsDate() == null){
            return false;
        }
        if(t.getStartAsDate().isAfter(t.getEndAsDate())){
            return true;
        }
        Pair p = trimestresOverlap(trimestres);
        return p != null && (p.getKey() == t || p.getValue() == t);
    }
    
    private static Pair<Trimestre, Trimestre> trimestresOverlap(List<Trimestre> list){
    
        for (int i = 0; i < list.size(); i++) {
            for (int k = i + 1; k < list.size(); k++) {
                if (NodeUtil.datesOverlap(list.get(i), list.get(k))) {
                    return new Pair(list.get(i), list.get(k));
                }
            }
        }
        return null;
    }
    
    @Override
    protected boolean isUnique(String trimestreName){
        List<String> names = trimestres.stream().map( t -> t.getName() ).collect(Collectors.toList());
        return Collections.frequency(names, trimestreName) == 1;
    }
    
    private void openErrorDialog() {
        ModalUtil.showErrors(ListValidators.validateTrimestreList(trimestres));
    }
}
