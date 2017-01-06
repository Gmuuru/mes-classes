/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mesclasses.java.display.pages;

import javafx.scene.layout.VBox;
import mesclasses.java.display.MesClassesTestBase;
import mesclasses.java.donnees.Ids;
import mesclasses.java.util.MyAssert;
import static mesclasses.java.util.MyFXAssert.*;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author rrrt3491
 */
public class TestAccueilFull extends MesClassesTestBase {
    
    @Before
    public void beforeEachTest() {
        back();
    }
    
    @Test
    public void testBlocClasses(){
        assertVisibleAndText(Ids.ACCUEIL_CLASSES, "4 classes présentes. ");
        assertVisibleAndText(Ids.ACCUEIL_CLASSES_LINK, "Accédez aux séances d'aujourd'hui");
        assertVisibleAndText(Ids.ACCUEIL_CLASSES_OU, "ou");
        assertVisibleAndText(Ids.ACCUEIL_CLASSES_NEW, "Ajoutez des classes");
        assertVisible(Ids.ACCUEIL_CLASSES_BOX);
        VBox classesBox = (VBox)find(Ids.ACCUEIL_CLASSES_BOX);
        MyAssert.assertSize(classesBox.getChildren(), 4);
        assertTextFlow(classesBox.getChildren().get(0), "6°1 : 28 élèves.");
        assertTextFlow(classesBox.getChildren().get(1), "6°3 : 28 élèves.");
        assertTextFlow(classesBox.getChildren().get(2), "6°4 : 28 élèves.");
        assertTextFlow(classesBox.getChildren().get(3), "5°1 : 27 élèves.");
        clickOn(Ids.ACCUEIL_CLASSES_NEW);
        assertVisible(Ids.CLASSES);
        back();
        clickOn(Ids.ACCUEIL_CLASSES_LINK);
        assertVisible(Ids.JOURNEES);
    }
    
    @Test
    public void testBlocEleves(){
        assertVisibleAndText(Ids.ACCUEIL_ELEVES, "111 élèves présents. ");
        assertVisibleAndText(Ids.ACCUEIL_ELEVES_LINK, "Accédez aux rapports par élève");
        assertVisibleAndText(Ids.ACCUEIL_CLASSES_OU, "ou");
        assertVisibleAndText(Ids.ACCUEIL_ELEVES_NEW, "Ajoutez des élèves");
        assertVisibleAndText(Ids.ACCUEIL_PUNITIONS, "67 punitions.");
        clickOn(Ids.ACCUEIL_ELEVES_NEW);
        assertVisible(Ids.ELEVES);
        back();
        clickOn(Ids.ACCUEIL_ELEVES_LINK);
        assertVisible(Ids.RAPPORTS_TAB_CONTAINER);
        assertVisible(Ids.RAPPORTS_TAB_PANE);
        assertVisible(Ids.RAPPORT_CLASSE);
    }
    
    @Test
    public void testBlocCours(){
        assertVisibleAndText(Ids.ACCUEIL_COURS, "Nombre d'heures total : 15h. ");
        assertVisibleAndText(Ids.ACCUEIL_COURS_LINK, "Voir l'emploi du temps");
        VBox elevesBox = (VBox)find(Ids.ACCUEIL_COURS_BOX);
        MyAssert.assertSize(elevesBox.getChildren(), 4);
        assertTextFlow(elevesBox.getChildren().get(0), "6°1 : 4h");
        assertTextFlow(elevesBox.getChildren().get(1), "6°3 : 4h");
        assertTextFlow(elevesBox.getChildren().get(2), "6°4 : 4h");
        assertTextFlow(elevesBox.getChildren().get(3), "5°1 : 3h");
        clickOn(Ids.ACCUEIL_COURS_LINK);
        assertVisible(Ids.TIMETABLE);
    }
    
    @Test
    public void testBlocTrimestres(){
        assertVisibleAndText(Ids.ACCUEIL_TRIMESTRES, "3 trimestres. ");
        assertVisibleAndText(Ids.ACCUEIL_TRIMESTRES_LINK, "Voir les trimestres");
        VBox trimestresBox = (VBox)find(Ids.ACCUEIL_TRIMESTRES_BOX);
        MyAssert.assertSize(trimestresBox.getChildren(), 3);
        assertTextFlow(trimestresBox.getChildren().get(0), "T1 : du jeudi 01 septembre 2016 au vendredi 09 décembre 2016");
        assertTextFlow(trimestresBox.getChildren().get(1), "T2 : du samedi 10 décembre 2016 au vendredi 10 mars 2017");
        assertTextFlow(trimestresBox.getChildren().get(2), "T3 : du samedi 11 mars 2017 au samedi 08 juillet 2017");
        clickOn(Ids.ACCUEIL_TRIMESTRES_LINK);
        assertVisible(Ids.TRIMESTRES);
    }
    
    private void back(){
        clickOn(Ids.MENU_OUVRIR).clickOn(Ids.OUVRIR_HOME);
    }
}
