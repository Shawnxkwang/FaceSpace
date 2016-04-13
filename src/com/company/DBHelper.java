package com.company;

import java.sql.*;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.*;
import java.io.*;

/**
 * Created by Zach on 4/7/16.
 */
public class DBHelper {

    private Connection connection;
    private Statement statement;
    private ResultSet resultSet; //used to hold the result of your query (if one
    private PreparedStatement prepStatement; //used to create a prepared statement, that will be later reused

    DBHelper(Connection connection, Statement statement, ResultSet resultSet ){
        this.connection = connection;
        this.statement = statement;
        this.resultSet = resultSet;
    }

    public boolean userExists(String email){
        //Check  if user exists
        try {
            statement = connection.createStatement();
            String checkUser = "SELECT userID FROM UserTable WHERE email ="+email; //sample query
            resultSet = statement.executeQuery(checkUser);

            if (resultSet.next()) return true; // has user
            else return false;                  // no user
        }catch (SQLException e){
            System.out.println("Failure to check if user exists");
        }
        return false;
    }

    public boolean isEstablishedFriend(String email1, String email2){
        //Check friendship is established
        try {
            statement = connection.createStatement();
            String query = "SELECT timeEstablished FROM Friendship WHERE person1 ="+email1
                    +" person2 = "+ email2
                    +" OR person2 = "+ email1
                    +" person1 = "+ email2;
            resultSet = statement.executeQuery(query);

            if(resultSet.next()){
                Timestamp timestamp = resultSet.getTimestamp(1);
                if (timestamp == null) return false;
                else return true;
            }
            else return false;
        }catch (SQLException e){
            System.out.println("An Error Occurred when trying to check if the friendship is established");
        }
        return false;
    }
    public boolean createUser(User user){
        // Creates user retruns false if userExists
        if(userExists(user.getEmail())){
            System.out.println("Failed to Create:\n");
            System.out.print(user.toString());
            System.out.println("-- EXISTS ---");
            return false;
        }
        try {
            String inputQuery = "INSERT INTO UserTable VALUES (?,?,?,?)";
            prepStatement = connection.prepareStatement(inputQuery);
            prepStatement.setString(1, user.getEmail());
            prepStatement.setString(2, user.getFirstName());
            prepStatement.setString(3, user.getLastName());
            prepStatement.setDate(4,user.getBirthDate());
            // Now that the statement is ready. Let's execute it. Note the use of
            // executeUpdate for insertions and updates instead of executeQuery for
            // selections.
            prepStatement.executeUpdate();
        }catch (SQLException e){
            System.out.println("Something Went wrong adding that user");
        }
        return true;
    }
    public boolean addPendingFriend(){
        // Adds Friend to users Pending
        return true;
    }
}
