// package com.company;

/**
 * Created by xiaokaiwang on 4/15/16.
 */
//STEP 1. Import required packages
import java.sql.*;
import java.text.SimpleDateFormat;
;
import java.util.*;
public class MessageGen {
    // JDBC driver name and database URL

    private static final String JDBC_DRIVER = "oracle.jdbc.driver.OracleDriver";
    // private static final String DB_URL = "jdbc:oracle:thin:hr/hr@localhost:1521/XE";
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



            System.out.println("Connecting to database...");
            conn = DriverManager.getConnection(DB_URL,USER,PASS);


            System.out.println("Creating statement...");
            stmt = conn.createStatement();
            String sql;
            sql = "SELECT *from UserTable";
            ResultSet rs = stmt.executeQuery(sql);


            while(rs.next()){


                String email = rs.getString("email");
                arr.add(email);
            }
            String sql2;
            int  i = 0;
            while (i < 310){
               for (int j = 0; j < arr.size(); j++){
                   Collections.shuffle(arr);
                   String em1 = arr.get(0);
                   java.util.Date date1= new java.util.Date();

                   String em2 = arr.get(1);
                   String sbj = em1+" TO "+em2;
                   String body = em1 + " Hello --> " + em2;
                   if (em1.equals(em2)){
                       em2 = arr.get(2);

                   }
                    i++;
                   sql2 = "INSERT INTO Message VALUES('"+i+"', '"+em1+"', '"+em2+"', " +
                           "TO_TIMESTAMP('"+new Timestamp(date1.getTime())+"','YYYY-MM-DD HH24:MI:SS:FF')," +
                           "'"+sbj+"', '"+body+"')";
                    System.out.println(sql2);
                 //  stmt.executeQuery(sql2);
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