package com.company;

import java.io.Console;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * Created by Zach on 4/7/16.
 */

//
public class Driver {
    private Scanner sc = new Scanner(System.in);
    private DBHelper dbHelper;
    //  Database credentials
    //  login to the DB.
    static final String JDBC_DRIVER = "oracle.jdbc.driver.OracleDriver";
    static final String DB_URL = "jdbc:oracle:thin:@class3.cs.pitt.edu:1521:dbclass";
    Connection conn = null;
    Statement stmt = null;
    static final String USER = "xiw69";
    static final String PASS = "3799662";

    Driver(){
        /*
        " ______                _____                        __   ___  "
        "|  ____|              / ____|                      /_ | / _ \ "
        "| |__ __ _  ___ ___  | (___  _ __   __ _  ___ ___   | || | | |"
        "|  __/ _` |/ __/ _ \  \___ \| '_ \ / _` |/ __/ _ \  | || | | |"
        "| | | (_| | (_|  __/  ____) | |_) | (_| | (_|  __/  | || |_| |"
        "|_|  \__,_|\___\___| |_____/| .__/ \__,_|\___\___|  |_(_)___/ "
        "                            | |                               "
        "                            |_|                               "

         */



        try{
            //Register JDBC driver
            Class.forName("oracle.jdbc.driver.OracleDriver");

            //Open a connection
            System.out.println("Connecting to database...");
            conn = DriverManager.getConnection(DB_URL,USER,PASS);

            // Clean-up Environment
            //stmt.close();
            //conn.close();
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
            }//end finally try
        }//end try


