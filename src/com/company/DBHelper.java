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
            System.out.println("\n-- User Exists");
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
            int worked = prepStatement.executeUpdate();
            if(worked == 0) return false;
            prepStatement.close();
        }catch (SQLException e){
            System.out.println("Something Went wrong adding that user");
        }
        return true;
    }

    public boolean dropUser(User user){
        // Delete does not Cascade FK_M_USER violated - child record found
        if(getUser(user.getEmail()) == null){
            System.out.println("\n-- User Does Not Exist");
            System.out.println("----------------------------------------------------------------------------");
            System.out.print(user.toString());
            System.out.println();
            return false;
        }
        try {
            String delete = "DELETE FROM UserTable WHERE email='"+user.getEmail()+"'";
            prepStatement = connection.prepareStatement(delete);
            int worked = prepStatement.executeUpdate();
            if(worked == 0){
                System.out.println("\nFailed to delete \n"+user.toString());
                return false;
            }
            prepStatement.close();
        }catch (SQLException e){
            e.printStackTrace();
            System.out.println("\nFailed to delete \n"+user.toString());
        }
        return true;
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
        if(email1.equals(email2)){
            System.out.println("You cannot request yourself");
            return false;
        }
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
                    String entry = sc.nextLine().toLowerCase();
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

    public Group getGroupByID(long groupID){
        //Check  if group exists
        Group group = new Group(groupID);
        try {

        	// NOTE:
        	// need to use a new Statement and ResultSet, otherwise would overwrite the caller's (displayGroup())
            Statement statement = connection.createStatement();
            String checkGroup = "SELECT * FROM GroupTable WHERE groupID ="+ groupID;
            ResultSet resultSet = statement.executeQuery(checkGroup);
            if (resultSet.next()){
                group.setName(resultSet.getString(2));
                group.setDescription(resultSet.getString(3));
                group.setMembershipLimit(resultSet.getInt(4));     // has group
                group.initMembers(connection);
            }
            else System.out.println(" A Group With that ID does not exist");            // no group

            statement.close();
            resultSet.close();
            return group;
        }catch (SQLException e){
            e.printStackTrace();
            System.out.println("Failure to check if group exists");
        }

        return null;
    }

    public boolean createGroup(Group group){
        try {
            //String inputQuery = "INSERT INTO GroupTable VALUES (?,?,?,?)";
            // ALlows for same name groups
            String inputQuery = "INSERT INTO GroupTable (name,description,mLimit) VALUES (?,?,?)";
            prepStatement = connection.prepareStatement(inputQuery);
            //prepStatement.setLong(1, group.getGroupID());
            prepStatement.setString(1, group.getName());
            prepStatement.setString(2, group.getDescription());
            prepStatement.setInt(3, group.getMembershipLimit());
            prepStatement.executeUpdate();
            prepStatement.close();
        }catch (SQLException e){
            e.printStackTrace();
            System.out.println("\nSomething Went wrong adding that group\n");
        }
        
        System.out.println("\n-- Group Created!\n");
        System.out.println("----------------------------------------------------------------------------");
        System.out.println("\tGroup Name        : "+group.getName());
        System.out.println("\tGroup Description : "+group.getDescription());
        System.out.println("\tGroup Size        : (0/"+group.getMembershipLimit()+")");
        System.out.println();
        return true;
    }

    public boolean addToGroup(String email, Group group) {
    	// check if user exists
        if (getUser(email) == null) {
            System.out.println("\nIt seems that user no longer exists\n");
            return false;
        }
        // check if group exists
        if (getGroupByID(group.getGroupID()) == null) {
            System.out.println("\nIt seems that group no longer exists\n");
            return false;
        }

        // check if added
        try {

            statement = connection.createStatement();
            String checkExist = "SELECT * FROM Membership " +
                    "WHERE groupID=" + group.getGroupID() + " AND member='" + email + "'";
            resultSet = statement.executeQuery(checkExist);

            if (resultSet.next()) {
                System.out.println("\nYou are already in "+group.getName()+"\n");
                return false;
            } else {
                String inputQuery = "INSERT INTO Membership VALUES (?,?)";
                prepStatement = connection.prepareStatement(inputQuery);
                prepStatement.setLong(1, group.getGroupID());
                prepStatement.setString(2, email);
                int worked = prepStatement.executeUpdate();
                prepStatement.close();
                if(worked != 0) System.out.println("\nCongratulations you have joined " + group.getName() +" :)\n\n");
                else System.out.println("\nSorry " + group.getName() +" is already full :(\n\n");
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Something Went wrong trying to join "+group.getName());
            return false;
        }finally{
        	try {
				statement.close();
				resultSet.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
        	
        }
    }

    public void printGroupContents(long groupID){
        try {
            Group group = getGroupByID(groupID);
            if(group != null){
                String query = "SELECT * FROM UserTable WHERE email IN (SELECT member FROM Membership WHERE groupID='"+groupID+"') ORDER BY lastName";
                statement = connection.createStatement();
                resultSet = statement.executeQuery(query);
                System.out.println("\n\n----------------------------------------------------------------------------");
                System.out.println("-- Group Name       : "+group.getName()+" ("+group.getMembers()+"/"+group.getMembershipLimit()+")");
                System.out.println("-- Group Description: "+group.getDescription());
                System.out.printf("\n   %-20s%-20s%-20s\n", "First", "Last","D.O.B");
                System.out.println("----------------------------------------------------------------------------");
                if(resultSet.next()){
                    do{
                        System.out.printf("   %-20s%-20s%-20s\n",resultSet.getString(2), resultSet.getString(3),
                                resultSet.getString(4).substring(0,10));
                    }while (resultSet.next());
                }else System.out.println("\nThere is no users this group right now\n");

            }
            System.out.println("\n\n");
        }catch (SQLException e){
            e.printStackTrace();
        }


    }

    public Group searchGroup(String groupName, Scanner sc){
        ArrayList<Group> closeMatches = null;
        try{
            groupName = groupName.toLowerCase().trim();
            StringBuilder sb = new StringBuilder();
            char[] suffix = groupName.toCharArray();
            for (int i = 0; i < suffix.length-1; i++) {
                sb.append(" OR name LIKE '%");
                for (int j = 0; j < (i+1); j++) sb.append(suffix[j]);
                sb.append("%'");
            }

            String nearMatch = "SELECT groupID, name, description, mLimit " +
                    "FROM GroupTable " +
                    "WHERE (lower(name) " +
                    "LIKE  '%"+groupName+"%')"+sb.toString();

            System.out.println(nearMatch);

            statement = connection.createStatement();
            resultSet = statement.executeQuery(nearMatch);


            closeMatches = new ArrayList<Group>();
            ArrayList<Group> perfectMatches = new ArrayList<Group>();

            while (resultSet.next()){
                Group g = new Group(resultSet.getLong(1));
                g.setName(resultSet.getString(2));
                g.setDescription(resultSet.getString(3));
                g.setMembershipLimit(resultSet.getInt(4));
                g.initMembers(connection);
                closeMatches.add(g);
                if(g.getName().equalsIgnoreCase(groupName))perfectMatches.add(g);
            }

            if(perfectMatches.size() == 1){
                return perfectMatches.get(0);
            }
            else if (perfectMatches.size() > 1){
                System.out.println("\n\n-- Multiple Matches\n");
                System.out.printf("%-5s%-35s%-20s%-15s\n", "ID", "Name", "Description","Members");
                System.out.println("----------------------------------------------------------------------------");
                for (int i = 0; i < perfectMatches.size(); i++) {
                    System.out.printf("%-5d%-35s%-20s%-15s\n",(i+1),
                            perfectMatches.get(i).getName(),
                            perfectMatches.get(i).getDescription(),
                            "("+perfectMatches.get(i).getMembers() +"/"+perfectMatches.get(i).getMembershipLimit()+")");
                }
                System.out.println();
                String choice;
                boolean valid;
                do {
                    valid = true;
                    System.out.print("More than one group with that name. Did you mean one From Above? (Y/N): ");
                    choice = sc.nextLine();
                    if(choice.equalsIgnoreCase("Y")){
                        int id;
                        boolean hold;
                        do {
                            hold = true;
                            System.out.print("Please Enter the ID Of The Group Above : ");
                            try {
                                id = Integer.parseInt(sc.nextLine());
                                if(id <= 0 || id > perfectMatches.size()){
                                    System.out.println("\n--> Please Only Enter a Valid ID From The List Above\n"); hold = false;
                                }
                                else return perfectMatches.get(id-1);
                            }catch (NumberFormatException e){
                                System.out.println("\n--> Please Only Enter a Valid ID From The List Above\n");
                                hold = false;
                            }
                        }while (!hold);
                    }
                    else if (!choice.equalsIgnoreCase("N")){
                        System.out.println("\nPlease Only (Y/N)\n");
                        valid = false;
                    }
                }while (!valid);
            }
            else if(closeMatches.size() == 0){
                System.out.println("\nThere Is No Group Name like that and No Partial Matches For that Group\n");
                return null;
            }
            else if(closeMatches.size() == 1){
                System.out.println("\n\n-- Closest Suggestion\n");
                System.out.printf("%-5s%-35s%-20s%-15s\n", "ID", "Name", "Description","Members");
                System.out.println("----------------------------------------------------------------------------");
                System.out.printf("%-5d%-35s%-20s%-15s\n",1,
                        closeMatches.get(0).getName(),
                        closeMatches.get(0).getDescription(),
                            "("+closeMatches.get(0).getMembers() +"/"+closeMatches.get(0).getMembershipLimit()+")");
                System.out.println("\n----------------------------------------------------------------------------");
                boolean valid;
                String choice;
                do {
                    valid = true;
                    System.out.print("There was only one close match. Were you looking for this Group (Y/N) : ");
                    choice = sc.nextLine();
                    if (choice.equalsIgnoreCase("Y")){
                        return closeMatches.get(0);
                    }
                    else if (!choice.equalsIgnoreCase("N")){
                        System.out.println("Please Only (Y/N)");
                        valid = false;
                    }
                }while (!valid);
                System.out.println("\n\n");
                return null;
            }
            else {
                System.out.println("\n\n-- Suggestions\n");
                System.out.printf("%-5s%-35s%-20s%-15s\n", "ID", "Name", "Description","Members");
                System.out.println("----------------------------------------------------------------------------");
                for (int i = 0; i < closeMatches.size(); i++) {
                    System.out.printf("%-5d%-35s%-20s%-15s\n",(i+1),
                            closeMatches.get(i).getName(),
                            closeMatches.get(i).getDescription(),
                            "("+closeMatches.get(i).getMembers() +"/"+closeMatches.get(i).getMembershipLimit()+")");
                }
                System.out.println();
                String choice;
                boolean valid;
                do {
                    valid = true;
                    System.out.print("There is no group with that name. Did you mean one From Above? (Y/N): ");
                    choice = sc.nextLine();
                    if(choice.equalsIgnoreCase("Y")){
                        int id;
                        boolean hold;
                        do {
                            hold = true;
                            System.out.print("Please Enter the ID Of The Group Above : ");
                            try {
                                id = Integer.parseInt(sc.nextLine());
                                if(id <= 0 || id > closeMatches.size()){
                                    System.out.println("\n--> Please Only Enter a Valid ID From The List Above\n"); hold = false;
                                }
                                else return closeMatches.get(id-1);
                            }catch (NumberFormatException e){
                                System.out.println("\n--> Please Only Enter a Valid ID From The List Above\n");
                                hold = false;
                            }
                        }while (!hold);
                    }
                    else if (!choice.equalsIgnoreCase("N")){
                        System.out.println("\nPlease Only (Y/N)\n");
                        valid = false;
                    }
                }while (!valid);
            }
            System.out.println("\n\n");
        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }


    public ArrayList<Long> displayMyGroups(String email) {
        ArrayList<Long> myGroups = null;
        try {
            String groupQuery = "SELECT DISTINCT groupID FROM Membership WHERE member= '" + email + "'";
            statement = connection.createStatement();
            resultSet = statement.executeQuery(groupQuery);
            System.out.println("\n\n-- Your Groups\n\n");
            System.out.printf("   %-20s%-35s%-20s\n", "#", "Group Name", "Members");
            System.out.println("----------------------------------------------------------------------------");
            int rank = 1;

            if (resultSet.next()) {
                myGroups = new ArrayList<Long>();
                do {
                    Group group = getGroupByID(resultSet.getLong(1));
                    System.out.printf("   %-20d%-35s%-20s\n\n   Description : %-20s\n\n",
                            rank,
                            group.getName(),
                            group.getMembers()+" / "+
                            group.getMembershipLimit(),
                            group.getDescription());
                    System.out.println("----------------------------------------------------------------------------");
                    rank++;
                } while (resultSet.next());
                System.out.println("\n\n");
            }
            else System.out.println("\nYou are not a member of any groups\n");
            statement.close();
            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return myGroups;
    }

    public ArrayList<Long> displayTopGroups(){
    	// currently display all groups
    	 try {
             statement = connection.createStatement();
             //String groupQuery = "SELECT * FROM GroupTable";

             String groupQuery = "SELECT GroupTable.groupID, GroupTable.name, T.C ,GroupTable.mLimit, GroupTable.description " +
                     "FROM GroupTable, "+
                     "(SELECT * FROM (SELECT groupID, COUNT(*) as C FROM Membership GROUP BY groupID ORDER BY COUNT(*) DESC) WHERE rownum <= 3) T "+
                     "WHERE T.groupID=GroupTable.groupID";


             resultSet = statement.executeQuery(groupQuery);

             System.out.println("\n\n-- Top Groups (By Population)\n\n");
             System.out.printf("   %-20s%-35s%-20s\n", "Rank", "Group Name", "Members");
             System.out.println("----------------------------------------------------------------------------");
             int rank = 1;

             if (resultSet.next()){
                 ArrayList<Long> topGroups = new ArrayList<Long>();
                do{
                    long gid = resultSet.getLong(1);
                    topGroups.add(gid);

                    System.out.printf("   %-20d%-35s%-20s\n\n   Description : %-20s\n\n",
                            rank,
                            resultSet.getString(2),
                            resultSet.getInt(3)+" / "+
                            resultSet.getInt(4),
                            resultSet.getString(5));
                    System.out.println("----------------------------------------------------------------------------");
                    rank++;
                }while (resultSet.next());
                 System.out.println("\n\n");
                 return topGroups;
             }
             else {
                 System.out.println("\nSorry All The Groups Are Currently Empty So There's No Top Groups\n");
             }

         }catch (SQLException e){
             e.printStackTrace();
         }
        return null;
    }


    /////////////////////////////////////////////////////////////////
    // Message Functions
    /////////////////////////////////////////////////////////////////

    public void displayUserRecentMessages(User user){
        // displays Recent 5 Messages given a users email
        try {

            String message = "SELECT T.R, T.time_sent FROM "+
                    "(SELECT recipientEmail AS R,time_sent " +
                    "FROM Message, User " +
                    "WHERE senderEmail='"+user.getEmail()+"' " +
                    "AND "+
                    "UNION " +
                    "SELECT senderEmail AS R,time_sent " +
                    "FROM Message,User " +
                    "WHERE recipientEmail='"+user.getEmail()+"') T " +
                    "WHERE rownum <= 5" +
                    "ORDER BY T.time_sent DESC";

            statement = connection.createStatement();
            resultSet = statement.executeQuery(message);

            int count = 1;
            while(resultSet.next()){
                System.out.println("----------------------------------------------------------------------------");
                System.out.printf("%2s%14s%34s\n\n",count,resultSet.getString(1),resultSet.getString(2));
                count++;
            }

        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public ArrayList<User> getRecentMessages(String email){
        // displays Recent 5 Messages given a users email
        return null;
    }

    public String displayConversation(User one, User two){
        if (one.getEmail().equalsIgnoreCase(two.getEmail())){
            System.out.println("\nYou cannot send a message to yourself\n");
            return null;
        }
        try{
            String conv = "SELECT senderEmail, time_sent, msg_subject, msg_body " +
                    "FROM Message " +
                    "WHERE (senderEmail='"+one.getEmail()+"' AND recipientEmail='"+two.getEmail()+"') " +
                    "OR (senderEmail='"+two.getEmail()+"' AND recipientEmail='"+one.getEmail()+"') ORDER BY time_sent ASC";

            statement = connection.createStatement();
            resultSet = statement.executeQuery(conv);

            if (resultSet.next()){
                do {
                    if (resultSet.getString(1).equals(one.getEmail())) {
                        System.out.println("-- You (" + resultSet.getString(2) + ")");
                        System.out.println(resultSet.getString(3));
                        String[] message = resultSet.getString(4).split("\\s+");
                        for (int i = 0; i < message.length; i++) {
                            System.out.print(message[i] + " ");
                            if (i % 8 == 0) System.out.println();
                        }
                    } else {
                        System.out.printf("%35s-- %s (%s)", "", one.getFirstName() + " " + two.getLastName(), resultSet.getString(2));
                        System.out.printf("%30s\n", resultSet.getString(3));
                        String[] message = resultSet.getString(4).split("\\s+");
                        for (int i = 0; i < message.length; i++) {
                            System.out.print(message[i] + " ");
                            if (i % 8 == 0) System.out.println();
                        }
                    }
                }while (resultSet.next());
            }
            else System.out.println("    You do not have any message history with this person at this time            ");


        }catch (SQLException e){
            e.printStackTrace();
        }
        return null; // null if no messages exist
    }

    public boolean sendMessage(Message m){
        // send message from email1 to email2
        try {
            String inputQuery = "INSERT INTO Message (senderEmail,recipientEmail,time_sent,msg_subject,msg_body) VALUES (?,?,?,?,?)";
            prepStatement = connection.prepareStatement(inputQuery);
            prepStatement.setString(1, m.getSenderEmail());
            prepStatement.setString(2, m.getRecipientEmail());
            prepStatement.setTimestamp(3, m.getTimeSent());
            prepStatement.setString(4, m.getMsgSubject());
            prepStatement.setString(5, m.getMsgBody());
            int worked = prepStatement.executeUpdate();
            if(worked == 0) return false;
            prepStatement.close();
        }catch (SQLException e){
            System.out.println("Failed to send that message");
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public void displayTopMessagers(int months, int users){
        // display these users by top #  of messages sent to these users
        //Display the top k users
        //who have sent or received the highest number of messages during the
        // past x months.
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
        String[] keyWords = searchTerm.split(" ");
        ArrayList<String> users = new ArrayList<String>();
        int count = 0;
        // displays all users similar to name
        try {
            statement = connection.createStatement();
            String pattern = null;

            for (int i = 0; i < keyWords.length;i++) {
                pattern = keyWords[i].toUpperCase();
                String searchUserQuery = "SELECT firstName, lastName FROM UserTable WHERE " +
                        "upper(firstName) LIKE ('%" + pattern + "%') OR " +
                        "upper(lastName) LIKE ('%" + pattern + "%') OR " +
                        "upper(email) LIKE ('%" + pattern + "%')";
                resultSet = statement.executeQuery(searchUserQuery);
                while(resultSet.next()){

                    String fName = resultSet.getString("firstName");
                    String lName = resultSet.getString("lastName");
                    StringBuilder sb = new StringBuilder();
                    sb.append(fName).append(lName);
                    String user = sb.toString();

                    if (users.contains(user)){

                    }else {
                        count++;
                        System.out.println(fName + " " +lName);
                        users.add(user);
                    }
                }
            }
            //System.out.println(count);
            if (count == 0){
                System.out.println("No User Found.");
            }
        }catch (SQLException e){
            System.out.println("Failed to send that message");
            e.printStackTrace();

        }finally{
            try{
                if (!(resultSet == null)) {
                    resultSet.close();
                }
                statement.close();
                connection.close();
            }catch (SQLException ex){
                ex.printStackTrace();
            }
        }
    }

    public void debug(){
        try {
            /*
            statement = connection.createStatement();
            String m = "SELECT * FROM Message";
            System.out.println(m);
            resultSet = statement.executeQuery(m);
            System.out.println("\n --> Message");
            /*
            msgID number(10) not null,
	senderEmail varchar2(128) not null,
	recipientEmail varchar2(128) not null,
	time_sent timestamp,
	msg_subject varchar2(1024),
	msg_body varchar2(1024),
             */
            /*
            System.out.printf("\n    %-20s%-20s%-20s-20s%-20s%-20s\n","msgID","senderEmail","recipientEmail","time_sent","msg_subject","msg_body");
            System.out.println("----------------------------------------------------------------------------");
            if (resultSet.next()){
                do{
                    System.out.printf("\n    %-20s%-20s%-20s-20s%-20s%-20s\n",resultSet.getString(1),resultSet.getString(2),
                            resultSet.getString(3),resultSet.getString(4),resultSet.getString(5),resultSet.getString(6));
                }while (resultSet.next());
            }
            */
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

                statement = connection.createStatement();
                String all3 = "SELECT * FROM GroupTable";
                System.out.println(all3);
                resultSet = statement.executeQuery(all3);
                System.out.println("\n --> Group Table");
                System.out.printf("\n    %-20s%-20s%-20s%-20s\n","id","name","des","limit");
                System.out.println("----------------------------------------------------------------------------");
                if (resultSet.next()){
                    do{
                        System.out.printf("    %-20s%-20s%-20s%-20s\n",resultSet.getString(1),resultSet.getString(2),resultSet.getString(3),resultSet.getString(4));
                    }while (resultSet.next());
                }
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

}