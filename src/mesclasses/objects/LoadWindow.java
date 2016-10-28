/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mesclasses.objects;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import mesclasses.objects.tasks.AppTask;
import mesclasses.objects.tasks.WaitTask;
import mesclasses.util.AppLogger;

/**
 *
 * @author rrrt3491
 */
public class LoadWindow {
    
    ProgressBar bar;
    VBox vbox;
    Stage dialogStage;
    List<LoadingService> services = new ArrayList<>();
    int currentService = -1;
    long minExecTime = 2000;
    long start;
 
    
    public LoadWindow(Stage stage, AppTask task){
        this(stage, Arrays.asList(new AppTask[]{task}));
    }
    
    public LoadWindow(Stage stage, AppTask... tasks){
        this(stage, Arrays.asList(tasks));
    }
    
    public LoadWindow(Stage stage, List<AppTask> tasks){
        tasks.forEach(t -> {
            services.add(new LoadingService(t));
        });
        dialogStage = new Stage();
        dialogStage.setTitle("Chargement");
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(stage);
        dialogStage.setScene(createloadingScene());
        
    }
    
    private void next(){
        currentService++;
        if(currentService >= services.size()){
            close();
            return;
        }
        services.get(currentService).start();
    }
    
    private Scene createloadingScene() {
        bar = new ProgressBar();
        vbox = new VBox();
        BorderPane p = new BorderPane();
        p.setCenter(bar);
        p.setTop(vbox);
        BorderPane.setAlignment(vbox, Pos.CENTER);
        return new Scene(p, 300, 150);        
    }
    
    public void startAndWait() {  
        start = System.currentTimeMillis();
        next();
        AppLogger.log("Loading window, waiting for close");
        dialogStage.showAndWait();
        
    }
    
    public void close(){
        long end = System.currentTimeMillis();
        AppLogger.log("Closing after "+(end - start)+"ms");
        if(end - start < minExecTime){
           temporize(minExecTime+start-end);
        } else {
            dialogStage.close();
        }
    }
    
    private void temporize(long millis){
        Service<Object> service = new Service(){
            @Override
            protected Task createTask() {
                return new WaitTask(millis);
            }
        };
        service.stateProperty().addListener(
                (ObservableValue<? extends Worker.State> o, Worker.State oldValue, Worker.State newValue) -> {
            switch (newValue) {
                case FAILED:
                case CANCELLED:
                    dialogStage.close();
                    break;
                    
                case SUCCEEDED:
                    next();
            }
        });
        service.start();
    }
    
    public void cancel(){
        services.get(currentService).cancel();
    }
    
    public void setProgress(Double progress) {
        bar.setProgress(progress);
    }
    
    public void addProgress(Double progress) {
        bar.setProgress(bar.getProgress()+progress);
    }
    
    
    class LoadingService extends Service<Object> {
        
        AppTask<Object> task;
        
        @Override
        public void start(){
            bar.progressProperty().bind(task.progressProperty());
            Label label = new Label(task.getName());
            label.setAlignment(Pos.TOP_CENTER);
            label.setMaxWidth(Double.MAX_VALUE);
            vbox.getChildren().add(label);
            super.start();
        }   
        
        LoadingService(AppTask<Object> task){
            this.task = task;
            this.stateProperty().addListener(
                (ObservableValue<? extends Worker.State> o, Worker.State oldValue, Worker.State newValue) -> {
                AppLogger.log("state = "+newValue);
                switch (newValue) {
                    case FAILED:
                    case CANCELLED:
                    case SUCCEEDED:
                        AppLogger.log("Task '"+task.getName()+"' closing");
                        close();
                        break;
                }
            });
        }
        
        @Override
        protected Task<Object> createTask() {
            return task;
        }
    }
}

