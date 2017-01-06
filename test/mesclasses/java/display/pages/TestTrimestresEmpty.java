/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mesclasses.java.display.pages;

import java.util.List;
import javafx.scene.Node;
import mesclasses.controller.BasicController;
import mesclasses.java.builders.TrimestreBuilder;
import mesclasses.java.display.MesClassesTestBase;
import mesclasses.java.donnees.Ids;
import static mesclasses.java.util.MyAssert.*;
import static mesclasses.java.util.MyFXAssert.*;
import static mesclasses.java.util.SmartGridAssert.*;
import mesclasses.model.Trimestre;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.smartgrid.SmartGrid;

/**
 *
 * @author rrrt3491
 */
public class TestTrimestresEmpty extends MesClassesTestBase {
    
    private static final Logger LOG = LogManager.getLogger(TestTrimestresEmpty.class);
    
    private SmartGrid grid;
    
    @Before
    public void beforeEachTest(){
        back();
        interact(() -> {
            grid.clear();
            model.getTrimestres().clear();
        });
    }
    
    @Test
    public void testAffichage(){
        assertVisible(Ids.TRIMESTRES);
        assertVisible(Ids.TRIMESTRES+" "+Ids.TRIMESTRE_GRID);
        assertVisible(Ids.TRIMESTRE_NEW);
        assertVisible(Ids.TRIMESTRE_ERRORS);
        assertEnabled(Ids.TRIMESTRE_NEW);
        
        assertSize(grid.getChildren(), 6);
        assertCols(grid, 6);
        assertDataRows(grid, 0);
    }
    
    @Test
    public void testNewTrimestre(){
        clickOn(Ids.TRIMESTRE_NEW);
        
        //creation d'un trimestre
        assertSize(model.getTrimestres(), 1);
        assertEmpty(model.getTrimestres().get(0).validate());
        List<Node> rowNodes = grid.getRow(1);
        assertSize(rowNodes, 4);
        
        //nom
        clickOn(rowNodes.get(0)).andClear();
        assertFieldInError(rowNodes.get(0));
        assertText(Ids.TRIMESTRE_ERRORS, BasicController.ERROR_MSG);
        clickOn(rowNodes.get(0)).write("Trimestre 1");
        assertFieldOK(rowNodes.get(0));
        assertText(Ids.TRIMESTRE_ERRORS, "");
        assertEquals("Trimestre 1", model.getTrimestres().get(0).getName());
        
        //start date
        clickOn(rowNodes.get(1)).andClear().clickOutside(rowNodes.get(1));
        assertFieldInError(rowNodes.get(1));
        assertText(Ids.TRIMESTRE_ERRORS, BasicController.ERROR_MSG);
        clickOn(rowNodes.get(1)).write("01/09/2016").clickOutside(rowNodes.get(1));
        assertFieldOK(rowNodes.get(1));
        assertEquals("2016-09-01", model.getTrimestres().get(0).getStart());
        assertText(Ids.TRIMESTRE_ERRORS, "");
        
        //end date
        clickOn(rowNodes.get(2)).andClear().clickOutside(rowNodes.get(2));
        assertFieldInError(rowNodes.get(2));
        assertText(Ids.TRIMESTRE_ERRORS, BasicController.ERROR_MSG);
        clickOn(rowNodes.get(2)).write("09/12/2016").clickOutside(rowNodes.get(2));
        assertEquals("2016-12-09", model.getTrimestres().get(0).getEnd());
        assertFieldOK(rowNodes.get(2));
        assertText(Ids.TRIMESTRE_ERRORS, "");
        
        assertEmpty(model.getTrimestres().get(0).validate());
        
    }
    
    @Test
    public void test_invalid_names(){
        clickOn(Ids.TRIMESTRE_NEW);
        clickOn(Ids.TRIMESTRE_NEW);
        List<Node> rowNodes = grid.getRow(2);
        assertSize(model.getTrimestres(), 2);
        
        clickOn(rowNodes.get(0)).andClear().write(model.getTrimestres().get(0).getName());
        assertFieldInError(rowNodes.get(0));
        assertText(Ids.TRIMESTRE_ERRORS, BasicController.ERROR_MSG);
        
        clickOn(rowNodes.get(0)).write(model.getTrimestres().get(0).getName()+" bis");
        assertFieldOK(rowNodes.get(0));
        assertText(Ids.TRIMESTRE_ERRORS, "");
    }
    
    public void test_dates_invalides_from_change(){
        Trimestre t1 = new TrimestreBuilder().name("t1").start("2016-12-02").end("2016-12-03").build();
        interact(() -> {
            model.getTrimestres().add(t1);
        });
        clickOn(Ids.TRIMESTRE_NEW);
        List<Node> rowNodes = grid.getRow(1);
        assertFieldOK(rowNodes.get(1));
        assertFieldOK(rowNodes.get(2));
        assertText(Ids.TRIMESTRE_ERRORS, "");
        clickOn(rowNodes.get(2)).andClear().write("01/12/2016").clickOutside(rowNodes.get(2));
        assertFieldInError(rowNodes.get(1));
        assertFieldInError(rowNodes.get(2));
        assertText(Ids.TRIMESTRE_ERRORS, BasicController.ERROR_MSG);
        clickOn(rowNodes.get(2)).andClear().write("03/12/2016").clickOutside(rowNodes.get(2));
        assertFieldOK(rowNodes.get(1));
        assertFieldOK(rowNodes.get(2));
        assertText(Ids.TRIMESTRE_ERRORS, "");
        
    }
    
