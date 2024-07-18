package PriorityPilot;

import Task.Task;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class TaskController {
    private Stage stage;
    private static Task selectedTask;
    
    @FXML private Label taskLabel = new Label();
    @FXML private Label taskDescLabel = new Label();
    @FXML private Label dueDate = new Label();
    
    public void initTask(Task task)
    {
        selectedTask = task;
        this.taskLabel.setText(selectedTask.getName());
        this.taskDescLabel.setText(selectedTask.getDescription());
        this.dueDate.setText(selectedTask.getDueDate());
    }
    
    public void switchToListView(ActionEvent event) throws IOException
    {
        try{
            Parent root = FXMLLoader.load(getClass().getResource("ListView.fxml"));
            stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } 
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }
        
}
