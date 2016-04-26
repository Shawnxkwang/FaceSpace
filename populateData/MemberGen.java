// package com.company;

/**
 * Created by xiaokaiwang on 4/15/16.
 */
//STEP 1. Import required packages
import java.sql.*;
import java.text.SimpleDateFormat;
;
import java.util.*;
public class MemberGen {
    // JDBC driver name and database URL

    private static final String JDBC_DRIVER = "oracle.jdbc.driver.OracleDriver";
    private static final String DB_URL = "jdbc:oracle:thin:@class3.cs.pitt.edu:1521:dbclass";
    private static final String USER = "xiw69";
    private static final String PASS = "3799662";

    public static void main(String[] args) {
        Connection conn = null;
        Statement stmt = null;
        ArrayList<String> list = new ArrayList<String>();
        try{



            System.out.println("Connecting to database...");
            conn = DriverManager.getConnection(DB_URL,USER,PASS);


            System.out.println("Creating statement...");
            stmt = conn.createStatement();
            String sql;
            sql = "SELECT email from UserTable";
            ResultSet rs = stmt.executeQuery(sql);
         

            while(rs.next()){

                list.add(rs.getString("email"));

            }
            Random rn = new Random();
            int id1 = 0;
            int id2 = 0;
            String sql2;
            String sql3;
            for (int i = 0; i < 100; i++){

                String em1 = new String();
                em1 = list.get(i);
                id1 = rn.nextInt(10) + 1;
                id2 = rn.nextInt(10) + 1;
                while(id1 == id2){
                    if (id1 != 10){
                        id2 = id1+1;
                    }else{
                        id2 = id1 -1;
                    }
                }
                sql2 = "INSERT INTO Membership VALUES " +
                        "('"+id1+"', '"+em1+"')";
                sql3 = "INSERT INTO Membership VALUES " +
                        "('"+id2+"', '"+em1+"')";
                // stmt.executeQuery(sql2);
                // stmt.executeQuery(sql3);
                System.out.println(sql2);
                System.out.println(sql3);

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