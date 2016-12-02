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
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import mesclasses.MainApp;
import mesclasses.controller.PageController;
import mesclasses.handlers.EventBusHandler;
import mesclasses.model.Constants;
import mesclasses.model.Cours;
import mesclasses.model.Eleve;
import mesclasses.model.EleveData;
import mesclasses.model.Journee;
import mesclasses.model.Seance;
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
public class JourneeController extends PageController implements Initializable {

    // FXML elements
    
    @FXML TabPane tabPane;
    @FXML Tab vieScolaireTab;
    @FXML Tab travailTab;
    @FXML Tab sanctionsTab;
    
    @FXML SmartGrid vieScolaireGrid; 
    @FXML SmartGrid travailGrid; 
    @FXML SmartGrid sanctionsGrid; 
    
    @FXML Label trimestreName; 
    
    @FXML DatePicker currentDate; 
    
    @FXML Label currentSeanceLabel;
    
    @FXML Button previousSeanceBtn;
    
    @FXML Button nextSeanceBtn;
    
    @FXML Button remCours;
    @FXML Button addCours;
    
    @FXML Button previousDayBtn;
    
    @FXML Button nextDayBtn;
    
    @FXML Button switchNonActifsBtn;
    
    // fields
    private Trimestre trimestre;
    
    private Journee journee;
    
    private Seance currentSeance;
    
    private ScrollPane selectedScroll;
    private SmartGrid selectedGrid;
    
    // bindings
    BooleanBinding isFirstCours;
    BooleanBinding isLastCours;  
    BooleanProperty displayNonActifs;    
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        name = "Journee Ctrl";
        super.initialize(url ,rb);
        initTabs();
        Btns.makeLeft(previousSeanceBtn);
        Btns.makeLeft(previousDayBtn);
        Btns.makeRight(nextSeanceBtn);
        Btns.makeRight(nextDayBtn);
        Btns.makeAdd(addCours);
        Btns.makeDelete(remCours);
        
