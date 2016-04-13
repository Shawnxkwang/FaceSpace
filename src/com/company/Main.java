package com.company;
/*
  Written by Thao N. Pham.
  Updated by: Lory Al Moakar, Roxana Georghiu, Nick R. Katsipoulakis
  Purpose: Demo JDBC for CS1555 Class

  IMPORTANT (otherwise, your code may not compile)
  Same as using sqlplus, you NEED TO SET oracle environment variables by
  sourcing bash.env or tcsh.env
*/

import java.sql.*;  //import the file containing definitions for the parts
import java.text.ParseException;


//import oracle.jdbc.*;


//needed by java for database connection and manipulation

public class Main {
    private static Connection connection; //used to hold the jdbc connection to the DB
    private Statement statement; //used to create an instance of the connection
    private PreparedStatement prepStatement; //used to create a prepared statement, that will be later reused
    private ResultSet resultSet; //used to hold the result of your query (if one
    // exists)
    private String query;  //this will hold the query we are using


    public Main(int example_no) {

        switch ( example_no) {
            case 0:
                Example0();
                break;
            case 1:
                Example1();
                break;
            case 2:
                Example2(2);
                break;
            case 3:
                Example2(3);
                break;
            case 4:
                Example4();
                break;
            case 5:
                Example5();
                break;
            default:
                System.out.println("Example not found for your entry: " + example_no);
                try {
                    connection.close();
                }
                catch(Exception Ex)  {
                    System.out.println("Error connecting to database.  Machine Error: " +
                            Ex.toString());
                }
                break;
        }

    }

    /////////////////EXAMPLE 0 //////////////////////////
    public void Example0() {

        int counter = 1;
	/*We will now perform a simple query to the database, asking for all the
	  records it has.  For your project, performing queries will be similar*/
        try{
            statement = connection.createStatement(); //create an instance
            String selectQuery = "SELECT * FROM register"; //sample query

            resultSet = statement.executeQuery(selectQuery); //run the query on the DB table

	    /*the results in resultSet have an odd quality. The first row in result
	      set is not relevant data, but rather a place holder.  This enables us to
	      use a while loop to go through all the records.  We must move the pointer
	      forward once using resultSet.next() or you will get errors*/

            while (resultSet.next()) //this not only keeps track of if another record
            //exists but moves us forward to the first record
            {
                System.out.println("Record " + counter + ": " +
                        resultSet.getString(1) + ", " + //since the first item was of type
                        //string, we use getString of the
                        //resultSet class to access it.
                        //Notice the one, that is the
                        //position of the answer in the
                        //resulting table
                        resultSet.getLong(2) + ", " +   //since second item was number(10),
                        //we use getLong to access it
                        resultSet.getDate(3)); //since type date, getDate.
                counter++;
            }


	    /*Now, we show an insert, using preparedStatement.
	      Of course for this you can also write the query
	      directly as the above case with select, and vice versa. */

            // a string that stores the query. Put question marks as placeholders for the
            // values you need to enter. Each question mark will later be replaced with
            // the value specified by the set* method
            query = "insert into Register values (?,?,?)";
            prepStatement = connection.prepareStatement(query);

            String name = "student 2";
            long classid = 1;
            // This is how you can specify the format for the dates you will use
            java.text.SimpleDateFormat df = new java.text.SimpleDateFormat("yyyy-MM-dd");
            // This is how you format a date so that you can use the setDate format below
            java.sql.Date date_reg = new java.sql.Date (df.parse("2012-02-24").getTime());


            // You need to specify which question mark to replace with a value.
            // They are numbered 1 2 3 etc..
            prepStatement.setString(1, name);
            prepStatement.setLong(2, classid);
            prepStatement.setDate(3, date_reg);
            // Now that the statement is ready. Let's execute it. Note the use of
            // executeUpdate for insertions and updates instead of executeQuery for
            // selections.
            prepStatement.executeUpdate();

            //I will show the insert worked by selecting the content of the table again
            //statement = connection.createStatement();
            //query = "SELECT * FROM Register";

            resultSet = statement.executeQuery(selectQuery);
            System.out.println("\nAfter the insert, data is...\n");
            counter=1;
            while(resultSet.next()) {
                System.out.println("Record " + counter + ": " +
                        resultSet.getString(1) + ", " +
                        resultSet.getLong(2) + ", " +
                        resultSet.getDate(3));
                counter ++;
            }
            resultSet.close();
	    /*
	     * The preparedStatement can be and should be reused instead of creating a new object.
	     * NOTE that when you have many insert statements (more than 300), creating a new statement
	     * for every insert will end up in throwing an error.
	     */

            //Reuse of the prepare statement

            prepStatement.setString(1, "student 3");
            prepStatement.setLong(2, 2);
            prepStatement.setDate(3, date_reg);
            prepStatement.executeUpdate();

            resultSet = statement.executeQuery(selectQuery);
            System.out.println("\nAfter the insert, data is...\n");
            counter=1;
            while(resultSet.next()) {
                System.out.println("Record " + counter + ": " +
                        resultSet.getString(1) + ", " +
                        resultSet.getLong(2) + ", " +
                        resultSet.getDate(3));
                counter ++;
            }
            resultSet.close();
        }
        catch(SQLException Ex) {
            System.out.println("Error running the sample queries.  Machine Error: " +
                    Ex.toString());
        } catch (ParseException e) {
            System.out.println("Error parsing the date. Machine Error: " +
                    e.toString());
        }
        finally{
            try {
                if (statement != null) statement.close();
                if (prepStatement != null) prepStatement.close();
            } catch (SQLException e) {
                System.out.println("Cannot close Statement. Machine error: "+e.toString());
            }
        }
    }


