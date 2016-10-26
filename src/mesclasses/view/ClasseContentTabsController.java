/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mesclasses.view;

import com.google.common.eventbus.Subscribe;
import java.net.URL;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import mesclasses.controller.AbstractClasseTabsController;
import mesclasses.model.Classe;
import mesclasses.model.Constants;
import mesclasses.objects.ClasseTab;
import mesclasses.objects.events.SelectDateEvent;

/**
 * FXML Controller class
 *
 * @author rrrt3491
 */
public class ClasseContentTabsController extends AbstractClasseTabsController {

    @FXML BorderPane borderPane;
    
    @FXML TabPane tabPane;
    
    private LocalDate currentDate;
    
    {
        if(LocalDate.now().getDayOfWeek() == DayOfWeek.SUNDAY){
            currentDate = LocalDate.now().plusDays(1);
        } else {
            currentDate = LocalDate.now();
        }  
    }
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        name = ("Classe Content Tabs Ctrl");
        super.initialize(url, rb);if(LocalDate.now().getDayOfWeek() == DayOfWeek.SUNDAY){
            currentDate = LocalDate.now().plusDays(1);
        } else {
            currentDate = LocalDate.now();
        }  
    }  
    
    @Override
    protected ClasseTab getTabOrCreateNew(Classe classe) {
        if(classe.getContentTab() == null){
            ClasseTab tab = new ClasseTab(classe);
            classe.setContentTab(tab);
        }
        return classe.getContentTab();
    }
    
    @Override
    protected void loadTabContent(Classe classe){
        
        classe.getContentTab().setContent(Constants.CLASSE_CONTENT_VIEW, primaryStage);
        classe.getContentTab().getCtrl().setCurrentDate(this.currentDate);
    }
    
    @Override
    protected void unloadTabContent(Classe classe){
        classe.getContentTab().setContent(null);
    }
    
    @Subscribe
    public void onSelectDateEvent(SelectDateEvent event){
        this.currentDate = event.getDate();
    }
}
