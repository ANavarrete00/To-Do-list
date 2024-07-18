package List;

import Task.Task;
import java.util.ArrayList;

public class List 
{
    //The list class holds the array of task objects. It also contains a List objects data.
    //The list class will be able to add, drop, and edit tasks in the database
    private int listId;
    private String listName;
    private ArrayList<Task> task = new ArrayList<>();
    
    public List(int listId, String listName)
    {
        //list constructor
        this.listId = listId;
        this.listName = listName;
    }
    
    public void setListId(int id)
    {
        //sets current list ID
        this.listId = id;
    }
    
    public int getListId()
    {
        //get current list ID
        return this.listId;
    }
    
    public void setListName(String name)
    {
        //sets current list name
        this.listName = name;
    }
    
    public String getListName()
    {
        //get current list name
        return this.listName;
    }
    
    public ArrayList<Task> getTask()
    {
        return this.task;
    }
    
    //adds task to task array and to database
    public void addTask(int id, String taskName, String description, int priority, String dueDate, boolean status)
    {
        
        task.add(new Task(id, taskName, description, priority, dueDate, status));
    }
    
    public String toString()
    {
        return this.listName;
    }
}