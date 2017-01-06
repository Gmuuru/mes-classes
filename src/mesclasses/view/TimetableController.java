package mesclasses.view;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.eventbus.Subscribe;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import mesclasses.MainApp;
import mesclasses.controller.PageController;
import mesclasses.model.Constants;
import mesclasses.model.Cours;
import mesclasses.objects.events.CreateCoursEvent;
import mesclasses.util.CssUtil;
import mesclasses.util.ModalUtil;
import mesclasses.util.NodeUtil;
import org.apache.commons.lang3.StringUtils;

/**
 * FXML Controller class
 *
 * @author rrrt3491
 */
public class TimetableController extends PageController implements Initializable {

    @FXML AnchorPane anchor;
    @FXML Pane mondayPane;
    @FXML Pane tuesdayPane;
    @FXML Pane wednesdayPane;
    @FXML Pane thursdayPane;
    @FXML Pane fridayPane;
    @FXML Pane saturdayPane;
    @FXML Button newCoursBtn;
    @FXML ScrollPane scroll;
    
    private BiMap<String, Pane> paneMap = HashBiMap.create();
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        name = "timetable ctrl";
        super.initialize(url, rb);
        paneMap.put(Constants.DAYS.get(0), mondayPane);
        paneMap.put(Constants.DAYS.get(1), tuesdayPane);
        paneMap.put(Constants.DAYS.get(2), wednesdayPane);
        paneMap.put(Constants.DAYS.get(3), thursdayPane);
        paneMap.put(Constants.DAYS.get(4), fridayPane);
        paneMap.put(Constants.DAYS.get(5), saturdayPane);
        handleKeys();
        handleClicks(mondayPane);
        handleClicks(tuesdayPane);
        handleClicks(wednesdayPane);
        handleClicks(thursdayPane);
        handleClicks(fridayPane);
        handleClicks(saturdayPane);
        
