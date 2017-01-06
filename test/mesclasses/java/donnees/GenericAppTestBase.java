/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mesclasses.java.donnees;

import java.io.File;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import mesclasses.handlers.PropertiesCache;
import org.junit.After;
import org.junit.Before;
import org.testfx.framework.junit.ApplicationTest;

/**
 *
 * @author rrrt3491
 */
public abstract class GenericAppTestBase extends ApplicationTest  {
    
    protected static final String SAVE_DIR = "test/mesclasses/resources/FullDataTest";
    protected static final File SAVE_FILE = new File(SAVE_DIR+File.separator+"mesClassesData_full.xml");
    protected static final File PROPERTIES_FILE = new File(SAVE_DIR+File.separator+"properties"+File.separator+"mesclasses.properties");
    
    @Before
    public void beforeEachTest() throws Exception {
        PropertiesCache.getInstance().load(PROPERTIES_FILE);
    }
    
    @Override
    public void start(Stage stage) throws Exception {
        stage.show();
    }
    
    @After
    public void afterEachTest() throws Exception {
        release(new KeyCode[]{});
        release(new MouseButton[]{});
    }
    
    
    
    public <T extends Node> T find(String query){
        return (T) lookup(query).queryAll().iterator().next();
    }
}
