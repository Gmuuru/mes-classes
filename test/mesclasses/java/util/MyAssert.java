/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mesclasses.java.util;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import mesclasses.util.validation.FError;
import org.junit.Assert;

/**
 *
 * @author rrrt3491
 */
public class MyAssert {
    
    //COLLECTIONS
    public static void assertNullOrEmpty(Collection<? extends Object> list){
        Assert.assertTrue(list == null || list.isEmpty());
    }
    
    public static void assertEmpty(Collection<? extends Object> list){
        Assert.assertNotNull(list);
        Assert.assertTrue(list.isEmpty());
    }
    
    public static void assertNotEmpty(Collection<? extends Object> c){
        Assert.assertNotNull(c);
        Assert.assertFalse(c.isEmpty());
    }
    
    public static void assertSize(Collection<? extends Object> list, int size){
        Assert.assertNotNull(list);
        Assert.assertEquals(size, list.size());
    }
    
    public static void assertNoNulls(Collection<? extends Object> list){
        Assert.assertNotNull(list);
        list.forEach(e -> Assert.assertNotNull(e));
    }
    
    //MAPS
    public static void assertNullOrEmpty(Map<? extends Object, ? extends Object> list){
        Assert.assertTrue(list == null || list.isEmpty());
    }
    
    public static void assertEmpty(Map<? extends Object, ? extends Object> list){
        Assert.assertNotNull(list);
        Assert.assertTrue(list.isEmpty());
    }
    
    public static void assertNotEmpty(Map<? extends Object, ? extends Object> m){
        Assert.assertNotNull(m);
        Assert.assertFalse(m.isEmpty());
    }
    
    public static void assertSize(Map<? extends Object, ? extends Object> map, int size){
        Assert.assertNotNull(map);
        Assert.assertEquals(size, map.size());
    }
    
    public static void assertSize(Map<? extends Object, ? extends Object> map, long size){
        Assert.assertNotNull(map);
        Assert.assertEquals(size, map.size());
    }
    
    public static void assertNoNulls(Map<? extends Object, ? extends Object> map){
        Assert.assertNotNull(map);
        map.entrySet().forEach(e -> {
            Assert.assertNotNull(e);
            Assert.assertNotNull(e.getKey());
            Assert.assertNotNull(e.getValue());
        });
    }
    
    
    // LISTS OF ERRORS
    public static void assertContainsSingleError(List<FError> list, String msg){
        if(msg == null){
            return;
        }
        assertNotEmpty(list);
        assertSize(list, 1);
        Assert.assertEquals(msg, list.get(0).getMessage());
    }
    
    public static void assertContainsError(List<FError> list, String msg){
        if(msg == null){
            return;
        }
        assertNotEmpty(list);
        for(FError error : list){
            if(msg.equals(error.getMessage())){
                return;
            }
        }
        Assert.fail("La liste ne contient pas l'erreur "+msg);
    }
    
    public static <T> void assertContains(List<T> list, T obj){
        if(list != null){
            Assert.assertTrue(list.contains(obj));
        }
    }
    
    public static <T> void assertNotContains(List<T> list, T obj){
        if(list != null){
            Assert.assertFalse(list.contains(obj));
        }
    }
}
