/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.depa.hisbroker;

import com.depa.hisbroker.model.CheckupModel;
import com.depa.hisbroker.model.GenericModel;
import com.depa.hisbroker.model.MedicalData;
import com.depa.hisbroker.model.XrayModel;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 *
 * @author Prasert
 */
public class InMemoryMedicalDataRepository implements MedicalDataRepository{

    private final ConcurrentMap<String, MedicalData> medicalDatas = new ConcurrentHashMap<>();
    
    public InMemoryMedicalDataRepository(){
        installData("");
    }
    
    @Override
    public String save(CheckupModel checkup) {
        return null;
    }

    @Override
    public MedicalData findMedicalData(String id) {
        return this.medicalDatas.get(id);
    }
    
    @Override
    public final boolean installData(String type){
        MedicalData medicalData = new MedicalData();
        medicalData.setId("3809900380115");
        medicalData.setPrename("Mr.");
        medicalData.setFirstname("Navin");
        medicalData.setLastname("Leenatam");
        medicalData.setBirthdate("1977-07-07");
        medicalData.setWeight("57");
        medicalData.setHeight("173");
        List<GenericModel> demographics = new ArrayList<>();
        demographics.add(new GenericModel("Name: Navin Leenatum", "2017-03-01", "Phuket Hospital", "Dr.A"));
        demographics.add(new GenericModel("DOB: 1977-07-07", "2017-05-11", "Pensook Clinic", "Dr.B"));
        demographics.add(new GenericModel("Personal ID: 3809900380115", "2017-08-15", "Hadyai Hospital", "Dr.C"));
        demographics.add(new GenericModel("Sex: Male", "2017-08-15", "Hadyai Hospital", "Dr.C"));
        demographics.add(new GenericModel("Martital Status: Single", "2017-08-15", "Hadyai Hospital", "Dr.C"));
        demographics.add(new GenericModel("Language: English", "2017-08-15", "Hadyai Hospital", "Dr.C"));
        demographics.add(new GenericModel("Religion:", "2017-08-15", "Hadyai Hospital", "Dr.C"));
        demographics.add(new GenericModel("Primary Insurance: AIA", "2017-08-15", "Hadyai Hospital", "Dr.C"));
        medicalData.setDemographics(demographics);
        
        List<GenericModel> medications = new ArrayList<>();
        medications.add(new GenericModel("Metformin", "2017-03-01", "Phuket Hospital", "Dr.A"));
        medications.add(new GenericModel("Lipitor", "2017-05-11", "Pensook Clinic", "Dr.B"));
        medications.add(new GenericModel("Lisinopril", "2017-08-15", "Hadyai Hospital", "Dr.C"));
        medicalData.setMedications(medications);
        
        List<List<GenericModel>> checkups = new ArrayList<>();
        List<GenericModel> checkup = new ArrayList<>();
        checkup.add(new GenericModel("FBS: Normal ", "2017-03-01", "Phuket Hospital", "Dr.A"));
        checkup.add(new GenericModel("BUN: Normal", "2017-03-01", "Phuket Hospital", "Dr.A"));
        checkup.add(new GenericModel("Creatinine: Normal", "2017-03-01", "Phuket Hospital", "Dr.A"));
        checkup.add(new GenericModel("Cholesterol: Normal", "2017-03-01", "Phuket Hospital", "Dr.A"));
        checkup.add(new GenericModel("Triglyceride: Normal", "2017-03-01", "Phuket Hospital", "Dr.A"));
        checkup.add(new GenericModel("AST: Normal", "2017-03-01", "Phuket Hospital", "Dr.A"));
        checkup.add(new GenericModel("ALT: Normal", "2017-03-01", "Phuket Hospital", "Dr.A"));
        checkups.add(checkup);
        medicalData.setCheckups(checkups);
        
        
        List<GenericModel> medicalProblems = new ArrayList<>();
        medicalProblems.add(new GenericModel("diabetes", "2017-03-01", "Phuket Hospital", "Dr.A"));
        medicalData.setDiagnosises(medicalProblems);
        
        List<GenericModel> vaccines = new ArrayList<>();
        /*vaccines.add(new GenericModel("Blod Pressure: 150/80", "2017-03-01", "Phuket Hospital", "Dr.A"));
        vaccines.add(new GenericModel("Height: 152 cm", "2017-03-01", "Phuket Hospital", "Dr.A"));
        vaccines.add(new GenericModel("Weight: 68 kg", "2017-03-01", "Phuket Hospital", "Dr.A"));
        vaccines.add(new GenericModel("BMI: 29 kg/m^2", "2017-03-01", "Phuket Hospital", "Dr.A"));
        vaccines.add(new GenericModel("BMI Status: Overweight", "2017-03-01", "Phuket Hospital", "Dr.A"));
        vaccines.add(new GenericModel("Respiration: 22 per min", "2017-03-01", "Phuket Hospital", "Dr.A"));
        vaccines.add(new GenericModel("Temperature: 37.23 C", "2017-03-01", "Phuket Hospital", "Dr.A"));
        vaccines.add(new GenericModel("Pulse: 99 per min", "2017-03-01", "Phuket Hospital", "Dr.A"));
        vaccines.add(new GenericModel("Oxygen Saturation: 97%", "2017-03-01", "Phuket Hospital", "Dr.A"));*/
        medicalData.setVaccines(vaccines);
        
        List<GenericModel> allergies = new ArrayList<>();
        allergies.add(new GenericModel("penicillin", "2017-03-01", "Phuket Hospital", "Dr.A"));
        medicalData.setProcedures(allergies);
        
        this.medicalDatas.put(medicalData.getId(), medicalData);
        return true;
    }

    @Override
    public String requestAuthorize(String id) {
        return "GRANT";
    }
    
    @Override
    public String revokeAuthorize(String id) {
        return "REVOKE";
    }

    @Override
    public String save(XrayModel xrayModel) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
