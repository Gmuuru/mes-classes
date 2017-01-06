/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mesclasses.java.util;

import java.util.ArrayList;
import java.util.List;
import javafx.scene.Node;
import org.junit.Assert;
import org.smartgrid.SmartGrid;
import org.smartgrid.elements.HeaderLabel;

/**
 *
 * @author rrrt3491
 */
public class SmartGridAssert {
    
    public static void assertEmpty(SmartGrid grid){
        assertSize(grid, 0);
    }
    
    public static void assertSize(SmartGrid grid, int size){
        if(grid == null){
            Assert.fail("La grid est nulle");
            return;
        }
        MyAssert.assertSize(getChildrenWithoutHeaders(grid), size);
    }
    
    public static void assertCols(SmartGrid grid, int nbCols){
        Assert.assertEquals(nbCols, grid.getGridWidth());
    }
    
    public static void assertDataRows(SmartGrid grid, int nbRows){
        Assert.assertEquals(nbRows, grid.getGridHeight() - grid.getNbHeaderRows());
    }
    
    private static List<Node> getChildrenWithoutHeaders(SmartGrid grid){
        if(grid == null){
            return new ArrayList<>();
        }
        return grid.getChildren().filtered(c -> c != null  && !(c instanceof HeaderLabel));
    }
}
