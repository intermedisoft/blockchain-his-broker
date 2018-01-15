/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.depa.hisbroker;

import com.depa.hisbroker.model.GenericModelData;
import com.depa.hisbroker.model.MedicalData;
import io.swagger.client.ApiClient;
import io.swagger.client.ApiException;
import io.swagger.client.api.CheckupHistoryApi;
import io.swagger.client.api.CreateCheckupHistoryApi;
import io.swagger.client.api.CreateVaccinationApi;
import io.swagger.client.api.HealthCareProviderApi;
import io.swagger.client.api.PatientApi;
import io.swagger.client.api.PermissionTransactionApi;
import io.swagger.client.api.SystemApi;
import io.swagger.client.api.VaccinationApi;
import io.swagger.client.api.WalletApi;
import io.swagger.client.model.Card;
import io.swagger.client.model.CheckupHistory;
import io.swagger.client.model.CreateCheckupHistory;
import io.swagger.client.model.CreateVaccination;
import io.swagger.client.model.HealthCareProvider;
import io.swagger.client.model.Patient;
import io.swagger.client.model.PermissionTransaction;
import io.swagger.client.model.PingResponse;
import io.swagger.client.model.Vaccination;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

/**
 *
 * @author Prasert
 */
public class BlockchainMedicalDataRepository implements MedicalDataRepository {

    private final static Logger LOG = Logger.getLogger(BlockchainMedicalDataRepository.class);
    ApiClient client = new ApiClient();

    public BlockchainMedicalDataRepository() {
        client.setBasePath("http://61.19.253.37/api");
        client.setAccessToken("2HfkjUDTElA8Hd2o3Epn1847jikNJgnoO6d9GrGNaXlQ0AxDtDYNWLMAEuwIc4Xe");

        // Test Websocket
        /*
        try {
            final WebsocketClientEndpoint clientEndPoint = new WebsocketClientEndpoint(new URI("ws://61.19.253.37"));

            clientEndPoint.addMessageHandler((String message) -> {
                System.out.println("[Websocket] " + message);
                System.out.println("");
            });
        } catch (URISyntaxException ex) {
            System.out.println("Web socket fail");
            LOG.error("Web socket fail", ex);
        }
        */
    }