        while (true) {
            dbHelper = new DBHelper(null); // switch null to real connection eventually
            boolean validUser;
            do {
                validUser = true;
                switch (loginScreen()) {
                    case 1:
                        validUser = logInPrompt();
                        break;
                    case 2:
                        validUser = registerPrompt();
                        break;
                }
            } while (!validUser);

            while (validUser){
                switch (getMenuChoice()){

                }
            }

        }
    }
    public int loginScreen(){
        int menuChoice = 0;
        boolean valid;
        System.out.println("----------------------------------------------------------------------------");
        System.out.println("||                                                                        ||");
        System.out.println("||      ______                _____                        __   ___       ||");
        System.out.println("||     |  ____|              / ____|                      /_ | / _ \\      ||");
        System.out.println("||     | |__ __ _  ___ ___  | (___  _ __   __ _  ___ ___   | || | | |     ||");
        System.out.println("||     |  __/ _` |/ __/ _ \\  \\___ \\| '_ \\ / _` |/ __/ _ \\  | || | | |     ||");
        System.out.println("||     | | | (_| | (_|  __/  ____) | |_) | (_| | (_|  __/  | || |_| |     ||");
        System.out.println("||     |_|  \\__,_|\\___\\___| |_____/| .__/ \\__,_|\\___\\___|  |_(_)___/      ||");
        System.out.println("||                                 | |                                    ||");
        System.out.println("||                                 |_|                                    ||");
        System.out.println("----------------------------------------------------------------------------");
        do{
            valid = true;

            System.out.println("||                            1. Log In                                   ||");
            System.out.println("||                            2. Register                                 ||");
            System.out.println("----------------------------------------------------------------------------");
            System.out.print("Enter Option: ");
            try {
                menuChoice = Integer.parseInt(sc.nextLine());
                if(menuChoice != 1 && menuChoice != 2){
                    valid = false;
                    System.out.println("\n--   Invalid Input   --\n");
                }
            }catch (NumberFormatException e){
                System.out.println("\n--   Invalid Input   --\n");
                valid = false;
            }
        }while (!valid);
        return menuChoice;

    }

    public boolean logInPrompt(){
        String email;
        String temp = null;
        boolean valid;
        do{
            valid = true;
            System.out.println("----------------------------------------------------------------------------");
            System.out.println("||                      _                 _                               ||");
            System.out.println("||                     | |               (_)                              ||");
            System.out.println("||                     | |     ___   __ _ _ _ __                          ||");
            System.out.println("||                     | |    / _ \\ / _` | | '_ \\                         ||");
            System.out.println("||                     | |___| (_) | (_| | | | | |                        ||");
            System.out.println("||                     |______\\___/ \\__, |_|_| |_|                        ||");
            System.out.println("||                                   __/ |                                ||");
            System.out.println("||                                  |___/                                 ||");
            System.out.println("----------------------------------------------------------------------------");
            System.out.print("Email: ");
            email = sc.nextLine();
            if(!dbHelper.userExists(email)){
                System.out.println("\nEmail or Password does not match any Users in the database");
                System.out.print("Would you like to try again? (Y/N): "); temp = sc.nextLine();
                if (temp.equalsIgnoreCase("Y")) valid = false;
                else return false;
                System.out.println("\n");
            }
        }while (!valid);

        return true;
    }

    public boolean registerPrompt(){
        User user = new User();
        System.out.println("----------------------------------------------------------------------------");
        System.out.println("||         _____            _     _             _   _                     ||");
        System.out.println("||        |  __ \\          (_)   | |           | | (_)                    ||");
        System.out.println("||        | |__) |___  __ _ _ ___| |_ _ __ __ _| |_ _  ___  _ __          ||");
        System.out.println("||        |  _  // _ \\/ _` | / __| __| '__/ _` | __| |/ _ \\| '_ \\         ||");
        System.out.println("||        | | \\ \\  __/ (_| | \\__ \\ |_| | | (_| | |_| | (_) | | | |        ||");
        System.out.println("||        |_|  \\_\\___|\\__, |_|___/\\__|_|  \\__,_|\\__|_|\\___/|_| |_|        ||");
        System.out.println("||                     __/ |                                              ||");
        System.out.println("||                    |___/                                               ||");
        System.out.println("----------------------------------------------------------------------------");
        boolean valid;
        String temp;
        do{
            valid = true;
            System.out.print("Enter First Name: ");
            temp = sc.nextLine().trim();
            if(temp.length() == 0) valid = false;
            else user.setFirstName(temp);
        }while (!valid);

        do{
            valid = true;
            System.out.print("Enter Last Name: ");
            temp = sc.nextLine().trim();
            if(temp.length() == 0) valid = false;
            else user.setLastName(temp);
        }while (!valid);

        do{
            valid = true;
            System.out.print("Enter Valid Email Address: ");
            temp = sc.nextLine().trim();
            if (!isValidEmailAddress(temp)) valid = false;
            else user.setEmail(temp);
        }while (!valid);

        do{
            valid = true;
            System.out.print("Enter Date of Birth (xx/xx/xxxx): ");
            temp = sc.nextLine().trim();
            if(!isValidDate(temp)) valid = false;
            else user.setBirthDate(temp);
        }while (!valid);

        return dbHelper.createUser(user);
    }

    public void welcomeScreen(){
        System.out.println("----------------------------------------------------------------------------");
        System.out.println("          __      __       .__");
        System.out.println("         /  \\    /  \\ ____ |  |   ____  ____   _____   ____");
        System.out.println("        \\   \\/\\/   // __ \\|  | _/ ___\\/  _ \\ /     \\_/ __ \\ ");
        System.out.println("         \\        /\\  ___/|  |_\\  \\__(  <_> )  Y Y  \\  ___/");
        System.out.println("          \\__/\\  /  \\___  >____/\\___  >____/|__|_|  /\\___  >");
        System.out.println("                \\/       \\/          \\/            \\/     \\/");
        System.out.println("----------------------------------------------------------------------------");

    }

    public int getMenuChoice() {
        // WOOOO Text art
        System.out.println("----------------------------------------------------------------------------");
        System.out.println("                         __  __                  ");
        System.out.println("                        |  \\/  | ___ _ __  _   _ ");
        System.out.println("                        | |\\/| |/ _ \\ '_ \\| | | |");
        System.out.println("                        | |  | |  __/ | | | |_| |");
        System.out.println("                        |_|  |_|\\___|_| |_|\\__,_|    ");
        System.out.println("----------------------------------------------------------------------------");
        System.out.println("||                        1.  Display Friends                             ||");
        System.out.println("||                        2.  Send Friend Request                         ||");
        System.out.println("||                        3.  Group Menu                                  ||");
        System.out.println("||                        4.  My Messages                                 ||");
        System.out.println("||                        5.  Log out                                     ||");
        System.out.println("----------------------------------------------------------------------------");
        int option = 0;
        boolean valid;
        do {
            valid = true;
            System.out.print("Please Enter An Option from above (1-8): ");
            try {
                option = Integer.parseInt(sc.nextLine().trim());
                if (option <= 0 || option > 8) {
                    System.out.println("--> " + option + " is not a valid menu number\n"); valid = false;
                }
            } catch (NumberFormatException e) {
                System.out.println("--> Please only enter numbers\n"); valid = false;
            }
        } while (!valid);
        return option;
    }

    public boolean isValidEmailAddress(String email) {
        Pattern ptr = Pattern.compile("(?:(?:\\r\\n)?[ \\t])*(?:(?:(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*))*@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*|(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)*\\<(?:(?:\\r\\n)?[ \\t])*(?:@(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*(?:,@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*)*:(?:(?:\\r\\n)?[ \\t])*)?(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*))*@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*\\>(?:(?:\\r\\n)?[ \\t])*)|(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)*:(?:(?:\\r\\n)?[ \\t])*(?:(?:(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*))*@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*|(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)*\\<(?:(?:\\r\\n)?[ \\t])*(?:@(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*(?:,@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*)*:(?:(?:\\r\\n)?[ \\t])*)?(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*))*@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*\\>(?:(?:\\r\\n)?[ \\t])*)(?:,\\s*(?:(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*))*@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*|(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)*\\<(?:(?:\\r\\n)?[ \\t])*(?:@(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*(?:,@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*)*:(?:(?:\\r\\n)?[ \\t])*)?(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*))*@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*\\>(?:(?:\\r\\n)?[ \\t])*))*)?;\\s*)");
        return ptr.matcher(email).matches();
    }

    public boolean isValidDate(String dateToValdate) {

        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
        formatter.setLenient(false);
        Date parsedDate = null;
        try {
            parsedDate = formatter.parse(dateToValdate);
            return true;
        } catch (ParseException e) {
            //Handle exception
            return false;

        }

    }

    public static void main(String[] args){
        new Driver();
    }
}
