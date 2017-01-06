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
public class TestAccueilEmpty extends MesClassesTestBase {
    
    @Before
    public void beforeEachTest() {
        clickOn(Ids.MENU_OUVRIR).clickOn(Ids.OUVRIR_HOME);
    }
    
    @Test
    public void testBlocClasses(){
        assertVisibleAndText(Ids.ACCUEIL_CLASSES, "0 classes présentes. ");
        assertNotVisible(Ids.ACCUEIL_CLASSES_LINK);
        assertNotVisible(Ids.ACCUEIL_CLASSES_OU);
        assertVisibleAndText(Ids.ACCUEIL_CLASSES_NEW, "Ajoutez des classes");
        assertVisible(Ids.ACCUEIL_CLASSES_BOX);
        MyAssert.assertEmpty(((VBox)find(Ids.ACCUEIL_CLASSES_BOX)).getChildren());
        clickOn(Ids.ACCUEIL_CLASSES_NEW);
        assertVisible(Ids.CLASSES);
    }
    
    @Test
    public void testBlocEleves(){
        assertVisibleAndText(Ids.ACCUEIL_ELEVES, "0 élèves présents. ");
        assertNotVisible(Ids.ACCUEIL_ELEVES_LINK);
        assertNotVisible(Ids.ACCUEIL_ELEVES_OU);
        assertVisibleAndText(Ids.ACCUEIL_ELEVES_NEW, "Ajoutez des élèves");
        assertVisibleAndText(Ids.ACCUEIL_PUNITIONS, "0 punition.");
        clickOn(Ids.ACCUEIL_ELEVES_NEW);
        assertVisible(Ids.ELEVES);
    }
    
    @Test
    public void testBlocCours(){
        assertVisibleAndText(Ids.ACCUEIL_COURS, "Nombre d'heures total : 0h. ");
        assertVisibleAndText(Ids.ACCUEIL_COURS_LINK, "Voir l'emploi du temps");
        MyAssert.assertEmpty(((VBox)find(Ids.ACCUEIL_COURS_BOX)).getChildren());
        clickOn(Ids.ACCUEIL_COURS_LINK);
        assertVisible(Ids.TIMETABLE);
    }
    
    @Test
    public void testBlocTrimestres(){
        assertVisibleAndText(Ids.ACCUEIL_TRIMESTRES, "0 trimestres. ");
        assertVisibleAndText(Ids.ACCUEIL_TRIMESTRES_LINK, "Voir les trimestres");
        MyAssert.assertEmpty(((VBox)find(Ids.ACCUEIL_TRIMESTRES_BOX)).getChildren());
        clickOn(Ids.ACCUEIL_TRIMESTRES_LINK);
        assertVisible(Ids.TRIMESTRES);
    }
}
