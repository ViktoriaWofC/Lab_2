package com.example.user.lab_2;

import java.io.File;
import java.util.Date;

/**
 * Created by User on 18.10.2016.
 */

public class Meeting {

    private String name;
    private String description;
    private String priority;//Urgent, Planned, Possible
    private String file;
    private String dateStart;
    private String dateEnd;

    public Meeting() {
    }

    public Meeting(String name, String description, String dateStart, String dateEnd,String priority, String file) {
        this.name = name;
        this.description = description;
        this.dateStart = dateStart;
        this.dateEnd = dateEnd;
        this.file = file;
        this.priority = priority;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDateStart() {
        return dateStart;
    }

    public void setDateStart(String dateStart) {
        this.dateStart = dateStart;
    }

    public String getDateEnd() {
        return dateEnd;
    }

    public void setDateEnd(String dateEnd) {
        this.dateEnd = dateEnd;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

}
