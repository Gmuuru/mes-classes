/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mesclasses.view;

import com.google.common.eventbus.Subscribe;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import mesclasses.controller.PageController;
import mesclasses.handlers.EventBusHandler;
import mesclasses.model.Classe;
import mesclasses.model.Constants;
import mesclasses.model.Punition;
import mesclasses.model.Trimestre;
import mesclasses.objects.events.OpenMenuEvent;
import mesclasses.objects.events.SelectClasseEvent;
import mesclasses.util.Btns;
import mesclasses.util.ModalUtil;
import mesclasses.util.NodeUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.smartgrid.SmartGrid;

/**
 * FXML Controller class
 *
 * @author rrrt3491
 */
public class PunitionsController extends PageController implements Initializable {

    private static final Logger LOG = LogManager.getLogger(PunitionsController.class);
    
    @FXML Label titleLabel;
    
    @FXML SmartGrid gridEnCours;
    
    @FXML SmartGrid gridFermees;
    
    @FXML Button displayOldBtn;
    
    private Classe classe;
    private List<Punition> enCours = new ArrayList<>();;
    private List<Punition> fermees = new ArrayList<>();;
    
    private boolean includeOldTrimestres = false;
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        name = "Punitions Ctrl";
        super.initialize(url, rb);
        displayOldBtn.setText(includeOldTrimestres? "Exclure trimestres précédents" : "Inclure trimestres précédents");
    }    

    public Classe getClasse() {
        return classe;
    }

    public void setClasse(Classe classe) {
        this.classe = classe;
        titleLabel.textProperty().bind(new SimpleStringProperty("Punitions pour la ").concat(this.classe.nameProperty()));
        init();
    }
    
    @Subscribe
    public void onSelectClasse(SelectClasseEvent event){
        logEvent(event);
        if(event.getClasse() != null && event.getClasse() != classe){
            setClasse(event.getClasse());
        }
    }
    public void init(){
        populate();
        drawPunitions();
    }
    
    private void populate(){
        LOG.debug("remplissage des listes. includeOldTrimestres="+includeOldTrimestres);
        enCours.clear();
        fermees.clear();
        
        if(trimestres == null || trimestres.isEmpty()){
            return;
        }
        Trimestre first = trimestres.get(0);
        Trimestre current = modelHandler.getForDate(LocalDate.now());
        if(current == null){
            notif("Aucun trimestre pour aujourd'hui");
            return;
        }
        if(includeOldTrimestres){
            // depuis la rentrée jusqu'à la fin du trimestre en cours
            classe.getEleves().forEach(eleve -> {
                modelHandler.filterPunitionsByTrimestre(eleve.getPunitions(), first, current.getEndAsDate())
                        .forEach(punition -> {
                    if(punition.isClosed()){
                        fermees.add(punition);
                    } else {
                        enCours.add(punition);
                    }
                });
            });
        } else {
            // seulement le trimestre en cours
            classe.getEleves().forEach(eleve -> {
                modelHandler.filterPunitionsByTrimestre(eleve.getPunitions(), current, null)
                        .forEach(punition -> {
                    if(punition.isClosed()){
                        fermees.add(punition);
                    } else {
                        enCours.add(punition);
                    }
                });
            });
        }
    }
    
    private void drawPunitions(){
        gridEnCours.clear();
        if(enCours.isEmpty()){
            gridEnCours.drawNoDataInGrid("Aucune punition en cours");
        } else {
            enCours.forEach(punition -> drawPunition(punition, false));
        }
        gridFermees.clear();
        if(fermees.isEmpty()){
            gridFermees.drawNoDataInGrid("Aucune punition fermée");
        } else {
            fermees.forEach(punition -> drawPunition(punition, true));
        }
    }
    
    private void drawPunition(Punition punition, boolean isClosed){
        SmartGrid grid = isClosed ? gridFermees : gridEnCours;
        Hyperlink lastName = NodeUtil.buildEleveLink(punition.getEleve(), punition.getEleve().lastNameProperty(), Constants.CLASSE_PUNITIONS_VIEW);
        int rowIndex = grid.addOnNewLineIfNecessary(lastName, 1, HPos.LEFT);
        Hyperlink firstName = NodeUtil.buildEleveLink(punition.getEleve(), punition.getEleve().firstNameProperty(), Constants.CLASSE_PUNITIONS_VIEW);
        grid.add(firstName, 2, rowIndex, HPos.LEFT);
        
        Label date = new Label(punition.getSeance().getDateAsDate().format(Constants.DATE_FORMATTER_FR)
                +" (cours de "+NodeUtil.getStartTime(punition.getSeance().getCours())+")");
        grid.add(date, 3, rowIndex, null);
        
        if(isClosed){
            grid.add(new Label(punition.getTexte()), 4, rowIndex, null);
            Button openBtn = Btns.arrowUpBtn();
            openBtn.setOnAction((event) -> {
                openPunition(punition);
            });
            grid.add(openBtn, 5, rowIndex, null);
        } else {
            TextField texte = new TextField();
            texte.textProperty().bindBidirectional(punition.texteProperty());

            markAsMandatory(texte);
            checkMandatory(texte);
            texte.setMaxWidth(200);
            grid.add(texte, 4, rowIndex, null);
            
            Button closeBtn = Btns.arrowDownBtn();
            closeBtn.setOnAction((event) -> {
                closePunition(punition);
            });
            grid.add(closeBtn, 5, rowIndex, null);
        }
        
        Button deleteBtn = Btns.deleteBtn();
        deleteBtn.setOnAction((event) -> {
            deletePunition(punition);
        });
        grid.add(deleteBtn, 6, rowIndex, null);
    }
    
    private void closePunition(Punition punition){
        punition.setClosed(true);
        init();
    }
    
    private void openPunition(Punition punition){
        punition.setClosed(false);
        init();
    }
    
    private void deletePunition(Punition punition){
        if(ModalUtil.confirm("Supprimer la punition", "Etes-vous sûr(e) ?")){
            modelHandler.deletePunition(punition);
            init();
        }
    }
    
    @FXML public void switchInclusion(){
        includeOldTrimestres = !includeOldTrimestres;
        displayOldBtn.setText(includeOldTrimestres? "Exclure trimestres précédents" : "Inclure trimestres précédents");
        populate();
        drawPunitions();
    }
    
    @FXML public void openClasse(){
        EventBusHandler.post(new OpenMenuEvent(Constants.JOURNEE_VIEW).setReload(true));
    }
    
    @Override
    public void reload(){
        super.reload();
        
        if(classe == null){
            return;
        }
        
        init();
    }
}
