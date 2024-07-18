package Task;

import javafx.scene.control.DatePicker;

/**
 *
 * @author Anav2
 */
//Task class contains data that was retrived from the database. Each task object is contained in an array.
public class Task 
{
    private int id, priority;
    private String taskName, description, dueDate;
    private boolean status;
    
    public Task(int id, String taskName, String description, int priority, String dueDate, boolean status)
    {
        //Task constructor sets attributes
        this.id = id;
        this.taskName = taskName;
        this.description = description;
        this.priority = priority;
        this.dueDate = dueDate;
        this.status = status;
    }
    
    public void setID(int id)
    {
        //sets current task ID
        this.id = id;
    }
    
    public int getID()
    {
        //returns current task ID
        return this.id;
    }
    
    public void setName(String name)
    {
        //sets current task name
        this.taskName = name;
    }
    
    public String getName()
    {
        //returns current task name
        return this.taskName;
    }
    
    public void setDescription(String description)
    {
        //sets current task description
        this.description = description;
    }
    
    public String getDescription()
    {
        //returns current task description
        return this.description;
    }
    
    public void setPriority(int priority)
    {
        //sets current task priority
        this.priority = priority;
    }
    
    public int getPriority()
    {
        //returns current task priority
        return this.priority;
    }
    
    public void setDueDate(String dueDate)
    {
        //sets current task due date
        this.dueDate = dueDate;
    }
    
    public String getDueDate()
    {
        //returns current task due date
        return this.dueDate;
    }
    
    public void setStatus(boolean status)
    {
        //sets boolean status of current task
        this.status = status;
    }
    
    public boolean getStatus()
    {
        //returns the boolean status of current task to determine completion
        return this.status;
    }
    
    @Override
    public String toString()
    {
        return this.taskName + "\n" + this.description;
    }
}