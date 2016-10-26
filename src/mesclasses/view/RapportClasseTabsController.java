/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mesclasses.view;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import mesclasses.controller.AbstractClasseTabsController;
import mesclasses.model.Classe;
import mesclasses.model.Constants;
import mesclasses.objects.ClasseTab;

/**
 * FXML Controller class
 *
 * @author rrrt3491
 */
public class RapportClasseTabsController extends AbstractClasseTabsController {

    @FXML BorderPane borderPane;
    
    @FXML TabPane tabPane;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        name = ("Rapport Classe Tabs Ctrl");
        super.initialize(url, rb);
    }  
    

    @Override
    protected ClasseTab getTabOrCreateNew(Classe classe) {
        if(classe.getRapportTab() == null){
            ClasseTab tab = new ClasseTab(classe);
            classe.setRapportTab(tab);
        }
        return classe.getRapportTab();
    }
    
    @Override
    protected void loadTabContent(Classe classe){
        classe.getRapportTab().setContent(Constants.CLASSE_RAPPORT_VIEW, primaryStage);
    }
    
    @Override
    protected void unloadTabContent(Classe classe){
        classe.getRapportTab().setContent(null);
    }
}