    @Override
    public String checkAuthorize(String id) {
        //client.setBasePath("http://61.19.253.37/api");
        //client.setAccessToken("2HfkjUDTElA8Hd2o3Epn1847jikNJgnoO6d9GrGNaXlQ0AxDtDYNWLMAEuwIc4Xe");
        try {
            WalletApi walletApi = new WalletApi(client);
            walletApi.cardSetDefault("bc_hcp_pensook");
            //Card card = walletApi.cardGetCardByName("bc_hcp_pensook");
            //LOG.info("checkAuthorize : card = "+card.getName()+", default = "+card.getDefault());
            
            SystemApi apiInstance = new SystemApi(client);
            PingResponse result = apiInstance.systemPing();
            LOG.info("checkAuthorize : Ping result = "+result);
            
            PatientApi patientApi = new PatientApi(client);
            Patient patient = patientApi.patientFindById(id, null);

            if (patient.getAuthorizedHcpPermissionRequest().contains("resource:com.depa.blockchain.core.HealthCareProvider#bc_hcp_pensook")) {
                // found grant permisstion so go on
                LOG.info("Found existed grant, on patient id = " + id);
                return "GRANT";
            } else if (patient.getPendingHcpPermissionRequest().contains("resource:com.depa.blockchain.core.HealthCareProvider#bc_hcp_pensook")) {
                // found existed request so waiting
                LOG.warn("Found existed request, please tell patient to grant on id = " + id);
                return "REQUEST_EXIST";
            } else {
                // not found so request permission
                HealthCareProviderApi healthcareProviderApi = new HealthCareProviderApi(client);
                HealthCareProvider us = healthcareProviderApi.healthCareProviderFindById("bc_hcp_pensook", null);

                PermissionTransactionApi permissionTxApi = new PermissionTransactionApi(client);
                PermissionTransaction permissionRequestTx = new PermissionTransaction();
                permissionRequestTx.setPatient(patient.getPropertyClass() + "#" + id);
                permissionRequestTx.setHealthCareProvider(us.getPropertyClass() + "#bc_hcp_pensook");
                permissionRequestTx.setPermissionType(PermissionTransaction.PermissionTypeEnum.REQUEST);

                PermissionTransaction submittedPermissionRequestTx = permissionTxApi.permissionTransactionCreate(permissionRequestTx);

                if (submittedPermissionRequestTx != null) {
                    System.out.println("Trasaction submitted = " + submittedPermissionRequestTx.getTransactionId());
                    return "REQUEST_NEW";
                } else {
                    return "REQUEST_FAIL";
                }
            }

            /*

            PermissionTransactionApi permissionTxApi = new PermissionTransactionApi(client);
            String queryPermission = "{\"where\" : {\"and\" : [{\"patient\":\"resource:" + patient.getPropertyClass() + "#" + id + "\"}, {\"healthCareProvider\":\"resource:" + us.getPropertyClass() + "#bc_hcp_bpk" + "\"}]}}";
            //LOG.info(queryPermission);
            List<PermissionTransaction> existPermissionRequestTx = permissionTxApi.permissionTransactionFind(queryPermission);
            if (existPermissionRequestTx != null && !existPermissionRequestTx.isEmpty()) {
                LOG.info(existPermissionRequestTx);
                transactionId = existPermissionRequestTx.get(0).getTransactionId();
            } else {
                PermissionTransaction permissionRequestTx = new PermissionTransaction();
                permissionRequestTx.setPatient(patient.getPropertyClass() + "#" + id);
                permissionRequestTx.setHealthCareProvider(us.getPropertyClass() + "#bc_hcp_bpk");
                permissionRequestTx.setPermissionType(PermissionTransaction.PermissionTypeEnum.REQUEST);

                PermissionTransaction submittedPermissionRequestTx = permissionTxApi.permissionTransactionCreate(permissionRequestTx);

                if (submittedPermissionRequestTx != null) {
                    System.out.println("Trasaction submitted = " + submittedPermissionRequestTx.getTransactionId());
                    transactionId = submittedPermissionRequestTx.getTransactionId();
                }
            }
             */
        } catch (ApiException e) {
            System.err.println("Exception on API call");
            System.out.println(e.getResponseBody());
            LOG.error("checkAuthorize error ",e);
        }
        return null; 
    }

    @Override
    public MedicalData save(MedicalData medicalData) {
        try{
            WalletApi walletApi = new WalletApi(client);
            walletApi.cardSetDefault("bc_hcp_pensook");
        } catch (ApiException e) {
            System.err.println("Exception on API call");
            System.out.println(e.getResponseBody());
            LOG.error("checkAuthorize error ",e);
        }
        return null;
    }

