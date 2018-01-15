/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.depa.hisbroker;

import com.depa.hisbroker.model.MedicalData;

/**
 *
 * @author Prasert
 */
public interface MedicalDataRepository {
    String checkAuthorize(String id);
    MedicalData save(MedicalData medicalData);
    MedicalData findMedicalData(String id);
    boolean installData(String type);
}
