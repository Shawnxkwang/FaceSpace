package com.company;

/**
 * Created by Zach on 4/7/16.
 */
public class DBHelper {



    public boolean userExists(String email, String password){
        //Check  if user exists
        return false;
    }


    public boolean isEstablishedFriend(){
        //Check friendship is established
        return false;
    }

    public boolean isPending(){
        //Check if user exists
        return false;
    }

    public boolean createUser(User user){
        // Creates user retruns false if userExists
        if(userExists(user.getEmail(),user.getPassword())) return false;
        return true;
    }

    public boolean addPendingFriend(){
        // Adds Friend to users Pending
        return true;
    }

}
