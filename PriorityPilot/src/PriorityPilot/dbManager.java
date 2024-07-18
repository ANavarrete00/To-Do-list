package PriorityPilot;

import java.sql.*;

public class dbManager 
{
    //the dbManager class will handle the connection to the database, inserting, dropping, updating,
    //and selecting data.
    private final String username = "root";
    private final String pass = "toor";
    private final String dbCon = "jdbc:mysql://localhost:3306/prioritypilot";
    private Connection dbConnection = null;
    private Statement dbStatment = null;
    private ResultSet dbResult = null;
    
    dbManager()
    {
        //dbManager constructor will set database connection
        try
        {
            dbConnection = DriverManager.getConnection(dbCon, username, pass);
            dbStatment = dbConnection.createStatement();
            dbResult = dbStatment.executeQuery("SELECT * FROM list;");
            dbConnection.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
    //Inserts data into database
    public void InsertTask(int listID, String taskName, String taskDesc, int priority, String dueDate, boolean status)
    {   
        try
        {
            dbConnection = DriverManager.getConnection(dbCon, username, pass);
            dbStatment = dbConnection.createStatement();
            //if statment to account for null value
            if(dueDate == null || dueDate == "")
            {
                dbStatment.executeUpdate("INSERT INTO task(listID, taskName, taskDesc, priority, dueDate, status) VALUES(" + listID + ", '" + taskName + "', '" + taskDesc +"', " + priority +", NULL, " + status + ")");
            }
            else
            {
                dbStatment.executeUpdate("INSERT INTO task(listID, taskName, taskDesc, priority, dueDate, status) VALUES(" + listID + ", '" + taskName + "', '" + taskDesc +"', " + priority +", '" + dueDate + "', " + status + ")");
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
    //Inserts data into database
    public void InsertList(String listName)
    {
        try
        {
            dbConnection = DriverManager.getConnection(dbCon, username, pass);
            dbStatment = dbConnection.createStatement();
            dbStatment.executeUpdate("INSERT INTO list(listName) VALUES('" + listName + "');");
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
    //delete a list from database
    public void dbDrop(String table, int ID)
    {
        try
        {
            dbConnection = DriverManager.getConnection(dbCon, username, pass);
            dbStatment = dbConnection.createStatement();
            dbStatment.executeUpdate("DELETE FROM " + table + " WHERE " + table +"ID = " + ID);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
    public ResultSet getResult(String table) throws SQLException
    {
        //send select statment to database
        //returns data from select statment
        dbConnection = DriverManager.getConnection(dbCon, username, pass);
        dbStatment = dbConnection.createStatement();
        dbResult = dbStatment.executeQuery("SELECT * FROM " + table);
        return this.dbResult;
    }
    
    public ResultSet getSettingsResult() throws SQLException
    {
        dbConnection = DriverManager.getConnection(dbCon, username, pass);
        dbStatment = dbConnection.createStatement();
        dbResult = dbStatment.executeQuery("SELECT * FROM settings WHERE settingsID = 1");
        return this.dbResult;
    }
}