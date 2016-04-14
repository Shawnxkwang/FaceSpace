package com.company;

import java.sql.*;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
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

    DBHelper(Connection connection){
        this.connection = connection;
    }

    public User getUser(String email){
        //Check  if user exists
        try {
            statement = connection.createStatement();
            String checkUser = "SELECT email FROM UserTable WHERE email ='"+email+"'";
            resultSet = statement.executeQuery(checkUser);
            if (resultSet.next()){
                User user = new User();
                user.setFirstName(resultSet.getString(1));
                user.setLastName(resultSet.getString(2));
                user.setEmail(resultSet.getString(3));
                user.setBirthDate(resultSet.getDate(4));
                end();
                return user;                    // has user
            }
            else{
                end();
                return null;                  // no user
            }
        }catch (SQLException e){
            System.out.println("Failure to check if user exists");
        }
        return null;
    }

    public boolean createUser(User user){
        // Creates user returns false if does not exist
        if(getUser(user.getEmail()) == null){
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
            prepStatement.executeUpdate();
            end();
        }catch (SQLException e){
            System.out.println("Something Went wrong adding that user");
        }
        return true;
    }


    public void displayFriends(String email){
        try {
            /////////////////////////////////////////////////////////////////////////////////////////////////////

            // Collects Formed Friendships
            statement = connection.createStatement();
            String formedFriends = "SELECT fName,lName " +
                    "FROM UserTable, " +
                    "(SELECT person2 FROM Friendship WHERE person1="+email+"AND timeEstablished is NOT NULL ) as F_Emails " +
                    "WHERE UserTable.email = F_Emails.person2";
            resultSet = statement.executeQuery(formedFriends);

            System.out.println("           Formed Friendships            ");
            System.out.println("----------------------------------------------------------------------------");
            while (resultSet.next()){
                System.out.printf("%-20s%-20s\n",resultSet.getString(1),resultSet.getString(2));
            }
            end();

            /////////////////////////////////////////////////////////////////////////////////////////////////////

            //Collects Pending Friendships
            statement = connection.createStatement();
            String pendingFriends = "SELECT fName,lName " +
                    "FROM UserTable, " +
                    "(SELECT person2 FROM Friendship WHERE person1="+email+"AND timeEstablished is NULL ) as F_Emails " +
                    "WHERE UserTable.email = F_Emails.person2 AND";

            System.out.println("           Pending Friendships            ");
            System.out.println("----------------------------------------------------------------------------");

            while (resultSet.next()){
                System.out.printf("%-20s%-20s\n",resultSet.getString(1),resultSet.getString(2));
            }
            end();

        }catch (SQLException e){
            System.out.println("Failure to Display Friends");
        }
    }

    public boolean addPendingFriend(String email1, String email2){
        try {
            statement = connection.createStatement();
            String alreadyFriends = "SELECT timeEstablished " +
                    "FROM Friendship " +
                    "WHERE (person1="+email1+" AND person2="+email2+") " +
                    "OR (person1="+email2+" AND person2="+email1+")";
            resultSet = statement.executeQuery(alreadyFriends);

            if(resultSet.next() && resultSet.getTimestamp(1) == null){       // Establish the Friendship
                establishFriend(email1,email2);
            }
            else if (resultSet.next()){                                      // Already friends Create
                System.out.println("You are already friends with them");
            }
            else {                                                           // Pending Friendship
                createPendingFriend(email1,email2);
            }
            end();
            return true;
        }catch (SQLException e){
            System.out.println("Something Went wrong attempting to add a pending friend");
            return false;
        }
    }

    private boolean establishFriend(String email1, String email2){
        try {
            Timestamp timestamp = Timestamp.from(Calendar.getInstance().getTime().toInstant());
            String establishFriendship = "UPDATE Friendship " +
                    "SET timeEstablished="+timestamp.toString()+
                    " WHERE (person1="+email1+" AND person2="+email2+") OR " +
                    "(person1="+email2+" AND person2="+email1+")";
            prepStatement = connection.prepareStatement(establishFriendship);
            prepStatement.executeUpdate();
            end();
            return true;
        }catch (SQLException e){
            System.out.println("Something Went establishing the friendship");
            return false;
        }
    }

    private boolean createPendingFriend(String email1,String email2){
        try {
            Timestamp timestamp = Timestamp.from(Calendar.getInstance().getTime().toInstant());
            String inputQuery = "INSERT INTO Friendship VALUES (?,?,?,?)";
            prepStatement = connection.prepareStatement(inputQuery);
            prepStatement.setString(1, email1);
            prepStatement.setString(2, email2);
            prepStatement.setTimestamp(3, timestamp);
            prepStatement.setTimestamp(4,null); //null until a friendship is established
            prepStatement.executeUpdate();
            end();
        }catch (SQLException e){
            System.out.println("Something Went wrong adding that user");
        }
        return true;
    }

    private void end(){
        try {
            statement.close();
            prepStatement.close();
            resultSet.close();
        }catch (SQLException e){
            System.out.println("Error Closing Connections");
        }
    }
}
