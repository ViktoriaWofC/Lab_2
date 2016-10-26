package com.example.user.lab_2;

/**
 * Created by User on 18.10.2016.
 */

public class Meeting {

    private String name;
    private String names;
    private String description;
    private String dateStart;
    private String dateEnd;
    private String priority;

    public Meeting() {
    }

    public Meeting(String name, String names) {
        this.name = name;
        this.names = names;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNames() {
        return names;
    }

    public void setNames(String names) {
        this.names = names;
    }

}
