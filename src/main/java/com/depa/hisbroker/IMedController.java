/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.depa.hisbroker;

import com.depa.hisbroker.model.CheckupModel;
import com.depa.hisbroker.model.MedicalData;
import java.util.HashMap;
import java.util.Map;
import javax.validation.Valid;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 *
 * @author Prasert
 */
@RestController
@RequestMapping("/")
public class IMedController {
    private final static Logger LOG = Logger.getLogger(IMedController.class);

    private final MedicalDataRepository medicalDataRepository;

    @Autowired
    private Environment env;
    
    public IMedController(MedicalDataRepository medicalDataRepository) {
        this.medicalDataRepository = medicalDataRepository;
    }

    @GetMapping
    /*http://localhost:8181*/
    public ModelAndView index() {
        return new ModelAndView("index");
    }

    @GetMapping("{id}")
    /*http://localhost:8181/3809900380115*/
    public ModelAndView view(@PathVariable("id") String id) {
        MedicalData medicalData = new MedicalData();
        LOG.info("request id = " + id);
        if ("GRANT".equals(this.medicalDataRepository.requestAuthorize(id))) {
            LOG.info("permission Grant");
            medicalData = this.medicalDataRepository.findMedicalData(id);
        }
        LOG.info("return medical data = " + medicalData);
        if (medicalData != null && medicalData.getId() != null) {
            LOG.debug("return checkups data = " + medicalData.getCheckups());
            LOG.debug("return demographics data = " + medicalData.getDemographics());
            LOG.debug("return diagnosis data = " + medicalData.getDiagnosises());
            LOG.debug("return vaccine data = " + medicalData.getVaccines());
            return new ModelAndView("view", "medicalData", medicalData);
        } else {
            Map<String, String> permission = new HashMap();
            permission.put("type", "request");
            permission.put("status", "FAIL");
            permission.put("pid", id);
            permission.put("hcp", env.getRequiredProperty("blockchain.his.wallet"));
            
            String baseHttpUrl = env.getRequiredProperty("blockchain.server.base.url");
            String wsUrl = baseHttpUrl.replace("http", "ws").replace("api", "");
            permission.put("url", wsUrl);
            return new ModelAndView("index", "permission", permission);
        }
    }

    @GetMapping("revoke/{id}")
    /*http://localhost:8181/revoke/3809900380115*/
    public ModelAndView revoke(@PathVariable("id") String id) {
        LOG.info("revoke id = " + id);
        Map<String, String> permission = new HashMap();
        permission.put("type", "revoke");
        permission.put("status", this.medicalDataRepository.revokeAuthorize(id));
        permission.put("pid", id);
        permission.put("hcp", env.getRequiredProperty("blockchain.his.wallet"));
        return new ModelAndView("index", "permission", permission);
    }

    @PostMapping
    public String create(@Valid CheckupModel checkupModel, BindingResult result, RedirectAttributes redirect) {
        if (result.hasErrors()) {
            return result.getAllErrors().toString();
        }
        String retStatus = this.medicalDataRepository.save(checkupModel);
        redirect.addFlashAttribute("globalMessage", "Successfully created a new message");
        return retStatus;
    }
}
