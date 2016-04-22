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


    /////////////////////////////////////////////////////////////////
    // User Functions
    /////////////////////////////////////////////////////////////////
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

    public boolean dropUser(User user){
        // delete user here
        return false;
    }

    /////////////////////////////////////////////////////////////////
    // Friend Functions
    /////////////////////////////////////////////////////////////////

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
            } else {
                if(!pendingFriendExists(email1,email2)) createPendingFriend(email1, email2);
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

    private boolean pendingFriendExists(String email1, String email2){
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

    // Second Degree works Third does not
    public void threeDegrees(String email1, Scanner sc){
        try {
            boolean valid;
            String email2;
            do{
                valid = true;
                System.out.print("Enter Email Of User You Want To Run Three Degrees On: ");
                email2 = sc.nextLine().trim();
                if (!Driver.isValidEmailAddress(email2)) valid = false;
            }while (!valid);
            // Check valid email
            if (getUser(email2) == null) {
                System.out.println("\nThat User Does not exist in our database\n"); return;
            }
            // Check first Degree
            if(isFriend(email1,email2)){
                System.out.println("\n"+email1 + " <--> "+email2+"\n"); return;
            }

            // Second Degree
            statement = connection.createStatement();
            String degree2 = "SELECT T.firstName, T.lastName, T.email "+
                    "FROM UserTable T WHERE T.email IN "+
                    "(SELECT person2 FROM Friendship WHERE person1='"+email1+"' AND timeEstablished IS NOT NULL "+
                    "UNION " +
                    "SELECT person1 FROM Friendship WHERE person2='"+email1+"' AND timeEstablished IS NOT NULL) "+
                    "AND T.email IN"+
                    "(SELECT person2 FROM Friendship WHERE person1='"+email2+"' AND timeEstablished IS NOT NULL "+
                    "UNION " +
                    "SELECT person1 FROM Friendship WHERE person2='"+email2+"' AND timeEstablished IS NOT NULL)";


            resultSet = statement.executeQuery(degree2);
            ArrayList<String> names1;

            if(resultSet.next()){
                System.out.println();
                names1 = new ArrayList<String>();
                do {
                    names1.add(resultSet.getString(3));
                }while (resultSet.next());
                System.out.println(email1);
                System.out.println("     |");
                System.out.println("     |");
                System.out.println("     |");
                System.out.println("     |");
                for (int i = 0; i < names1.size(); i++) {
                    System.out.println(names1.get(i));
                }
                System.out.println("     |");
                System.out.println("     |");
                System.out.println("     |");
                System.out.println("     |");
                System.out.println(email2);
                System.out.println();
                return;
            }
            else {
                System.out.println("Failed 2 Degrees");
            }

            statement = connection.createStatement();


            // UserTable       (id,firstname,lastname)

            // Friendship      (person1,person2,timeInit,timeEstablished)


            String degree3 = "SELECT T.firstName, T.lastName, T.email "+
                    "FROM UserTable T WHERE T.email IN "+
                    "(SELECT person2 FROM Friendship WHERE person1='"+email1+"' AND timeEstablished IS NOT NULL "+
                    "UNION " +
                    "SELECT person1 FROM Friendship WHERE person2='"+email1+"' AND timeEstablished IS NOT NULL) "+
                    // User ones friends


                    "AND T.email IN"+
                    "(SELECT person2 FROM Friendship WHERE person1='"+email2+"' AND timeEstablished IS NOT NULL "+
                    "UNION " +
                    "SELECT person1 FROM Friendship WHERE person2='"+email2+"' AND timeEstablished IS NOT NULL)";
                    // User twos friends


            System.out.println(degree3);
            resultSet = statement.executeQuery(degree3);
            ArrayList<String> names2;

            if(resultSet.next()){
                System.out.println();
                names2 = new ArrayList<String>();
                do {
                    System.out.println(resultSet.getString(1));
                    names2.add(resultSet.getString(1));
                }while (resultSet.next());

                return;
            }
            else {
                System.out.println("Failed 3 Degrees");
            }

        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    /////////////////////////////////////////////////////////////////
    // Group Functions
    /////////////////////////////////////////////////////////////////

    public Group getGroup(long groupID){
        //Check  if group exists
        try {
        	// NOTE:
        	// need to use a new Statement and ResultSet, otherwise would overwrite the caller's (displayGroup())
            Statement statement = connection.createStatement();
            String checkGroup = "SELECT * FROM GroupTable WHERE groupID ="+ groupID;
            ResultSet resultSet = statement.executeQuery(checkGroup);
            if (resultSet.next()){
                Group group = new Group();
                group.setGroupID(groupID);
                group.setName(resultSet.getString(2));
                group.setDescription(resultSet.getString(3));
                group.setMembershipLimit(resultSet.getLong(4));
                statement.close();
                resultSet.close();
                return group;                    // has group
            }
            else{
                statement.close();
                resultSet.close();
                System.out.println(" group not exists");
                return null;                  // no group
            }
        }catch (SQLException e){
            e.printStackTrace();
            System.out.println("Failure to check if group exists");
        }

        return null;
    }

    public boolean createGroup(Group group){

        if( getGroup(group.getGroupID()) != null){
            System.out.println("\n-- Group EXISTS ---");
            System.out.println("----------------------------------------------------------------------------");
            System.out.print(group.toString());
            System.out.println();
            return false;
        }
        try {
            String inputQuery = "INSERT INTO GroupTable VALUES (?,?,?,?)";
            prepStatement = connection.prepareStatement(inputQuery);
            prepStatement.setLong(1, group.getGroupID());
            prepStatement.setString(2, group.getName());
            prepStatement.setString(3, group.getDescription());
            prepStatement.setLong(4, group.getMembershipLimit());
            prepStatement.executeUpdate();
            prepStatement.close();
        }catch (SQLException e){
            System.out.println("Something Went wrong adding that group");
        }
        
        System.out.println("\n-- Creating Group Succeeds! ---");
    //    System.out.println(group);
        return true;
    }

    public boolean addToGroup(String email, long groupID) {
    	// check if user exists
        if (getUser(email) == null) {
            System.out.println("\n-- USER NOT EXISTS ---\n Failed to add it to the group.");
            System.out.println("----------------------------------------------------------------------------");
            return false;
        }
        // check if group exists
        if (getGroup(groupID) == null) {
            System.out.println("\n-- GROUP NOT EXISTS ---\n Failed to add it to the group.");
            System.out.println("----------------------------------------------------------------------------");
            return false;
        }

        // check if added
        try {
            statement = connection.createStatement();
            String checkExist = "SELECT * FROM Membership " +
                    "WHERE groupID=" + groupID + " AND member='" + email + "'";
            resultSet = statement.executeQuery(checkExist);
            if (resultSet.next()) {
                System.out.println("The user is already in the group.");
                return false;
            } else {
                String inputQuery = "INSERT INTO Membership VALUES (?,?)";
                prepStatement = connection.prepareStatement(inputQuery);
                prepStatement.setLong(1, groupID);
                prepStatement.setString(2, email);
                prepStatement.executeUpdate();
                prepStatement.close();
                
                System.out.println("You are in group " + groupID +"! ");
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Something Went wrong adding that user to the group");
            return false;
        }finally{
        	try {
				statement.close();
				resultSet.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	
        }
    }
    
    public boolean addToGroup(User user, Group group) {
    	return addToGroup(user.getEmail(),  group.getGroupID());
    }

    public void displayGroups(String email){
        try {
            statement = connection.createStatement();
            String groupQuery = "SELECT DISTINCT groupID FROM Membership WHERE member= '"+email +"'";
            resultSet = statement.executeQuery(groupQuery);
            
            int count = 0;
            while (resultSet.next()){
            	count++;
            	System.out.println("\n--------- My Group " +count+ " ---------\n");
            	 Group group = getGroup(resultSet.getLong(1));
                 System.out.println(group.toString());
            }
            
            if( count==0){
            	System.out.println("\nYou are not a member of any groups\n");
            }
            
        }catch (SQLException e){
            e.printStackTrace();
        }finally{
        	try {
				statement.close();
				resultSet.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	
        }
    }

    public void displayTopGroups(){
    	// currently display all groups
    	 try {
             statement = connection.createStatement();
             String groupQuery = "SELECT * FROM GroupTable";
             resultSet = statement.executeQuery(groupQuery);
             
             while (resultSet.next()){
            	 Long id = resultSet.getLong(1);
                 String name = resultSet.getString(2);
                 System.out.println("ID: " + id+ ", group Name: "+ name );
             }
             
//             if (resultSet.next()){
//                 do{
//                     Long id = resultSet.getLong(1);
//                     String name = resultSet.getString(2);
//                     System.out.println("ID: " + id+ ", group Name: "+ name );
//                 }while (resultSet.next());
//             }

         }catch (SQLException e){
             e.printStackTrace();
         }
    }

    public ArrayList<String> fetchGroupMatches(){
        try {
            ArrayList<String> matches = new ArrayList<String>();
            statement = connection.createStatement();
            String pendingFriends = "";
            resultSet = statement.executeQuery(pendingFriends);
        }catch (SQLException e){

        }
        return null;
    }

    public boolean openGroup(){

        return false;
    }

    public void getGroupMessages(String groupID){

    }

    /////////////////////////////////////////////////////////////////
    // Message Functions
    /////////////////////////////////////////////////////////////////

    public void displayRecentMessages(String email){
        // displays Recent 5 Messages given a users email

    }

    public ArrayList<User> getRecentMessages(String email){
        // displays Recent 5 Messages given a users email
        return null;
    }

    public String openMessageFromEmail(String email1, String email2){
        // get all messages
        return null; // null if no messages exist
    }

    public void sendMessage(String email1, String email2, String subject, String body){
        // send message from email1 to email2
        try {
            //Timestamp timestamp = Timestamp.from(Calendar.getInstance().getTime().toInstant());

            // get the current largest msgID

            String selectIdQuery = "SELECT NVL(MAX(msgID),0) as MAX_VAL FROM Message";
            prepStatement = connection.prepareStatement(selectIdQuery);
            prepStatement.executeQuery();
            resultSet = prepStatement.getResultSet();

            int msgID = 0;

            while (resultSet.next()){
                msgID = resultSet.getInt("MAX_VAL");

            }

            // Insert new message
            msgID++;
            String sendMsg = "INSERT INTO Message VALUES (?,?,?,?,?,?)";
            prepStatement = connection.prepareStatement(sendMsg);
            prepStatement.setInt(1, msgID);
            prepStatement.setString(2, email1);
            prepStatement.setString(3, email2);
            prepStatement.setTimestamp(4,new Timestamp(new java.util.Date().getTime()));
            prepStatement.setString(5, subject);
            prepStatement.setString(6, body);
            prepStatement.executeUpdate();
            prepStatement.close();

            System.out.println("\nMessage Sent From: " + email1 + " To: " + email2 +"\n");
            System.out.println("Message ID: : " + msgID +"\n");
            System.out.println("Subject: " + subject +"\n");
            System.out.println("Message: " + body +"\n");

        }catch (SQLException e){
            e.printStackTrace();
            System.out.println("Something Went wrong Sending the Message.");
        }finally {
            try{
                //statement.close();
                resultSet.close();

            }catch (SQLException e){

            }
        }
    }

    public void displayTopMessagers(ArrayList<User> users){
        // display these users by top #  of messages sent to these users
    }

    public void displayAllMessages(String email){
        // display all Messages
    }

    /////////////////////////////////////////////////////////////////
    // Search For A User
    /////////////////////////////////////////////////////////////////

    public void searchUser(Scanner sc){

        System.out.println("Please Enter The Name of the User you are looking for: ");
        String searchTerm = sc.nextLine();

        // displays all users similar to name


    }

    public void debug(){
        try {
            statement = connection.createStatement();
            String all = "SELECT firstName, lastName,email "+ "FROM UserTable";
            System.out.println(all);
            resultSet = statement.executeQuery(all);
            System.out.println("\n --> UserTable");
            System.out.printf("\n    %-20s%-20s%-20s\n","First","Last","Email");
            System.out.println("----------------------------------------------------------------------------");
            if (resultSet.next()){
                do{
                    System.out.printf("    %-20s%-20s%-20s\n",resultSet.getString(1),resultSet.getString(2),resultSet.getString(3));
                }while (resultSet.next());
            }
            String e = "SELECT person1, person2,timeEstablished "+ "FROM Friendship";
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
                    System.out.printf("%-50s%-50s%-50s\n",resultSet.getString(1),resultSet.getString(2),s);
                }while (resultSet.next());
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

}