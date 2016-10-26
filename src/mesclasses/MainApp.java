/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mesclasses;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import mesclasses.model.Constants;
import mesclasses.view.RootLayoutController;

/**
 *
 * @author rrrt3491
 */
public class MainApp extends Application {
    
    private Stage primaryStage;
    private AnchorPane rootLayout;
     
    @Override
    
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle(Constants.APPLICATION_TITLE);
        this.primaryStage.getIcons().add(new Image(
            MainApp.class.getResourceAsStream( "/resources/package/windows/MesClasses.png" ))); 
        MainApp.class.getResourceAsStream("/resources/fonts/fontawesome-webfont.ttf");
        
        Platform.setImplicitExit(false);
        initRootLayout();
    }
    
    /**
     * initialise le root layout, qui contient uniquement la barre de menus
     */
    public void initRootLayout() {
        try {
            // Load root layout from fxml file.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/RootLayout.fxml"));
            rootLayout = loader.load();
            RootLayoutController rootController = loader.getController();
            rootController.setPrimaryStage(primaryStage);
            // Show the scene containing the root layout.
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            scene.getStylesheets().add(MainApp.class.getResource(Constants.DEFAULT_EVENT_CSS).toExternalForm());
            primaryStage.show();
        } catch (IOException e) {
            Logger.getLogger(RootLayoutController.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
