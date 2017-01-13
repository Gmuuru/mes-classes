/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mesclasses.view;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextFlow;
import mesclasses.controller.PageController;
import mesclasses.handlers.EventBusHandler;
import mesclasses.model.Classe;
import mesclasses.model.Constants;
import mesclasses.model.Cours;
import mesclasses.model.Eleve;
import mesclasses.objects.events.OpenMenuEvent;
import mesclasses.objects.events.SelectClasseEvent;
import mesclasses.util.CssUtil;
import mesclasses.util.NodeUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * FXML Controller class
 *
 * @author rrrt3491
 */
public class AccueilController extends PageController implements Initializable {

    private static final Logger LOG = LogManager.getLogger(AccueilController.class);
    
    @FXML VBox classesBox;
    @FXML Label classesLabel;
    @FXML Hyperlink classesLinkToData;
    @FXML Label classesOu;
    @FXML Hyperlink classesLinkToAdmin;
    @FXML Label elevesLabel;
    @FXML Hyperlink elevesLinkToRapports;
    @FXML Label elevesOu;
    @FXML Hyperlink elevesLinkToAdmin;
    @FXML Label punitionsLabel;
    @FXML VBox coursBox;
    @FXML Label timetableLabel;
    @FXML Label trimestresLabel;
    @FXML VBox trimestresBox;
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        name = "Accueil ctrl";
        super.initialize(url, rb);
        init();
    }    
    
    public void init(){
        
        // GESTION DES CLASSES
        classesLabel.setText(classes.size()+" classes présentes. ");
        
        classesLinkToData.managedProperty().bind(Bindings.size(classes).isNotEqualTo(0));
        classesLinkToData.visibleProperty().bind(Bindings.size(classes).isNotEqualTo(0));
        classesOu.managedProperty().bind(Bindings.size(classes).isNotEqualTo(0));
        classesOu.visibleProperty().bind(Bindings.size(classes).isNotEqualTo(0));
        
        classesBox.getChildren().clear();
        int nbEleves = 0;
        int nbPunitions = 0;
        for(Classe classe : classes) {
            if(classe.getEleves().isEmpty()){
                classesBox.getChildren().add(buildClasseVideFlow(classe));
            } else {
                classesBox.getChildren().add(buildClasseFlow(classe));
                nbEleves+= classe.getEleves().size();
                for(Eleve eleve : classe.getEleves()){
                    nbPunitions += eleve.getPunitions().size();
                }
            }
        }
        
        // GESTION DES ELEVES
        elevesLabel.setText(nbEleves+" élèves présents. ");
        elevesLinkToRapports.setManaged(nbEleves > 0);
        elevesLinkToRapports.setVisible(nbEleves > 0);
        elevesOu.setManaged(nbEleves > 0);
        elevesOu.setVisible(nbEleves > 0);
        
        //GESTION DES PUNITIONS
        punitionsLabel.setText(nbPunitions+(nbPunitions <=1 ? " punition.": " punitions."));
        
        //GESTION DES COURS
        Map<String, Double> seanceMap = new HashMap<>();
        Double total = 0.0;
        for(Cours seance : cours){
            String classe = seance.getClasse().getName();
            if(!seanceMap.containsKey(classe)){
                seanceMap.put(classe, 0.0);
            }
            if(seance.getWeek().equals(config.getProperty(Constants.CONF_WEEK_DEFAULT))){
                seanceMap.put(classe, seanceMap.get(classe)+1);
                total++;
            } else {
                seanceMap.put(classe, seanceMap.get(classe)+0.5);
                total+= 0.5;
            }
        }
        
        coursBox.getChildren().clear();
        for(Classe classe : classes){
            String heures;
            if(!seanceMap.containsKey(classe.getName())){
                heures = toTimeString(0.0);
            } else {
                heures = toTimeString(seanceMap.get(classe.getName()));
            }
            Label lbl = new Label(classe.getName()+" : "+heures);
            coursBox.getChildren().add(textFlow(lbl));
        }
        timetableLabel.setText("Nombre d'heures total : "+toTimeString(total)+". ");
        trimestresLabel.setText(trimestres.size()+" trimestres. ");
        trimestresBox.getChildren().clear();
        trimestres.forEach(t -> {
            Label tDates = new Label(t.getName()+" : du "+t.getStartAsDate().format(Constants.LONG_DATE_FORMATTER)
                    +" au "+t.getEndAsDate().format(Constants.LONG_DATE_FORMATTER));
            trimestresBox.getChildren().add(textFlow(tDates));
        });
    }

    private TextFlow textFlow(Node... nodes){
        TextFlow textFlow = new TextFlow(nodes);
        CssUtil.addClass(textFlow, "text-flow");
        return textFlow;
    }
    
    private String toTimeString(double val) {
        int intPart = (int)val;
        return NodeUtil.formatTime(intPart, (val - intPart) > 0 ? 30:0);
    }
    
    private TextFlow buildClasseVideFlow(Classe classe){
        Label lbl = new Label(classe.getName()+" : aucun élève !");
        CssUtil.addClass(lbl, "text-red");
        Hyperlink link = new Hyperlink("Ajoutez des élèves pour la "+classe.getName());
        link.setOnAction(e -> {
            EventBusHandler.post(new OpenMenuEvent(Constants.ADMIN_ELEVE_VIEW));
            EventBusHandler.post(new SelectClasseEvent(classe));
        });
        return textFlow(lbl, link);
    }
    
    private TextFlow buildClasseFlow(Classe classe){
        Label lbl = new Label(classe.getName()+" : "+classe.getEleves().size()+" élèves.");
        return textFlow(lbl);
    }
    
    @FXML public void openClasses(){
            EventBusHandler.post(new OpenMenuEvent(Constants.JOURNEE_VIEW));
    }
    @FXML public void openRapports(){
            EventBusHandler.post(new OpenMenuEvent(Constants.CLASSE_RAPPORT_TABS_VIEW));
    }
    @FXML public void openClasseAdmin(){
            EventBusHandler.post(new OpenMenuEvent(Constants.ADMIN_CLASSE_VIEW));
    }
    @FXML public void openEleveAdmin(){
        EventBusHandler.post(new OpenMenuEvent(Constants.ADMIN_ELEVE_VIEW));
    }
    @FXML public void openTimetable(){
        EventBusHandler.post(new OpenMenuEvent(Constants.EMPLOI_DU_TEMPS_VIEW));
    }
    @FXML public void openTrimestres(){
        EventBusHandler.post(new OpenMenuEvent(Constants.ADMIN_TRIMESTRE_VIEW));
    }
    
    @Override
    public void reload(){
        init();
    }
}
