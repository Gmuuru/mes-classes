/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mesclasses.controller;

import com.google.common.eventbus.Subscribe;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.TextFlow;
import mesclasses.handlers.EventBusHandler;
import mesclasses.model.Classe;
import mesclasses.model.Constants;
import mesclasses.objects.ClasseTab;
import mesclasses.objects.events.ClassesChangeEvent;
import mesclasses.objects.events.OpenMenuEvent;
import mesclasses.objects.events.SelectClasseEvent;

/**
 * FXML Controller class
 *
 * @author rrrt3491
 */
public abstract class AbstractClasseTabsController extends PageController implements Initializable {

    @FXML BorderPane borderPane;
    
    @FXML TabPane tabPane;
    
    private ChangeListener<Tab> selectionListener;
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        name = (name == null) ? "Classe Tabs Ctrl" : name;
        super.initialize(url, rb);
        
        handleTabChangeEvent();
        
        setTabs();
        
    }  
    
    /**
     * synchronise la liste des classes avec la liste des tabs
     */
    @Subscribe
    public void onClassesChange(ClassesChangeEvent event){
        logEvent(event);
        setTabs();
    }
    
    /**
     * crée un tab pour chaque classe et l'ajoute dans le tabPane
     * @param list 
     */
    private void setTabs(){
        
        if(classes.isEmpty()){
            Label alert = new Label("Aucune classe définie");
            Hyperlink openMenu = new Hyperlink("Menu de gestion des classes");
            openMenu.setOnAction((event) -> {
                EventBusHandler.post(new OpenMenuEvent(Constants.ADMIN_CLASSE_VIEW));
            });
            Label indication = new Label(". Ajoutez une classe dans le ");
            TextFlow flow = new TextFlow(alert, indication, openMenu);
            BorderPane.setMargin(flow,new Insets(5,0,0,5));
            borderPane.setCenter(flow);
        } else {
            tabPane.getSelectionModel().selectedItemProperty().removeListener(selectionListener);
            Classe selectedClasse = getSelectedClasse();
            tabPane.getSelectionModel().clearSelection();
            tabPane.getTabs().clear();
            classes.stream().forEach((classe) -> {
                tabPane.getTabs().add(getTabOrCreateNew(classe));
            });
            if(selectedClasse != null){
                selectClasse(selectedClasse);
            }
            log("Loading tab content for classe "+getSelectedClasse());
            loadTabsContent();
            tabPane.getSelectionModel().selectedItemProperty().addListener(selectionListener);
            
            borderPane.setCenter(tabPane);
        }
    }
    
    private void loadTabsContent(){
        classes.stream().forEach((classe) -> {
            if(classe.getName().equals(getSelectedClasse().getName())){
                loadTabContent(classe);
            } else {
                unloadTabContent(classe);
            }
        });
            
    }
    /**
     * retourne le tab lié à la classe
     * @param classe
     * @return 
     */
    protected abstract ClasseTab getTabOrCreateNew(Classe classe);
    
    protected abstract void loadTabContent(Classe classe);
    
    protected abstract void unloadTabContent(Classe classe);
    
    /**
     * capte la sélection d'un tab, émet un event
     */
    protected void handleTabChangeEvent(){
        selectionListener = new ChangeListener<Tab>() {
            @Override
            public void changed(ObservableValue<? extends Tab> observable, Tab oldValue, Tab newValue) {
                EventBusHandler.post(new SelectClasseEvent(getSelectedClasse()));
            }
        };
        tabPane.getSelectionModel().selectedItemProperty().addListener(selectionListener);
    }
    
    
    /**
     * retourne la classe du tab sélectionné
     * @return 
     */
    private Classe getSelectedClasse(){
        ClasseTab selectedTab = (ClasseTab)tabPane.getSelectionModel().getSelectedItem();
        if(selectedTab == null){
            return null;
        }
        return (Classe) selectedTab.getClasse();
    }
    
    private void selectClasse(Classe classeToSelect){
        Tab tabToSelect = getTabOrCreateNew(classeToSelect);
        if(tabPane.getTabs().contains(tabToSelect)){
            tabPane.getSelectionModel().select(tabToSelect);
        }
    }
    
    @Subscribe
    public void onSelectClasseEvent(SelectClasseEvent event){
        logEvent(event);
        Classe classeToSelect = event.getClasse();
        Classe currentClasse = getSelectedClasse();
        if(classeToSelect != null && currentClasse != classeToSelect){
            selectClasse(classeToSelect);
        }
        loadTabsContent();
    }
    
    @Override
    public void reload(){
        super.reload();
        ClasseTab selectedTab = (ClasseTab)tabPane.getSelectionModel().getSelectedItem();
        if(selectedTab != null){
            selectedTab.getCtrl().reload();
        }
    }
}
