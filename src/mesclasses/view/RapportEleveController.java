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
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
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
import mesclasses.model.Eleve;
import mesclasses.model.EleveData;
import mesclasses.model.Punition;
import mesclasses.objects.events.OpenMenuEvent;
import mesclasses.objects.events.SelectEleveEvent;
import mesclasses.util.Btns;
import mesclasses.util.CssUtil;
import mesclasses.util.EleveFileUtil;
import mesclasses.util.ModalUtil;
import org.apache.commons.lang3.StringUtils;
import org.smartgrid.SmartGrid;

/**
 * FXML Controller class
 *
 * @author rrrt3491
 */
public class RapportEleveController extends PageController implements Initializable {
    
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
            selectTrimestre(0);
        }
        closePanes();
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
        List<EleveData> data = modelHandler.filterByTrimestre(eleve.getData(), trimestres.get(trimestreIndex.get()), null);
        Collections.sort(data);
        List<EleveData> absences = data.stream().filter(d -> d.isAbsent()).collect(Collectors.toList());
        handlePaneTitle(absencesPane, absences.size(), "absence", "absences");
        handleBasicPaneContent(absencesBox, absences);
        
        List<EleveData> retards = data.stream().filter(d -> d.getRetard() > 0).collect(Collectors.toList());
        handlePaneTitle(retardsPane, retards.size(), "retard", "retards");
        handleRetardPaneContent(retards);
        
        List<EleveData> travail = data.stream().filter(d -> d.isTravailPasFait()).collect(Collectors.toList());
        handlePaneTitle(travauxPane, travail.size(), "travail non fait", "travaux non faits");
        handleBasicPaneContent(travauxBox, travail);
        
        List<EleveData> devoirs = data.stream().filter(d -> d.isDevoir()).collect(Collectors.toList());
        handlePaneTitle(devoirsPane, devoirs.size(), "devoir non rendu", "devoirs non rendus");
        handleBasicPaneContent(devoirsBox, devoirs);
        
        List<Punition> punitions = modelHandler.filterByTrimestre(eleve.getPunitions(), trimestres.get(trimestreIndex.get()));
        handlePaneTitle(punitionsPane, punitions.size(), "punition", "punitions");
        handlePunitionContent(punitionsBox, punitions);
        
        List<EleveData> mots = data.stream().filter(d -> d.isMotCarnet()).collect(Collectors.toList());
        handlePaneTitle(motsPane, mots.size(), "mot carnet", "mots carnet");
        handleBasicPaneContent(motsBox, mots);
        
        List<EleveData> motsSignes = data.stream().filter(d -> d.isMotSigne()).collect(Collectors.toList());
        handlePaneTitle(motsPane, motsSignes.size(), "mot signé", "mots signés");
        handleBasicPaneContent(motsBox, motsSignes);
        
        List<EleveData> oublis = data.stream().filter(d -> StringUtils.isNotBlank(d.getOubliMateriel())).collect(Collectors.toList());
        handlePaneTitle(oublisPane, oublis.size(), "oubli matériel", "oublis matériel");
        handleOubliPaneContent(oublis);
        
        List<EleveData> exclusions = data.stream().filter(d -> d.isExclus()).collect(Collectors.toList());
        handlePaneTitle(exclusPane, exclusions.size(), "exclusion", "exclusions");
        handleBasicPaneContent(exclusBox, exclusions);
        
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
    
    private void handleBasicPaneContent(VBox pane, List<EleveData> liste){
        pane.getChildren().clear();
        liste.forEach(eleveData -> {
            String formattedDate = eleveData.getDateAsDate().format(Constants.LONG_DATE_FORMATTER);
            pane.getChildren().add(new Label(formattedDate+", cours "+eleveData.getCours()));
        });
    }
    
    private void handleRetardPaneContent(List<EleveData> liste){
        retardsBox.getChildren().clear();
        liste.forEach(eleveData -> {
            String formattedDate = eleveData.getDateAsDate().format(Constants.LONG_DATE_FORMATTER);
            retardsBox.getChildren().add(
                    new Label(formattedDate+", cours "+eleveData.getCours()+" ("+eleveData.getRetard()+")"));
        });
    }
    
    private void handleOubliPaneContent(List<EleveData> liste){
        oublisBox.getChildren().clear();
        liste.forEach(eleveData -> {
            String formattedDate = eleveData.getDateAsDate().format(Constants.LONG_DATE_FORMATTER);
            oublisBox.getChildren().add(
                    new Label(formattedDate+", cours "+eleveData.getCours()+" ("+eleveData.getOubliMateriel()+")"));
        });
    }
    
    private void handlePunitionContent(VBox pane, List<Punition> liste){
        pane.getChildren().clear();
        liste.forEach(punition -> {
            String formattedDate = punition.getDateAsDate().format(Constants.LONG_DATE_FORMATTER);
            pane.getChildren().add(new Label(formattedDate+", cours "+punition.getCours()+" ("+punition.getTexte()+")"));
        });
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
            Logger.getLogger(RapportEleveController.class.getName()).log(Level.SEVERE, null, ex);
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
            EventBusHandler.post(new OpenMenuEvent(Constants.CLASSE_CONTENT_TABS_VIEW).setReload(false));
        }
    }
    
}
