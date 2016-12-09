/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mesclasses.java.donnees;

import com.google.common.collect.Lists;
import java.util.List;
import mesclasses.java.builders.TrimestreBuilder;
import mesclasses.util.Validators;
import mesclasses.model.Trimestre;
import mesclasses.util.validation.FError;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author rrrt3491
 */
public class ValidatorsTest {
    
    @Test
    public void trimestreValidatorsTest(){
        Trimestre t = new TrimestreBuilder()
                .name("junit")
                .start("2016-09-01")
                .end("2016-12-31").build();
        
        //OK
        List<FError> e = Validators.validate(t);
        Assert.assertNull(e);
        
        // name null
        t.setName(null);
        e = Validators.validate(t);
        Assert.assertNotNull(e);
        Assert.assertEquals(1, e.size());
        
        // start = end 
        t.setName("junit");
        t.setStart("2016-09-01");
        t.setEnd("2016-09-01");
        e = Validators.validate(t);
        Assert.assertNull(e);
        
        // start > end 
        t.setStart("2016-09-02");
        t.setEnd("2016-09-01");
        e = Validators.validate(t);
        Assert.assertNotNull(e);
        Assert.assertEquals(1, e.size());
    }
    
    @Test
    public void trimestreListValidatorsTest(){
        List<Trimestre> list = Lists.newArrayList(
            new TrimestreBuilder().name("junit").start("2016-09-01").end("2016-12-31").build()  ,
            new TrimestreBuilder().name("junit").start("2016-09-01").end("2016-12-31").build()  ,
            new TrimestreBuilder().name("junit2").start("2016-09-01").end("2016-12-31").build() , 
            new TrimestreBuilder().name("junit2").start("2016-09-01").end("2016-12-31").build() ,
            new TrimestreBuilder().name("junit3").start("2016-09-01").end("2016-12-31").build()  
        );
        List<FError> e = Validators.validateTrimestreList(list);
        Assert.assertNotNull(e);
        Assert.assertEquals(2, e.size());
        
    }
}
