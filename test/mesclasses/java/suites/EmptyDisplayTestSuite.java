/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mesclasses.java.suites;

import mesclasses.MainApp;
import mesclasses.java.display.pages.TestTrimestresEmpty;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
    //TestMenus.class,
    //TestAccueilEmpty.class
    TestTrimestresEmpty.class
})

public class EmptyDisplayTestSuite {
    
    protected static String SAVE_DIR = "test/mesclasses/resources/EmptyDataTest";
    protected static String SAVE_FILE = "mesClassesData_empty";
    
  // the class remains empty,
  // used only as a holder for the above annotations
    @BeforeClass
    public static void setup() throws Exception {
        ApplicationTest.launch(MainApp.class, SAVE_DIR, SAVE_FILE);
    }
    
    @AfterClass
    public static void afterAllTests() throws Exception {
        FxToolkit.hideStage();
    }
}