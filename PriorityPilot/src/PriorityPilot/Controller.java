package PriorityPilot;

import List.List;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class Controller implements Initializable
{
    private Stage stage = new Stage();
    private Stage popupStage = new Stage();
    private Scene scene;
    private Scene popupScene;
    
    private static Settings settings;
    private static dbManager dbm;
    private static ResultSet dbResult;
    private static ArrayList<List> list = new ArrayList<>();
    
    @FXML private Button saveSettings = new Button();
    @FXML private Button addListBtn = new Button();
    @FXML private ChoiceBox<String> freq = new ChoiceBox();
    @FXML private ChoiceBox<String> startOptions = new ChoiceBox();
    @FXML private ChoiceBox<String> endOptions = new ChoiceBox();
    @FXML private Label listNameCount = new Label();
    @FXML private ListView<List> listView = new ListView<>();
    @FXML private TextField newList = new TextField();
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        //if statments creates dbManager once if list ArrayList has not been populated
        if(list.size()<1){
            try 
            {
                initdb();
                getdbSettings();
            } 
            catch (SQLException ex) 
            {
                Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        //sets the items for the list view with the list names.
        listView.setItems(getLists());
        //sets and formats the settings for the user settings
        freq.setItems(getFrequencyOptions());
        startOptions.setItems(getTimeOptions());
        endOptions.setItems(getTimeOptions());
        freq.setValue(String.valueOf(settings.getNotiFreq()));
        startOptions.setValue(settings.getNotStartTime().format(DateTimeFormatter.ofPattern("hh:mm a")));
        endOptions.setValue(settings.getNotEndTime().format(DateTimeFormatter.ofPattern("hh:mm a")));
        
        //disable add list button and add Listener to validate list name if < 45 and > 0
        addListBtn.setDisable(true);
        newList.textProperty().addListener((observerable, oldText, newText) -> {
            int nameCount = newText.length();
            
            if(nameCount <= 0 || nameCount > 45)
            {
                addListBtn.setDisable(true);
                listNameCount.setTextFill(Color.RED);
            }
            else
            {
                addListBtn.setDisable(false);
                listNameCount.setTextFill(Color.BLACK);
            }
            listNameCount.setText("ListName: " + nameCount + "/45");
        });
        
        
        //disable save button and add listener to choice boxes that will enable save button
        saveSettings.setDisable(true);
        freq.getSelectionModel().selectedIndexProperty().addListener((observable, oldStr, newStr) -> {
            if(!newStr.equals(oldStr))
            {
                saveSettings.setDisable(false);
            }
        });
    }
    
    public void initdb() throws SQLException
    {
        //create database manager class then select items in "list" table
        dbm = new dbManager();
        dbResult = dbm.getResult("list");
        getdbList();
        dbResult.close();
    }
    
    public void getdbList() throws SQLException
    {
        list.clear();
        //loop to add items from "list" table to the list class
        while(dbResult.next())
        {
            list.add(new List(dbResult.getInt("listID"), dbResult.getString("listName")));
        }
    }
    
    public void getdbSettings() throws SQLException
    {
        //selects the row in the settings table
        dbResult = dbm.getSettingsResult();
        while(dbResult.next())
        {
            //allocates the settings class with data from database
            settings = new Settings(dbResult.getInt("NotifFrequency"), dbResult.getTime("StartNotif").toLocalTime(), dbResult.getTime("EndNotif").toLocalTime());
        }
        dbResult.close();
    }
    
    //Creates an observable list of the object List
    public ObservableList<List> getLists()
    {
        ObservableList lists = FXCollections.observableArrayList();
        //for loop to add list names to the ObservableList
        for(int i = 0; i < list.size(); i++)
        {
            lists.add(list.get(i));
        }
        
        return lists;
    }
    
    //returns the frequency options for a user to choose from
    public ObservableList<String> getFrequencyOptions()
    {
        ObservableList options = FXCollections.observableArrayList();
        for(int i = 0; i < 24; i++)
        {
            options.add(String.valueOf(i));
        }
        return options;
    }
    
    //returns the notfication start/end time options for a user to choose from
    public ObservableList<String> getTimeOptions()
    {
        ObservableList options = FXCollections.observableArrayList();
        options.addAll("12:00am", "1:00am", "2:00am", "3:00am", "4:00am", "5:00am", "6:00am", "7:00am", "8:00am", "9:00am", "10:00am", "11:00am", "12:00pm", 
            "1:00pm", "2:00pm", "3:00pm", "4:00pm", "5:00pm", "6:00pm", "7:00pm", "8:00pm", "9:00pm", "10:00pm", "11:00pm");
        return options;
    }

    //new popup to add a list.
    public void addListPopup(ActionEvent event) throws IOException
    {
        try{
            this.stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("newListPopup.fxml"));
            Parent root1 = (Parent) loader.load();
            
            popupScene = new Scene(root1);
            popupStage.setTitle("Add a List!");
            popupStage.initOwner(stage);
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.setScene(popupScene);
            popupStage.show();
        } 
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }
    
    //adds the new list to the list array when addNewList button is triggered
    public void addNewList(ActionEvent event) throws IOException, SQLException
    {
        //call method to insert the list name into the database then closing it.
        dbm.InsertList(newList.getText());
        dbResult = dbm.getResult("list");
        getdbList();
        dbResult.close();
        //sets items to the ListView listView
        listView.setItems(getLists());
        
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("MainMenu.fxml"));
        Parent root = loader.load();
        
        popupStage = (Stage)((Node)event.getSource()).getScene().getWindow();
        popupStage.close();
        this.stage = (Stage) popupStage.getOwner();
        
        scene = new Scene(root);
        this.stage.setScene(scene);
        this.stage.show();
    }
    
    //this method creates an alert window to confirm the deletion of a list
    public void deleteList(ActionEvent event) throws IOException, SQLException
    {
        //alert box to caution users from deleting a List
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText("Deleting list '" + listView.getSelectionModel().getSelectedItem().getListName() + "'");
        alert.setContentText("CAUTION: Are you ok with this?");
        Optional<ButtonType> result = alert.showAndWait();
        
        //if the "OK" button is pressed, then the program continues with deletion
        if(result.get() == ButtonType.OK)
        {
            int id = listView.getSelectionModel().getSelectedItem().getListId();
            dbm.dbDrop("list", id);
            dbResult = dbm.getResult("list");
            getdbList();
            dbResult.close();
        }
        
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("MainMenu.fxml"));
        Parent root = loader.load();
        
        scene = new Scene(root);
        this.stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        this.stage.setScene(scene);
        this.stage.show();
    }
    
    public void settingsPopup(ActionEvent event) throws IOException, SQLException
    {
        try{
            this.stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("SettingsPopup.fxml"));
            Parent root1 = (Parent) loader.load();
            
            popupScene = new Scene(root1);
            popupStage.setTitle("Add a List!");
            popupStage.initOwner(stage);
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.setScene(popupScene);
            popupStage.show();
        } 
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }
    
    //Action method to swithc user to "MainMenu.fxml"
    public void switchtoMainMenu(ActionEvent event)
    {
        try{
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("MainMenu.fxml"));
            Parent root = loader.load();
            popupStage = (Stage)((Node)event.getSource()).getScene().getWindow();
            popupStage.close();
            this.stage = (Stage) popupStage.getOwner();

            scene = new Scene(root);
            this.stage.setScene(scene);
            this.stage.show();
        } 
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }
    
    //Method to switch the user to the List View fxml file
    public void switchToListView(MouseEvent event) throws IOException, SQLException
    {
        //Double clicking item in the listView will trigger if statement below
        if (event.getClickCount() == 2) {
            try{
                //This will change the scene displaying the selected lists' task
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("ListView.fxml"));
                Parent root = loader.load();
                scene = new Scene(root);

                ListController controller = loader.getController();
                controller.initList(listView.getSelectionModel().getSelectedItem());

                stage = (Stage)((Node)event.getSource()).getScene().getWindow();
                stage.setScene(scene);
                stage.show();
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
        }
    }
}