    @Override
    public MedicalData findMedicalData(String id) {
        MedicalData medData = null;

        SystemApi apiInstance = new SystemApi(client);
        WalletApi walletApi = new WalletApi(client);

        // Test API
        try {
            walletApi.cardSetDefault("bc_hcp_pensook");
            PingResponse result = apiInstance.systemPing();
            System.out.println(result);

            PatientApi patientApi = new PatientApi(client);
            Patient patient = patientApi.patientFindById(id, null);
            LOG.info("findMedicalData : patient id "+patient.getName());
            
            medData = new MedicalData();
            medData.setId(patient.getPatientId());
            medData.setPrename(patient.getPrename());
            medData.setFirstname(patient.getName());
            medData.setLastname(patient.getSurname());
            medData.setBirthdate(new LocalDate(patient.getDob()).toString());
            medData.setHeight(patient.getHeight() + " cm.");
            medData.setWeight(patient.getWeight() + " kg.");
            //medData.setBmi(calculateBMI(patient.getWeight(), patient.getHeight()));

            List<GenericModelData> demographics = new ArrayList<>();
            demographics.add(new GenericModelData("Personal ID: " + patient.getPatientId(), "", "", ""));
            demographics.add(new GenericModelData("Sex: " + patient.getSex(), "", "", ""));
            demographics.add(new GenericModelData("Martital Status: " + patient.getMarriage(), "", "", ""));
            demographics.add(new GenericModelData("Nation: " + patient.getNation(), "", "", ""));
            demographics.add(new GenericModelData("Race: " + patient.getRace(), "", "", ""));
            demographics.add(new GenericModelData("Phone: " + patient.getMobilePhone(), "", "", ""));
            demographics.add(new GenericModelData("Live Address: " + patient.getLiveHomeId(), "", "", ""));
            demographics.add(new GenericModelData("Work Address: " + patient.getCensusHomeId(), "", "", ""));
            medData.setDemographics(demographics);

            List<GenericModelData> medications = new ArrayList<>();
            //medications.add(new GenericModelData("Metformin", "2017-03-01", "Phuket Hospital", "Dr.A"));
            //medications.add(new GenericModelData("Lipitor", "2017-05-11", "Pensook Clinic", "Dr.B"));
            //medications.add(new GenericModelData("Lisinopril", "2017-08-15", "Hadyai Hospital", "Dr.C"));
            medData.setDrugs(medications);

            List<List<GenericModelData>> checkups = new ArrayList<>();

            CheckupHistoryApi checkupHistoryApi = new CheckupHistoryApi(client);
            String queryCheckup = "{\"where\" : {\"patient\":\"resource:" + patient.getPropertyClass() + "#" + id + "\"}}";
            List<CheckupHistory> existCheckupTx = checkupHistoryApi.checkupHistoryFind(queryCheckup);

            if (existCheckupTx != null && !existCheckupTx.isEmpty()) {
                existCheckupTx.stream().map((checkupTx) -> {
                    List<GenericModelData> checkup = new ArrayList<>();
                    checkup.add(new GenericModelData("Waist Width: ", "", "", ""));
                    checkup.add(new GenericModelData("Hb: " + checkupTx.getHb(), "", "", ""));
                    checkup.add(new GenericModelData("WBC: " + checkupTx.getCbcWbc(), "", "", ""));
                    checkup.add(new GenericModelData("RBC: " + checkupTx.getRbc(), "", "", ""));
                    checkup.add(new GenericModelData("HCT: " + checkupTx.getHct(), "", "", ""));
                    checkup.add(new GenericModelData("Lymphocytes: " + checkupTx.getLym(), "", "", ""));
                    checkup.add(new GenericModelData("Monocyte: " + checkupTx.getMono(), "", "", ""));
                    checkup.add(new GenericModelData("Eosinophils: " + checkupTx.getEos(), "", "", ""));
                    checkup.add(new GenericModelData("FBS: " + checkupTx.getFbs(), "", "", ""));
                    checkup.add(new GenericModelData("Cholesterol: " + checkupTx.getChlt(), "", "", ""));
                    checkup.add(new GenericModelData("Creatinine: " + checkupTx.getCreatinine(), "", "", ""));
                    checkup.add(new GenericModelData("BUN: " + checkupTx.getBun(), "", "", ""));
                    checkup.add(new GenericModelData("SGOT: " + checkupTx.getSgot(), "", "", ""));
                    checkup.add(new GenericModelData("SGPT: " + checkupTx.getSgpt(), "", "", ""));
                    checkup.add(new GenericModelData("Triglyceride: " + checkupTx.getTrig(), "", "", ""));
                    checkup.add(new GenericModelData("HDL: " + checkupTx.getHdl(), "", "", ""));
                    checkup.add(new GenericModelData("Uric: " + checkupTx.getUric(), "", "", ""));
                    checkup.add(new GenericModelData("LDL: " + checkupTx.getLdl(), "", "", ""));
                    checkup.add(new GenericModelData("Sp.Gr: " + checkupTx.getSpgr(), "", "", ""));
                    checkup.add(new GenericModelData("ph: " + checkupTx.getPh(), "", "", ""));
                    checkup.add(new GenericModelData("Glucose: " + checkupTx.getSugar(), "", "", ""));
                    return checkup;
                }).forEach((checkup) -> {
                    checkups.add(checkup);
                });

                medData.setCheckups(checkups);

                List<GenericModelData> medicalProblems = new ArrayList<>();
                //medicalProblems.add(new GenericModelData("diabetes", "2017-03-01", "Phuket Hospital", "Dr.A"));
                medData.setDiagnosises(medicalProblems);

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
                medData.setVaccines(vaccines);

                List<GenericModelData> allergies = new ArrayList<>();
                //allergies.add(new GenericModelData("penicillin", "2017-03-01", "Phuket Hospital", "Dr.A"));
                medData.setProcedures(allergies);
            }

        } catch (ApiException e) {
            System.err.println("Exception on API call");
            System.out.println(e.getResponseBody());
            LOG.error("checkAuthorize error ", e);
        }
        System.out.println("Finished!");
        return medData;
    }

