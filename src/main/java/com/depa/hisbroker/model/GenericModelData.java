/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.depa.hisbroker.model;

/**
 *
 * @author Prasert
 */
public class GenericModelData {
    private String description;
    private String datetime;
    private String place;
    private String person;
    
    public GenericModelData(String description, String datetime, String place, String person){
        this.description = description;
        this.datetime = datetime;
        this.place = place;
        this.person = person;
    }
    
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getPerson() {
        return person;
    }

    public void setPerson(String person) {
        this.person = person;
    }
    
    
}