    /*
     * @desc: Multi-version concurrency control in Oracle
     */

    ///////////////////EXAMPLE 1////////////////////////
    public void Example1() {
        try {
            connection.setAutoCommit(false); //the default is true and every statement executed is considered a transaction.
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            statement = connection.createStatement();

            query = "update class set max_num_students = 5 where classid = 1";
            int result = statement.executeUpdate(query);

            //sleep for 5 seconds, so that we have time to switch to the other transaction
            Thread.sleep(5000);

            query ="select * from CLASS";
            ResultSet resultSet =statement.executeQuery(query);
            System.out.println("ClassID\tMAX_NUM_STUDENTS\tCUR_NUM_STUDENTS");
            while(resultSet.next())
            {
                System.out.println(resultSet.getLong(1)+"\t"+resultSet.getLong(2)+"\t"+resultSet.getDouble(3));
            }

	    /*
	     * Releases this ResultSet object's database and JDBC resources immediately instead of waiting for this to happen when it is automatically closed.
	     */
            resultSet.close();
            //now rollback to end the transaction and release the lock on data.
            //You can use connection.commit() instead for this example, I just don't want to change the value
            connection.rollback();
            System.out.println("Transaction Rolled Back!");
        }
        catch(Exception Ex)
        {
            System.out.println("Machine Error: " +
                    Ex.toString());
        }
        finally{
            try {
                if (statement!=null) statement.close();
            } catch (SQLException e) {
                System.out.println("Cannot close Statement. Machine error: "+e.toString());
            }
        }

    }

    /*
     * @desc: Example2: (Implicit) Unrepeatable read problem
     * Example3: Serializable Is@rec7db.sqlolation Level
     */
    //////////EXAMPLE 2 + 3//////////////////////////////

