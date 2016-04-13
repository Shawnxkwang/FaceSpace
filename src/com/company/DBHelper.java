package com.company;

import java.sql.*;
import java.util.*;
import java.io.*;

/**
 * Created by Zach on 4/7/16.
 */
public class DBHelper {

    private Connection connection;
    private Statement statement;
    private ResultSet resultSet; //used to hold the result of your query (if one

    DBHelper(Connection connection){
        this.connection = connection;
    }

    public boolean userExists(String email){
        //Check  if user exists
        try {
            statement = connection.createStatement();
            String checkUser = "SELECT userID FROM UserTable WHERE email ="+email; //sample query
            resultSet = statement.executeQuery(checkUser);

            if (resultSet.next()) return true; // has user
            else return false;  // no user
        }catch (SQLException e){
            System.out.println("Failure to check if user exists");
        }
        return false;
    }

    public boolean isEstablishedFriend(){
        //Check friendship is established
        return false;
    }

    public boolean isPending(){
        //Check if user exists
        return false;
    }

    public boolean createUser(User user){
        // Creates user retruns false if userExists
        if(userExists(user.getEmail())){
            System.out.println("Failed to Create:\n");
            return false;
        }
        return true;
    }

    public boolean addPendingFriend(){
        // Adds Friend to users Pending
        return true;
    }



}
