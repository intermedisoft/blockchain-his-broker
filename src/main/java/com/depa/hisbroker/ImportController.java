/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.depa.hisbroker;

import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Prasert
 */
@RestController
@RequestMapping("/import/")
public class ImportController {
    private final static Logger LOG = Logger.getLogger(IMedController.class);
    
    private final MedicalDataRepository medicalDataRepository;
    
    public ImportController(MedicalDataRepository medicalDataRepository){
        this.medicalDataRepository = medicalDataRepository;
    }
    
    @GetMapping("{type}")
    public String installData(@PathVariable("type") String type){
        LOG.warn("param type = "+type);
        if("vaccine".equals(type)){
            this.medicalDataRepository.installData(type);
        }else if("checkup".equals(type)){
            this.medicalDataRepository.installData(type);
        }else{
            
        }
        return "";
    }
}
