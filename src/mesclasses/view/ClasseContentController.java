/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mesclasses.view;

import java.io.IOException;
import java.net.URL;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import mesclasses.MainApp;
import mesclasses.controller.TabContentController;
import mesclasses.handlers.EventBusHandler;
import mesclasses.model.Classe;
import mesclasses.model.Constants;
import mesclasses.model.Eleve;
import mesclasses.model.EleveData;
import mesclasses.model.Trimestre;
import mesclasses.objects.IntegerOnlyConverter;
import mesclasses.objects.TunedDayCellFactory;
import mesclasses.objects.events.CreateCoursEvent;
import mesclasses.objects.events.MessageEvent;
import mesclasses.objects.events.OpenMenuEvent;
import mesclasses.objects.events.SelectClasseEvent;
import mesclasses.objects.events.SelectDateEvent;
import mesclasses.util.Btns;
import mesclasses.util.CssUtil;
import mesclasses.util.ModalUtil;
import mesclasses.util.NodeUtil;
import org.apache.commons.lang3.StringUtils;
import org.smartgrid.SmartGrid;

/**
 * FXML Controller class
 *
 * @author rrrt3491
 */
public class ClasseContentController extends TabContentController implements Initializable {

    // FXML elements
    
    @FXML SmartGrid grid; 
    
    @FXML Label trimestreName; 
    
    @FXML DatePicker currentDate; 
    
    @FXML Label currentCoursLabel;
    
    @FXML Button previousCoursBtn;
    
    @FXML Button nextCoursBtn;
    
    @FXML Button addCours;
    
    @FXML Button previousDayBtn;
    
    @FXML Button nextDayBtn;
    
    @FXML Button switchNonActifsBtn;
    
    // fields
    private Classe classe;
    private Trimestre trimestre;
    private IntegerProperty currentCours;
    private IntegerProperty nbCours;
    private static final StringProperty COURS_PREFIX = new SimpleStringProperty("Cours ");
    
    private Map<Eleve, IntegerProperty> cumulTravailMap;
    private Map<Eleve, IntegerProperty> cumulRetardMap;
    private Map<Eleve, IntegerProperty> cumulOubliMap;
    // bindings
    BooleanBinding isFirstCours;
    BooleanBinding isLastCours;  
    BooleanProperty displayNonActifs;    
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        name = "Classe Content Ctrl";
        super.initialize(url ,rb);
        
        cumulTravailMap = new HashMap<>();
        cumulRetardMap = new HashMap<>();
        cumulOubliMap = new HashMap<>();
        currentDate.setDayCellFactory(new TunedDayCellFactory().setAllowSundays(false));
        currentDate.setConverter(NodeUtil.DATE_WITH_DAY_NAME);
        currentDate.valueProperty().addListener((observable, oldValue, newValue) -> {
            EventBusHandler.post(new SelectDateEvent(newValue));
            computeNbOfCoursForTheDay(newValue, classe);
            refreshGrid();
        });
        displayNonActifs = new SimpleBooleanProperty(false);
        switchNonActifsBtn.setText("Afficher inactifs");
        displayNonActifs.addListener((observable, oldValue, newValue) -> {
            switchNonActifsBtn.setText(displayNonActifs.get()? "Masquer inactifs" : "Afficher inactifs");
            refreshGrid();
        });
        currentCours = new SimpleIntegerProperty();
        nbCours = new SimpleIntegerProperty();
        
