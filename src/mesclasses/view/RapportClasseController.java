/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mesclasses.view;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import mesclasses.controller.TabContentController;
import mesclasses.handlers.EventBusHandler;
import mesclasses.model.Classe;
import mesclasses.model.Constants;
import mesclasses.model.Eleve;
import mesclasses.model.EleveData;
import mesclasses.objects.events.OpenMenuEvent;
import mesclasses.objects.events.SelectClasseEvent;
import mesclasses.util.Btns;
import mesclasses.util.NodeUtil;
import org.apache.commons.lang3.StringUtils;
import org.smartgrid.SmartGrid;

/**
 *
 * @author rrrt3491
 */
public class RapportClasseController extends TabContentController implements Initializable {
    
    // FXML elements
    
    @FXML SmartGrid grid; 
    
    @FXML Label trimestreLabel;
    
    @FXML Label rapportLabel;
    
    @FXML Button previousBtn;
    
    @FXML Button nextBtn;
    
    @FXML Button ouvrirClasseBtn;
    
    private Classe classe;
    private IntegerProperty trimestreIndex;
    private StringProperty selectedTrimestre;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        name = "Rapport Classe Ctrl";
        super.initialize(url ,rb);
        
        trimestreIndex = new SimpleIntegerProperty();
        selectedTrimestre = new SimpleStringProperty();
        
        trimestreLabel.textProperty().bind(selectedTrimestre);
        
        previousBtn.disableProperty().bind(
                Bindings.or(
                        Bindings.size(trimestres).lessThan(2),
                        trimestreIndex.isEqualTo(0)
                )
        );
        Btns.makeLeft(previousBtn);
        nextBtn.disableProperty().bind(
                Bindings.or(
                        Bindings.size(trimestres).lessThan(2),
                        trimestreIndex.isEqualTo(trimestres.size()-1)
                )
        );
        Btns.makeRight(nextBtn);
        
    }
    
    
    public Classe getClasse() {
        return classe;
    }

    
    public void setClasse(Classe classe) {
        this.classe = classe;
        init();
    }
    
    public void init(){
        name +=" for classe "+classe;
        rapportLabel.setText("Rapport pour la "+classe);
        if(trimestres != null && !trimestres.isEmpty()){
            selectTrimestre(0);
        }
    }
    private void selectTrimestre(int index){
        trimestreIndex.set(index);
        selectedTrimestre.set(trimestres.get(index).getName());
        refreshGrid();
    }
    
    @FXML
    public void previous(){
        selectTrimestre(trimestreIndex.get() - 1);
    }
    
    @FXML
    public void next(){
        selectTrimestre(trimestreIndex.get() + 1);
    }
    
    private void refreshGrid(){
        grid.clear();
        if(trimestres == null || trimestres.isEmpty()){
            grid.drawNoTrimestreAlert(null, (event) -> {
                    EventBusHandler.post(new OpenMenuEvent(Constants.ADMIN_TRIMESTRE_VIEW));
                }
            );
            return;
        }
        if(classe.getEleves() == null || classe.getEleves().isEmpty()){
            grid.drawNoEleveAlert(classe.getName(), (event) -> {
                    EventBusHandler.post(new OpenMenuEvent(Constants.ADMIN_ELEVE_VIEW));
                    EventBusHandler.post(new SelectClasseEvent(classe));
                }
            );
            return;
        }
        List<Eleve> elevesToDisplay = modelHandler.getOnlyActive(classe.getEleves());
        for(int i = 0; i < elevesToDisplay.size(); i++){
            drawRow(elevesToDisplay.get(i), i+1);
        }
    }
    
    private void drawRow(Eleve eleve, int rowIndex){
        List<EleveData> data = modelHandler.filterByTrimestre(eleve.getData(), trimestres.get(trimestreIndex.get()), null);
        grid.add(NodeUtil.buildEleveLink(eleve, eleve.lastNameProperty(), Constants.CLASSE_RAPPORT_TABS_VIEW), 1, rowIndex, HPos.LEFT);
        
        grid.add(NodeUtil.buildEleveLink(eleve, eleve.firstNameProperty(), Constants.CLASSE_RAPPORT_TABS_VIEW), 2, rowIndex, HPos.LEFT);
        // ABSENT
        long abs = data.stream().filter(d -> d.isAbsent()).count();
        Label absent = new Label(String.valueOf(abs));
        grid.add(absent, 3, rowIndex, null);
        // RETARD
        long ret = data.stream().filter(d -> d.getRetard() > 0).count();
        Label retard = new Label(String.valueOf(ret));
        grid.add(retard, 4, rowIndex, null);
        // TRAVAIL
        long tr = data.stream().filter(d -> d.isTravailPasFait()).count();
        Label travail = new Label(String.valueOf(tr));
        grid.add(travail, 5, rowIndex, null);
        // DEVOIR
        long dv = data.stream().filter(d -> d.isDevoir()).count();
        Label devoir = new Label(String.valueOf(dv));
        grid.add(devoir, 6, rowIndex, null);
        // PUNITION
        Label punition = new Label(""+eleve.getPunitions().size());
        grid.add(punition, 7, rowIndex, null);
        // MOT CARNET
        long mc = data.stream().filter(d -> d.isMotCarnet()).count();
        Label mot = new Label(String.valueOf(mc));
        grid.add(mot, 8, rowIndex, null);
        // OUBLI MATERIEL
        long om = data.stream().filter(d -> StringUtils.isNotBlank(d.getOubliMateriel())).count();
        Label oubli = new Label(String.valueOf(om));
        grid.add(oubli, 9, rowIndex, null);
        // EXCLUSION
        long ex = data.stream().filter(d -> d.isExclus()).count();
        Label exclus = new Label(String.valueOf(ex));
        grid.add(exclus, 10, rowIndex, null);
    }
    
    @FXML public void openJournee(){
        EventBusHandler.post(new OpenMenuEvent(Constants.JOURNEE_VIEW));
    }
    
    @Override
    public void reload() {
        super.reload(); //To change body of generated methods, choose Tools | Templates.
        refreshGrid();
    }
}
