/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mesclasses.view;

import com.google.common.eventbus.Subscribe;
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
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;
import mesclasses.MainApp;
import mesclasses.controller.PageController;
import mesclasses.handlers.EventBusHandler;
import mesclasses.model.Constants;
import mesclasses.model.Cours;
import mesclasses.model.Devoir;
import mesclasses.model.Eleve;
import mesclasses.model.EleveData;
import mesclasses.model.Journee;
import mesclasses.model.Mot;
import mesclasses.model.Seance;
import mesclasses.model.Trimestre;
import mesclasses.objects.IntegerOnlyConverter;
import mesclasses.objects.TunedDayCellFactory;
import mesclasses.objects.events.CreateCoursEvent;
import mesclasses.objects.events.MessageEvent;
import mesclasses.objects.events.OpenMenuEvent;
import mesclasses.objects.events.SelectClasseEvent;
import mesclasses.objects.events.SelectDateEvent;
import mesclasses.objects.events.SelectSeanceEvent;
import mesclasses.util.Btns;
import mesclasses.util.CssUtil;
import mesclasses.util.LogUtil;
import mesclasses.util.ModalUtil;
import mesclasses.util.NodeUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.smartgrid.SmartGrid;
import org.smartselect.SmartSelect;

/**
 * FXML Controller class
 *
 * @author rrrt3491
 */
public class JourneeController extends PageController implements Initializable {

    private static final Logger LOG = LogManager.getLogger(JourneeController.class);
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
    
    @FXML private SmartSelect<Seance> seanceSelect;
    @FXML Button remCoursBtn;
    @FXML Button addCoursBtn;
    @FXML Button previousDayBtn;
    @FXML Button nextDayBtn;
    @FXML Button switchNonActifsBtn;
    @FXML Button rapportsBtn;
    @FXML Button punitionsBtn;
    @FXML Button postItBtn;
    @FXML Button ramasserBtn;
    
    // fields
    private Trimestre trimestre;
    
    private Journee journee;
    
    private ScrollPane selectedScroll;
    private SmartGrid selectedGrid;
    
    // bindings
    BooleanBinding isFirstCours;
    BooleanBinding isLastCours;  
    BooleanProperty displayNonActifs;    
    
    private final ChangeListener<LocalDate> dateListener = (observable, oldValue, newValue) -> {
            LOG.debug("Date change detected --> setting selectSeance");
            setCurrentDateAndSeance(newValue, null);
        };
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        LogUtil.logStart();
        name = "Journee Ctrl";
        super.initialize(url ,rb);
        LOG.debug("Initialisation de "+name);
        initTabs();
        Btns.makeLeft(previousDayBtn);
        Btns.makeRight(nextDayBtn);
        Btns.makeLeft(seanceSelect.getBtnLeft());
        Btns.makeRight(seanceSelect.getBtnRight());
        
        Btns.makeAdd(addCoursBtn);
        Btns.makeDelete(remCoursBtn);
        
        Btns.tooltip(previousDayBtn, "Jour précédent");
        Btns.tooltip(nextDayBtn, "Jour suivant");
        Btns.tooltip(seanceSelect.getBtnLeft(), "Séance précédente");
        Btns.tooltip(seanceSelect.getBtnRight(), "Séance suivante");
        Btns.tooltip(addCoursBtn, "Ajouter une séance ponctuelle");
        Btns.tooltip(remCoursBtn, "Supprimer la séance ponctuelle");
        Btns.tooltip(switchNonActifsBtn, "active/désactive l'affichage des élèves inactifs") ;
        Btns.tooltip(rapportsBtn, "Ouvre la page des rapports trimestriels pour la classe");
        Btns.tooltip(punitionsBtn, "Ouvre le suivi des punitions, devoirs et mots pour la classe");
        Btns.tooltip(postItBtn, "ouvre le postIt de la classe");
        Btns.tooltip(ramasserBtn, "ouvre la fenêtre des actions à faire pour la classe");
        
        currentDate.setDayCellFactory(new TunedDayCellFactory().setAllowSundays(false));
        currentDate.setConverter(NodeUtil.DATE_WITH_DAY_NAME);
        
        currentDate.valueProperty().addListener(dateListener);
        
