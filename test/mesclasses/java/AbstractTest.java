/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mesclasses.java;

import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import mesclasses.MainApp;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;

/**
 *
 * @author rrrt3491
 */
public class AbstractTest extends ApplicationTest  {

    protected final String SAVE_DIR = "test/mesclasses/resources";
    protected final String SAVE_FILE = "mesClassesData_empty";
    
    @Override
    public void start(Stage stage) throws Exception {
        stage.show();
    }

    @Before
    public void setup() throws Exception {
        ApplicationTest.launch(MainApp.class, SAVE_DIR, SAVE_FILE);
    }
    
    @After
    public void afterEachTest() throws Exception {
        FxToolkit.hideStage();
        release(new KeyCode[]{});
        release(new MouseButton[]{});
    }
    
    @Test
    public void abstractTest(){
    }
    
    public <T extends Node> T find(String query){
        return (T) lookup(query).queryAll().iterator().next();
    }
    
}
