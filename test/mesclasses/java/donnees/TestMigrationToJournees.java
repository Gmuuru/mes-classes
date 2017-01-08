/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mesclasses.java.donnees;

import java.io.File;
import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.List;
import mesclasses.handlers.ModelHandler;
import mesclasses.handlers.PropertiesCache;
import static mesclasses.util.LogUtil.*;
import static mesclasses.java.util.MyAssert.*;
import mesclasses.model.datamodel.ObservableData;
import mesclasses.objects.tasks.MigrationTask;
import mesclasses.util.DataLoadUtil;
import mesclasses.util.validation.FError;
import mesclasses.util.validation.FErrorBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author rrrt3491
 */
public class TestMigrationToJournees extends GenericAppTestBase {

    private static final Logger LOG = LogManager.getLogger(TestMigrationToJournees.class);
    private static final String SAVE_DIR_MIGRATION = "test/mesclasses/resources/JourneeMigrationTest";
    private static final File SAVE_FILE_MIGRATION = new File(SAVE_DIR_MIGRATION+File.separator+"mesClassesData_full_sans_journees.xml");
    private static final File FILE_FOR_SAVE_TEST = new File(SAVE_DIR_MIGRATION+File.separator+"mesClassesData_full_save_test.xml");
    private static final File PROPERTIES_FILE_MIGRATION = 
            new File(SAVE_DIR_MIGRATION+File.separator+"properties"+File.separator+"mesclasses.properties");
    private final LocalDate date = LocalDate.of(2016, Month.DECEMBER, 7);
    
    
    @Override
    @Before
    public void beforeEachTest() throws Exception {
        PropertiesCache.getInstance().load(PROPERTIES_FILE_MIGRATION);
        if(FILE_FOR_SAVE_TEST.exists()){
            FILE_FOR_SAVE_TEST.delete();
        }
    }
    
    @Override
    @After
    public void afterEachTest() throws Exception {
        super.afterEachTest();
        if(FILE_FOR_SAVE_TEST.exists()){
            FILE_FOR_SAVE_TEST.delete();
        }
    }
    
    @Test
    public void LoadSaveMigrationTest() throws Exception {
        
        ObservableData sourceData = loadDonneesTest();
        
        checkDonneesTest(sourceData);
        
        saveDonneesTest(sourceData);
        
    }
    
    public ObservableData loadDonneesTest(){
        
        logStart();
        try {
            ObservableData sourceData = null;

            try {
                sourceData = DataLoadUtil.initializeData(SAVE_FILE_MIGRATION);
            } catch (Exception e){
                LOG.error("Impossible de lire les données source", e);
                fail("Impossible de lire les données source");
            }
            assertNotNull("les données devraient être chargées", sourceData);
            assertEmpty(sourceData.getJournees());
            List<FError> err = sourceData.validate();
            if(!err.isEmpty()){
                FErrorBuilder.showListInConsole(err);
            }
            assertSize(err, 580);

            return sourceData;
        } finally {
            logEnd();
        }
    }
    
    public void checkDonneesTest(ObservableData data){
        ModelHandler model = ModelHandler.getInstance();
        model.injectData(data);
        
        //PRECHECKS
        assertSize(data.getTrimestres(), 3);
        assertNoNulls(data.getTrimestres());
        assertSize(data.getCours(), 17);
        assertNoNulls(data.getCours());
        assertSize(data.getClasses(), 4);
        assertNoNulls(data.getClasses());
        assertEmpty(data.getJournees());
        assertSize(model.getClasses().get(0).getEleves(), 28);
        assertSize(model.getClasses().get(1).getEleves(), 28);
        assertSize(model.getClasses().get(2).getEleves(), 28);
        assertSize(model.getClasses().get(3).getEleves(), 27);
        
        // MIGRATION
        try {
            new MigrationTask().call();
        } catch (Exception ex) {
            LOG.error("Erreur pendant l'exécution de la tâche de migration", ex);
            fail("Erreur pendant l'exécution de la tâche de migration");
        }
    }
    
    public void saveDonneesTest(ObservableData data){
        
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
            err = savedData.validate();
            if(!err.isEmpty()){
                FErrorBuilder.showListInConsole(err);
            }
            assertEmpty(err);
            
            assertSize(savedData.getTrimestres(), 3);
            assertNoNulls(savedData.getTrimestres());
            assertSize(savedData.getCours(), 17);
            assertNoNulls(savedData.getCours());
            assertSize(savedData.getClasses(), 4);
            assertNoNulls(savedData.getClasses());
            assertNotNull(model.getJournee(LocalDate.now()));
            assertNotNull(model.getJournee(savedData.getTrimestres().get(0).getStartAsDate()));
            long days = ChronoUnit.DAYS.between(savedData.getTrimestres().get(0).getStartAsDate(), LocalDate.now().plusDays(1));
            assertSize(savedData.getJournees(), days);
        } finally {
            logEnd();
        }
    }
    
}
