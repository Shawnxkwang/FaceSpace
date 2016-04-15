// package com.company;


import java.sql.Date;

/**
 * Created by Zach on 4/12/16.
 */
public class User{
    private String firstName;
    private String lastName;
    private String email;
    private Date birthDate;

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    @Override
    public String toString() {
        return ("First Name = " + firstName +"\n"+
                "Last Name = " + lastName  +"\n"+
                "Email = " + email +"\n"+
                "Birth Date = " + birthDate+"\n");
    }
}
