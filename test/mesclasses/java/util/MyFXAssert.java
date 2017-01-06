/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mesclasses.java.util;

import java.util.List;
import java.util.stream.Collectors;
import javafx.scene.Node;
import javafx.scene.control.Labeled;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import junit.framework.AssertionFailedError;
import mesclasses.controller.BasicController;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import static org.testfx.api.FxAssert.verifyThat;
import org.testfx.api.FxAssertContext;
import org.testfx.matcher.base.NodeMatchers;
import org.testfx.service.finder.NodeFinder;
import org.testfx.service.query.NodeQuery;

/**
 *
 * @author rrrt3491
 */
public class MyFXAssert {
    
    private static final Logger LOG = LogManager.getLogger(MyFXAssert.class);
    private static FxAssertContext context;

    public static void assertNotExists(String query){
        verifyThat(query, NodeMatchers.isNull());
    }
    
    public static void assertExists(String query){
        verifyThat(query, NodeMatchers.isNotNull());
    }
    
    public static void assertNotVisible(String query){
        verifyThat(query, NodeMatchers.isNotNull());
        verifyThat(query, NodeMatchers.isInvisible());
    }
    
    public static void assertVisible(String query){
        verifyThat(query, NodeMatchers.isNotNull());
        verifyThat(query, NodeMatchers.isVisible());
    }
    
    public static void assertVisible(Node node){
        verifyThat(node, NodeMatchers.isNotNull());
        verifyThat(node, NodeMatchers.isVisible());
    }
    
    public static void assertVisibleAndText(String query, String text){
        assertVisible(query);
        assertText(query, text);
    }
    
    public static void assertVisibleAndText(Node node, String text){
        assertVisible(node);
        assertText(node, text);
    }
    
    public static void assertEnabled(String query){
        verifyThat(query, NodeMatchers.isNotNull());
        verifyThat(query, NodeMatchers.isEnabled());
        
    }
    public static void assertDisabled(String query){
        verifyThat(query, NodeMatchers.isNotNull());
        verifyThat(query, NodeMatchers.isDisabled());
    }
    
    public static void assertCss(String query, String cssClass){
        verifyThat(query, NodeMatchers.isNotNull());
        assertCss(toNode(query), cssClass);
    }
    
    public static void assertCss(Node node, String cssClass){
        verifyThat(node, NodeMatchers.isNotNull());
        MyAssert.assertContains(node.getStyleClass(), cssClass);
    }
    
    public static void assertFieldInError(String query){
        verifyThat(query, NodeMatchers.isNotNull());
        assertFieldInError(toNode(query));
    }
    
    public static void assertFieldInError(Node node){
        verifyThat(node, NodeMatchers.isNotNull());
        MyAssert.assertContains(node.getStyleClass(), BasicController.ERROR_CLASS);
    }
    
    public static void assertFieldOK(Node node){
        verifyThat(node, NodeMatchers.isNotNull());
        MyAssert.assertNotContains(node.getStyleClass(), BasicController.ERROR_CLASS);
    }
    
    public static void assertText(String query, String text){
        assertText(toNode(query), text);
    }
    
    public static void assertText(Node node, String text){
        try {
            verifyThat(node, NodeMatchers.hasText(text)); 
        } catch(AssertionError e) {
            if(node == null || !hasText(node)){
                throw e;
            }
            throw new AssertionFailedError("Expecting ["+text+"] but found ["+
                getText(node)+"]");
        }
    }
    
    public static void assertTextFlow(Node node, String text){
        Assert.assertTrue(node instanceof TextFlow);
        Assert.assertEquals(text, getText(node));
    }
    
    private static <T extends Node> T toNode(String nodeQuery) {
        NodeFinder nodeFinder = assertContext().getNodeFinder();
        return toNode(nodeFinder.lookup(nodeQuery));
    }

    private static <T extends Node> T toNode(NodeQuery nodeQuery) {
        return nodeQuery.query();
    }
    
    public static FxAssertContext assertContext() {
        if (context == null) {
            context = new FxAssertContext();
        }
        return context;
    }
    
    private static boolean hasText(Node node){
        boolean res =  node instanceof Labeled ||
                node instanceof TextFlow;
        return res;
    }
    
    private static String getText(Node node){
        if(node instanceof Labeled){
            return ((Labeled) node).getText();
        }
        if(node instanceof Text){
            return ((Text) node).getText();
        }
        if(node instanceof TextFlow){
            List<String> list = ((TextFlow) node).getChildren().stream().map(c -> getText(c)).collect(Collectors.toList());
            return StringUtils.join(list, "");
        }
        return "";
    }
}
