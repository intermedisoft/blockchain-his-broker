/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.depa.hisbroker;

import com.depa.hisbroker.model.GenericModelData;
import com.depa.hisbroker.model.MedicalData;
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
    public MedicalData save(MedicalData medicalData) {
        String id = medicalData.getId();
        if (id == null) {
            throw new RuntimeException("Can not save medical data cause id is null.");
        }
        this.medicalDatas.put(id, medicalData);
        return medicalData;
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
        List<GenericModelData> demographics = new ArrayList<>();
        demographics.add(new GenericModelData("Name: Navin Leenatum", "2017-03-01", "Phuket Hospital", "Dr.A"));
        demographics.add(new GenericModelData("DOB: 1977-07-07", "2017-05-11", "Pensook Clinic", "Dr.B"));
        demographics.add(new GenericModelData("Personal ID: 3809900380115", "2017-08-15", "Hadyai Hospital", "Dr.C"));
        demographics.add(new GenericModelData("Sex: Male", "2017-08-15", "Hadyai Hospital", "Dr.C"));
        demographics.add(new GenericModelData("Martital Status: Single", "2017-08-15", "Hadyai Hospital", "Dr.C"));
        demographics.add(new GenericModelData("Language: English", "2017-08-15", "Hadyai Hospital", "Dr.C"));
        demographics.add(new GenericModelData("Religion:", "2017-08-15", "Hadyai Hospital", "Dr.C"));
        demographics.add(new GenericModelData("Primary Insurance: AIA", "2017-08-15", "Hadyai Hospital", "Dr.C"));
        medicalData.setDemographics(demographics);
        
        List<GenericModelData> medications = new ArrayList<>();
        medications.add(new GenericModelData("Metformin", "2017-03-01", "Phuket Hospital", "Dr.A"));
        medications.add(new GenericModelData("Lipitor", "2017-05-11", "Pensook Clinic", "Dr.B"));
        medications.add(new GenericModelData("Lisinopril", "2017-08-15", "Hadyai Hospital", "Dr.C"));
        medicalData.setDrugs(medications);
        
        List<List<GenericModelData>> checkups = new ArrayList<>();
        List<GenericModelData> checkup = new ArrayList<>();
        checkup.add(new GenericModelData("FBS: Normal ", "2017-03-01", "Phuket Hospital", "Dr.A"));
        checkup.add(new GenericModelData("BUN: Normal", "2017-03-01", "Phuket Hospital", "Dr.A"));
        checkup.add(new GenericModelData("Creatinine: Normal", "2017-03-01", "Phuket Hospital", "Dr.A"));
        checkup.add(new GenericModelData("Cholesterol: Normal", "2017-03-01", "Phuket Hospital", "Dr.A"));
        checkup.add(new GenericModelData("Triglyceride: Normal", "2017-03-01", "Phuket Hospital", "Dr.A"));
        checkup.add(new GenericModelData("AST: Normal", "2017-03-01", "Phuket Hospital", "Dr.A"));
        checkup.add(new GenericModelData("ALT: Normal", "2017-03-01", "Phuket Hospital", "Dr.A"));
        checkups.add(checkup);
        medicalData.setCheckups(checkups);
        
        
        List<GenericModelData> medicalProblems = new ArrayList<>();
        medicalProblems.add(new GenericModelData("diabetes", "2017-03-01", "Phuket Hospital", "Dr.A"));
        medicalData.setDiagnosises(medicalProblems);
        
        List<GenericModelData> vaccines = new ArrayList<>();
        /*vaccines.add(new GenericModelData("Blod Pressure: 150/80", "2017-03-01", "Phuket Hospital", "Dr.A"));
        vaccines.add(new GenericModelData("Height: 152 cm", "2017-03-01", "Phuket Hospital", "Dr.A"));
        vaccines.add(new GenericModelData("Weight: 68 kg", "2017-03-01", "Phuket Hospital", "Dr.A"));
        vaccines.add(new GenericModelData("BMI: 29 kg/m^2", "2017-03-01", "Phuket Hospital", "Dr.A"));
        vaccines.add(new GenericModelData("BMI Status: Overweight", "2017-03-01", "Phuket Hospital", "Dr.A"));
        vaccines.add(new GenericModelData("Respiration: 22 per min", "2017-03-01", "Phuket Hospital", "Dr.A"));
        vaccines.add(new GenericModelData("Temperature: 37.23 C", "2017-03-01", "Phuket Hospital", "Dr.A"));
        vaccines.add(new GenericModelData("Pulse: 99 per min", "2017-03-01", "Phuket Hospital", "Dr.A"));
        vaccines.add(new GenericModelData("Oxygen Saturation: 97%", "2017-03-01", "Phuket Hospital", "Dr.A"));*/
        medicalData.setVaccines(vaccines);
        
        List<GenericModelData> allergies = new ArrayList<>();
        allergies.add(new GenericModelData("penicillin", "2017-03-01", "Phuket Hospital", "Dr.A"));
        medicalData.setProcedures(allergies);
        
        this.medicalDatas.put(medicalData.getId(), medicalData);
        return true;
    }

    @Override
    public String checkAuthorize(String id) {
        return "GRANT";
    }
}
