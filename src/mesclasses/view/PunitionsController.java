/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mesclasses.view;

import com.google.common.eventbus.Subscribe;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import mesclasses.controller.PageController;
import mesclasses.handlers.EventBusHandler;
import mesclasses.model.Classe;
import mesclasses.model.Constants;
import mesclasses.objects.events.OpenMenuEvent;
import mesclasses.objects.events.SelectClasseEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.smartgrid.SmartGrid;

/**
 * FXML Controller class
 *
 * @author rrrt3491
 */
public class PunitionsController extends PageController implements Initializable {

    private static final Logger LOG = LogManager.getLogger(PunitionsController.class);

    @FXML
    private TabPane tabPane;
    @FXML
    private Tab punitionsTab;
    @FXML
    private Tab devoirsTab;
    @FXML
    private Tab motsTab;
    @FXML
    private Label titleLabel;
    @FXML
    private SmartGrid gridPunitionsEnCours;
    @FXML
    private SmartGrid gridPunitionsFermees;
    @FXML
    private SmartGrid gridDevoirsEnCours;
    @FXML
    private SmartGrid gridDevoirsFermes;
    @FXML
    private SmartGrid gridMotsEnCours;
    @FXML
    private SmartGrid gridMotsFermes;
    @FXML
    private Button displayOldDataBtn;

    private Classe classe;
    private DevoirManager devoirManager;
    private PunitionManager punitionManager;
    private MotManager motManager;

    public static final BooleanProperty INCLUDE_OLD_TRIMESTRES = new SimpleBooleanProperty(false);

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        name = "Punitions Ctrl";
        super.initialize(url, rb);
        initTabs();
        displayOldDataBtn.textProperty().bind(Bindings.when(INCLUDE_OLD_TRIMESTRES)
                .then("Exclure trimestres précédents")
                .otherwise("Inclure trimestres précédents")
        );
        displayOldDataBtn.setOnAction(e -> INCLUDE_OLD_TRIMESTRES.set(!INCLUDE_OLD_TRIMESTRES.get()));

    }

    private void initTabs() {
        initTab(punitionsTab);
        initTab(devoirsTab);
        initTab(motsTab);
        tabPane.getSelectionModel().clearAndSelect(0);
        selectTab(punitionsTab);
    }

    private void initTab(Tab tab) {
        unselectTab(tab);
        tab.selectedProperty().addListener((ob, o, n) -> {
            if (n) {
                selectTab(tab);
            } else {
                unselectTab(tab);
            }
        });
    }

    private void selectTab(Tab tab) {
        tab.getContent().setManaged(true);
        tab.getContent().setVisible(true);
    }

    private void unselectTab(Tab tab) {
        tab.getContent().setManaged(false);
        tab.getContent().setVisible(false);
    }

    public Classe getClasse() {
        return classe;
    }

    public void setClasse(Classe classe) {
        this.classe = classe;
        titleLabel.textProperty().bind(new SimpleStringProperty("Punitions, devoirs et mots pour la ").concat(this.classe.nameProperty()));
        init();
    }

    private void init() {
        devoirManager = new DevoirManager(classe, gridDevoirsEnCours, gridDevoirsFermes);
        punitionManager = new PunitionManager(classe, gridPunitionsEnCours, gridPunitionsFermees);
        motManager = new MotManager(classe, gridMotsEnCours, gridMotsFermes);
        devoirManager.init();
        punitionManager.init();
        motManager.init();
    }

    @Subscribe
    public void onSelectClasse(SelectClasseEvent event) {
        logEvent(event);
        if (event.getClasse() != null && event.getClasse() != classe) {
            setClasse(event.getClasse());
        }
    }

    @FXML
    public void openClasse() {
        EventBusHandler.post(new OpenMenuEvent(Constants.JOURNEE_VIEW));
    }

    @Override
    public void reload() {
        super.reload();
        if (classe == null) {
            return;
        }
        init();
    }
}
