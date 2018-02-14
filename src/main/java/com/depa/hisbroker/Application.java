/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.depa.hisbroker;

import java.util.Properties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.ejb.access.SimpleRemoteStatelessSessionProxyFactoryBean;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 *
 * @author Prasert
 */
@SpringBootApplication
@EnableScheduling
public class Application {
    
    @Autowired
    private Environment env;
    
    private Properties getEnv(){
        //System.out.println(env.getRequiredProperty("jboss.factory.initial"));
        //System.out.println(env.getRequiredProperty("jboss.provider.url"));
        //System.out.println(env.getRequiredProperty("jboss.factory.url.pkgs"));
        Properties mappings = new Properties();
        mappings.put("java.naming.factory.initial", env.getRequiredProperty("jboss.factory.initial"));
        mappings.put("java.naming.provider.url", env.getRequiredProperty("jboss.provider.url"));
        mappings.put("java.naming.factory.url.pkgs", env.getRequiredProperty("jboss.factory.url.pkgs"));
        return mappings;
    }
    
    private SimpleRemoteStatelessSessionProxyFactoryBean getEjb(Class clazz, String jndiName){
        SimpleRemoteStatelessSessionProxyFactoryBean ejb = new SimpleRemoteStatelessSessionProxyFactoryBean();
        ejb.setBusinessInterface(clazz);
        ejb.setJndiName(jndiName);
        ejb.setJndiEnvironment(getEnv());
        ejb.setLookupHomeOnStartup(false);
        
        return ejb;
    }
    
    @Bean
    public SimpleRemoteStatelessSessionProxyFactoryBean registerEjb() {
        return getEjb(IMedRegistrationEjb.class, "RegistrationManage");
    }
    
    private Properties getBlockchainConfig(){
        Properties configs = new Properties();
        configs.put("base_url", env.getRequiredProperty("blockchain.server.base.url"));
        configs.put("token", env.getRequiredProperty("blockchain.his.token"));
        configs.put("wallet", env.getRequiredProperty("blockchain.his.wallet"));
        return configs;
    }
    
    @Bean
    public MedicalDataRepository medicalDataRepository(){
        //return new InMemoryMedicalDataRepository();
        return new BlockchainMedicalDataRepository(getBlockchainConfig());
    }
    
    public static void main(String[] args) throws Exception {
        SpringApplication.run(Application.class);
    }
}