    public void Example2(int mode ) {

        try {
            connection.setAutoCommit(false); //the default is true and every statement executed is considered a transaction.
            if ( mode == 2 )
                connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED); //which is the default
            else
                connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            statement = connection.createStatement();


            //read the maximum and current number of students in the class
            query = "SELECT max_num_students, cur_num_students FROM class where classid = 1";
            resultSet = statement.executeQuery(query);

            //note that there is no sleep here in this transaction

            int max, cur;
            if(resultSet.next()) {
                max = resultSet.getInt(1);
                cur = resultSet.getInt(2);
                System.out.println( "Max is: " + max + " Cur is: " + cur);
                //sleep for 5 seconds, so that we have time to switch to the other transaction
                Thread.sleep(5000);

                if(cur<max) {

                    query = "update class set cur_num_students = cur_num_students +1 where classid = 1";
                    int result = statement.executeUpdate(query);
                    if (result == 1)
                        System.out.println("Update is successful " + result);
                    else
                        System.out.println("No rows were updated");
                }
                else {
                    System.out.println("The class is full");
                }

            }
            connection.commit();
            resultSet.close();
        }
        catch(Exception Ex)  {
            System.out.println("Machine Error: " +
                    Ex.toString());
        }
        finally{
            try {
                if (statement!=null) statement.close();
            } catch (SQLException e) {
                System.out.println("Cannot close Statement. Machine error: "+e.toString());
            }
        }
    }

    /*
     * @desc: Exclusive Lock acquired by using FOR UPDATE OF
     */
    ////// EXAMPLE 4 /////////////////////////////////////////////////////

    public void Example4() {
        try{
            connection.setAutoCommit(false); //the default is true and every statement executed is considered a transaction.
            connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED); //which is the default
            statement = connection.createStatement();


            //read the maximum and current number of students in the class
            query = "SELECT max_num_students, cur_num_students "+
                    "FROM class where classid = 1 "+
                    "FOR UPDATE OF cur_num_students";
            resultSet = statement.executeQuery(query);

            //note that there is no sleep here in this transaction

            int max, cur;
            if(resultSet.next()) {
                max = resultSet.getInt(1);
                cur = resultSet.getInt(2);
                System.out.println( "Max is: " + max + " Cur is: " + cur);
                //sleep for 5 seconds, so that we have time to switch to the other transaction
                Thread.sleep(5000);

                if(cur<max) {

                    query = "update class set cur_num_students = cur_num_students +1 where classid = 1";
                    int result = statement.executeUpdate(query);

                    if (result == 1)
                        System.out.println("Update is successful " + result);
                    else
                        System.out.println("No rows were updated");
                }
                else{ System.out.println("The class is full");}

            }

            //We need this because the connection was set with auto-commit=false
            connection.commit();
            resultSet.close();
        }
        catch(Exception Ex) {
            System.out.println("Machine Error: " +
                    Ex.toString());
        }
        finally{
            try {
                if (statement !=null) statement.close();
            } catch (SQLException e) {
                System.out.println("Cannot close Statement. Machine error: "+e.toString());
            }
        }
    }

    // //// EXAMPLE 5 /////////////////////////////////////////////////////

    public void Example5() {
        try {
            connection.setAutoCommit(false); // the default is true and every
            // statement executed is
            // considered a transaction.
            statement = connection.createStatement();

            query = "update class set max_num_students = 10 where classid = 1";
            int result = statement.executeUpdate(query);
            if (result == 1)
                System.out.println("Update1 is successful " + result);
            else
                System.out.println("No rows were updated");

            Thread.sleep(5000);

            query = "update class set max_num_students = 10 where classid = 2";
            result = statement.executeUpdate(query);

            if (result == 1)
                System.out.println("Update2 is successful " + result);
            else
                System.out.println("No rows were updated");

            connection.commit();

        } catch (Exception Ex) {
            System.out.println("Machine Error: " + Ex.toString());
        }
        finally{
            try {
                if (statement!=null) statement.close();
            } catch (SQLException e) {
                System.out.println("Cannot close Statement. Machine error: "+e.toString());
            }
        }

    }

    public static void main(String args[]) throws SQLException
    {
    /* Making a connection to a DB causes certain exceptions.  In order to handle
	   these, you either put the DB stuff in a try block or have your function
	   throw the Exceptions and handle them later.  For this demo I will use the
	   try blocks */

        String username, password;
        username = "zjw13"; //This is your username in oracle
        password = "3832025"; //This is your password in oracle

        try{
            System.out.println("Registering DB..");
            // Register the oracle driver.

           // DriverManager.registerDriver (new oracle.jdbc.driver.OracleDriver());


            System.out.println("Set url..");
            //This is the location of the database.  This is the database in oracle
            //provided to the class
            String url = "jdbc:oracle:thin:@class3.cs.pitt.edu:1521:dbclass";

            System.out.println("Connect to DB..");
            //create a connection to DB on class3.cs.pitt.edu
            connection = DriverManager.getConnection(url, username, password);
            Main demo = new Main(Integer.parseInt(args[0]));

        }
        catch(Exception Ex)  {
            System.out.println("Error connecting to database.  Machine Error: " +
                    Ex.toString());
        }
        finally
        {
		/*
		 * NOTE: the connection should be created once and used through out the whole project;
		 * Is very expensive to open a connection therefore you should not close it after every operation on database
		 */
            connection.close();
        }
    }
}
