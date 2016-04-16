package com.company;


import java.sql.*;
import java.sql.Date;
import java.text.DateFormat;
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
            prepStatement.executeUpdate();
            prepStatement.close();
        }catch (SQLException e){
            System.out.println("Something Went wrong adding that user");
        }
        return true;
    }


    public void displayFriendSummary(String email){
        try {
            statement = connection.createStatement();
            String formedFriends = "SELECT firstName, lastName, email "+
                                    "FROM UserTable "+
                                    "WHERE email IN (SELECT person1 "+
                                    "FROM Friendship "+
                                    "WHERE person2='"+email+"' AND timeEstablished IS NOT NULL) "+
                                    "OR email IN (SELECT person2 "+"FROM Friendship " +
                                    "WHERE person1='"+email+"' AND timeEstablished IS NOT NULL)";

            resultSet = statement.executeQuery(formedFriends);

            System.out.println("\n\n --> Formed Friendships\n");
            System.out.printf("          %-20s%-20s%-20s\n", "First Name", "Last Name", "Email");
            System.out.println("----------------------------------------------------------------------------");
            if (resultSet.next()){
                do{
                    System.out.printf("          %-20s%-20s%-20s\n",resultSet.getString(1),resultSet.getString(2),resultSet.getString(3));
                }while (resultSet.next());
            }
            else System.out.println("              O how very sad....you appear to have no friends");
            statement.close();
            resultSet.close();

            //Collects Pending Friendships
            statement = connection.createStatement();
            String pendingFriends = "SELECT firstName, lastName, email "+
                    "FROM UserTable "+
                    "WHERE email IN (SELECT person1 "+
                    "FROM Friendship "+
                    "WHERE person2='"+email+"' AND timeEstablished IS NULL) "+
                    "OR email IN (SELECT person2 "+"FROM Friendship " +
                    "WHERE person1='"+email+"' AND timeEstablished IS NULL)";
            resultSet = statement.executeQuery(pendingFriends);

            System.out.println("\n\n --> All Pending Friendships (Sent And Received)\n");
            System.out.printf("          %-20s%-20s%-20s\n", "First Name", "Last Name", "Email");
            System.out.println("----------------------------------------------------------------------------");
            if(resultSet.next()){
                do{
                    System.out.printf("          %-20s%-20s%-20s\n",resultSet.getString(1),resultSet.getString(2),resultSet.getString(3));
                }while (resultSet.next());
            }
            else System.out.println("          You haven't sent or been sent any requests yet :(");
            statement.close();
            resultSet.close();
            System.out.println("\n\n");



        }catch (SQLException e){
            System.out.println("Failure to Display Friends");
            e.printStackTrace();
        }
    }


    public boolean createRequest(String email1, String email2){
        try {
            // Check if anyone even has that email
            if(getUser(email2) == null){
                System.out.println("\nSorry nobody in our database has that email\n");
                return false;
            }
            // Check if they are already friends
            if(isFriend(email1,email2)){
                System.out.println("\nYou are already friends with: "+email2+"\n");
                return false;
            }
            // Checks if at email 2 has sent email 1 a request
            statement = connection.createStatement();
            String checkPendingRequests = "SELECT timeInitiated FROM Friendship " +
                    "WHERE person1='"+email2+"' AND person2='"+email1+"' AND timeEstablished IS NULL";
            resultSet = statement.executeQuery(checkPendingRequests);


            if (resultSet.next()) {
                System.out.println("\n"+email2 +" already sent you a friend request establishing friendship now :)\n");
                establishFriend(email1, email2);
            }
            else {
                if(!pendingExists(email1,email2)) createPendingFriend(email1, email2);
                else System.out.println("\n       -- You Requested them already geez have some patience :p --\n");
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

            Calendar c = Calendar.getInstance();
            java.util.Date date= new java.util.Date();
            String establishFriendship = "UPDATE Friendship " +
                    "SET timeEstablished=TO_TIMESTAMP('"+new Timestamp(date.getTime())+"','YYYY-MM-DD HH24:MI:SS:FF')"+
                    " WHERE (person1='"+email1+"' AND person2='"+email2+"' AND timeEstablished IS NULL) OR " +
                    "(person1='"+email2+"' AND person2='"+email1+"' AND timeEstablished IS NULL)";
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
            //Timestamp timestamp = Timestamp.from(Calendar.getInstance().getTime().toInstant());
            String inputQuery = "INSERT INTO Friendship VALUES (?,?,?,?)";
            prepStatement = connection.prepareStatement(inputQuery);
            prepStatement.setString(1, email1);
            prepStatement.setString(2, email2);
            prepStatement.setTimestamp(3, new Timestamp(new java.util.Date().getTime()));
            prepStatement.setTimestamp(4,null); //null until a friendship is established
            prepStatement.executeUpdate();
            prepStatement.close();
            System.out.println("\nRequest Sent To: "+ email2 +"\n");
        }catch (SQLException e){
            System.out.println("Something Went wrong adding that user");
        }
        return true;
    }

    public void acceptRequest(String email, Scanner sc){
        try {
            statement = connection.createStatement();
            String recRequests = "SELECT firstName, lastName, email " +
                    "FROM UserTable " +
                    "WHERE email IN (SELECT person1 " +
                    "FROM Friendship " +
                    "WHERE person2='" + email + "' AND timeEstablished IS NULL)";
            resultSet = statement.executeQuery(recRequests);
            System.out.println("\n\n --> Pending Friend Requests Sent To You\n");
            System.out.printf("          %-20s%-20s%-20s\n", "First Name", "Last Name", "Email");
            System.out.println("----------------------------------------------------------------------------");

            if (resultSet.next()) {
                ArrayList<String> validEmails = new ArrayList<String>();
                do {
                    validEmails.add(resultSet.getString(3));
                    System.out.printf("          %-20s%-20s%-20s\n", resultSet.getString(1), resultSet.getString(2), resultSet.getString(3));
                } while (resultSet.next());

                // Get Entry for
                boolean valid;
                do{
                    valid = true;
                    System.out.println("\n----------------------------------------------------------------------------");
                    System.out.print("Enter Email of the friend you would like to confirm: ");
                    String entry = sc.nextLine();
                    if(Driver.isValidEmailAddress(entry) && validEmails.contains(entry) ){
                        if (establishFriend(email,entry)) System.out.println("\nYou are now friends with "+ entry+" :)\n");
                        else System.out.println("There was an issue confirming the request from "+entry);
                    }
                    else System.out.println("There is no pending request with the email : "+entry);
                    return;

                }while (!valid);

            } else System.out.println("            You have not been sent any requests at this time :(");

            System.out.println("\n\n");
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public void displayRequests(String email){
        try {
            statement = connection.createStatement();
            String sentRequests = "SELECT firstName, lastName, email "+
                    "FROM UserTable "+
                    "WHERE email IN (SELECT person2 "+
                    "FROM Friendship "+
                    "WHERE person1='"+email+"' AND timeEstablished IS NULL)";
            resultSet = statement.executeQuery(sentRequests);
            System.out.println("\n\n --> Pending Friend Requests Sent By You\n");
            System.out.printf("          %-20s%-20s%-20s\n", "First Name", "Last Name", "Email");
            System.out.println("----------------------------------------------------------------------------");
            if(resultSet.next()){
                do{
                    System.out.printf("          %-20s%-20s%-20s\n",resultSet.getString(1),resultSet.getString(2),resultSet.getString(3));
                }while (resultSet.next());
            }
            else System.out.println("          You do have any pending requests at this time :(");


            statement = connection.createStatement();
            String recRequests = "SELECT firstName, lastName, email "+
                    "FROM UserTable "+
                    "WHERE email IN (SELECT person1 "+
                    "FROM Friendship "+
                    "WHERE person2='"+email+"' AND timeEstablished IS NULL)";
            resultSet = statement.executeQuery(recRequests);
            System.out.println("\n\n --> Pending Friend Requests Sent To You\n");
            System.out.printf("          %-20s%-20s%-20s\n", "First Name", "Last Name", "Email");
            System.out.println("----------------------------------------------------------------------------");
            if(resultSet.next()){
                do{
                    System.out.printf("          %-20s%-20s%-20s\n",resultSet.getString(1),resultSet.getString(2),resultSet.getString(3));
                }while (resultSet.next());
            }
            else System.out.println("            You have not been sent any requests at this time :(");
            System.out.println("\n\n\n");

        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    private void debug(){
        try {


            statement = connection.createStatement();

            String all = "SELECT firstName, lastName,email "+
                    "FROM UserTable";


            System.out.println(all);

            resultSet = statement.executeQuery(all);

            System.out.println("\n --> UserTable");
            System.out.println("----------------------------------------------------------------------------");
            if (resultSet.next()){
                do{
                    System.out.printf("                  %-20s%-20s%-20s\n",resultSet.getString(1),resultSet.getString(2),resultSet.getString(3));
                }while (resultSet.next());
            }


            String e = "SELECT person1, person2,timeEstablished "+
                    "FROM Friendship";


            System.out.println(e);

            resultSet = statement.executeQuery(e);

            System.out.println("\n --> Friendship");
            System.out.println("----------------------------------------------------------------------------");
            if (resultSet.next()){
                do{
                    String s;
                    try{
                        s = resultSet.getTimestamp(3).toString();
                    }catch (NullPointerException e2){
                        s = null;
                    }

                    System.out.printf("             %-20s%-20s%-20s\n",resultSet.getString(1),resultSet.getString(2),s);
                }while (resultSet.next());
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
    }
}