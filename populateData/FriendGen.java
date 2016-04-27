// package com.company;

/**
 * Created by xiaokaiwang on 4/15/16.
 */
//STEP 1. Import required packages
import java.sql.*;
import java.text.SimpleDateFormat;
;
import java.util.*;
public class FriendGen {
    // JDBC driver name and database URL

    private static final String JDBC_DRIVER = "oracle.jdbc.driver.OracleDriver";
    private static final String DB_URL = "jdbc:oracle:thin:@class3.cs.pitt.edu:1521:dbclass";


    public static void main(String[] args) {
        Connection conn = null;
        Statement stmt = null;
        ArrayList<String> arr = new ArrayList<String>();
        Scanner sc = new Scanner(System.in);
        System.out.println("Please enter your username: ");
        String USER = sc.nextLine();
        System.out.println("Please enter your password: ");
        String PASS = sc.nextLine();

        try{

            Class.forName("oracle.jdbc.driver.OracleDriver");

            System.out.println("Connecting to database...");
            conn = DriverManager.getConnection(DB_URL,USER,PASS);

            System.out.println(conn.toString());
            System.out.println("Creating statement...");
            stmt = conn.createStatement();
            String sql;
            sql = "SELECT email from UserTable";
            ResultSet rs = stmt.executeQuery(sql);

            while(rs.next()){


                String email = rs.getString("email");
                arr.add(email);


            }
            int n = 0;
            String sql2;
            for (int i = 1; i < 100; i++) {


                String em1 = arr.get(i);
                java.util.Date date1 = new java.util.Date();
                for (int j = 0;j < i;j++) {
                    java.util.Date date2 = new java.util.Date();
                    String em2 = arr.get(j);
                    sql2 = "INSERT INTO Friendship VALUES('" + em1 + "', '" + em2 + "', " +
                            "TO_TIMESTAMP('" + new Timestamp(date1.getTime()) + "','YYYY-MM-DD HH24:MI:SS:FF')," +
                            "TO_TIMESTAMP('" + new Timestamp(date2.getTime()) + "','YYYY-MM-DD HH24:MI:SS:FF'))";
                    //stmt.executeQuery(sql2);
                    System.out.println(sql2);
                    n++;
                    if (n == 500){
                        break;
                    }
                }
                if (n == 500){
                    break;
                }

            }





            rs.close();
            stmt.close();
            conn.close();
        }catch(SQLException se){
            //Handle errors for JDBC
            se.printStackTrace();
        }catch(Exception e){
            //Handle errors for Class.forName
            e.printStackTrace();
        }finally{
            //finally block used to close resources
            try{
                if(stmt!=null)
                    stmt.close();
            }catch(SQLException se2){
            }// nothing we can do
            try{
                if(conn!=null)
                    conn.close();
            }catch(SQLException se){
                se.printStackTrace();
            }
        }
        System.out.println("Goodbye!");
    }
}