        currentDate.setDayCellFactory(new TunedDayCellFactory().setAllowSundays(false));
        currentDate.setConverter(NodeUtil.DATE_WITH_DAY_NAME);
        currentDate.valueProperty().addListener((observable, oldValue, newValue) -> {
            EventBusHandler.post(new SelectDateEvent(newValue));
            refreshGrid();
        });
        displayNonActifs = new SimpleBooleanProperty(false);
        switchNonActifsBtn.setText("Afficher inactifs");
        displayNonActifs.addListener((observable, oldValue, newValue) -> {
            switchNonActifsBtn.setText(displayNonActifs.get()? "Masquer inactifs" : "Afficher inactifs");
            refreshGrid();
        });
        
        
        setCurrentDate(LocalDate.now());
        
    }    

    private void initTabs(){
        initTab(vieScolaireTab, vieScolaireGrid );
        initTab(travailTab, travailGrid );
        initTab(sanctionsTab, sanctionsGrid );
        tabPane.getSelectionModel().clearAndSelect(0);
        selectTab(vieScolaireTab, vieScolaireGrid);
    }
    private void initTab( Tab tab , SmartGrid grid ){
        tab.getContent().setManaged(false);
        tab.getContent().setVisible(false);
       
        tab.selectedProperty().addListener((ob, o, n) -> {
            if(n){
                selectTab(tab, grid);
            } else {
                unselectTab(tab);
            }
        });
    }
    
    private void selectTab(Tab tab, SmartGrid grid){
        tab.getContent().setManaged(true);
        tab.getContent().setVisible(true);
        double vvalue = 0;
        if(selectedScroll != null){
            vvalue = selectedScroll.getVvalue();
        }
        selectedGrid = grid;
        selectedScroll = getScrollPane(tab);
        selectedScroll.setVvalue(vvalue);
    }
    
    private ScrollPane getScrollPane(Tab tab){
        for(Node child : ((VBox)tab.getContent()).getChildren()){
            if(child instanceof ScrollPane){
                return (ScrollPane)child;
            }
        }
        return null;
    }
    
    private void unselectTab(Tab tab){
        tab.getContent().setManaged(false);
        tab.getContent().setVisible(false);
    }
    
    public void setCurrentDate(LocalDate date){
        journee = modelHandler.getJournee(date);
        if(journee.getSeances().isEmpty()){
            setCurrentSeance(null);
        } else {
            setCurrentSeance(journee.getSeances().get(0));
        }
        log("Setting the content date at "+date+". current seance is "+currentSeance);
        this.currentDate.setValue(date);
    }
    
    public void setCurrentSeance(Seance seance){
        currentSeance = seance;
        if(seance == null){
            return;
        }
        if(currentSeance.isFirst()){
            previousSeanceBtn.setDisable(true);
        } else {
            previousSeanceBtn.setDisable(false);
        }
        if(currentSeance.isLast()){
            nextSeanceBtn.setDisable(true);
        } else {
            nextSeanceBtn.setDisable(false);
        }
        currentSeanceLabel.setText(currentSeance.toString());
        
        // activation du bouton remCours si besoin
        remCours.disableProperty().bind(Bindings.not(currentSeance.getCours().ponctuelProperty()));
        // reinitialisation du scroll vertical
        getScrollPane(tabPane.getSelectionModel().getSelectedItem()).setVvalue(0.0);
    }
    
    private void refreshGrid(){
        if(currentSeance != null){
            if(currentSeance.getClasse().getEleves().isEmpty()){
                selectedGrid.drawNoEleveAlert(currentSeance.getClasse().getName(), (event) ->{
                    EventBusHandler.post(new OpenMenuEvent(Constants.ADMIN_ELEVE_VIEW));
                    }
                );
                addCours.setDisable(true);
                return;
            }
            trimestre = modelHandler.getForDate(currentDate.getValue());
            if(trimestre == null){
                selectedGrid.drawNoTrimestreAlert(currentDate.getValue(), (event) -> {
                        EventBusHandler.post(new OpenMenuEvent(Constants.ADMIN_TRIMESTRE_VIEW));
                    }
                );
                addCours.setDisable(true);
                return;
            }
            addCours.setDisable(false);
            trimestreName.setText(trimestre.getName());
        
            drawGrid();
        } else {
            String day = Constants.DAYMAP.get(currentDate.getValue().getDayOfWeek());
            selectedGrid.drawNoSeanceAlert(day,
                    (event) -> {
                        EventBusHandler.post(new OpenMenuEvent(Constants.EMPLOI_DU_TEMPS_VIEW));
                        EventBusHandler.post(new CreateCoursEvent(day, null));
                    }
            );
        }
    }
    
    @FXML
    public void previousDay(){
        if(currentDate.getValue().getDayOfWeek() == DayOfWeek.MONDAY){
            setCurrentDate(currentDate.getValue().minusDays(2));
        } else {
            setCurrentDate(currentDate.getValue().minusDays(1));
        }
    }
    
    @FXML
    public void nextDay(){
        if(currentDate.getValue().getDayOfWeek() == DayOfWeek.SATURDAY){
            setCurrentDate(currentDate.getValue().plusDays(2));
        } else {
            setCurrentDate(currentDate.getValue().plusDays(1));
        }
    }
    
    @FXML
    public void previousSeance(){
        setCurrentSeance(currentSeance.previous());
        refreshGrid();
    }
    
    @FXML
    public void nextSeance(){
        setCurrentSeance(currentSeance.next());
        refreshGrid();
    }
    
    @FXML
    public void ajouterCours() throws IOException {
        
        //creation d'un cours ponctuel
        Cours coursPonctuel = new Cours();
        coursPonctuel.setPonctuel(true);
        coursPonctuel.setDay(NodeUtil.getJour(currentDate.getValue()));
        coursPonctuel.setWeek("ponctuel ("+currentDate.getValue().format(Constants.DATE_FORMATTER_FR)+")");
        coursPonctuel.setStartHour(16);
        coursPonctuel.setStartMin(00);
        coursPonctuel.setEndHour(17);
        coursPonctuel.setEndMin(00);
        
        coursPonctuel = openCoursDialog(coursPonctuel);
        
        if(coursPonctuel == null){
            return;
        }
        Seance newSeance = modelHandler.addSeanceWithCours(journee, coursPonctuel);
        setCurrentSeance(newSeance);
        refreshGrid();
    }
    
    @FXML
    public void supprimerCours() throws IOException {
        StringBuilder confirmMsg = new StringBuilder("Supprimer la séance ponctuelle de ")
        .append(NodeUtil.formatTime(currentSeance.getCours().getStartHour(), currentSeance.getCours().getStartMin()))
        .append(" avec la ")
        .append(currentSeance.getClasse().getName());
        if(ModalUtil.confirm("Suppression de séance", confirmMsg.toString())){
            Seance seanceToDisplay = currentSeance.previous() == null ? currentSeance.next() : currentSeance.previous();
            journee.getCoursPonctuels().remove(currentSeance.getCours());
            journee.getSeances().remove(currentSeance);
            setCurrentSeance(seanceToDisplay);
            refreshGrid();
        }
    }
    private Cours openCoursDialog(Cours cours){
        
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource(Constants.COURS_EDIT_DIALOG));
            AnchorPane page = (AnchorPane) loader.load();

            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Création d'un cours ponctuel");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);
            // Set the person into the controller.
            CoursEditDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setCours(cours, true);

            dialogStage.showAndWait();
            int status = controller.getStatus();
            if(status >= 0){
                //update/cancel
                return controller.getCours();
            }
        } catch(IOException e){
            ModalUtil.alert("Erreur I/O", e.getMessage());
        }
        return null;
    }
    
    /* BOTTOM BUTTONS */
    @FXML
    public void switchNonActifs(){
        displayNonActifs.set(!displayNonActifs.get());
        
    }
    
    @FXML
    public void openRapport(){
        EventBusHandler.post(new OpenMenuEvent(Constants.CLASSE_RAPPORT_TABS_VIEW));
        EventBusHandler.post(new SelectClasseEvent(currentSeance.getClasse()));
    }
    
    @FXML
    public void openPunitions(){
        EventBusHandler.post(new OpenMenuEvent(Constants.CLASSE_PUNITIONS_VIEW));
        EventBusHandler.post(new SelectClasseEvent(currentSeance.getClasse()));
        
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
            controller.setClasse(currentSeance.getClasse());

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
            controller.setClasse(currentSeance.getClasse());

            // Show the dialog and wait until the user closes it
            dialogStage.showAndWait();

        } catch (IOException e) {
            log(e);
        }
    }
    
    public void openPunitionDialog(Eleve eleve){
        String texte = ModalUtil.prompt("Punition pour "+eleve.getFirstName()+" "+eleve.getLastName(), "");
        if(!StringUtils.isEmpty(texte)){
            modelHandler.createPunition(eleve, currentDate.getValue(), currentSeance.getCours(), texte);
            EventBusHandler.post(MessageEvent.success("Punition créée"));
        }
    }
    
    /* GRID */
    private void drawGrid(){
        vieScolaireGrid.clear();
        travailGrid.clear();
        sanctionsGrid.clear();
        List<Eleve> elevesToDisplay = currentSeance.getClasse().getEleves();
        if(!displayNonActifs.get()){
            elevesToDisplay = modelHandler.getOnlyActive(elevesToDisplay);
        }
        for(int i = 0; i < elevesToDisplay.size(); i++){
            drawRow(elevesToDisplay.get(i), i);
        }
    }
    
    private void drawEleveName(SmartGrid grid, Eleve eleve, int rowIndex){
        //index debute à 1, le slot 0,X est réservé pour un separator dans le header
        grid.add(NodeUtil.buildEleveLink(eleve, eleve.lastNameProperty(), Constants.JOURNEE_VIEW), 1, rowIndex, HPos.LEFT);
        
        grid.add(NodeUtil.buildEleveLink(eleve, eleve.firstNameProperty(), Constants.JOURNEE_VIEW), 2, rowIndex, HPos.LEFT);
        RowConstraints rc = new RowConstraints();
        rc.setMinHeight(35.0);
        rc.setPrefHeight(35.0);
        rc.setMaxHeight(35.0);
        grid.getRowConstraints().add(rc);
        if(!eleve.isInClasse(currentDate.getValue())){
            grid.add(new Label("ne faisait pas partie de la classe à cette date"), 3, rowIndex, vieScolaireGrid.getGridWidth()-3, 1);
        }
    }
    
    /**
     * dessine la grid vie scolaire
     * @param eleve
     * @param rowIndex 
     */
    private void drawVieScolaire(Eleve eleve, int rowIndex){
        
        EleveData eleveData = donneesHandler.getOrCreateDonneeForEleve(currentSeance, eleve);
        
        drawEleveName(vieScolaireGrid, eleve, rowIndex);
        if(!eleve.isInClasse(currentDate.getValue())){
            return;
        }
        CheckBox box = new CheckBox();
        Bindings.bindBidirectional(box.selectedProperty(), eleveData.absentProperty());
        vieScolaireGrid.add(box, 3, rowIndex, null);
        
        TextField retardField = new TextField();
        Bindings.bindBidirectional(retardField.textProperty(), eleveData.retardProperty(), new IntegerOnlyConverter());
        markAsInteger(retardField);
        
        
        vieScolaireGrid.add(retardField, 4, rowIndex, HPos.LEFT);
        
        Label cumulRetard = new Label();
        retardField.textProperty().addListener((observable, oldValue, newValue) -> {
            stats.computeSeancesWithRetard(eleve, trimestre);
            markInRed(cumulRetard, stats.getCumulRetard(eleve, trimestre), 3);
        });
        
        cumulRetard.textProperty().bind(Bindings.size(stats.getSeancesWithRetard(eleve, trimestre)).asString());
        markInRed(cumulRetard, stats.getCumulRetard(eleve, trimestre), 3);
        vieScolaireGrid.add(cumulRetard, 5, rowIndex, null);
    }
    
    /**
     * dessine la grid travail
     * @param eleve
     * @param rowIndex 
     */
    private void drawTravail(Eleve eleve, int rowIndex){
        
        EleveData eleveData = currentSeance.getDonneesAsMap().get(eleve);
        
        drawEleveName(travailGrid, eleve, rowIndex);
        if(!eleve.isInClasse(currentDate.getValue())){
            return;
        }
        CheckBox travailBox = new CheckBox();
        Bindings.bindBidirectional(travailBox.selectedProperty(), eleveData.travailPasFaitProperty());
        travailGrid.add(travailBox, 3, rowIndex, null);
        Label cumulTravail = new Label();
        travailBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            //computeCumulTravail(eleve, trimestre);
            //markInRed(cumulTravail, cumulTravailMap.get(eleve).get(), 3);
        });
        
        //cumulTravail.textProperty().bind(cumulTravailMap.get(eleve).asString());
        //markInRed(cumulTravail, cumulTravailMap.get(eleve).get(), 3);
        travailGrid.add(cumulTravail, 4, rowIndex, null);
        
        CheckBox devoir = new CheckBox();
        Bindings.bindBidirectional(devoir.selectedProperty(), eleveData.devoirProperty());
        travailGrid.add(devoir, 5, rowIndex, null);
        
        TextField oubliMaterielField = new TextField();
        Bindings.bindBidirectional(oubliMaterielField.textProperty(), eleveData.oubliMaterielProperty());
        travailGrid.add(oubliMaterielField, 6, rowIndex, HPos.LEFT);
        Label cumulOubli = new Label();
        oubliMaterielField.textProperty().addListener((observable, oldValue, newValue) -> {
            //computeCumulOubli(eleve, trimestre);
            //markInRed(cumulOubli, cumulOubliMap.get(eleve).get(), 3);
        });
        
        //cumulOubli.textProperty().bind(cumulOubliMap.get(eleve).asString());
        //markInRed(cumulOubli, cumulOubliMap.get(eleve).get(), 3);
        travailGrid.add(cumulOubli, 7, rowIndex, null);
    
    }
    
    /**
     * dessine la grid sanctions
     * @param eleve
     * @param rowIndex 
     */
    private void drawSanctions(Eleve eleve, int rowIndex){
        
        EleveData eleveData = currentSeance.getDonneesAsMap().get(eleve);
        
        drawEleveName(sanctionsGrid, eleve, rowIndex);
        if(!eleve.isInClasse(currentDate.getValue())){
            return;
        }
        Button punition = Btns.punitionBtn();
        punition.setOnAction((event) -> {
            openPunitionDialog(eleve);
        });
        sanctionsGrid.add(punition, 3, rowIndex, null);
        
        CheckBox motCarnet = new CheckBox();
        Bindings.bindBidirectional(motCarnet.selectedProperty(), eleveData.motCarnetProperty());
        sanctionsGrid.add(motCarnet, 4, rowIndex, null);
        
        CheckBox motSigne = new CheckBox();
        Bindings.bindBidirectional(motSigne.selectedProperty(), eleveData.motSigneProperty());
        sanctionsGrid.add(motSigne, 5, rowIndex, null);
        
        
        CheckBox exclus = new CheckBox();
        Bindings.bindBidirectional(exclus.selectedProperty(), eleveData.exclusProperty());
        sanctionsGrid.add(exclus, 6, rowIndex, null);
    }
        
    private void drawRow(Eleve eleve, int rowIndex){
        drawVieScolaire(eleve, rowIndex);
        drawTravail(eleve, rowIndex);
        drawSanctions(eleve, rowIndex);
    }
    
    private void markInRed(Label label, int value, int threshold){
    
        if(value >= threshold){
                CssUtil.addClass(label, "text-red");
            } else {
                CssUtil.removeClass(label, "text-red");
            }
    }
    
    
}
