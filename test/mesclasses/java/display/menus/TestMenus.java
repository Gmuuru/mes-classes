/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mesclasses.java.display.menus;

import mesclasses.java.display.MesClassesTestBase;
import mesclasses.java.donnees.Ids;
import static mesclasses.java.util.MyFXAssert.*;
import org.junit.Test;

/**
 *
 * @author rrrt3491
 */
public class TestMenus extends MesClassesTestBase {
    
    @Test
    public void defaultPageTest(){
        assertVisible(Ids.ACCUEIL_PANE);
        //TODO a enlever une fois la migration faite
        //Assert.assertFalse(model.getData().isChanged());
    }
    
    @Test
    public void MenuBarTest(){
        assertVisible(Ids.MENU_BAR);
        assertVisibleAndText(Ids.MENU_FICHIER, "Fichier");
        assertVisibleAndText(Ids.MENU_OUVRIR, "Ouvrir");
        assertVisibleAndText(Ids.MENU_EDITER, "Editer");
        assertVisibleAndText(Ids.MENU_OUTILS, "Outils");
    }
    
    @Test
    public void menuFichierTest(){
        assertVisible(Ids.MENU_FICHIER);
        assertNotExists(Ids.FICHIER_SAVE);
        clickOn(Ids.MENU_FICHIER);
        assertVisible(Ids.FICHIER_SAVE);
        assertVisible(Ids.FICHIER_LOAD);
        assertVisible(Ids.FICHIER_IMPORT);
        assertVisible(Ids.FICHIER_EXPORT);
        assertVisible(Ids.FICHIER_EXIT);
        clickOn(Ids.MENU_FICHIER);
    }
    
    @Test
    public void menuOuvrirTest(){
        assertVisible(Ids.MENU_OUVRIR);
        assertNotExists(Ids.OUVRIR_HOME);
        clickOn(Ids.MENU_OUVRIR);
        assertVisible(Ids.OUVRIR_HOME);
        assertVisible(Ids.OUVRIR_SEANCE);
        assertVisible(Ids.OUVRIR_OLD);
        assertVisible(Ids.OUVRIR_REPORTS);
        clickOn(Ids.MENU_OUVRIR);
    }
    
    @Test
    public void menuEditerTest(){
        assertVisible(Ids.MENU_EDITER);
        assertNotExists(Ids.EDITER_CLASSE);
        clickOn(Ids.MENU_EDITER);
        assertVisible(Ids.EDITER_TRIMESTRE);
        assertVisible(Ids.EDITER_CLASSE);
        assertVisible(Ids.EDITER_ELEVE);
        assertVisible(Ids.EDITER_EDT);
        clickOn(Ids.MENU_EDITER);
    }
    
    @Test
    public void menuOutilsTest(){
        assertVisible(Ids.MENU_OUTILS);
        assertNotExists(Ids.OUTILS_CHANGE);
        clickOn(Ids.MENU_OUTILS);
        assertVisible(Ids.OUTILS_CHANGE);
        assertVisible(Ids.OUTILS_CONFIG);
        assertVisible(Ids.OUTILS_RESET);
        clickOn(Ids.MENU_OUTILS);
    }
}