    @Override
    public boolean installData(String type) {
        //client.setBasePath("http://61.19.253.37/api");
        //client.setAccessToken("2HfkjUDTElA8Hd2o3Epn1847jikNJgnoO6d9GrGNaXlQ0AxDtDYNWLMAEuwIc4Xe");
        try {
            WalletApi walletApi = new WalletApi(client);
            walletApi.cardSetDefault("bc_hcp_pensook");
            //Card card = walletApi.cardGetCardByName("bc_hcp_pensook");
            //LOG.info("card = "+card.getName()+", default = "+card.getDefault());
            
            SystemApi apiInstance = new SystemApi(client);
            PingResponse result = apiInstance.systemPing();
            LOG.info("Ping result = "+result);
            
        } catch (ApiException ex) {
            LOG.error("installData set card default error ", ex);
            return false;
        }
        
        if("vaccine".equals(type)){
            String csvFile = "D:/vaccine.csv";
            String line = "";
            String cvsSplitBy = ",";

            try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(csvFile),"UTF-8"))) {

                while ((line = br.readLine()) != null) {
                    // use comma as separator
                    String[] vaccineArr = line.split(cvsSplitBy);

                    System.out.println("Vaccine [name= " + vaccineArr[0] + " , type=" + vaccineArr[1] 
                            + " , datetimeServe=" + vaccineArr[2] + " , booster=" + vaccineArr[3] 
                            + " , assetId=" + vaccineArr[4] + " , patientId=" + vaccineArr[5] + " , hcpId=" + vaccineArr[6] + "]");
                    
                    try{
                        Vaccination vaccine = new Vaccination();
                        vaccine.setVaccineName(vaccineArr[0]);
                        vaccine.setVaccineType(vaccineArr[1]);
                        vaccine.setDateTimeServe(DateTime.parse(vaccineArr[2]));
                        vaccine.setNumberOfBooster(vaccineArr[3]);
                        vaccine.setAssetId(vaccineArr[4]);
                        vaccine.setPatient("resource:com.depa.blockchain.core.Patient#"+vaccineArr[5]);
                        vaccine.setHealthCareProvider("resource:com.depa.blockchain.core.HealthCareProvider#"+vaccineArr[6]);
                        
                    /*  CreateVaccinationApi vaccineApi = new CreateVaccinationApi(client);
                        CreateVaccination createVaccine = new CreateVaccination();
                        createVaccine.setProtectedAsset(vaccine);
                        CreateVaccination createdVaccine = vaccineApi.createVaccinationCreate(createVaccine);
                        LOG.info("vaccine created transaction id = "+createdVaccine.getTransactionId());
                    */
                        VaccinationApi vaccineApi = new VaccinationApi(client);
                        Vaccination createdVaccine = vaccineApi.vaccinationCreate(vaccine);
                        LOG.info("vaccine created transaction id = "+createdVaccine.getAssetId());
                    }catch(ApiException e) {
                        System.err.println("Exception on API call");
                        System.out.println(e.getResponseBody());
                        LOG.error("installData vaccine error ", e);
                        return false;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else if("checkup".equals(type)){
            String csvFile = "D:/checkup.csv";
            String line = "";
            String cvsSplitBy = ",";

            try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(csvFile),"UTF-8"))) {

                while ((line = br.readLine()) != null) {
                    // use comma as separator
                    String[] checkupArr = line.split(cvsSplitBy);

                    System.out.println("Checkup [id= " + checkupArr[0] + " , dateTimeServe=" + checkupArr[1] + " , dateTimeUpdate=" + checkupArr[2] 
                            + " , pulse=" + checkupArr[3] + " , pressure=" + checkupArr[4] 
                            + " , wais_width=" + checkupArr[5] + " , hb=" + checkupArr[6] + " , cbc_wbc=" + checkupArr[7] 
                            + " , rbc=" + checkupArr[8] + " , hct=" + checkupArr[9] + " , lym=" + checkupArr[10]
                            + " , mono=" + checkupArr[11] + " , eos=" + checkupArr[12] + " , fbs=" + checkupArr[13]
                            + " , chlt=" + checkupArr[14] + " , creatinine=" + checkupArr[15] + " , bun=" + checkupArr[16]
                            + " , sgot=" + checkupArr[17] + " , sgpt=" + checkupArr[18] + " , trig=" + checkupArr[19]
                            + " , hdl=" + checkupArr[20] + " , uric=" + checkupArr[21] + " , ldl=" + checkupArr[22]
                            + " , spgr=" + checkupArr[23] + " , ph=" + checkupArr[24] + " , sugar=" + checkupArr[25]
                            + " , assetId=" + checkupArr[26] + " , patientId=" + checkupArr[27] + " , hcpId=" + checkupArr[28]
                            + "]");
            try{
                CheckupHistory checkupHist = new CheckupHistory();
                checkupHist.setCheckupHistoryId(checkupArr[0]);
                checkupHist.setDateTimeServe(checkupArr[1]);
                checkupHist.setDateTimeUpdate(checkupArr[2]);
                
                checkupHist.setPulse(checkupArr[3]);
                checkupHist.setPressure(checkupArr[4]);
                checkupHist.setHb(checkupArr[6]);
                checkupHist.setCbcWbc(checkupArr[7]);
                checkupHist.setRbc(checkupArr[8]);
                checkupHist.setHct(checkupArr[9]);
                checkupHist.setLym(checkupArr[10]);
                checkupHist.setMono(checkupArr[11]);
                checkupHist.setEos(checkupArr[12]);
                checkupHist.setFbs(checkupArr[13]);
                checkupHist.setChlt(checkupArr[14]);
                checkupHist.setCreatinine(checkupArr[15]);
                checkupHist.setBun(checkupArr[16]);
                checkupHist.setSgot(checkupArr[17]);
                checkupHist.setSgpt(checkupArr[18]);
                checkupHist.setTrig(checkupArr[19]);
                checkupHist.setHdl(checkupArr[20]);
                checkupHist.setUric(checkupArr[21]);
                checkupHist.setLdl(checkupArr[22]);
                checkupHist.setSpgr(checkupArr[23]);
                checkupHist.setPh(checkupArr[24]);
                checkupHist.setSugar(checkupArr[25]);
                checkupHist.setAssetId(checkupArr[26]);
                checkupHist.setPatient("resource:com.depa.blockchain.core.Patient#"+checkupArr[27]);
                checkupHist.setHealthCareProvider("resource:com.depa.blockchain.core.HealthCareProvider#"+checkupArr[28]);

/*
                CreateCheckupHistoryApi checkupHistoryApi = new CreateCheckupHistoryApi(client);
                CreateCheckupHistory createCheckupHist = new CreateCheckupHistory();
                createCheckupHist.setProtectedAsset(checkupHist);
                CreateCheckupHistory createdCheckupHist = checkupHistoryApi.createCheckupHistoryCreate(createCheckupHist);
                LOG.info("checkup created transaction id = "+createdCheckupHist.getTransactionId());
*/
                CheckupHistoryApi checkupHistoryApi = new CheckupHistoryApi(client);
                CheckupHistory createdCheckupHist = checkupHistoryApi.checkupHistoryCreate(checkupHist);
                LOG.info("checkup created transaction id = "+createdCheckupHist.getCheckupHistoryId());
            }catch(ApiException e) {
                System.err.println("Exception on API call");
                System.out.println(e.getResponseBody());
                LOG.error("installData checkup error ", e);
                return false;
            }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }
}
