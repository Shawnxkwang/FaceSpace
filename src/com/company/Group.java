package com.company;

/**
 * Created by Zach on 4/20/16.
 */
// package com.company;   //comment out for thoth


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by Tong on 4/17/16.
 */
public class Group{

    private long groupID;
    private String name;
    private String description;
    private String creator;
    private int mLimit;
    private int memberCount;

    // constructor
    public Group(long groupID){
        this.groupID = groupID;
    }
    public Group(){

    }

    public long getGroupID() {
        return groupID;
    }

    public void setGroupID(long groupID) {
        this.groupID = groupID;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMembershipLimit() {
        return mLimit;
    }

    public void initMembers(Connection connection){
        try {
            String getMem = "SELECT COUNT(member) FROM Membership WHERE groupID='"+groupID+"'";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(getMem);
            if (resultSet.next()) memberCount = resultSet.getInt(1);
            else System.out.println("FAILED");
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public int getMembers(){
        return memberCount;
    }

    public void setMembershipLimit(int mLimit) {
        this.mLimit = mLimit;
    }

    public void setCreator(String email){
        creator = email;
    }
    public String getCreator(){
        return creator;
    }
    @Override
    public String toString() {
        return ("groupID = " + groupID +"\n"+
                "name = " + name  +"\n"+
                "description = " + description +"\n"+
                "mLimit = " + mLimit+"\n");
    }
}
