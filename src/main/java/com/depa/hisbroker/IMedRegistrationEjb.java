/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.depa.hisbroker;

import java.util.Hashtable;

/**
 *
 * @author prasert
 */
public interface IMedRegistrationEjb {
    public Hashtable getPatientBasicDetailByPatientId(String patientId) throws Exception;
}
