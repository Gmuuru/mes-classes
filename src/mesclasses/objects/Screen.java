/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mesclasses.objects;

import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import mesclasses.MainApp;
import mesclasses.controller.PageController;
import mesclasses.handlers.EventBusHandler;
import mesclasses.util.AppLogger;

/**
 *
 * @author rrrt3491
 */
public class Screen {
    
    private String view;
    
    private Pane root;
    
    private PageController ctrl;
    
    public Screen(String view, Stage stage){
        this.view = view;
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(MainApp.class.getResource(view));
        
        ctrl = null;
        try {
            root = loader.load();
            ctrl = loader.getController();
            ctrl.setPrimaryStage(stage);
        } catch (IOException ex) {
            AppLogger.log(ex);
        }
    }

    public String getView() {
        return view;
    }

    public Pane getRoot() {
        return root;
    }

    public PageController getCtrl() {
        return ctrl;
    }
    
    public void stop(){
        root.setManaged(false);
        root.setVisible(false);
        EventBusHandler.unregister(ctrl);
    }
}
