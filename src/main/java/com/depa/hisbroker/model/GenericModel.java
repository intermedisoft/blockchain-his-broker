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
public class GenericModel {
    private String description;
    private byte[] rawData;
    private String datetime;
    private String place;
    private String person;
    
    public GenericModel(String description, String datetime, String place, String person){
        this.description = description;
        this.datetime = datetime;
        this.place = place;
        this.person = person;
    }
    
    public GenericModel(byte[] rawData, String datetime, String place, String person){
        this.rawData = rawData;
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

    public byte[] getRawData() {
        return rawData;
    }

    public void setRawData(byte[] rawData) {
        this.rawData = rawData;
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

    @Override
    public String toString() {
        return "GenericModelData{" + "description=" + description + ", datetime=" + datetime + ", place=" + place + ", person=" + person + '}';
    }
    
    
    
}
