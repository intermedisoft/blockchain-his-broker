/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.depa.hisbroker.model;

import java.util.ArrayList;
import java.util.List;
import org.joda.time.LocalDate;
import org.joda.time.Years;


/**
 *
 * @author Prasert
 */
public class MedicalData {
    private String id;
    private String prename;
    private String firstname;
    private String lastname;
    private String birthdate;
    private String height;
    private String weight;
    private Iterable<List<GenericModel>> checkups = new ArrayList();
    private Iterable<GenericModel> medications = new ArrayList();
    private Iterable<List<GenericModel>> xrays = new ArrayList();
    private Iterable<GenericModel> diagnosises = new ArrayList();
    private Iterable<GenericModel> procedures = new ArrayList();
    private Iterable<GenericModel> medHistorys = new ArrayList();
    private Iterable<GenericModel> vaccines = new ArrayList();
    private Iterable<GenericModel> demographics = new ArrayList();
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPrename() {
        return prename;
    }

    public void setPrename(String prename) {
        this.prename = prename;
    }
    
    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }
   
    public String getAge() {
        LocalDate jodaBirthdate = LocalDate.parse(this.birthdate);
        Years jodaAge = Years.yearsBetween(jodaBirthdate, LocalDate.now());
        return jodaAge.getYears()+" Years";
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getBmi() {
        String bmiStr = "0.00 [Invalid]";
        try{
            double weightNum = Double.parseDouble(weight);
            double heightNum = Double.parseDouble(height);
            if (weightNum > 2 && weightNum < 200 && heightNum > 50 && heightNum < 250) {
                double bmi = (weightNum * 10000) / (heightNum * heightNum);
                if (bmi < 18.5) {
                    bmiStr = String.format("%.2f",bmi) + " [Underweight]";
                } else if (bmi < 25) {
                    bmiStr = String.format("%.2f",bmi) + " [Normal]";
                } else if (bmi < 30) {
                    bmiStr = String.format("%.2f",bmi) + " [Overweight]";
                } else {
                    bmiStr = String.format("%.2f",bmi) + " [Obese]";
                }
            }
        }catch(NumberFormatException ex){}
        
        return bmiStr;
    }
    
    public Iterable<List<GenericModel>> getCheckups() {
        return checkups;
    }

    public void setCheckups(Iterable<List<GenericModel>> checkups) {
        this.checkups = checkups;
    }

    public Iterable<GenericModel> getMedications() {
        return medications;
    }

    public void setMedications(Iterable<GenericModel> medications) {
        this.medications = medications;
    }

    public Iterable<List<GenericModel>> getXrays() {
        return xrays;
    }

    public void setXrays(Iterable<List<GenericModel>> xrays) {
        this.xrays = xrays;
    }

    public Iterable<GenericModel> getDiagnosises() {
        return diagnosises;
    }

    public void setDiagnosises(Iterable<GenericModel> diagnosises) {
        this.diagnosises = diagnosises;
    }

    public Iterable<GenericModel> getProcedures() {
        return procedures;
    }

    public void setProcedures(Iterable<GenericModel> procedures) {
        this.procedures = procedures;
    }

    public Iterable<GenericModel> getMedHistorys() {
        return medHistorys;
    }

    public void setMedHistorys(Iterable<GenericModel> medHistorys) {
        this.medHistorys = medHistorys;
    }

    public Iterable<GenericModel> getVaccines() {
        return vaccines;
    }

    public void setVaccines(Iterable<GenericModel> vaccines) {
        this.vaccines = vaccines;
    }

    public Iterable<GenericModel> getDemographics() {
        return demographics;
    }

    public void setDemographics(Iterable<GenericModel> demographics) {
        this.demographics = demographics;
    }
    
    @Override
    public String toString() {
        return "MedicalData{" + "id=" + id + ", prename=" + prename + ", firstname=" + firstname + ", lastname=" + lastname + ", birthdate=" + birthdate + ", height=" + height + ", weight=" + weight + ", checkups=" + checkups + ", medications=" + medications + ", xrays=" + xrays + ", diagnosises=" + diagnosises + ", procedures=" + procedures + ", medHistorys=" + medHistorys + ", vaccines=" + vaccines + ", demographics=" + demographics + '}';
    }
    
}