        newCoursBtn.setText("\uf067");
        CssUtil.addClass(newCoursBtn, "button-arrow");
        cours.forEach(c -> {
            createEvent(c);
        });
        cours.forEach(c -> {
            resizeEvent(c);
            getPane(c).getChildren().add(c.getEvent());
        });
        
    }  
    
    private void handleKeys(){
        anchor.setOnKeyReleased((ev) -> {
            if (null != ev.getCode()) switch (ev.getCode()) {
                case ENTER:
                    newCoursBtn.fire();
                    break;
                case UP:
                    scroll.setVvalue(0);
                    break;
                case DOWN:
                    scroll.setVvalue(1.0);
                    break;
                default:
                    break;
            }
            ev.consume(); 
        });
    }
    
    private VBox createEvent(Cours theCours){
        VBox event = new VBox();
        CssUtil.addClass(event, "event");
        CssUtil.addClass(event, getCSS(theCours));
        String periodicite = theCours.getWeek().equals(config.getProperty(Constants.CONF_WEEK_DEFAULT)) ? "" : theCours.getWeek();
        String salle = StringUtils.isNotBlank(theCours.getRoom()) ? "salle "+theCours.getRoom() : "";
        Label periodiciteAndSalle = new Label((periodicite+" "+salle).trim());
        bindWidth(periodiciteAndSalle, event, 1);
        Label classe = new Label(theCours.getClasse().getName());
        CssUtil.addClass(classe, "event-classe");
        bindWidth(classe, event, 1);
        
        //horaires
        HBox horaires = new HBox();
        CssUtil.addClass(horaires, "horaires");
        Label start = new Label(" "+NodeUtil.getStartTime(theCours));
        CssUtil.addClass(start, "start");
        Label end = new Label(NodeUtil.getEndTime(theCours)+" ");
        CssUtil.addClass(end, "end");
        horaires.getChildren().add(start);
        horaires.getChildren().add(end);
        bindWidth(horaires, event, 1);
        bindWidth(start, horaires, 2);
        bindWidth(end, horaires, 2);
        
        
        event.getChildren().add(periodiciteAndSalle);
        bindHeight(periodiciteAndSalle, event, 3);
        event.getChildren().add(classe);
        bindHeight(classe, event, 3);
        event.getChildren().add(horaires);
        bindHeight(horaires, event, 3);
        event.setOnMouseReleased((e) ->{
            handleEditCours(theCours);
        });
        theCours.setEvent(event);
        return event;
    }
    
    private String getCSS(Cours theCours){
        return "event"+classes.indexOf(theCours.getClasse()) % 8;
    }
    
    private void bindWidth(Region node, Pane parent, int divider) {
        node.minWidthProperty().bind(parent.widthProperty().divide(divider));
        node.prefWidthProperty().bind(parent.widthProperty().divide(divider));
        node.maxWidthProperty().bind(parent.widthProperty().divide(divider));
    }
    
    private void bindHeight(Region node, Pane parent, int divider) {
        node.minHeightProperty().bind(parent.heightProperty().divide(divider));
        node.prefHeightProperty().bind(parent.heightProperty().divide(divider));
        node.maxHeightProperty().bind(parent.heightProperty().divide(divider));
    }
    
    private void resizeEvent(Cours cours){
        VBox event = cours.getEvent();
        Pane pane = getPane(cours);
        bindWidth(event, pane, 1);
        event.layoutXProperty().bind(pane.layoutXProperty());
        Double startTime = cours.getStartHour()+cours.getStartMin()/60.0;
        Double endTime = cours.getEndHour()+cours.getEndMin()/60.0;
        Double height = Math.max((endTime - startTime)*60, 20);
        event.layoutYProperty().bind(pane.layoutYProperty().add(60* (startTime - 7)));
        event.setMinHeight(height);
        event.setPrefHeight(height);
        event.setMaxHeight(height);
        handleSimultaneousEvents(cours);
    }
    
    private void handleSimultaneousEvents(Cours cours){
        List<Cours> liste = getSimultaneousCours(cours);
        Pane pane = getPane(cours);
        if(liste.size() == 1){
            if(cours.getWeek().equals(config.getProperty(Constants.CONF_WEEK_P1_NAME)) || 
                    cours.getWeek().equals(config.getProperty(Constants.CONF_WEEK_P2_NAME))){
                bindWidth(cours.getEvent(), pane, 2);
            }
            if(cours.getWeek().equals(config.getProperty(Constants.CONF_WEEK_P2_NAME))){
                cours.getEvent().layoutXProperty().bind(pane.layoutXProperty().add(pane.widthProperty().divide(2)));
            }
            return;
        }
        for(int i = 0; i < liste.size(); i++){
            VBox event = liste.get(i).getEvent();
            event.layoutXProperty().bind(pane.layoutXProperty().add(pane.widthProperty().multiply(i).divide(liste.size())));
            bindWidth(event, pane, liste.size());
        }
        
    }
    
    private List<Cours> getSimultaneousCours(Cours theCours){
        List<Cours> liste = cours.stream().filter(c -> {
            return c.getDay().equals(theCours.getDay()) && Math.max(start(c),start(theCours)) < Math.min(end(c),end(theCours));
        }).collect(Collectors.toList());
        liste.sort((Cours t, Cours t1) -> {
            if(t.getWeek().equals(t1.getWeek())){
                return classes.indexOf(t.getClasse()) - classes.indexOf(t1.getClasse());
            }
            return t.getWeek().compareTo(t1.getWeek());
        });
        return liste;
    }
    
    private int start(Cours cours){
        return cours.getStartHour()*60 + cours.getStartMin();
    }
    
    private int end(Cours cours){
        return cours.getEndHour()*60 + cours.getEndMin();
    }
    @FXML
    public void handleNewCours(){Cours newCours = new Cours();
        newCours.setClasse(classes.get(0));
        newCours.setDay(Constants.DAYS.get(0));
        newCours.setWeek(config.getProperty(Constants.CONF_WEEK_DEFAULT));
        
        createNewCours(newCours);
    }
    
    private void createNewCours(Cours newCours){
        
        newCours = openEditDialog(newCours, null);
        if(newCours != null){
            modelHandler.createCours(newCours);
            VBox event = createEvent(newCours);
            resizeEvent(newCours);
            getPane(newCours).getChildren().add(event);
        }
    }
    
    @Subscribe
    public void createNewCoursFromEvent(CreateCoursEvent event){
        logEvent(event);
        if(event.getClasse() != null){
            Cours newCours = new Cours();
            newCours.setClasse(classes.get(0));
            newCours.setDay(Constants.DAYS.get(0));
            newCours.setWeek(config.getProperty(Constants.CONF_WEEK_DEFAULT));
            createNewCours(newCours);
        }
    }
    
    private void handleEditCours(Cours coursToEdit){
        Cours editedCours = openEditDialog(modelHandler.cloneCours(coursToEdit), coursToEdit);
        if(editedCours != null){
            
            getPane(coursToEdit).getChildren().remove(coursToEdit.getEvent());
            modelHandler.updateCours(coursToEdit, editedCours);
            createEvent(editedCours);
            resizeEvent(editedCours);
            getPane(editedCours).getChildren().add(editedCours.getEvent());
        }
    }
    
    private Pane getPane(Cours cours){
        return paneMap.get(cours.getDay());
    }
    
    private void handleClicks(Pane pane){
        pane.setOnMouseClicked((MouseEvent mouseEvent) -> {
            if(mouseEvent.getButton().equals(MouseButton.PRIMARY)){
                if(mouseEvent.getClickCount() == 2){
                    Cours newCours = new Cours();
                    newCours.setClasse(classes.get(0));
                    newCours.setDay(paneMap.inverse().get(pane));
                    newCours.setWeek(config.getProperty(Constants.CONF_WEEK_DEFAULT));
                    newCours.setStartHour((int)(mouseEvent.getY())/60 + 7);
                    newCours.setEndHour(newCours.getStartHour()+1);
                    createNewCours(newCours);
                }
            }
        });
    }
    
    private Cours openEditDialog(Cours coursToEdit, Cours originalCours){
    try {
            // Load the fxml file and create a new stage for the popup dialog.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource(Constants.COURS_EDIT_DIALOG));
            AnchorPane page = (AnchorPane) loader.load();

            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Edition d'un cours");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            // Set the person into the controller.
            CoursEditDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setCours(coursToEdit, false);

            // Show the dialog and wait until the user closes it
            dialogStage.showAndWait();
            int status = controller.getStatus();
            if(status >= 0){
                //update/cancel
                return controller.getCours();
            }
            else {
                //delete
                getPane(coursToEdit).getChildren().remove(coursToEdit.getEvent());
                int seances = modelHandler.delete(originalCours).size();
                ModalUtil.info("Séances modifiées", seances+" séances ont été modifiées");
                return null;
            }

        } catch (IOException e) {
            notif(e);
            return null;
        }
    }
}
