/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mesclasses.java.donnees;

import java.io.File;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import mesclasses.handlers.ModelHandler;
import static mesclasses.java.util.LogUtil.logEnd;
import static mesclasses.java.util.LogUtil.logStart;
import static mesclasses.java.util.MyAssert.assertEmpty;
import static mesclasses.java.util.MyAssert.assertNoNulls;
import static mesclasses.java.util.MyAssert.assertSize;
import mesclasses.model.datamodel.ObservableData;
import mesclasses.util.DataLoadUtil;
import mesclasses.util.validation.FError;
import mesclasses.util.validation.FErrorBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Test;
import static mesclasses.java.util.MyAssert.assertEmpty;
import static mesclasses.java.util.MyAssert.assertNoNulls;
import static mesclasses.java.util.MyAssert.assertSize;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static mesclasses.java.util.MyAssert.assertEmpty;
import static mesclasses.java.util.MyAssert.assertNoNulls;
import static mesclasses.java.util.MyAssert.assertSize;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static mesclasses.java.util.MyAssert.assertEmpty;
import static mesclasses.java.util.MyAssert.assertNoNulls;
import static mesclasses.java.util.MyAssert.assertSize;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 *
 * @author rrrt3491
 */
public class TestLoadAndSave extends GenericAppTestBase {

    private static final Logger LOG = LogManager.getLogger(TestMigrationToJournees.class);
    private static final File FILE_FOR_SAVE_TEST = new File(SAVE_DIR+File.separator+"mesClassesData_full_save_test.xml");
    
    @Override
    @After
    public void afterEachTest() throws Exception {
        super.afterEachTest();
        if(FILE_FOR_SAVE_TEST.exists()){
            FILE_FOR_SAVE_TEST.delete();
        }
    }
    
    @Test
    public void LoadSaveTest(){
        ObservableData sourceData = loadDonnees();
        
        checkDonnees(sourceData);
        
        saveDonnees(sourceData);
        
    }
    
    public ObservableData loadDonnees(){
        
        logStart();
        try {
            ObservableData sourceData = null;

            try {
                LOG.info("Save file : "+SAVE_FILE.getPath());
                sourceData = DataLoadUtil.initializeData(SAVE_FILE);
            } catch (Exception e){
                LOG.error("Impossible de lire les données source", e);
                fail("Impossible de lire les données source");
            }
            assertNotNull("les données devraient être chargées", sourceData);
            List<FError> err = sourceData.validate();
            if(!err.isEmpty()){
                FErrorBuilder.showListInConsole(err);
            }
            assertEmpty(err);
            return sourceData;
        } finally {
            logEnd();
        }
    }
    
    public void checkDonnees(ObservableData data){
        ModelHandler model = ModelHandler.getInstance();
        model.injectData(data);
        
        //PRECHECKS
        assertSize(data.getTrimestres(), 3);
        assertNoNulls(data.getTrimestres());
        assertSize(data.getCours(), 17);
        assertNoNulls(data.getCours());
        assertSize(data.getClasses(), 4);
        assertNoNulls(data.getClasses());
        assertSize(data.getJournees(), 112);
        assertSize(model.getClasses().get(0).getEleves(), 28);
        assertSize(model.getClasses().get(1).getEleves(), 28);
        assertSize(model.getClasses().get(2).getEleves(), 28);
        assertSize(model.getClasses().get(3).getEleves(), 27);
    }
    
    public void saveDonnees(ObservableData data){
        
        logStart();
        try {
        
            ObservableData savedData = null;
            ModelHandler model = ModelHandler.getInstance();
            model.cleanupClasses();
            model.cleanUpJournees();
            List<FError> err = model.getData().validate();
            if(!err.isEmpty()){
                FErrorBuilder.showListInConsole(err);
            }
            assertEmpty(err);
            DataLoadUtil.writeData(model.getData(), FILE_FOR_SAVE_TEST);
            assertTrue(FILE_FOR_SAVE_TEST.exists());
            try {
                savedData = DataLoadUtil.initializeData(FILE_FOR_SAVE_TEST);
            } catch (Exception e){
                LOG.error("Impossible de lire les données sauvegardées", e);
                fail("Impossible de lire les données sauvegardées");
            }
            assertNotNull(savedData);
            assertSize(data.getTrimestres(), 3);
            assertNoNulls(data.getTrimestres());
            assertSize(data.getCours(), 17);
            assertNoNulls(data.getCours());
            assertSize(data.getClasses(), 4);
            assertNoNulls(data.getClasses());
            long days = ChronoUnit.DAYS.between(data.getTrimestres().get(0).getStartAsDate(), LocalDate.now().plusDays(1));
            assertSize(data.getJournees(), 112);
        
        } finally {
            logEnd();
        }
    }
    
}
