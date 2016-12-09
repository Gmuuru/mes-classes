/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mesclasses.java.donnees;

import mesclasses.handlers.ModelHandler;
import mesclasses.java.AbstractTest;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author rrrt3491
 */
public class LoadSaveDonneesTest extends AbstractTest {
    
    /**
     *  on récupère un fichier de données plein
     */
    protected final String SAVE_FILE = "mesClassesData_full";
    
    @Test
    public void LoadSaveDonneesTest(){
        loadDonneesTest();
        saveDonneesTest();
    }
    
    public void loadDonneesTest(){
        //app started, so loading should be fine :-)
        ModelHandler model = ModelHandler.getInstance();
        Assert.assertNotNull("les données devraient être chargées", model.getData());
        Assert.assertEquals("il devrait y avoir 3 trimestres", 3, model.getTrimestres().size());
    }
    
    public void saveDonneesTest(){
    
    }
    
}
