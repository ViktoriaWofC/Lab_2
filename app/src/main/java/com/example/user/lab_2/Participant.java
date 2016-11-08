package com.example.user.lab_2;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by User on 08.11.2016.
 */

public class Participant {

    private String firstName;
    private String lastName;
    private String middleName;
    private String post;

    public Participant() {
    }

    public Participant(String lastName, String firstName, String middleName, String post) {
        this.lastName = lastName;
        this.firstName = firstName;
        this.middleName = middleName;
        this.post = post;
    }

    //public String getAllNames(){ return lastName+" "+firstName+" "+middleName;  }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String name) {
        this.lastName = name;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String name) {
        this.firstName = name;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String name) {
        this.middleName = name;
    }



}