    public void test_dates_invalides_from_start(){
        clickOn(Ids.TRIMESTRE_NEW);
        List<Node> rowNodes = grid.getRow(1);
        
        //start date
        clickOn(rowNodes.get(1)).andClear().write("02/09/2016").clickOutside(rowNodes.get(1));
        clickOn(rowNodes.get(2)).andClear().write("01/09/2016").clickOutside(rowNodes.get(2));
        assertFieldInError(rowNodes.get(1));
        assertFieldInError(rowNodes.get(2));
        assertText(Ids.TRIMESTRE_ERRORS, BasicController.ERROR_MSG);
        
        clickOn(rowNodes.get(2)).andClear().write("03/09/2016").clickOutside(rowNodes.get(2));
        assertFieldOK(rowNodes.get(1));
        assertFieldOK(rowNodes.get(2));
        assertText(Ids.TRIMESTRE_ERRORS, "");
        
    }
    
    @Test
    public void test_dates_overlap_from_start(){
        Trimestre t1 = new TrimestreBuilder().name("t1").start("2016-09-01").end("2016-12-01").build();
        Trimestre t2 = new TrimestreBuilder().name("t2").start("2016-12-01").end("2016-12-31").build();
        Trimestre t3 = new TrimestreBuilder().name("t3").start("2017-01-01").end("2017-12-31").build();
        interact(() -> {
            model.getTrimestres().add(t1);
            model.getTrimestres().add(t2);
            model.getTrimestres().add(t3);
        });
        back();
        List<Node> rowNodes1 = grid.getRow(1);
        List<Node> rowNodes2 = grid.getRow(2);
        List<Node> rowNodes3 = grid.getRow(3);
        assertFieldInError(rowNodes1.get(1));
        assertFieldInError(rowNodes1.get(2));
        assertFieldInError(rowNodes2.get(1));
        assertFieldInError(rowNodes2.get(2));
        assertFieldOK(rowNodes3.get(1));
        assertFieldOK(rowNodes3.get(2));
        assertText(Ids.TRIMESTRE_ERRORS, BasicController.ERROR_MSG);
        
        clickOn(rowNodes2.get(1)).andClear().write("02/12/2016").clickOutside(rowNodes2.get(1));
        assertFieldOK(rowNodes1.get(1));
        assertFieldOK(rowNodes1.get(2));
        assertFieldOK(rowNodes2.get(1));
        assertFieldOK(rowNodes2.get(2));
        assertText(Ids.TRIMESTRE_ERRORS, "");
        
    }
    
    @Test
    public void test_dates_overlap_from_change(){
        Trimestre t1 = new TrimestreBuilder().name("t1").start("2016-09-01").end("2016-12-01").build();
        Trimestre t2 = new TrimestreBuilder().name("t2").start("2016-12-02").end("2016-12-31").build();
        Trimestre t3 = new TrimestreBuilder().name("t3").start("2017-01-01").end("2017-12-31").build();
        interact(() -> {
            model.getTrimestres().add(t1);
            model.getTrimestres().add(t2);
            model.getTrimestres().add(t3);
        });
        back();
        List<Node> rowNodes1 = grid.getRow(1);
        List<Node> rowNodes2 = grid.getRow(2);
        List<Node> rowNodes3 = grid.getRow(3);
        assertFieldOK(rowNodes1.get(1));
        assertFieldOK(rowNodes1.get(2));
        assertFieldOK(rowNodes2.get(1));
        assertFieldOK(rowNodes2.get(2));
        assertFieldOK(rowNodes3.get(1));
        assertFieldOK(rowNodes3.get(2));
        assertText(Ids.TRIMESTRE_ERRORS, "");
        
        clickOn(rowNodes2.get(1)).andClear().write("02/11/2016").clickOutside(rowNodes2.get(1));
        assertFieldInError(rowNodes1.get(1));
        assertFieldInError(rowNodes1.get(2));
        assertFieldInError(rowNodes2.get(1));
        assertFieldInError(rowNodes2.get(2));
        assertFieldOK(rowNodes3.get(1));
        assertFieldOK(rowNodes3.get(2));
        assertText(Ids.TRIMESTRE_ERRORS, BasicController.ERROR_MSG);
        
    }
    
    private void back(){
        clickOn(Ids.MENU_EDITER).clickOn(Ids.EDITER_TRIMESTRE);
        grid = (SmartGrid)find(Ids.TRIMESTRES+" "+Ids.TRIMESTRE_GRID);
    }
}
