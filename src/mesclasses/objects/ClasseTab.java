/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mesclasses.objects;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import mesclasses.MainApp;
import mesclasses.controller.TabContentController;
import mesclasses.model.Classe;
import mesclasses.util.CssUtil;
import mesclasses.view.RootLayoutController;

/**
 *
 * @author rrrt3491
 */
public class ClasseTab extends Tab {
    
    private Classe classe;
    private TabContentController ctrl;
    
    public ClasseTab(Classe classe){
        this.classe = classe;
        this.textProperty().bind(classe.nameProperty());
        if(classe.isPrincipale()){
            CssUtil.addClass(this, "tab-principale");
        }
        classe.principaleProperty().addListener((observable, oldValue, newValue ) -> {
            if(newValue){
                CssUtil.addClass(this, "tab-principale");
            } else {
                CssUtil.removeClass(this, "tab-principale");
            }
        });
    }
    
    public void setContent(String view, Stage stage){
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(MainApp.class.getResource(view));
        try {
            Pane pane = loader.load();
            ctrl = loader.getController();
            ctrl.setPrimaryStage(stage);
            ctrl.setClasse(classe);
            setContent(pane);
        } catch (IOException ex) {
            Logger.getLogger(RootLayoutController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Classe getClasse() {
        return classe;
    }

    public void setClasse(Classe classe) {
        this.classe = classe;
    }

    public TabContentController getCtrl() {
        return ctrl;
    }

    public void setCtrl(TabContentController ctrl) {
        this.ctrl = ctrl;
    }
    
    
}
