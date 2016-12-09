/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mesclasses.java.menus;

import javafx.scene.Node;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.BorderPane;
import mesclasses.java.AbstractTest;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author rrrt3491
 */
public class MenuTest extends AbstractTest {
    
    private static final String MAIN_PANE_ID = "#mainPane";
    
    @Test
    public void MenuBarTest(){
        BorderPane main = find(MAIN_PANE_ID);
        Assert.assertNotNull(main);
        
        Node top = main.getTop();
        Assert.assertNotNull("La barre de menu devrait exister", top);
        Assert.assertTrue("La barre de menu devrait être une MenuBar", top instanceof MenuBar);
    }
}
