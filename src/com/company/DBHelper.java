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
            String checkUser = "SELECT firstName,lastName,email,birthday FROM UserTable WHERE email ='"+email+"'";
            resultSet = statement.executeQuery(checkUser);
            if (resultSet.next()){
                User user = new User();
                user.setFirstName(resultSet.getString(1));
                user.setLastName(resultSet.getString(2));
                user.setEmail(resultSet.getString(3));
                user.setBirthDate(resultSet.getDate(4));
                statement.close();
                resultSet.close();
                return user;                    // has user
            }
            else{
                statement.close();
                resultSet.close();
                return null;                  // no user
            }
        }catch (SQLException e){
            e.printStackTrace();
            System.out.println("Failure to check if user exists");
        }
        return null;
    }

    public boolean createUser(User user){
        // Creates user returns false if does not exist
        if(getUser(user.getEmail()) != null){
            System.out.println("\n-- USER EXISTS ---");
            System.out.println("----------------------------------------------------------------------------");
            System.out.print(user.toString());
            System.out.println();
            return false;
        }
        try {
            String inputQuery = "INSERT INTO UserTable VALUES (?,?,?,?)";
            prepStatement = connection.prepareStatement(inputQuery);
            prepStatement.setString(1, user.getEmail());
            prepStatement.setString(2, user.getFirstName());
            prepStatement.setString(3, user.getLastName());
            prepStatement.setDate(4, user.getBirthDate());
            System.out.println(prepStatement.toString());
            prepStatement.executeUpdate();
            prepStatement.close();
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
            String formedFriends = "SELECT t.firstName, t.lastName " +
                    "FROM UserTable t, " +
                    "(SELECT Friendship.person2 FROM Friendship WHERE (Friendship.person1='"+email+"' AND Friendship.timeEstablished is NOT NULL) F_Emails " +
                    "WHERE t.email=F_Emails.person2";

            //  SELECT T.firstName, T.lastName FROM UserTable T, (SELECT Friendship.person2='email' AND Friendship.timeEstablished
            //  SELECT

            System.out.println(formedFriends);
            resultSet = statement.executeQuery(formedFriends);

            System.out.println("\n --> Formed Friendships");
            System.out.println("----------------------------------------------------------------------------");
            System.out.println(resultSet.next());
            if (resultSet.next()){
                do{
                    System.out.printf("%-20s%-20s\n",resultSet.getString(1),resultSet.getString(2));
                }while (resultSet.next());
            }
            else System.out.println("              O how very sad....you appear to have no friends");

            System.out.println("\n----------------------------------------------------------------------------\n\n");

            statement.close();
            resultSet.close();

            /////////////////////////////////////////////////////////////////////////////////////////////////////

            //Collects Pending Friendships
            statement = connection.createStatement();
            String pendingFriends = "SELECT fName,lName " +
                    "FROM UserTable, " +
                    "(SELECT person2 FROM Friendship WHERE person1="+email+" AND timeEstablished is NULL ) F_Emails " +
                    "WHERE UserTable.email = F_Emails.person2";
            resultSet = statement.executeQuery(formedFriends);

            System.out.println("\n --> Pending Friendships");
            System.out.println("----------------------------------------------------------------------------");

            if(resultSet.next()){
                do{
                    System.out.printf("%-20s%-20s\n",resultSet.getString(1),resultSet.getString(2));
                }while (resultSet.next());
            }
            else System.out.println("                 You haven't sent any requests yet :(");
            System.out.println("\n----------------------------------------------------------------------------");
            statement.close();
            resultSet.close();
            System.out.println("\n\n");

        }catch (SQLException e){
            System.out.println("Failure to Display Friends");
            e.printStackTrace();
        }
    }


    public boolean addPendingFriend(String email1, String email2){
        try {
            // Check if anyone even has that email
            if(getUser(email2) == null){
                System.out.println("Sorry nobody in our database has that email");
                return false;
            }
            // Check if they are already friends
            if(isFriend(email1,email2)){
                System.out.println("Your are already friends with: "+email2);
                return false;
            }
            // Checks if at email 2 has sent email 1 a request
            statement = connection.createStatement();
            String checkPendingRequests = "SELECT timeInitiated FROM Friendship " +
                    "WHERE person1='"+email2+"' AND person2='"+email1+"' AND timeEstablished IS NULL";
            resultSet = statement.executeQuery(checkPendingRequests);


            if (resultSet.next()) {
                System.out.println("Establishing friendship");
                establishFriend(email1, email2);
            }
            else {
                if(!pendingExists(email1,email2)) createPendingFriend(email1, email2);
                else System.out.println("You Requested them already geez have some patience");
            }

            statement.close();
            resultSet.close();
            return true;
        }catch (SQLException e){
            e.printStackTrace();
            System.out.println("Something Went wrong attempting to add a pending friend");
            return false;
        }
    }

    private boolean isFriend(String email1,String email2){
        try {
            statement = connection.createStatement();
            String isFriends = "SELECT timeInitiated FROM Friendship " +
                    "WHERE (person1='"+email1+"' AND person2='"+email2+"' AND timeEstablished IS NOT NULL) OR"+
                    "(person1='"+email2+"' AND person2='"+email1+"' AND timeEstablished IS NOT NULL)";
            resultSet = statement.executeQuery(isFriends);
            if(resultSet.next()) return true;
            else return false;
        }catch (SQLException e){
            e.printStackTrace();
        }
        return false;

    }

    private boolean establishFriend(String email1, String email2){
        try {

            // if email2 has sent a request, good to est. friendship
            //SimpleDateFormat df = new SimpleDateFormat("YYYY-MM-DD HH24:MI:SS");
            //SimpleDateFormat df = new SimpleDateFormat("DD-MON-YYYY HH24:MI:SS:FF");
            Calendar c = Calendar.getInstance();

            System.out.println();

            java.util.Date date= new java.util.Date();
            // not a valid month
            String establishFriendship = "UPDATE Friendship " +
                    "SET timeEstablished=TO_TIMESTAMP('"+new Timestamp(date.getTime())+"','YYYY-MM-DD HH24:MI:SS:FF')"+
                    " WHERE (person1='"+email1+"' AND person2='"+email2+"' AND timeEstablished IS NULL) OR " +
                    "(person1='"+email2+"' AND person2='"+email1+"' AND timeEstablished IS NULL)";

            System.out.println(establishFriendship);
            statement = connection.createStatement();
            statement.executeQuery(establishFriendship);
            statement.close();
            return true;
        }catch (SQLException e){
            System.out.println("Something Went wrong establishing the friendship");
            e.printStackTrace();
            return false;
        }

    }


    private boolean pendingExists(String email1, String email2){
        try {
            statement = connection.createStatement();
            String exists = "SELECT timeInitiated FROM Friendship "+
                    "WHERE (person1='"+email1+"' AND person2='"+email2+"' AND timeEstablished IS NULL) OR"+
                    "(person1='"+email2+"' AND person2='"+email1+"' AND timeEstablished IS NULL)";
            resultSet = statement.executeQuery(exists);
            if(resultSet.next()) return true;
        }catch (SQLException e){
            e.printStackTrace();
        }
        return false;
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
            prepStatement.close();
            System.out.println("Request Sent");
        }catch (SQLException e){
            System.out.println("Something Went wrong adding that user");
        }
        return true;
    }

    ///////////////////////////



    public ArrayList<String> getFriends(String email){
        ArrayList<String> myFriends = new ArrayList<String>();
        try {
            /////////////////////////////////////////////////////////////////////////////////////////////////////

            // Collects Formed Friendships
            statement = connection.createStatement();
            String formedFriends = "SELECT UserTable.firstName, UserTable.lastName " +
                    "FROM UserTable, " +
                    "(SELECT Friendship.person2 FROM Friendship WHERE person1='"+email+"' AND timeEstablished is NOT NULL) F_Emails " +
                    "WHERE UserTable.email=F_Emails.person2";

            resultSet = statement.executeQuery(formedFriends);

            System.out.println("\n --> Formed Friendships");
            System.out.println("----------------------------------------------------------------------------");
            if (resultSet.next()){
                do{
                    System.out.printf("%-20s%-20s\n",resultSet.getString(1),resultSet.getString(2));
                    myFriends.add(resultSet.getString(0));
                }while (resultSet.next());
            }
            else System.out.println("              O how very sad....you appear to have no friends");

            System.out.println("\n----------------------------------------------------------------------------\n\n");

            statement.close();
            resultSet.close();

            /////////////////////////////////////////////////////////////////////////////////////////////////////

            //Collects Pending Friendships
//            statement = connection.createStatement();
//            String pendingFriends = "SELECT fName,lName " +
//                    "FROM UserTable, " +
//                    "(SELECT person2 FROM Friendship WHERE person1="+email+" AND timeEstablished is NULL ) F_Emails " +
//                    "WHERE UserTable.email = F_Emails.person2";
//            resultSet = statement.executeQuery(formedFriends);
//
//            System.out.println("\n --> Pending Friendships");
//            System.out.println("----------------------------------------------------------------------------");
//
//            if(resultSet.next()){
//                do{
//                    System.out.printf("%-20s%-20s\n",resultSet.getString(1),resultSet.getString(2));
//                }while (resultSet.next());
//            }
//            else System.out.println("                 You haven't sent any requests yet :(");
//            System.out.println("\n----------------------------------------------------------------------------");
//            statement.close();
//            resultSet.close();
//            System.out.println("\n\n");

        }catch (SQLException e){
            System.out.println("Failure to Display Friends");
            e.printStackTrace();
        }

        return myFriends;
    }

    public  ArrayList<String> displayFriendsRequest(String email){

        ArrayList<String> pendingRequests = new ArrayList<String>();
        // List All Friends And Pending
        try {
            statement = connection.createStatement();
            String alreadyFriends = "SELECT person1 " +
                    "FROM Friendship " +
                    "WHERE ( person2='"+email+"' AND timeEstablished is NULL) ";


            System.out.println(alreadyFriends);
            resultSet = statement.executeQuery(alreadyFriends);

            while (resultSet.next()){
                resultSet.getString(0);
                pendingRequests.add(resultSet.getString(0));
                // if result set has the

            }

        }catch (SQLException e){
            e.printStackTrace();
        }
        return   pendingRequests;
    }
}
