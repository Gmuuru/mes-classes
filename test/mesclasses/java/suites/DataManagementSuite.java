/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mesclasses.java.suites;

import mesclasses.java.donnees.TestLoadAndSave;
import mesclasses.java.donnees.TestMigrationToJournees;
import mesclasses.java.donnees.TestValidators;
import mesclasses.java.util.TestApp;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
    TestMigrationToJournees.class,
    TestLoadAndSave.class,
    TestValidators.class
})

public class DataManagementSuite {
    

    @BeforeClass
    public static void setup() throws Exception {
        ApplicationTest.launch(TestApp.class);
    }
    
    @AfterClass
    public static void afterAllTests() throws Exception {
        FxToolkit.hideStage();
    }
}