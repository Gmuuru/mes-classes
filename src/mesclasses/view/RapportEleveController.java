/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mesclasses.view;

import com.google.common.eventbus.Subscribe;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import mesclasses.controller.PageController;
import mesclasses.handlers.EventBusHandler;
import mesclasses.handlers.PropertiesCache;
import mesclasses.model.Constants;
import mesclasses.model.Devoir;
import mesclasses.model.Eleve;
import mesclasses.model.Mot;
import mesclasses.model.Punition;
import mesclasses.model.Seance;
import mesclasses.model.Trimestre;
import mesclasses.objects.events.OpenMenuEvent;
import mesclasses.objects.events.SelectEleveEvent;
import mesclasses.objects.events.SelectSeanceEvent;
import mesclasses.util.Btns;
import mesclasses.util.CssUtil;
import mesclasses.util.EleveFileUtil;
import mesclasses.util.ModalUtil;
import mesclasses.util.NodeUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.smartgrid.SmartGrid;

/**
 * FXML Controller class
 *
 * @author rrrt3491
 */
public class RapportEleveController extends PageController implements Initializable {
    
    private static final Logger LOG = LogManager.getLogger(RapportEleveController.class);
    
    @FXML Label trimestreLabel;
    
    @FXML Label rapportLabel;
    
    @FXML Button previousBtn;
    
    @FXML Button nextBtn;
    
    @FXML Button ouvrirClasseBtn;
    
    @FXML TitledPane absencesPane;
    @FXML VBox absencesBox;
    
    @FXML TitledPane retardsPane;
    @FXML VBox retardsBox;
    
    @FXML TitledPane travauxPane;
    @FXML VBox travauxBox;
    
    @FXML TitledPane devoirsPane;
    @FXML VBox devoirsBox;
    
    @FXML TitledPane punitionsPane;
    @FXML VBox punitionsBox;
    
    @FXML TitledPane motsPane;
    @FXML VBox motsBox;
    
    @FXML TitledPane oublisPane;
    @FXML VBox oublisBox;
    
    @FXML TitledPane exclusPane;
    @FXML VBox exclusBox;
    
    @FXML Pane bulletinPane;
    @FXML Button bulletinBtn;
    @FXML Pane vieScolairePane;
    @FXML Button vieScolaireBtn;
    @FXML Pane orientationPane;
    @FXML Button orientationBtn;
    
    @FXML SmartGrid fileGrid;
    @FXML HBox fileSection;
    @FXML Button addFileBtn;
    
    private Eleve eleve;
    private IntegerProperty trimestreIndex;
    private StringProperty selectedTrimestre;
    
    private StringProperty selectedFileType;
    private List<File> files;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        name = "Rapport Classe Ctrl";
        super.initialize(url ,rb);
        
        trimestreIndex = new SimpleIntegerProperty();
        selectedTrimestre = new SimpleStringProperty();
        selectedFileType = new SimpleStringProperty();
        selectBulletin();
        selectedFileType.addListener((observable, oldValue, newValue) -> {
            refreshGrid();
        });
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
        