        displayNonActifs = new SimpleBooleanProperty(false);
        switchNonActifsBtn.setText("Afficher inactifs");
        displayNonActifs.addListener((observable, oldValue, newValue) -> {
            
            LOG.debug("Switch actifs/inactifs");
            switchNonActifsBtn.setText(displayNonActifs.get()? "Masquer inactifs" : "Afficher inactifs");
            refreshGrid();
        });
        
        seanceSelect.addChangeListener((ob, o, n) -> {
            LOG.debug("seanceSelect changed --> reloading");
            loadCurrentSeance();
        });
        // activation du bouton remCours si besoin
        rapportsBtn.disableProperty().bind(Bindings.isNull(seanceSelect.valueProperty()));
        punitionsBtn.disableProperty().bind(Bindings.isNull(seanceSelect.valueProperty()));
        postItBtn.disableProperty().bind(Bindings.isNull(seanceSelect.valueProperty()));
        
        // on ne change que la date, le listener sur currentDate se charge du reste
        LocalDate today = LocalDate.now();
        if(today.getDayOfWeek().equals(DayOfWeek.SUNDAY)){
            today = today.minusDays(1);
        }
        LOG.debug("Initializing with today's date");
        currentDate.setValue(today);
        LogUtil.logEnd();
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
        VBox box = (VBox)tab.getContent();
        for(Node child : box.getChildren()){
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
    
    public void setCurrentDateAndSeance(LocalDate date, Seance seance){
        LogUtil.logStart();
        // on coupe le listener sur la date pour éviter les appels en boucle
        currentDate.valueProperty().removeListener(dateListener);
        LOG.debug("Setting the date silently");
        currentDate.setValue(date);
        // on rétablit le listener une fois la date changée
        currentDate.valueProperty().addListener(dateListener);
        
        journee = model.getJournee(date);
        if(seance != null){
            LOG.debug("Chargement des séances et sélection de la séance "+seance);
            seanceSelect.setItems(journee.getSeances(), false);
            // will trigger the refreshGrid
            seanceSelect.select(seance);
        } else {
            LOG.debug("Chargement des séances. nb de séances = "+journee.getSeances().size());
            // will trigger the refreshGrid
            seanceSelect.setItems(journee.getSeances(), true);
            if(journee.getSeances().isEmpty()){
                // on force le refresh de la grid
                loadCurrentSeance();
            }
        }
        LogUtil.logEnd();
    }
    
    public void loadCurrentSeance(){
        LogUtil.logStart();
        remCoursBtn.setDisable(seanceSelect.getValue() == null || !seanceSelect.getValue().getCours().isPonctuel());
        // reinitialisation du scroll vertical
        getScrollPane(tabPane.getSelectionModel().getSelectedItem()).setVvalue(0.0);
        
        if(seanceSelect.getValue() != null &&
            seanceSelect.getValue().getClasse() != null){
            seanceSelect.getValue().getClasse().postItProperty().addListener((ob, o, n) -> {
                setPostItColor();
            });
            setPostItColor();
            ramasserBtn.setVisible(model.hasActionsEnCours(seanceSelect.getValue().getClasse()));
        } else {
            ramasserBtn.setVisible(false);
        }
        
        refreshGrid();
        LogUtil.logEnd();
    }
    
    private void setPostItColor(){
        if(!StringUtils.isEmpty(seanceSelect.getValue().getClasse().getPostIt())){
            CssUtil.addClass(postItBtn, "button-delete");
            CssUtil.removeClass(postItBtn, "button-warning");
        } else {
            CssUtil.addClass(postItBtn, "button-warning");
            CssUtil.removeClass(postItBtn, "button-delete");
        }
    }
    
    @Subscribe
    public void onReceiveSelectSeanceEvent(SelectSeanceEvent e){
        logEvent(e);
        setCurrentDateAndSeance(e.getSeance().getDateAsDate(), e.getSeance());
    }
    
    @Subscribe
    public void onReceiveSelectDateEvent(SelectDateEvent e){
        logEvent(e);
        setCurrentDateAndSeance(e.getDate(), null);
    }
    
    private void refreshGrid(){
        LogUtil.logStart();
        LOG.debug("Refreshing the grid for "+name);
        trimestre = model.getForDate(currentDate.getValue());
        if(trimestre == null){
            selectedGrid.drawNoTrimestreAlert(currentDate.getValue(), (event) -> {
                    EventBusHandler.post(new OpenMenuEvent(Constants.ADMIN_TRIMESTRE_VIEW));
                }
            );
            addCoursBtn.setDisable(true);
            trimestreName.setText("");
            return;
        }
        
        addCoursBtn.setDisable(false);
        trimestreName.setText(trimestre.getName());
        
        if(seanceSelect.getValue() != null){
            if(seanceSelect.getValue().getClasse().getEleves().isEmpty()){
                selectedGrid.drawNoEleveAlert(seanceSelect.getValue().getClasse().getName(), (event) ->{
                    EventBusHandler.post(new OpenMenuEvent(Constants.ADMIN_ELEVE_VIEW));
                    }
                );
                addCoursBtn.setDisable(true);
                return;
            }
            trimestre = model.getForDate(currentDate.getValue());
            drawGrid();
        } else {
            addCoursBtn.setDisable(false);
            String day = Constants.DAYMAP.get(currentDate.getValue().getDayOfWeek());
            selectedGrid.drawNoSeanceAlert(day,
                    (event) -> {
                        EventBusHandler.post(new OpenMenuEvent(Constants.EMPLOI_DU_TEMPS_VIEW));
                        EventBusHandler.post(new CreateCoursEvent(day, null));
                    }
            );
        }
        LogUtil.logEnd();
    }
    
    @FXML
    public void previousDay(){
        if(currentDate.getValue().getDayOfWeek() == DayOfWeek.MONDAY){
            setCurrentDateAndSeance(currentDate.getValue().minusDays(2), null);
        } else {
            setCurrentDateAndSeance(currentDate.getValue().minusDays(1), null);
        }
    }
    
    @FXML
    public void nextDay(){
        if(currentDate.getValue().getDayOfWeek() == DayOfWeek.SATURDAY){
            setCurrentDateAndSeance(currentDate.getValue().plusDays(2), null);
        } else {
            setCurrentDateAndSeance(currentDate.getValue().plusDays(1), null);
        }
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
        Seance newSeance = model.addSeanceWithCoursPonctuel(journee, coursPonctuel);
        seanceSelect.select(newSeance);
    }
    
    @FXML
    public void supprimerCours() throws IOException {
        StringBuilder confirmMsg = new StringBuilder("Supprimer la séance ponctuelle de ")
        .append(NodeUtil.getStartTime(seanceSelect.getValue().getCours()))
        .append(" avec la ")
        .append(seanceSelect.getValue().getClasse().getName());
        if(ModalUtil.confirm("Suppression de séance", confirmMsg.toString())){
            journee.getCoursPonctuels().remove(seanceSelect.getValue().getCours());
            journee.getSeances().remove(seanceSelect.getValue());
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
        EventBusHandler.post(new SelectClasseEvent(seanceSelect.getValue().getClasse()));
    }
    
    @FXML
    public void openPunitions(){
        EventBusHandler.post(new OpenMenuEvent(Constants.CLASSE_PUNITIONS_VIEW));
        EventBusHandler.post(new SelectClasseEvent(seanceSelect.getValue().getClasse()));
        
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
            controller.setClasse(seanceSelect.getValue().getClasse());

            // Show the dialog and wait until the user closes it
            dialogStage.showAndWait();

        } catch (IOException e) {
            LOG.error(e);
        }
    }
    
    @FXML
    public void openActions(){
        try {
            // Load the fxml file and create a new stage for the popup dialog.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource(Constants.ACTIONS_DIALOG));
            AnchorPane page = (AnchorPane) loader.load();

            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Actions à faire");
            dialogStage.initModality(Modality.NONE);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            // Set the person into the controller.
            ActionsEnCoursController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setClasse(seanceSelect.getValue().getClasse());

            // Show the dialog and wait until the user closes it
            dialogStage.showAndWait();

        } catch (IOException e) {
            LOG.error(e);
        }
    }
    
    public boolean openPunitionDialog(Eleve eleve){
        String texte = ModalUtil.prompt("Punition pour "+eleve.getFirstName()+" "+eleve.getLastName(), "");
        if(!StringUtils.isEmpty(texte)){
            model.createPunition(eleve, seanceSelect.getValue(), texte);
            EventBusHandler.post(MessageEvent.success("Punition créée"));
            return true;
        }
        return false;
    }
    
    /* GRID */
    private void drawGrid(){
        vieScolaireGrid.clear();
        travailGrid.clear();
        sanctionsGrid.clear();
        List<Eleve> elevesToDisplay = seanceSelect.getValue().getClasse().getEleves();
        if(!displayNonActifs.get()){
            elevesToDisplay = model.getOnlyActive(elevesToDisplay);
        }
        LOG.debug("drawing the grid for "+name);
        LOG.debug("Donnees présentes : "+seanceSelect.getValue().getDonnees());
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
        EleveData eleveData = seanceSelect.getValue().getDonnees().get(eleve);
        drawEleveName(vieScolaireGrid, eleve, rowIndex);
        if(!eleve.isInClasse(currentDate.getValue())){
            return;
        }
        CheckBox box = new CheckBox();
        Bindings.bindBidirectional(box.selectedProperty(), eleveData.absentProperty());
        vieScolaireGrid.add(box, 3, rowIndex, null);
        
        TextField retardField = new TextField();
        retardField.setMaxWidth(50);
        Bindings.bindBidirectional(retardField.textProperty(), eleveData.retardProperty(), new IntegerOnlyConverter());
        markAsInteger(retardField);
        
        
        vieScolaireGrid.add(retardField, 4, rowIndex, HPos.CENTER);
        
        Label cumulRetard = new Label();
        retardField.textProperty().addListener((observable, oldValue, newValue) -> {
            writeAndMarkInRed(cumulRetard, stats.getNbRetardsUntil(eleve, currentDate.getValue()), 3);
        });
        
        writeAndMarkInRed(cumulRetard, stats.getNbRetardsUntil(eleve, currentDate.getValue()), 3);
        vieScolaireGrid.add(cumulRetard, 5, rowIndex, null);
    }
    
    /**
     * dessine la grid travail
     * @param eleve
     * @param rowIndex 
     */
    private void drawTravail(Eleve eleve, int rowIndex){
        
        EleveData eleveData = seanceSelect.getValue().getDonnees().get(eleve);
        drawEleveName(travailGrid, eleve, rowIndex);
        if(!eleve.isInClasse(currentDate.getValue())){
            return;
        }
        CheckBox travailBox = new CheckBox();
        Bindings.bindBidirectional(travailBox.selectedProperty(), eleveData.travailPasFaitProperty());
        travailGrid.add(travailBox, 3, rowIndex, null);
        Label cumulTravail = new Label();
        travailBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            writeAndMarkInRed(cumulTravail, stats.getNbTravailUntil(eleve, currentDate.getValue()), 3);
        });
        writeAndMarkInRed(cumulTravail, stats.getNbTravailUntil(eleve, currentDate.getValue()), 3);
        travailGrid.add(cumulTravail, 4, rowIndex, null);
        
