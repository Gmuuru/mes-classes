/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mesclasses.java.display;

import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import mesclasses.handlers.ModelHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.testfx.framework.junit.ApplicationTest;

/**
 *
 * @author rrrt3491
 */
public abstract class MesClassesTestBase extends ApplicationTest  {

    private static final Logger LOG = LogManager.getLogger(MesClassesTestBase.class);
    
    protected static String SAVE_DIR = "test/mesclasses/resources/EmptyDataTest";
    protected static String SAVE_FILE = "mesClassesData_empty";
    
    protected ModelHandler model = ModelHandler.getInstance();
    
    @Override
    public void start(Stage stage) throws Exception {
        stage.show();
    }
    
    @After
    public void afterEachTest() {
        release(new KeyCode[]{});
        release(new MouseButton[]{});
    }
    
    public <T extends Node> T find(String query){
        try {
            return (T) lookup(query).queryAll().iterator().next();
        } catch(Exception e){
            LOG.error("find "+query+" : ",e);
            return null;
        }
    }
    
    
    //UTILITIES
    @Override
    public MesClassesTestBase clickOn(Node node, MouseButton... buttons) {
        super.clickOn(node, buttons);
        return this;
    }
    
    @Override
    public MesClassesTestBase write(String text) {
        super.write(text);
        return this;
    }
    
    public MesClassesTestBase clickOutside(Node node){
        clickOn(node.localToScreen(node.getBoundsInLocal()).getMinX()+(node.getBoundsInLocal().getWidth()/2), 
                node.localToScreen(node.getBoundsInLocal()).getMaxY()+1);
        return this;
    }
    
    public MesClassesTestBase andClear(){
        press(KeyCode.CONTROL, KeyCode.A);
        release(KeyCode.CONTROL, KeyCode.A);
        press(KeyCode.BACK_SPACE);
        release(KeyCode.BACK_SPACE);
        return this;
    }
}
