package com.company;

/**
 * Created by Zach on 4/20/16.
 */
// package com.company;   //comment out for thoth


/**
 * Created by Tong on 4/17/16.
 */
public class Group{

    private long groupID;
    private String name;
    private String description;
    private long mLimit;

    // constructor
    public Group(){
        this.mLimit = 100;
        this.description= "description";
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

    public long getMembershipLimit() {
        return mLimit;
    }

    public void setMembershipLimit(long mLimit) {
        this.mLimit = mLimit;
    }

    @Override
    public String toString() {
        return ("groupID = " + groupID +"\n"+
                "name = " + name  +"\n"+
                "description = " + description +"\n"+
                "mLimit = " + mLimit+"\n");
    }
}
