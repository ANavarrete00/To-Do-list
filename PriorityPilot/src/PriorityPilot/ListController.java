package PriorityPilot;

import List.List;
import Task.Task;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ListController implements Initializable
{
    private Stage stage = new Stage();
    private Stage popupStage = new Stage();
    private Scene scene;
    private Scene popupScene;
    private ObservableList tasks = FXCollections.observableArrayList();
    
    private static dbManager dbm;
    private static ResultSet dbResult;
    private static List selectedList;
    private static ArrayList<Task> task = new ArrayList<>();
    
    @FXML private TextField taskName = new TextField();
    @FXML private Label taskNameCount = new Label();
    @FXML private DatePicker dueDate = new DatePicker();
    @FXML private TextArea taskDesc = new TextArea();
    @FXML private Label taskDescCount = new Label();
    @FXML private Label listNameLabel = new Label();
    @FXML private Button addTaskBtn = new Button();
    @FXML private ListView<Task> taskList = new ListView<>();
    
    //initializer for ListView.fxml
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        //validates a list was selected
        if(selectedList != null){
            try 
            {
                //connects and selects from task table in database
                initTaskdb();
            } catch (SQLException ex) 
            {
                Logger.getLogger(ListController.class.getName()).log(Level.SEVERE, null, ex);
            }
            //sets list label
            this.listNameLabel.setText(selectedList.getListName());
            //clears any items that are in the taskList ListView, then adds the updated tasks in the list
            this.taskList.getItems().clear();
            this.taskList.setItems(getTask());
        }
        
        //disable add task button on initial load of newTaskPopup.fxml
        addTaskBtn.setDisable(true);
        //Listener for TextField taskName to require input or have imput <= 45
        taskDesc.textProperty().addListener((observable, oldText, newText) -> {
            int descCount = newText.length();
            int nameCount = taskName.getText().length();
            
            //if statment to validate the task description is not over 200 characters
            if(descCount > 200)
            {
                addTaskBtn.setDisable(true);
                taskDescCount.setTextFill(Color.RED);
            }
            //validates task name length
            else if(nameCount <= 0 || nameCount > 45)
            {
                addTaskBtn.setDisable(true);
            }
            else
            {
                addTaskBtn.setDisable(false);
                taskDescCount.setTextFill(Color.BLACK);
            }
            //updates label with current character count in the taskDesc TextArea
            taskDescCount.setText("Description: " + descCount + "/200");
        });
        
        //Listener for TextField taskName to require input or have imput <= 45
        taskName.textProperty().addListener((obserable, oldText, newText) -> {
            int nameCount = newText.length();
            int descCount = taskDesc.getText().length();
            
            //if statment to validate the task name is between 0 and 46 charaters
            if(nameCount <= 0 || nameCount > 45)
            {
                addTaskBtn.setDisable(true);
                taskNameCount.setTextFill(Color.RED);
            }
            //valadates description length
            else if(descCount > 200)
            {
                addTaskBtn.setDisable(true);
            }
            else
            {
                addTaskBtn.setDisable(false);
                taskNameCount.setTextFill(Color.BLACK);
            }
            taskNameCount.setText("Task Name: " + nameCount + "/45");
        });
    }
    
    //This method initiates a List object that was selected in the MainMenu.fxml ListView,
    //populating it with the corresponding attributes.
    public void initList(List list) throws SQLException
    {
        selectedList = list;
        initTaskdb();
        //task.addAll(selectedList.getTask());
        this.listNameLabel.setText(selectedList.getListName());
        this.taskList.setItems(getTask());
    }
    
    public void initTaskdb() throws SQLException
    {
        dbm = new dbManager();
        dbResult = dbm.getResult("task");
        getdbTask();
        dbResult.close();
    }
    
    public void getdbTask() throws SQLException
    {
        task.clear();
        
        while(dbResult.next())
        {
            if(dbResult.getInt("listID") == selectedList.getListId())
            {
                //int id, String taskName, String description, int priority, String dueDate, boolean status
                String date;
                if(dbResult.getDate("dueDate") == null)
                {
                    date = "";
                }
                else
                {
                    date = dbResult.getDate("dueDate").toString();
                }
                task.add(new Task(dbResult.getInt("taskID"), dbResult.getString("taskName"), dbResult.getString("taskDesc"), dbResult.getInt("priority"), date, dbResult.getBoolean("status")));
            }
        }
    }
    
    //Method to return an ObservableList of the task ArrayList. Used to add task to ListView
    public ObservableList<Task> getTask()
    {
        this.tasks.clear();
        
        //for loop to add the selected lists' tasks to an ObservableList
        for(int i = 0; i < task.size(); i++)
        {
            this.tasks.add(task.get(i).toString());
        }
        return tasks;
    }
    
    //Creates the newTaskPopup.fxml window to add a new task to the list. This also binds the
    //new window to the ListView.fxml that loaded it.
    public void addTaskPopup(ActionEvent event) throws IOException
    {
        try{
            stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            Parent root1 = new FXMLLoader(getClass().getResource("newTaskPopup.fxml")).load();
            
            popupScene = new Scene(root1);
            popupStage.setTitle("Add a Task!");
            popupStage.initOwner(this.stage);
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.setScene(popupScene);
            popupStage.show();
        } 
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
        
    //adds the new task to the task array when addTask button is triggered
    public void addNewTask(ActionEvent event) throws IOException
    {
        try{
            //String changes if value in dueDate DatePicker is not null otherwise it will stay an empty string
            String DueDate = "";
            if(dueDate.getValue() != null)
            {
                DueDate = dueDate.getValue().toString();
            }
            
            dbm.InsertTask(selectedList.getListId(), taskName.getText(), taskDesc.getText(), task.size()+1, DueDate, true);
            dbResult = dbm.getResult("task");
            getdbTask();
            dbResult.close();

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("ListView.fxml"));
            Parent root = loader.load();


            popupStage = (Stage)((Node)event.getSource()).getScene().getWindow();
            popupStage.close();
            this.stage = (Stage) popupStage.getOwner();

            scene = new Scene(root);
            this.stage.setScene(scene);
            this.stage.show();
        } 
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
    //delete a selected task
    public void deleteTask(ActionEvent event) throws SQLException, IOException
    {
        //alert box to caution users from deleting a Task
        Alert taskAlert = new Alert(Alert.AlertType.CONFIRMATION);
        taskAlert.setTitle("Confirmation Dialog");
        taskAlert.setHeaderText("Deleting task '" + task.get(taskList.getSelectionModel().getSelectedIndex()).getName() + "'");
        taskAlert.setContentText("CAUTION: Are you ok with this?");
        Optional<ButtonType> result = taskAlert.showAndWait();
        
        //if the "OK" button is pressed, then the program continues with deletion
        if(result.get() == ButtonType.OK)
        {
            int id = task.get(taskList.getSelectionModel().getSelectedIndex()).getID();
            dbm.dbDrop("task", id);
            dbResult = dbm.getResult("task");
            getdbTask();
            dbResult.close();
        }
        
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("ListView.fxml"));
        Parent root = loader.load();
        
        scene = new Scene(root);
        this.stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        this.stage.setScene(scene);
        this.stage.show();
    }
    
    //Bound to Add Task button in ListView.fxml
    public void switchToListView(ActionEvent event) throws IOException
    {
        try{
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("ListView.fxml"));
            Parent root = loader.load();

            popupStage = (Stage)((Node)event.getSource()).getScene().getWindow();
            popupStage.close();
            this.stage = (Stage) popupStage.getOwner();

            scene = new Scene(root);
            this.stage.setScene(scene);
            this.stage.show();
        } catch(IOException e){
            e.printStackTrace();
        }
    }
    
    //This displays the main menu after the user activates the back button
    public void switchToMainMenu(ActionEvent event) throws IOException
    {
        try{
            Parent root = FXMLLoader.load(getClass().getResource("MainMenu.fxml"));
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
    
    //Creates the TaskView.fxml window to show the selected task in the list's ListView. 
    //This also binds the new window to the ListView.fxml that loaded it.
    public void switchToTaskView(MouseEvent event) throws IOException
    {
        if(event.getClickCount() == 2)
        {
            //This will change the scene displaying the selected lists' task
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("TaskView.fxml"));
            Parent root = loader.load();
            scene = new Scene(root);

            TaskController controller = loader.getController();
            controller.initTask(task.get(this.taskList.getSelectionModel().getSelectedIndex()));

            stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        }
    }
}
