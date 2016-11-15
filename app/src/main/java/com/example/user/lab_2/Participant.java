package com.example.user.lab_2;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;

/**
 * Created by User on 08.11.2016.
 */

public class Participant implements Serializable {

    private String firstName;
    private String lastName;
    private String middleName;
    private String post;
    private String login;
    private String password;

    public Participant(){
    }


    public Participant(String lastName, String firstName, String middleName, String post,String login,String password) {
        this.lastName = lastName;
        this.firstName = firstName;
        this.middleName = middleName;
        this.post = post;
        this.login = login;
        this.password = password;
    }

    //public String getAllNames(){ return lastName+" "+firstName+" "+middleName;  }

    public String getLogin(){
        return login;
    }

    public void setLogin(String login){
        this.login = login;
    }

    public String getPassword(){
        return password;
    }

    public void setPassword(String password){
        this.password = password;
    }

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
