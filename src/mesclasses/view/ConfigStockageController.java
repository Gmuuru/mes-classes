/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mesclasses.view;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Hyperlink;
import javafx.stage.DirectoryChooser;
import mesclasses.controller.PageController;
import mesclasses.model.FileConfigurationManager;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * FXML Controller class
 *
 * @author rrrt3491
 */
public class ConfigStockageController extends PageController implements Initializable {

    private static final Logger LOG = LogManager.getLogger(AccueilController.class);
    /**
     * Initializes the controller class.
     */

    @FXML
    Hyperlink rootDirLabel;
    @FXML
    Hyperlink saveDirLabel;
    @FXML
    Hyperlink docDirLabel;
    @FXML
    Hyperlink bckDirLabel;
    @FXML
    Hyperlink logDirLabel;
    @FXML
    Hyperlink archiveDirLabel;
    @FXML
    Hyperlink propertiesDirLabel;

    private final FileConfigurationManager conf = FileConfigurationManager.getInstance();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        name = "Config Stockage Ctrl";
        super.initialize(url, rb);
        init();
    }

    private void init() {
        rootDirLabel.setText(new File(conf.getRootDir()).getParent());
        saveDirLabel.setText(conf.getSaveDir());
        docDirLabel.setText(conf.getUploadDir());
        bckDirLabel.setText(conf.getBackupDir());
        logDirLabel.setText(conf.getLogDir());
        archiveDirLabel.setText(conf.getArchivesDir());
        propertiesDirLabel.setText(conf.getPropertiesDir());
    }

    @FXML
    void changeRoot() {
        File source = new File(conf.getRootDir()).getParentFile();
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Choix du répertoire de sauvegarde");
        chooser.setInitialDirectory(source);

        File dir = chooser.showDialog(primaryStage);
        if (dir != null && !dir.getAbsolutePath().equals(source.getAbsolutePath())) {
            LOG.info("Nouveau chemin de stockage saisi : " + dir);
            try {
                LOG.info("Copie des fichiers vers " + dir);
                FileUtils.copyDirectory(source, dir);
            } catch (IOException e) {
                notif("Impossible de déplacer les fichiers du répertoire " + conf.getRootDir() + " : " + e.getMessage());
                return;
            }
            FileConfigurationManager.setCustomStoragePath(dir.getAbsolutePath());
            try {
                LOG.info("Suppression de " + source);
                FileUtils.deleteDirectory(source);
            } catch (IOException e) {
                notif("Impossible de supprimer les fichiers du répertoire " + source + " : " + e.getMessage());
            }
            init();
        }
    }

    @FXML
    void openRoot() {
        openDir(conf.getRootDir());
    }

    @FXML
    void openFile() {
        openDir(conf.getSaveDir());
    }

    @FXML
    void openDoc() {
        openDir(conf.getUploadDir());
    }

    @FXML
    void openBck() {
        openDir(conf.getBackupDir());
    }

    @FXML
    void openLog() {
        openDir(conf.getLogDir());
    }

    @FXML
    void openArchive() {
        openDir(conf.getArchivesDir());
    }

    @FXML
    void openProps() {
        openDir(conf.getPropertiesDir());
    }

    private void openDir(String path) {
        try {
            Desktop.getDesktop().open(new File(path));
        } catch (IOException ex) {
            LOG.error("Ouverture du répertoire : " + path, ex);
        }
    }

}
