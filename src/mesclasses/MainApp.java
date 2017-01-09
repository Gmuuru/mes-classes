/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mesclasses;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import mesclasses.handlers.PropertiesCache;
import mesclasses.model.Constants;
import mesclasses.model.FileConfigurationManager;
import mesclasses.objects.LoadWindow;
import mesclasses.objects.tasks.FetchDataTask;
import mesclasses.objects.tasks.MigrationTask;
import mesclasses.objects.tasks.ValidateConfigTask;
import mesclasses.util.AppLogger;
import mesclasses.util.NodeUtil;
import mesclasses.view.RootLayoutController;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author rrrt3491
 */
public class MainApp extends Application {

    private static final Logger LOG;

    private Stage primaryStage;
    private AnchorPane rootLayout;

    static {
        try {
            FileConfigurationManager.autoDetect();
        } catch (FileNotFoundException ex) {
            System.out.println("Impossible de gérer la configuration...");
        }
        LOG = LogManager.getLogger(MainApp.class);
    }

    @Override
    public void start(Stage primaryStage) throws InterruptedException {
        this.primaryStage = primaryStage;

        AppLogger.logStart(MainApp.class);
        handleParams();

        Platform.setImplicitExit(false);
        LoadWindow loading = new LoadWindow(this.primaryStage, new ValidateConfigTask(), new FetchDataTask(), new MigrationTask());
        loading.startAndWait();
        if (loading.isSuccessful()) {
            
            this.primaryStage.setTitle(Constants.APPLICATION_TITLE + " version " + PropertiesCache.version());
            this.primaryStage.getIcons().add(new Image(
                    MainApp.class.getResourceAsStream("/resources/package/windows/MesClasses.png")));
            MainApp.class.getResourceAsStream("/resources/fonts/fontawesome-webfont.ttf");
            
            initRootLayout();
        } else {
            AppLogger.logExit(MainApp.class);
            System.exit(0);
        }
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
            LOG.error(e);
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    private void handleParams() {
        if (getParameters() == null || getParameters().getRaw().isEmpty()) {
            LOG.info("no additional paramaters sent with application startup");
        } else {
            List<String> params = getParameters().getRaw();
            if (params.size() != 2) {
                LOG.error(STYLESHEET_CASPIAN);
                LOG.error(params.size() + " paramaters sent with application startup, which is wrong. Should be 2. Default conf used");
            }
            String dataDir = params.get(0);
            String dataFile = params.get(1);
            FileConfigurationManager.setUserHome(dataDir);
            FileConfigurationManager.setSaveFileName(dataFile);
        }
        LOG.info("**** CONFIGURATION ****");
        NodeUtil.console(FileConfigurationManager.getInstance());
        LOG.info("***********************");
    }

}
