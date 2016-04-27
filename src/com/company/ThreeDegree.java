import java.security.Principal;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Pattern;
public class ThreeDegree {
	  //  Database credentials
    //  login to the DB.
     static final String JDBC_DRIVER = "oracle.jdbc.driver.OracleDriver";
     static final String DB_URL = "jdbc:oracle:thin:@class3.cs.pitt.edu:1521:dbclass";
    
     //xiw69
     //3799662
     private static final String USER = "xiw69";
     private static final String PASS = "3799662";
     private static Connection connection;
     private static Statement statement;
     private static ResultSet resultSet; //used to hold the result of your query (if one

     public static void main(String[] args){

		  String email1 = "1@a.com";
		  String email2 = "10@a.com";

		  try{
	            //Register JDBC driver
	           // Class.forName("oracle.jdbc.driver.OracleDriver");
	            //Open a connection
	            DriverManager.registerDriver (new oracle.jdbc.driver.OracleDriver());
	            System.out.println("Connecting to database...");
	            connection  = DriverManager.getConnection(DB_URL,USER,PASS);
	            

	    // Second Degree
        statement = connection.createStatement();
        String degree1 = "(SELECT person2 FROM Friendship WHERE person1='"+email1+"' AND timeEstablished IS NOT NULL "
                        + "UNION " +
                         "SELECT person1 FROM Friendship WHERE person2='"+email1+"' AND timeEstablished IS NOT NULL) ";

        resultSet = statement.executeQuery(degree1);   // contains email1's direct friends, we will use them to trace further

        ArrayList<String> friends_1 = new  ArrayList<String>();

        HashMap<String, ArrayList<String>> friends_2_map = new HashMap<String, ArrayList<String>>();

        HashMap<String,  HashMap<String, ArrayList<String>>> friends_3_map = new HashMap<String,  HashMap<String, ArrayList<String>>>();

        while (resultSet.next()) {
             friends_1.add(resultSet.getString(1));   // contains email1's direct friends,
        }


        for(String friend : friends_1){
            String degree2 = "SELECT person2 FROM Friendship WHERE person1='"+friend+"' AND person2 != '"+ email1+ "' AND timeEstablished IS NOT NULL "
                               + "UNION " +
                             "SELECT person1 FROM Friendship WHERE person2='"+friend+"' AND person1 != '"+ email1+ "' AND timeEstablished IS NOT NULL ";

               resultSet = statement.executeQuery(degree2);  

                ArrayList<String> friends_2 = new ArrayList<String>();
                while (resultSet.next()) {
                  friends_2.add(resultSet.getString(1));  // contains email1's 2-degree friends,         
                }
                if( friends_2.size()>0 )
                   friends_2_map.put(friend, friends_2);
       }


       for( String friend1 : friends_2_map.keySet()){

           HashMap<String, ArrayList<String>> friends_2_map_temp = new HashMap<String, ArrayList<String>>();
             for(String friend : friends_2_map.get(friend1)){


//                 String degree3 = "SELECT person2 FROM Friendship WHERE person1='"+friend+"' AND person2 != '"+ email1+ "' AND person2 NOT IN "+ friends_1 +" AND timeEstablished IS NOT NULL "
//                               + "UNION " +
//                               "SELECT person1 FROM Friendship WHERE person2='"+friend+"' AND person1 != '"+ email1+ "' AND person1 NOT IN "+ friends_1 +" AND timeEstablished IS NOT NULL";

            	 String degree3 = "SELECT person2 FROM Friendship WHERE person1='"+friend+"' AND person2 = '"+ email2+ " AND timeEstablished IS NOT NULL "
                         + "UNION " +
                         "SELECT person1 FROM Friendship WHERE person2='"+friend+"' AND person1 = '"+ email2+ " AND timeEstablished IS NOT NULL";

                resultSet = statement.executeQuery(degree3);  

                 ArrayList<String> friends_3 = new ArrayList<String>();
                  while (resultSet.next()) {
                      friends_3.add(resultSet.getString(1));  // contains email1's 3-degree friends,
                  }
                  friends_2_map_temp.put(friend, friends_3);
             }
             friends_3_map.put(friend1, friends_2_map_temp);
       }

//print
       System.out.print(  "  ----->  " );

       for(String friend1 : friends_3_map.keySet()){
            for(String friend2 : friends_3_map.get(friend1).keySet() ){

                   for(String friend3 :   friends_3_map.get(friend1).get(friend2) ){
                           System.out.println( email1 + "  ----->  "+ friend1 + "  ----->  "+ friend2 + "  ----->  "+ friend3 );
                   }
            }
       }
		  }catch(SQLException e){
			 System.out.println(e);
		  }

	}

}
