/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.depa.hisbroker;

import com.depa.hisbroker.model.MedicalData;
import javax.validation.Valid;
import org.apache.log4j.Logger;
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
    
    public IMedController(MedicalDataRepository medicalDataRepository){
        this.medicalDataRepository = medicalDataRepository;
    }
    
    @GetMapping /*http://localhost:8181*/
    public ModelAndView index(){
        return new ModelAndView("login");
    }
    
    @GetMapping("{id}") /*http://localhost:8181/3809900380115*/
    public ModelAndView view(@PathVariable("id") String id){
        MedicalData medicalData = new MedicalData();
        LOG.info("param id = "+id);
        if("GRANT".equals(this.medicalDataRepository.checkAuthorize(id))){
            LOG.info("permission Grant");
            medicalData = this.medicalDataRepository.findMedicalData(id);
        }
        
        LOG.warn("return data = "+medicalData);
        return medicalData != null && medicalData.getId() != null ? new ModelAndView("view", "medicalData", medicalData) : new ModelAndView("login");
    }
    
    @PostMapping
    public String create(@Valid MedicalData medicalData, BindingResult result, RedirectAttributes redirect) {
        if (result.hasErrors()) {
            return result.getAllErrors().toString();
        }
        medicalData = this.medicalDataRepository.save(medicalData);
        redirect.addFlashAttribute("globalMessage", "Successfully created a new message");
        return medicalData.getId();
    }
}