        isFirstCours = Bindings.or(
            currentCours.isEqualTo(0),
            currentCours.isEqualTo(1)
        );
        isLastCours = Bindings.or(
            currentCours.isEqualTo(0),
            currentCours.isEqualTo(nbCours)
        );  
        currentCoursLabel.textProperty().bind(COURS_PREFIX.concat(currentCours.asString()));
        previousCoursBtn.disableProperty().bind(isFirstCours);
        nextCoursBtn.disableProperty().bind(isLastCours);
        Btns.makeLeft(previousCoursBtn);
        Btns.makeLeft(previousDayBtn);
        Btns.makeRight(nextCoursBtn);
        Btns.makeRight(nextDayBtn);
    }    
    
    public Classe getClasse() {
        return classe;
    }

    @Override
    public void setClasse(Classe classe) {
        this.classe = classe;
        name +=" for classe "+classe;
    }
    
    @Override
    public void setCurrentDate(LocalDate date){
        log("Setting the content date at "+date+". classe is "+classe);
        this.currentDate.setValue(date);
    }
    
    private void refreshGrid(){
        if(classe.getEleves().isEmpty()){
            grid.drawNoEleveAlert(classe.getName(), (event) ->{
                EventBusHandler.post(new OpenMenuEvent(Constants.ADMIN_ELEVE_VIEW));
                }
            );
            addCours.setDisable(true);
            return;
        }
        trimestre = modelHandler.getForDate(currentDate.getValue());
        if(trimestre == null){
            grid.drawNoTrimestreAlert(currentDate.getValue(), (event) -> {
                    EventBusHandler.post(new OpenMenuEvent(Constants.ADMIN_TRIMESTRE_VIEW));
                }
            );
            addCours.setDisable(true);
            return;
        }
        addCours.setDisable(false);
        trimestreName.setText(trimestre.getName()+" - "+classe.getName());
        computeAllCumuls(trimestre);
        
        if(currentCours.get() != 0){
            drawGrid();
        } else {
            String day = Constants.DAYMAP.get(currentDate.getValue().getDayOfWeek());
            grid.drawNoCoursAlert(day, classe.getName(), 
                    (event) -> {
                        EventBusHandler.post(new OpenMenuEvent(Constants.EMPLOI_DU_TEMPS_VIEW));
                        EventBusHandler.post(new CreateCoursEvent(day, classe));
                    }
            );
        }
    }
    
    @FXML
    public void previousDay(){
        if(currentDate.getValue().getDayOfWeek() == DayOfWeek.MONDAY){
            currentDate.setValue(currentDate.getValue().minusDays(2));
        } else {
            currentDate.setValue(currentDate.getValue().minusDays(1));
        }
    }
    
    @FXML
    public void nextDay(){
        if(currentDate.getValue().getDayOfWeek() == DayOfWeek.SATURDAY){
            currentDate.setValue(currentDate.getValue().plusDays(2));
        } else {
            currentDate.setValue(currentDate.getValue().plusDays(1));
        }
    }
    
    private void computeNbOfCoursForTheDay(LocalDate date, Classe classe){
        if(date == null){
            nbCours.set(0);
            currentCours.set(0);
            return;
        }
        nbCours.set(modelHandler.getNbCoursForDay(date, classe));
        if(nbCours.get() == 0){
            currentCours.set(0);
        } else {
            currentCours.set(1);
        }
    }
    
    @FXML
    public void previousCours(){
        currentCours.setValue(currentCours.getValue()-1);
        refreshGrid();
    }
    
    @FXML
    public void nextCours(){
        currentCours.set(currentCours.get()+1);
        refreshGrid();
    }
    
    @FXML
    public void ajouterCours(){
       nbCours.set(nbCours.get()+1);
       currentCours.set(nbCours.get());
       drawGrid();
    }
    
    /* BOTTOM BUTTONS */
    @FXML
    public void switchNonActifs(){
        displayNonActifs.set(!displayNonActifs.get());
        
    }
    
    @FXML
    public void openRapport(){
        EventBusHandler.post(new OpenMenuEvent(Constants.CLASSE_RAPPORT_TABS_VIEW));
        EventBusHandler.post(new SelectClasseEvent(classe));
    }
    
    @FXML
    public void openPunitions(){
        EventBusHandler.post(new OpenMenuEvent(Constants.CLASSE_PUNITIONS_VIEW));
        log(classe.toString());
        EventBusHandler.post(new SelectClasseEvent(classe));
        
    }
    @FXML
    public void openPostIt(){
        try {
            // Load the fxml file and create a new stage for the popup dialog.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource(Constants.POSTIT_DIALOG));
            AnchorPane page = (AnchorPane) loader.load();

            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Post-It");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            // Set the person into the controller.
            PostItDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setClasse(classe);

            // Show the dialog and wait until the user closes it
            dialogStage.showAndWait();

        } catch (IOException e) {
            log(e);
        }
    }
    @FXML
    public void openMotDialog(){
        try {
            // Load the fxml file and create a new stage for the popup dialog.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource(Constants.CLASSE_MOTS_DIALOG));
            AnchorPane page = (AnchorPane) loader.load();

            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Cumul des mots non signés");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            // Set the person into the controller.
            CumulMotDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setClasse(classe);

            // Show the dialog and wait until the user closes it
            dialogStage.showAndWait();

        } catch (IOException e) {
            log(e);
        }
    }
    
    public void openPunitionDialog(Eleve eleve){
        String texte = ModalUtil.prompt("Punition pour "+eleve.getFirstName()+" "+eleve.getLastName(), "");
        if(!StringUtils.isEmpty(texte)){
            modelHandler.createPunition(eleve, currentDate.getValue(), currentCours.get(), texte);
            EventBusHandler.post(MessageEvent.success("Punition créée"));
        }
    }
    
    /* GRID */
    private void drawGrid(){
        grid.clear();
        List<Eleve> elevesToDisplay = classe.getEleves();
        if(!displayNonActifs.get()){
            elevesToDisplay = modelHandler.getOnlyActive(elevesToDisplay);
        }
        for(int i = 0; i < elevesToDisplay.size(); i++){
            drawRow(elevesToDisplay.get(i), i+1);
        }
    }
    
    private void drawRow(Eleve eleve, int rowIndex){
        
        EleveData eleveData = modelHandler.getDataForCoursAndDate(eleve, currentCours.get(), currentDate.getValue());
        
        //index debute à 1, le slot 0,X est réservé pour un separator dans le header
        grid.add(NodeUtil.buildEleveLink(eleve, eleve.lastNameProperty(), Constants.CLASSE_CONTENT_TABS_VIEW), 1, rowIndex, HPos.LEFT);
        
        grid.add(NodeUtil.buildEleveLink(eleve, eleve.firstNameProperty(), Constants.CLASSE_CONTENT_TABS_VIEW), 2, rowIndex, HPos.LEFT);
        
        if(!eleve.isInClasse(currentDate.getValue())){
            grid.add(new Label("ne faisait pas partie de la classe à cette date"), 3, rowIndex, grid.getGridWidth()-3, 1);
            return;
        }
        
        CheckBox box = new CheckBox();
        Bindings.bindBidirectional(box.selectedProperty(), eleveData.absentProperty());
        grid.add(box, 3, rowIndex, null);
        
        TextField retardField = new TextField();
        Bindings.bindBidirectional(retardField.textProperty(), eleveData.retardProperty(), new IntegerOnlyConverter());
        markAsInteger(retardField);
        
        
        grid.add(retardField, 4, rowIndex, HPos.LEFT);
        
        Label cumulRetard = new Label();
        retardField.textProperty().addListener((observable, oldValue, newValue) -> {
            computeCumulRetard(eleve, trimestre);
            markInRed(cumulRetard, cumulRetardMap.get(eleve).get(), 3);
        });
        
        cumulRetard.textProperty().bind(cumulRetardMap.get(eleve).asString());
        markInRed(cumulRetard, cumulRetardMap.get(eleve).get(), 3);
        grid.add(cumulRetard, 5, rowIndex, null);
        
        CheckBox travailBox = new CheckBox();
        Bindings.bindBidirectional(travailBox.selectedProperty(), eleveData.travailPasFaitProperty());
        grid.add(travailBox, 6, rowIndex, null);
        Label cumulTravail = new Label();
        travailBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            computeCumulTravail(eleve, trimestre);
            markInRed(cumulTravail, cumulTravailMap.get(eleve).get(), 3);
        });
        
        cumulTravail.textProperty().bind(cumulTravailMap.get(eleve).asString());
        markInRed(cumulTravail, cumulTravailMap.get(eleve).get(), 3);
        grid.add(cumulTravail, 7, rowIndex, null);
        
        CheckBox box3 = new CheckBox();
        Bindings.bindBidirectional(box3.selectedProperty(), eleveData.devoirProperty());
        grid.add(box3, 8, rowIndex, null);
        
        Button punition = Btns.punitionBtn();
        punition.setOnAction((event) -> {
            openPunitionDialog(eleve);
        });
        grid.add(punition, 9, rowIndex, null);
        
        CheckBox box4 = new CheckBox();
        Bindings.bindBidirectional(box4.selectedProperty(), eleveData.motCarnetProperty());
        grid.add(box4, 10, rowIndex, null);
        
        CheckBox box5 = new CheckBox();
        Bindings.bindBidirectional(box5.selectedProperty(), eleveData.motSigneProperty());
        grid.add(box5, 11, rowIndex, null);
        
        TextField oubliMaterielField = new TextField();
        Bindings.bindBidirectional(oubliMaterielField.textProperty(), eleveData.oubliMaterielProperty());
        grid.add(oubliMaterielField, 12, rowIndex, HPos.LEFT);
        Label cumulOubli = new Label();
        oubliMaterielField.textProperty().addListener((observable, oldValue, newValue) -> {
            computeCumulOubli(eleve, trimestre);
            markInRed(cumulOubli, cumulOubliMap.get(eleve).get(), 3);
        });
        
        cumulOubli.textProperty().bind(cumulOubliMap.get(eleve).asString());
        markInRed(cumulOubli, cumulOubliMap.get(eleve).get(), 3);
        grid.add(cumulOubli, 13, rowIndex, null);
        
        CheckBox box6 = new CheckBox();
        Bindings.bindBidirectional(box6.selectedProperty(), eleveData.exclusProperty());
        grid.add(box6, 14, rowIndex, null);
        
        TextField remarquesField = new TextField();
        Bindings.bindBidirectional(remarquesField.textProperty(), eleveData.RemarquesProperty());
        grid.add(remarquesField, 15, rowIndex, HPos.LEFT);
    }
         
    /* CALCUL CUMULS */
    private void computeAllCumuls(Trimestre trimestre){
        classe.getEleves().stream().forEach((eleve) -> {
            computeCumulTravail(eleve, trimestre);
            computeCumulRetard(eleve, trimestre);
            computeCumulOubli(eleve, trimestre);
        });
    }
    
    private void markInRed(Label label, int value, int threshold){
    
        if(value >= threshold){
                CssUtil.addClass(label, "text-red");
            } else {
                CssUtil.removeClass(label, "text-red");
            }
    }
    
    private void computeCumulTravail(Eleve eleve, Trimestre trimestre){
        List<EleveData> data = modelHandler.filterByTrimestre(eleve.getData(), trimestre, currentDate.getValue());
        if(!cumulTravailMap.containsKey(eleve)){
            cumulTravailMap.put(eleve, new SimpleIntegerProperty());
        }
        cumulTravailMap.get(eleve).set(modelHandler.countCumulTravail(data));
    }
    
    private void computeCumulRetard(Eleve eleve, Trimestre trimestre){
        List<EleveData> data = modelHandler.filterByTrimestre(eleve.getData(), trimestre, currentDate.getValue());
        if(!cumulRetardMap.containsKey(eleve)){
            cumulRetardMap.put(eleve, new SimpleIntegerProperty());
        }
        cumulRetardMap.get(eleve).set(modelHandler.countCumulRetard(data));
    }
    
    private void computeCumulOubli(Eleve eleve, Trimestre trimestre){
        List<EleveData> data = modelHandler.filterByTrimestre(eleve.getData(), trimestre, currentDate.getValue());
        if(!cumulOubliMap.containsKey(eleve)){
            cumulOubliMap.put(eleve, new SimpleIntegerProperty());
        }
        cumulOubliMap.get(eleve).set(modelHandler.countCumulOubli(data));
    }
    
    
}
