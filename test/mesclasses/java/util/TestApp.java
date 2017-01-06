/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mesclasses.java.util;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 *
 * @author rrrt3491
 */
public class TestApp extends Application {

    private Stage primaryStage;
    
    @Override
    public void start(Stage primaryStage) throws Exception {
       this.primaryStage = primaryStage;
    }
    
    public static void main(String[] args) {
        launch(args);
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }
}
