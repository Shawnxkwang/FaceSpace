 package com.company;  // Un comment for thoth serverss


import java.sql.Date;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * Created by Zach on 4/7/16.
 */

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
public class Driver {

    //  Database credentials
    //  login to the DB.
    private static final String JDBC_DRIVER = "oracle.jdbc.driver.OracleDriver";
    private static final String DB_URL = "jdbc:oracle:thin:@class3.cs.pitt.edu:1521:dbclass";
    //xiw69
    //3799662
    private static final String USER = "xiw69";
    private static final String PASS = "3799662";

    private User currentUser; // used for all queries about the user

    private Scanner sc = new Scanner(System.in);
    private DBHelper dbHelper;

    Driver(){
        try{
            //Register JDBC driver
           // Class.forName("oracle.jdbc.driver.OracleDriver");
            //Open a connection
            DriverManager.registerDriver (new oracle.jdbc.driver.OracleDriver());
            System.out.println("Connecting to database...");
            Connection conn = DriverManager.getConnection(DB_URL,USER,PASS);


            dbHelper = new DBHelper(conn); // switch null to real connection eventually

            while (true) {
                boolean validUser;
                //Switch Loop for login and register Menu

                do {
                    validUser = true;
                    switch (loginScreen()) {
                        case 1:
                            validUser = logInPrompt();  // false if failed to find email in db
                            break;
                        case 2:
                            validUser = registerPrompt();  //false if failed to register
                            break;
                    }
                } while (!validUser);

                // Switch Loop for main Menu
                while (validUser){
                    switch (getMenuChoice()){

                        case 1:  // Friend Menu
                            boolean friendsMenu = true;
                            while (friendsMenu){
                                switch (getFriendMenuChoice()){
                                    case 1: // 1.  Friend Summary
                                        dbHelper.displayFriendSummary(currentUser.getEmail());
                                        hold();
                                        break;
                                    case 2:
                                        dbHelper.displayRequests(currentUser.getEmail());
                                        hold();
                                        break;
                                    case 3:
                                        dbHelper.createRequest(currentUser.getEmail(),friendAddEntry());
                                        hold();
                                        break;
                                    case 4:
                                        dbHelper.acceptRequest(currentUser.getEmail(),sc);
                                        hold();
                                        break;
                                    case 5:
                                        // three degrees
                                        dbHelper.threeDegrees(currentUser.getEmail(),sc);
                                        hold();
                                        break;
                                    case 6:
                                        friendsMenu = false;
                                        break;
                                }
                            }
                            break;
                        case 2:   // Group Menu
                            boolean groupMenu = true;
                            while (groupMenu){
                                switch (getGroupMenuChoice()){
                                    case 1: //Display My Groups

                                        break;
                                    case 2: // Accept Group Requests

                                        break;
                                    case 3: // Open Group

                                        break;
                                    case 4:
                                        groupMenu = false;
                                        break;
                                }
                            }
                            break;
                        case 3:  // My Messages
                            boolean messageMenu = true;
                            while (messageMenu){
                                switch (getMessageMenuChoice()){
                                    case 1: // Open recent conversation from number
                                        break;
                                    case 2: // Open Messages from an email
                                        break;
                                    case 3: // Top Messagers
                                        break;
                                    case 4: // Display Detailed Message History
                                        break;
                                    case 5:
                                        messageMenu = false;
                                        break;
                                }
                            }
                            break;
                        case 4: // Search for a user
                            validUser = false;
                            break;
                        case 5: // Delete a User
                            validUser = false;
                            break;
                        case 6: // Logout
                            validUser = false;
                            break;
                        //  Log out
                    }
                }

            }

        }catch (SQLException e){
              System.out.println(e);
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
                if(menuChoice != 1 && menuChoice != 2) valid = false;
            }catch (NumberFormatException e){
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
            currentUser = dbHelper.getUser(email);
            if(currentUser == null){    //User does not exist yet with that email
                System.out.println("\nEmail does not match any Users in the database");
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
            System.out.print("Enter Date of Birth (xx/xx/xxxx): "); //mm/dd/yyyyy
            temp = sc.nextLine().trim();
            Date date = isValidDate(temp);
            if(date == null) valid = false;
            else user.setBirthDate(date);
        }while (!valid);

        currentUser = user;

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

    public String friendAddEntry(){
        boolean valid;
        String temp;
        do{
            valid = true;
            System.out.print("Enter Email Of The Friend You Want To Add: ");
            temp = sc.nextLine().trim();
            if (!isValidEmailAddress(temp)) valid = false;
        }while (!valid);

        return temp;
    }

    public int getMenuChoice() {
        // WOOOO Text art
        System.out.println("----------------------------------------------------------------------------");
        System.out.println("||                         __  __                                         ||");
        System.out.println("||                        |  \\/  | ___ _ __  _   _                        ||");
        System.out.println("||                        | |\\/| |/ _ \\ '_ \\| | | |                       ||");
        System.out.println("||                        | |  | |  __/ | | | |_| |                       ||");
        System.out.println("||                        |_|  |_|\\___|_| |_|\\__,_|                       ||");
        System.out.println("----------------------------------------------------------------------------");
        System.out.println("||                        1.  Friend Menu                                 ||");
        System.out.println("||                        2.  Group Menu                                  ||");
        System.out.println("||                        3.  My Messages                                 ||");
        System.out.println("||                        4.  Search For A User                           ||");
        System.out.println("||                        5.  Delete My Account                           ||");
        System.out.println("||                        6.  Log out                                     ||");
        System.out.println("----------------------------------------------------------------------------");
        int option = 0;
        boolean valid;
        do {
            valid = true;
            System.out.print("Please Enter An Option from above (1-6): ");
            try {
                option = Integer.parseInt(sc.nextLine().trim());
                if (option <= 0 || option > 6) {
                    System.out.println("--> " + option + " is not a valid menu number\n"); valid = false;
                }
            } catch (NumberFormatException e) {
                System.out.println("--> Please only enter numbers\n"); valid = false;
            }
        } while (!valid);
        return option;
    }

    public int getFriendMenuChoice(){
        System.out.println("----------------------------------------------------------------------------");
        System.out.println("||                     ______    _                _                       ||");
        System.out.println("||                    |  ____|  (_)              | |                      ||");
        System.out.println("||                    | |__ _ __ _  ___ _ __   __| |___                   ||");
        System.out.println("||                    |  __| '__| |/ _ \\ '_ \\ / _` / __|                  ||");
        System.out.println("||                    | |  | |  | |  __/ | | | (_| \\__ \\                  ||");
        System.out.println("||                    |_|  |_|  |_|\\___|_| |_|\\__,_|___/                  ||");
        System.out.println("----------------------------------------------------------------------------");
        System.out.println("||                        1.  Display Friends                             ||");
        System.out.println("||                        2.  Display Requests                            ||");
        System.out.println("||                        3.  Send Request                                ||");
        System.out.println("||                        4.  Accept Friend Request                       ||");
        System.out.println("||                        5.  Three Degrees                               ||");
        System.out.println("||                        6.  Return To Main Menu                         ||");
        System.out.println("----------------------------------------------------------------------------");
        int option = 0;
        boolean valid;
        do {
            valid = true;
            System.out.print("Please Enter An Option from above (1-6): ");
            try {
                option = Integer.parseInt(sc.nextLine().trim());
                if (option <= 0 || option > 6) {
                    System.out.println("--> " + option + " is not a valid menu number\n"); valid = false;
                }
            } catch (NumberFormatException e) {
                System.out.println("--> Please only enter numbers\n"); valid = false;
            }
        } while (!valid);
        return option;
    }

    public int getGroupMenuChoice(){
        System.out.println("----------------------------------------------------------------------------");
        System.out.println("||                     _____                                              ||");
        System.out.println("||                    / ____|                                             ||");
        System.out.println("||                    | |  __ _ __ ___  _   _ _ __                        ||");
        System.out.println("||                    | | |_ | '__/ _ \\| | | | '_ \\                       ||");
        System.out.println("||                    | |__| | | | (_) | |_| | |_) |                      ||");
        System.out.println("||                     \\_____|_|  \\___/ \\__,_| .__/                       ||");
        System.out.println("||                                           | |                          ||");
        System.out.println("||                                           |_|                          ||");
        System.out.println("----------------------------------------------------------------------------");
        System.out.println("||                        1.  Display My Groups                           ||");
        System.out.println("||                        2.  Accept Group Requests                       ||");
        System.out.println("||                        3.  Open Group                                  ||");
        System.out.println("||                        4.  Return To Main Menu                         ||");
        System.out.println("----------------------------------------------------------------------------");
        int option = 0;
        boolean valid;
        do {
            valid = true;
            System.out.print("Please Enter An Option from above (1-4): ");
            try {
                option = Integer.parseInt(sc.nextLine().trim());
                if (option <= 0 || option > 4) {
                    System.out.println("--> " + option + " is not a valid menu number\n"); valid = false;
                }
            } catch (NumberFormatException e) {
                System.out.println("--> Please only enter numbers\n"); valid = false;
            }
        } while (!valid);
        return option;
    }

    public int getMessageMenuChoice(){
        System.out.println("----------------------------------------------------------------------------");
        System.out.println("||                __  __                                                  ||");
        System.out.println("||               |  \\/  | ___  ___ ___  __ _  __ _  ___  ___              ||");
        System.out.println("||               | |\\/| |/ _ \\/ __/ __|/ _` |/ _` |/ _ \\/ __|             ||");
        System.out.println("||               | |  | |  __/\\__ \\__ \\ (_| | (_| |  __/\\__ \\             ||");
        System.out.println("||               |_|  |_|\\___||___/___/\\__,_|\\__, |\\___||___/             ||");
        System.out.println("||                                            |___/                       ||");

        System.out.println("----------------------------------------------------------------------------");
        System.out.println("||                            -- Inbox --                                 ||");
        System.out.println("||                  Top Five Most Recent Conversations                    ||");
        System.out.println("----------------------------------------------------------------------------");
            // ____________________________________________________________________________________________
            // 1      First Last                                                   Message here fjdsjn...
            //        email@email.com
            // ____________________________________________________________________________________________
            // 2      First Last                                                   Message here fjdsjn...
            //        email@email.com
        System.out.println("||                  1.  Open Messages From Conversation Number            ||");
        System.out.println("||                  2.  Open Messages From An Email                       ||");
        System.out.println("||                  3.  Top Messagers                                     ||");
        System.out.println("||                  4.  Display Detailed Message History                  ||");
        System.out.println("||                  5.  Return To The Main Menu                           ||");
        System.out.println("----------------------------------------------------------------------------");
        String email;
        int option = 0;
        boolean valid;
        do {
            valid = true;
            System.out.print("Please Enter An Option from above (1-5): ");
            try {
                email = sc.nextLine();
                option = Integer.parseInt(email);
                if (option <= 0 || option > 5) {
                    System.out.println("--> " + option + " is not a valid menu number\n"); valid = false;
                }
            } catch (NumberFormatException e) {
                System.out.println("--> Please only enter numbers\n"); valid = false;
            }
        } while (!valid);
        return option;
    }

    public static boolean isValidEmailAddress(String email) {
        Pattern ptr = Pattern.compile("(?:(?:\\r\\n)?[ \\t])*(?:(?:(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*))*@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*|(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)*\\<(?:(?:\\r\\n)?[ \\t])*(?:@(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*(?:,@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*)*:(?:(?:\\r\\n)?[ \\t])*)?(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*))*@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*\\>(?:(?:\\r\\n)?[ \\t])*)|(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)*:(?:(?:\\r\\n)?[ \\t])*(?:(?:(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*))*@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*|(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)*\\<(?:(?:\\r\\n)?[ \\t])*(?:@(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*(?:,@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*)*:(?:(?:\\r\\n)?[ \\t])*)?(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*))*@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*\\>(?:(?:\\r\\n)?[ \\t])*)(?:,\\s*(?:(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*))*@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*|(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)*\\<(?:(?:\\r\\n)?[ \\t])*(?:@(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*(?:,@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*)*:(?:(?:\\r\\n)?[ \\t])*)?(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*))*@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*\\>(?:(?:\\r\\n)?[ \\t])*))*)?;\\s*)");
        return ptr.matcher(email).matches();
    }

    public Date isValidDate(String dateToValdate) {

        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
        formatter.setLenient(false);
        Date parsedDate = null;
        try {
            parsedDate = new Date(formatter.parse(dateToValdate).getTime());
            return parsedDate;
        } catch (ParseException e) {
            //Handle exception
            System.out.println("AN Error occured when trying to parse that date please try again");
            return null;

        }

    }

    public void hold(){
        System.out.print("Hit Enter to Continue...");
        sc.nextLine();
        System.out.println("\n\n\n");
    }

    public static void main(String[] args){
        new Driver();
    }
}
