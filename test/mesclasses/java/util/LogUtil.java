/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mesclasses.java.util;

import org.apache.logging.log4j.LogManager;

/**
 *
 * @author rrrt3491
 */
public class LogUtil {
    
    public static void logStart(){
        String clazz = getCallerClassName();
        String method = getCallerMethodName();
        LogManager.getLogger("").info("\n*** "+method+" START ***\n");
    }
    
    public static void logEnd(){
        String clazz = getCallerClassName();
        String method = getCallerMethodName();
        LogManager.getLogger("").info("\n*** "+method+" FINISH ***\n");
    }
    
    public static String getCallerClassName() { 
        StackTraceElement[] stElements = Thread.currentThread().getStackTrace();
        for (int i=1; i<stElements.length; i++) {
            StackTraceElement ste = stElements[i];
            if (!ste.getClassName().equals(LogUtil.class.getName()) && ste.getClassName().indexOf("java.lang.Thread")!=0) {
                return ste.getClassName();
            }
        }
        return null;
    }
    
    public static String getCallerMethodName() { 
        StackTraceElement[] stElements = Thread.currentThread().getStackTrace();
        for (int i=1; i<stElements.length; i++) {
            StackTraceElement ste = stElements[i];
            if (!ste.getClassName().equals(LogUtil.class.getName()) && ste.getClassName().indexOf("java.lang.Thread")!=0) {
                return ste.getMethodName();
            }
        }
        return null;
    }
}