        CheckBox devoirBox = new CheckBox();
        devoirBox.setSelected(model.getDevoirForSeance(eleve, seanceSelect.getValue()) != null);
        devoirBox.selectedProperty().addListener((ob, o, checked) -> {
            Devoir devoir = model.getDevoirForSeance(eleve, seanceSelect.getValue());
            if(checked && devoir == null){
                model.createDevoir(eleve, seanceSelect.getValue());
            } else if(devoir != null){
                model.delete(devoir);
            }
        });
        travailGrid.add(devoirBox, 5, rowIndex, null);
        
        TextField oubliMaterielField = new TextField();
        Bindings.bindBidirectional(oubliMaterielField.textProperty(), eleveData.oubliMaterielProperty());
        travailGrid.add(oubliMaterielField, 6, rowIndex, HPos.LEFT);
        Label cumulOubli = new Label();
        oubliMaterielField.textProperty().addListener((observable, oldValue, newValue) -> {
            writeAndMarkInRed(cumulOubli, stats.getNbOublisUntil(eleve, currentDate.getValue()), 3);
        });
        
        writeAndMarkInRed(cumulOubli, stats.getNbOublisUntil(eleve, currentDate.getValue()), 3);
        travailGrid.add(cumulOubli, 7, rowIndex, null);
    
    }
    
    /**
     * dessine la grid sanctions
     * @param eleve
     * @param rowIndex 
     */
    private void drawSanctions(Eleve eleve, int rowIndex){
        EleveData eleveData = seanceSelect.getValue().getDonnees().get(eleve);
        
        drawEleveName(sanctionsGrid, eleve, rowIndex);
        if(!eleve.isInClasse(currentDate.getValue())){
            return;
        }
        HBox punitionsBox = new HBox();
        punitionsBox.setAlignment(Pos.CENTER_LEFT);
        TextFlow nbPunitions = new TextFlow();
        Label nbPunitionLabel = new Label(""+eleve.getPunitions().stream()
                .filter(p -> p.getSeance() == seanceSelect.getValue()).count());
        nbPunitionLabel.setPrefHeight(20);
        nbPunitions.getChildren().add(new Label(" ("));
        nbPunitions.getChildren().add(nbPunitionLabel);
        nbPunitions.getChildren().add(new Label(")"));
        
        nbPunitions.visibleProperty().bind(nbPunitionLabel.textProperty().isNotEqualTo("0"));
        nbPunitions.managedProperty().bind(nbPunitionLabel.textProperty().isNotEqualTo("0"));
        
        Button punitionBtn = Btns.punitionBtn();
        punitionBtn.setOnAction((event) -> {
            if(openPunitionDialog(eleve)){
                int nbPunition = Integer.parseInt(nbPunitionLabel.getText());
                nbPunitionLabel.setText(""+(nbPunition+1));
            }
        });
        punitionsBox.getChildren().add(punitionBtn);
        punitionsBox.getChildren().add(nbPunitions);
        
        sanctionsGrid.add(punitionsBox, 3, rowIndex, HPos.LEFT);
        
        CheckBox motCarnet = new CheckBox();
        motCarnet.setSelected(model.getMotForSeance(eleve, seanceSelect.getValue()) != null);
        motCarnet.selectedProperty().addListener((ob, o, checked) -> {
            Mot mot = model.getMotForSeance(eleve, seanceSelect.getValue());
            if(checked && mot == null){
                model.createMot(eleve, seanceSelect.getValue());
            } else if(mot != null){
                model.delete(mot);
            }
        });
        sanctionsGrid.add(motCarnet, 4, rowIndex, null);
        
        Label cumulMot = new Label();
        cumulMot.textProperty().addListener((observable, oldValue, newValue) -> {
            writeAndMarkInRed(cumulMot, stats.getMotsUntil(eleve, currentDate.getValue()).size(), 3);
        });
        writeAndMarkInRed(cumulMot, stats.getMotsUntil(eleve, currentDate.getValue()).size(), 3);
        sanctionsGrid.add(cumulMot, 5, rowIndex, null);
        
        CheckBox exclus = new CheckBox();
        TextField motif = new TextField();
        Bindings.bindBidirectional(exclus.selectedProperty(), eleveData.exclusProperty());
        sanctionsGrid.add(exclus, 6, rowIndex, null);
        exclus.selectedProperty().addListener((o, oldV, newV) -> {
            if(!newV && StringUtils.isNotBlank(motif.getText())
                    && ModalUtil.confirm("Effacer le motif ?", "Effacer le motif ?")){
                motif.clear();
            }
        });
        
        Bindings.bindBidirectional(motif.textProperty(), eleveData.MotifProperty());
        motif.textProperty().addListener((o, oldV, newV) -> {
            if(StringUtils.isNotBlank(newV)){
                exclus.setSelected(true);
            }
        });
        GridPane.setMargin(motif, new Insets(0,10,0,0));
        sanctionsGrid.add(motif, 7, rowIndex, HPos.LEFT);
        
    }
        
    private void drawRow(Eleve eleve, int rowIndex){
        LogUtil.logStart();
        donneesHandler.getOrCreateDonneeForEleve(seanceSelect.getValue(), eleve);
        drawVieScolaire(eleve, rowIndex);
        drawTravail(eleve, rowIndex);
        drawSanctions(eleve, rowIndex);
        LogUtil.logEnd();
    }
    
    private void writeAndMarkInRed(Label label, int value, int threshold){
    
        label.setText(""+value);
        if(value >= threshold){
                CssUtil.addClass(label, "text-red");
            } else {
                CssUtil.removeClass(label, "text-red");
            }
    }
    
    @Override
    public void reload(){
        super.reload();
        refreshGrid();
    }
    
}