        bulletinBtn.prefWidthProperty().bind(bulletinPane.widthProperty());
        vieScolaireBtn.prefWidthProperty().bind(vieScolairePane.widthProperty());
        orientationBtn.prefWidthProperty().bind(orientationPane.widthProperty());
        
    }

    public Eleve getEleve() {
        return eleve;
    }

    public void setEleve(Eleve eleve) {
        this.eleve = eleve;
        fileSection.managedProperty().bind(eleve.getClasse().principaleProperty());
        fileSection.visibleProperty().bind(eleve.getClasse().principaleProperty());
        init();
    }
    
    @Subscribe
    public void onSelectEleve(SelectEleveEvent event){
        logEvent(event);
        setEleve(event.getEleve());
    }
    
    public void init(){
        name +=" for élève "+eleve;
        rapportLabel.setText("Rapport pour "+eleve.getDisplayName());
        if(trimestres != null && !trimestres.isEmpty()){
            Trimestre current = model.getForDate(LocalDate.now());
            selectTrimestre(current != null ? trimestres.indexOf(current) : 0);
        }
        refreshGrid();
    }
    
    private void closePanes(){
        absencesPane.setExpanded(false);
        retardsPane.setExpanded(false);
        travauxPane.setExpanded(false);
        devoirsPane.setExpanded(false);
        punitionsPane.setExpanded(false);
        motsPane.setExpanded(false);
        oublisPane.setExpanded(false);
        exclusPane.setExpanded(false);
    }
    
    private void selectTrimestre(int index){
        trimestreIndex.set(index);
        selectedTrimestre.set(trimestres.get(index).getName());
        refreshPanes();
    }
    
    public void refreshPanes(){
        if(eleve == null){
            return;
        }
        closePanes();
        List<Seance> listeSeances = stats.getSeancesWithAbsenceOnTrimestre(eleve, trimestres.get(trimestreIndex.get()));
        handlePaneTitle(absencesPane, listeSeances.size(), "absence", "absences");
        handleBasicPaneContent(absencesBox, listeSeances);
        
        listeSeances = stats.getSeancesWithRetardOnTrimestre(eleve, trimestres.get(trimestreIndex.get()));
        handlePaneTitle(retardsPane, listeSeances.size(), "retard", "retards");
        handleRetardPaneContent(listeSeances);
        
        listeSeances = stats.getSeancesWithTravailOnTrimestre(eleve, trimestres.get(trimestreIndex.get()));
        handlePaneTitle(travauxPane, listeSeances.size(), "travail non fait", "travaux non faits");
        handleBasicPaneContent(travauxBox, listeSeances);
        
        List<Devoir> devoirs = model.filterDevoirsByTrimestre(eleve.getDevoirs(), trimestres.get(trimestreIndex.get()), null);
        handlePaneTitle(devoirsPane, devoirs.size(), "devoir non rendu", "devoirs non rendus");
        handleDevoirPaneContent(devoirs);
        
        List<Punition> punitions = model.filterPunitionsByTrimestre(eleve.getPunitions(), trimestres.get(trimestreIndex.get()), null);
        handlePaneTitle(punitionsPane, punitions.size(), "punition", "punitions");
        handlePunitionContent(punitions);
        
        List<Mot> mots = model.filterMotsByTrimestre(eleve.getMots(), trimestres.get(trimestreIndex.get()), null);
        handlePaneTitle(motsPane, mots.size(), "mot carnet", "mots carnet");
        handleMotPaneContent(mots);
        
        listeSeances = stats.getSeancesWithOubliOnTrimestre(eleve, trimestres.get(trimestreIndex.get()));
        handlePaneTitle(oublisPane, listeSeances.size(), "oubli matériel", "oublis matériel");
        handleOubliPaneContent(listeSeances);
        
        listeSeances = stats.getSeancesWithExclusionOnTrimestre(eleve, trimestres.get(trimestreIndex.get()));
        handlePaneTitle(exclusPane, listeSeances.size(), "exclusion", "exclusions");
        handleExclusPaneContent(listeSeances);
        
    }
    
    private void handlePaneTitle(TitledPane pane, long count, String sing, String plur){
        if(count == 0){
            pane.setText(0+" "+sing);
            pane.setDisable(true);
            return;
        }
            pane.setDisable(false);
        if(count == 1){
            pane.setText(1+" "+sing);
            return;
        }
        pane.setText(count+" "+plur);
    }
    
    private void handleBasicPaneContent(VBox pane, List<Seance> liste){
        pane.getChildren().clear();
        liste.forEach(s -> {
            pane.getChildren().add(buildLink(s, null));
        });
    }
    
    private void handleRetardPaneContent(List<Seance> liste){
        retardsBox.getChildren().clear();
        liste.forEach(s -> {
            retardsBox.getChildren().add(buildLink(s, " ("+s.getDonnees().get(eleve).getRetard()+")"));
        });
    }
    
    private void handleMotPaneContent(List<Mot> liste){
        motsBox.getChildren().clear();
        liste.forEach(m -> {
            motsBox.getChildren().add(buildLink(m.getSeance(), m.getDateCloture() != null ? "":" (à vérifier)"));
        });
    }
    
    private void handleDevoirPaneContent(List<Devoir> liste){
        devoirsBox.getChildren().clear();
        liste.forEach(d -> {
            devoirsBox.getChildren().add(buildLink(d.getSeance(), d.isClosed()? " (clos)":" (à ramasser)"));
        });
    }
    
    private void handleOubliPaneContent(List<Seance> liste){
        oublisBox.getChildren().clear();
        liste.forEach(s -> {
            oublisBox.getChildren().add(buildLink(s, " ("+s.getDonnees().get(eleve).getOubliMateriel()+")"));
        });
    }
    
    private void handleExclusPaneContent(List<Seance> liste){
        exclusBox.getChildren().clear();
        liste.forEach(s -> {
            String motif = s.getDonnees().get(eleve).getMotif();
            exclusBox.getChildren().add(buildLink(s, StringUtils.isBlank(motif)? "" :  "("+motif+")"));
        });
    }
    
    private void handlePunitionContent(List<Punition> liste){
        punitionsBox.getChildren().clear();
        liste.forEach(punition -> {
            punitionsBox.getChildren().add(buildLink(punition.getSeance(), " ("+punition.getTexte()+")"));
        });
    }
    
    private Hyperlink buildLink(Seance seance, String suffixe){
        String text = seance.getDateAsDate().format(Constants.LONG_DATE_FORMATTER);
        if(suffixe != null){
            text+=" "+suffixe;
        }
        text += ", cours de "+NodeUtil.getStartTime(seance.getCours());
        
        Hyperlink link = new Hyperlink(text);
        link.setOnAction(e -> {
            EventBusHandler.post(new SelectSeanceEvent(seance));
            EventBusHandler.post(new OpenMenuEvent(Constants.JOURNEE_VIEW));
        });
        return link;
    }
    public void refreshGrid(){
        fileGrid.clear();
        files = EleveFileUtil.getEleveFilesOfType(eleve, selectedFileType.get());
        if(files.isEmpty()){
            fileGrid.drawNoDataInGrid("Aucun fichier de type "+selectedFileType.get()+" importé");
        }
        files.forEach(file -> {
            drawGrid(file);
        });
    }
    
    private void drawGrid(File file) {
        Hyperlink link = new Hyperlink(file.getName());
        link.setOnAction((event) -> openFile(file));
        int rowIndex = fileGrid.addOnNewLineIfNecessary(link, 1, HPos.LEFT);
        
        ComboBox<String> typeBox = new ComboBox<>();
        typeBox.getItems().addAll(Constants.FILE_TYPES);
        typeBox.getSelectionModel().select(selectedFileType.get());
        typeBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue.equals(oldValue)){
                try {
                    EleveFileUtil.moveFileForEleve(eleve, file, newValue);
                    selectFileType(newValue);
                    refreshGrid();
                } catch(IOException e){
                    ModalUtil.alert("Impossible de déplacer le fichier", e.getMessage());
                }
            }
        });
        fileGrid.add(typeBox, 2, rowIndex, null);
        
        Button deleteBtn = Btns.deleteBtn();
        deleteBtn.setOnAction(event -> {
            if (ModalUtil.confirm("Suppression du fichier", "Etes vous sûr ?")) {
                if(file.delete()){
                    fileGrid.deleteRow(SmartGrid.row(deleteBtn));
                } else {
                    ModalUtil.alert("Suppression impossible", "Impossible de supprimer le fichier "+file.getName());
                }
            }
        });
        fileGrid.add(deleteBtn, 3, rowIndex, null);
    }
    
    private void openFile(File file) {
        try {
            Desktop.getDesktop().open(file);
        } catch (IOException ex) {
            LOG.error(ex);
        }
    }
    
    @FXML public void importFile(){
        FileChooser chooser = new FileChooser();
        PropertiesCache cache = PropertiesCache.getInstance();
        String lastDir = cache.getProperty(PropertiesCache.LAST_UPLOAD_DIR);
        if(lastDir != null && new File(lastDir).exists()){
            chooser.setInitialDirectory(new File(lastDir));
        }
        chooser.getExtensionFilters().addAll(
                new ExtensionFilter("All Files", "*.*"));
        chooser.setTitle("Sélectionnez un fichier");
        File file = chooser.showOpenDialog(primaryStage);
        if(file == null){
            return;
        }
        cache.setProperty(PropertiesCache.LAST_UPLOAD_DIR, file.getParent());
        try {
            EleveFileUtil.copyFileForEleve(eleve, file, selectedFileType.get());
        } catch(IOException e){
            ModalUtil.alert("Erreur lors de l'import du fichier", e.getMessage());
            return;
        }
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
    
    private void selectFileType(String type){
        switch(type){
            case Constants.FILE_TYPE_BULLETIN :
                selectBulletin();
                break;
            case Constants.FILE_TYPE_VIE_SCOLAIRE :
                selectVieScolaire();
                break;
            case Constants.FILE_TYPE_ORIENTATION :
                selectOrientation();
                break;
            default : 
                break;
        }
    }
    
    @FXML public void selectBulletin(){
        selectedFileType.set(Constants.FILE_TYPE_BULLETIN);
        addFileBtn.setText("Ajouter bulletin");
        CssUtil.addClass(bulletinBtn, "nav-selected");
        CssUtil.removeClass(vieScolaireBtn, "nav-selected");
        CssUtil.removeClass(orientationBtn, "nav-selected");
    }
    
    @FXML public void selectVieScolaire(){
        selectedFileType.set(Constants.FILE_TYPE_VIE_SCOLAIRE);
        addFileBtn.setText("Ajouter fichier de vie scolaire");
        CssUtil.removeClass(bulletinBtn, "nav-selected");
        CssUtil.addClass(vieScolaireBtn, "nav-selected");
        CssUtil.removeClass(orientationBtn, "nav-selected");
    }
    
    @FXML public void selectOrientation(){
        selectedFileType.set(Constants.FILE_TYPE_ORIENTATION);
        addFileBtn.setText("Ajouter fichier d'orientation");
        CssUtil.removeClass(bulletinBtn, "nav-selected");
        CssUtil.removeClass(vieScolaireBtn, "nav-selected");
        CssUtil.addClass(orientationBtn, "nav-selected");
    }
    
    @FXML public void back(){
        if(previousPage != null){
            EventBusHandler.post(new OpenMenuEvent(previousPage).setReload(false));
        } else {
            EventBusHandler.post(new OpenMenuEvent(Constants.JOURNEE_VIEW).setReload(false));
        }
    }
    
}
