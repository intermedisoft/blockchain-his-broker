/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.depa.hisbroker;

import com.depa.hisbroker.model.CheckupModel;
import com.depa.hisbroker.model.MedicalData;
import com.depa.hisbroker.model.XrayModel;

/**
 *
 * @author Prasert
 */
public interface MedicalDataRepository {
    String requestAuthorize(String id);
    String revokeAuthorize(String id);
    String save(CheckupModel checkupModel);
    String save(XrayModel xrayModel);
    MedicalData findMedicalData(String id);
    boolean installData(String type);
}
