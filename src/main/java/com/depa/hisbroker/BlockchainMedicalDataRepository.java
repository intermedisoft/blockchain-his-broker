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
import io.swagger.client.ApiClient;
import io.swagger.client.ApiException;
import io.swagger.client.api.CheckupHistoryApi;
import io.swagger.client.api.CheckupResultProducedTransactionApi;
import io.swagger.client.api.DiagHistoryApi;
import io.swagger.client.api.HealthCareProviderApi;
import io.swagger.client.api.PatientApi;
import io.swagger.client.api.PermissionTransactionApi;
import io.swagger.client.api.ServiceHistoryApi;
import io.swagger.client.api.SystemApi;
import io.swagger.client.api.VaccinationApi;
import io.swagger.client.api.WalletApi;
import io.swagger.client.api.XrayApi;
import io.swagger.client.api.XrayResultProducedTransactionApi;
import io.swagger.client.model.CheckupHistory;
import io.swagger.client.model.CheckupResultProducedTransaction;
import io.swagger.client.model.DiagHistory;
import io.swagger.client.model.Patient;
import io.swagger.client.model.PermissionTransaction;
import io.swagger.client.model.PingResponse;
import io.swagger.client.model.ServiceHistory;
import io.swagger.client.model.Vaccination;
import io.swagger.client.model.Xray;
import io.swagger.client.model.XrayResultProducedTransaction;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

/**
 *
 * @author Prasert
 */
public class BlockchainMedicalDataRepository implements MedicalDataRepository {
    private final static Logger LOG = Logger.getLogger(BlockchainMedicalDataRepository.class);
    private final static ApiClient CLIENT = new ApiClient();
    private final static Map<String, String> HCP_LOOKUP = new ConcurrentHashMap();
    private final String HCP_WALLET;

    private final static String HCP_RESOURCE_PREFIX = "resource:com.depa.blockchain.core.HealthCareProvider#";
    private final static String PATIENT_RESOURCE_PREFIX = "resource:com.depa.blockchain.core.Patient#";
    private final static String SERVICE_RESOURCE_PREFIX = "com.depa.blockchain.assets.ServiceHistory#";

    public BlockchainMedicalDataRepository(Properties config) {
        CLIENT.setBasePath(config.getProperty("base_url"));
        CLIENT.setAccessToken(config.getProperty("token"));

        HCP_WALLET = config.getProperty("wallet");

        initLookupHcp();
    }

    @Override
    public String requestAuthorize(String id) {
        try {
            WalletApi walletApi = new WalletApi(CLIENT);
            walletApi.cardSetDefault(HCP_WALLET);

            SystemApi apiInstance = new SystemApi(CLIENT);
            PingResponse result = apiInstance.systemPing();
            LOG.info("requestAuthorize : Ping result = " + result);

            PatientApi patientApi = new PatientApi(CLIENT);
            Patient patient = patientApi.patientFindById(id, null);

            if (patient.getAuthorizedHcpPermissionRequest().contains(HCP_RESOURCE_PREFIX + HCP_WALLET)) {
                // found grant permisstion so go on
                LOG.info("Found existed grant, on patient id = " + id);
                return "GRANT";
            } else {
                PermissionTransactionApi permissionTxApi = new PermissionTransactionApi(CLIENT);
                PermissionTransaction permissionRequestTx = new PermissionTransaction();
                permissionRequestTx.setPatient(patient.getPropertyClass() + "#" + id);
                permissionRequestTx.setHealthCareProvider(HCP_RESOURCE_PREFIX + HCP_WALLET);
                permissionRequestTx.setPermissionType(PermissionTransaction.PermissionTypeEnum.REQUEST);

                PermissionTransaction submittedPermissionRequestTx = permissionTxApi.permissionTransactionCreate(permissionRequestTx);

                if (submittedPermissionRequestTx != null) {
                    LOG.info("Request permission submitted = " + submittedPermissionRequestTx.getTransactionId());
                    return "REQUEST_NEW";
                } else {
                    LOG.error("Request permission fail!");
                    return "REQUEST_FAIL";
                }
            }
        } catch (ApiException e) {
            LOG.error("requestAuthorize error " + e.getResponseBody(), e);
        }
        return null;
    }

    @Override
    public String revokeAuthorize(String id) {
        try {
            WalletApi walletApi = new WalletApi(CLIENT);
            walletApi.cardSetDefault(HCP_WALLET);

            PatientApi patientApi = new PatientApi(CLIENT);
            Patient patient = patientApi.patientFindById(id, null);

            PermissionTransactionApi permissionTxApi = new PermissionTransactionApi(CLIENT);
            PermissionTransaction permissionRequestTx = new PermissionTransaction();
            permissionRequestTx.setPatient(patient.getPropertyClass() + "#" + id);
            permissionRequestTx.setHealthCareProvider(HCP_RESOURCE_PREFIX + HCP_WALLET);
            permissionRequestTx.setPermissionType(PermissionTransaction.PermissionTypeEnum.REVOKE);

            PermissionTransaction submittedPermissionRequestTx = permissionTxApi.permissionTransactionCreate(permissionRequestTx);

            if (submittedPermissionRequestTx != null) {
                LOG.info("Revoke permission submitted = " + submittedPermissionRequestTx.getTransactionId());
                return "SUCCESS";
            } else {
                LOG.error("Revoke permission fail!");
                return "FAIL";
            }

        } catch (ApiException e) {
            LOG.error("revokeAuthorize error " + e.getResponseBody(), e);
        }
        return null;
    }

    @Override
    public String save(CheckupModel checkup) {
        String transactionId = null;
        try {
            WalletApi walletApi = new WalletApi(CLIENT);
            walletApi.cardSetDefault(HCP_WALLET);

            
            CheckupHistory checkupHist = new CheckupHistory();
            checkupHist.setAlk(checkup.getAlk());
            checkupHist.setAlm(checkup.getAlm());
            checkupHist.setAssetId(checkup.getVisitId());
            checkupHist.setBun(checkup.getBun());
            checkupHist.setCbcWbc(checkup.getWbc());
            checkupHist.setCheckupHistoryId(checkup.getId());
            checkupHist.setChlt(checkup.getChlt());
            checkupHist.setCreatinine(checkup.getCreatinine());
            checkupHist.setDateTimeServe(checkup.getCheckupDateTime());
            checkupHist.setDateTimeUpdate(checkup.getCheckupDateTime());
            checkupHist.setEos(checkup.getEos());
            checkupHist.setFbs(checkup.getFbs());
            checkupHist.setHb(checkup.getHb());
            checkupHist.setHba1c(checkup.getHba1c());
            checkupHist.setHct(checkup.getHct());
            checkupHist.setHdl(checkup.getHdl());
            checkupHist.setHealthCareProvider(HCP_RESOURCE_PREFIX + HCP_WALLET);
            checkupHist.setLdl(checkup.getLdl());
            checkupHist.setLym(checkup.getLym());
            checkupHist.setMono(checkup.getMono());
            checkupHist.setPatient(PATIENT_RESOURCE_PREFIX + checkup.getPid());
            checkupHist.setPh(checkup.getPh());
            checkupHist.setPmn(checkup.getPmn());
            checkupHist.setPressure(checkup.getPressure());
            checkupHist.setPulse(checkup.getPulse());
            checkupHist.setRbc(checkup.getRbc());
            checkupHist.setSgot(checkup.getSgot());
            checkupHist.setSgpt(checkup.getSgpt());
            checkupHist.setSpgr(checkup.getSpgr());
            checkupHist.setSugar(checkup.getSugar());
            checkupHist.setTrig(checkup.getTrig());
            checkupHist.setUaWbc(checkup.getUawbc());
            checkupHist.setUric(checkup.getUric());
            checkupHist.setConclusion(checkup.getConclusion());
            checkupHist.setRecommendation(checkup.getRecommendation());
            
            CheckupResultProducedTransaction checkupTransaction = new CheckupResultProducedTransaction();
            checkupTransaction.setCheckupHistory(checkupHist);
            CheckupResultProducedTransactionApi checkupTransactionApi = new CheckupResultProducedTransactionApi(CLIENT);
            CheckupResultProducedTransaction checkupTransactionRet = checkupTransactionApi.checkupResultProducedTransactionCreate(checkupTransaction);
            transactionId = checkupTransactionRet.getTransactionId();
            LOG.info("checkup created transaction id = " + transactionId);

        } catch (ApiException e) {
            LOG.error("save data error " + e.getResponseBody(), e);
        }
        return transactionId;
    }
    
    @Override
    public String save(XrayModel xray) {
        String transactionId = null;
        try {
            WalletApi walletApi = new WalletApi(CLIENT);
            walletApi.cardSetDefault(HCP_WALLET);

            Xray data = new Xray();
            data.setAssetId(xray.getId());
            data.setDateTimeService(DateTime.parse(xray.getResultDateTime()));
            data.setDateTimeUpdate(DateTime.parse(xray.getResultDateTime()));
            data.setHealthCareProvider(HCP_RESOURCE_PREFIX + HCP_WALLET);
            data.setPatient(PATIENT_RESOURCE_PREFIX + xray.getPid());
            data.setXrayName(xray.getName());
            data.setXrayResult(xray.getResult());
            InputStream in = getClass().getResourceAsStream("/chest-xray.jpg");
            data.setXrayImage(Util.encodeImage(IOUtils.toByteArray(in)));
            
            XrayResultProducedTransaction xrayTransaction = new XrayResultProducedTransaction();
            xrayTransaction.setXray(data);
            XrayResultProducedTransactionApi xrayTransactionApi = new XrayResultProducedTransactionApi(CLIENT);
            XrayResultProducedTransaction xrayRetTransaction = xrayTransactionApi.xrayResultProducedTransactionCreate(xrayTransaction);
            transactionId = xrayRetTransaction.getTransactionId();
            LOG.info("xray created transaction id = " + transactionId);
        } catch (ApiException e) {
            LOG.error("save data error " + e.getResponseBody(), e);
        } catch (IOException e){
            LOG.error("save data, read file error " + e.getMessage(), e);
        }
        return transactionId;
    }

    @Override
    public MedicalData findMedicalData(String id) {
        MedicalData medData = null;

        try {
            WalletApi walletApi = new WalletApi(CLIENT);
            walletApi.cardSetDefault(HCP_WALLET);
            //walletApi.cardSetDefault(id);

            PatientApi patientApi = new PatientApi(CLIENT);
            Patient patient = patientApi.patientFindById(id, null);
            LOG.debug("findMedicalData : patient name " + patient.getName());

            medData = new MedicalData();
            medData.setId(patient.getPatientId());
            medData.setPrename(patient.getPrename());
            medData.setFirstname(patient.getName());
            medData.setLastname(patient.getSurname());
            medData.setBirthdate(new LocalDate(patient.getDob()).toString());
            medData.setHeight(patient.getHeight().toString());
            medData.setWeight(patient.getWeight().toString());

            List<GenericModel> demographics = new ArrayList<>();
            demographics.add(new GenericModel("Personal ID: <span class='font-weight-bold'>" + patient.getPatientId() + "</span>", "", "", ""));
            demographics.add(new GenericModel("Sex: <span class='font-weight-bold'>" + patient.getSex() + "</span>", "", "", ""));
            demographics.add(new GenericModel("Martital Status: <span class='font-weight-bold'>" + patient.getMarriage() + "</span>", "", "", ""));
            demographics.add(new GenericModel("Nation: <span class='font-weight-bold'>" + patient.getNation() + "</span>", "", "", ""));
            demographics.add(new GenericModel("Race: <span class='font-weight-bold'>" + patient.getRace() + "</span>", "", "", ""));
            demographics.add(new GenericModel("Phone: <span class='font-weight-bold'>" + patient.getMobilePhone() + "</span>", "", "", ""));
            demographics.add(new GenericModel("Live Address: <span class='font-weight-bold'>" + patient.getLiveHomeId() + "</span>", "", "", ""));
            demographics.add(new GenericModel("Work Address: <span class='font-weight-bold'>" + patient.getCensusHomeId() + "</span>", "", "", ""));
            medData.setDemographics(demographics);

            DiagHistoryApi diagHistApi = new DiagHistoryApi(CLIENT);
            String queryDiag = "{\"where\" : {\"patient\":\"" + PATIENT_RESOURCE_PREFIX + id + "\"}}";
            List<DiagHistory> existDiagTx = diagHistApi.diagHistoryFind(queryDiag);
            if (existDiagTx != null && !existDiagTx.isEmpty()) {
                Collections.sort(existDiagTx, (DiagHistory diagHist1, DiagHistory diagHist2) -> {
                    if (diagHist1.getDateTimeService() == null) {
                        return 1;
                    } else if (diagHist2.getDateTimeService() == null) {
                        return -1;
                    } else {
                        return diagHist2.getDateTimeService().compareTo(diagHist1.getDateTimeService());
                    }
                });

                List<GenericModel> diagnosises = new ArrayList<>();
                existDiagTx.stream().forEach((diag) -> {
                    diagnosises.add(new GenericModel(diag.getDiagName(), Util.formatDateTime(diag.getDateTimeService()), lookupHcp(diag.getHealthCareProvider()), diag.getDoctorId()));
                });

                medData.setDiagnosises(diagnosises);
            }

            List<GenericModel> medications = new ArrayList<>();
            medData.setMedications(medications);

            List<List<GenericModel>> checkups = new ArrayList<>();

            CheckupHistoryApi checkupHistoryApi = new CheckupHistoryApi(CLIENT);
            String queryCheckup = "{\"where\" : {\"patient\":\"" + PATIENT_RESOURCE_PREFIX + id + "\"}}";
            List<CheckupHistory> existCheckupTx = checkupHistoryApi.checkupHistoryFind(queryCheckup);

            if (existCheckupTx != null && !existCheckupTx.isEmpty()) {
                Collections.sort(existCheckupTx, (CheckupHistory checkupHist1, CheckupHistory checkupHist2) -> {
                    if (checkupHist1.getDateTimeServe().isEmpty()) {
                        return 1;
                    } else if (checkupHist2.getDateTimeServe().isEmpty()) {
                        return -1;
                    } else {
                        return DateTime.parse(checkupHist2.getDateTimeServe()).compareTo(DateTime.parse(checkupHist1.getDateTimeServe()));
                    }
                });

                existCheckupTx.stream().map((checkupTx) -> {
                    List<GenericModel> checkup = new ArrayList<>();
                    String place = lookupHcp(checkupTx.getHealthCareProvider());

                    if (Util.isNotEmpty(checkupTx.getHb()) || Util.isNotEmpty(checkupTx.getCbcWbc()) || Util.isNotEmpty(checkupTx.getHct())) {
                        checkup.add(new GenericModel(formatCheckupLab("Hb", checkupTx.getHb(), "14.1-18.1 g/dL"), Util.formatDateTime(checkupTx.getDateTimeServe()), place, ""));
                        checkup.add(new GenericModel(formatCheckupLab("WBC", checkupTx.getCbcWbc(), "4-10 10^3/mm3"), Util.formatDateTime(checkupTx.getDateTimeServe()), place, ""));
                        checkup.add(new GenericModel(formatCheckupLab("RBC", checkupTx.getRbc(), "4.5-6.0 *M/mm3"), Util.formatDateTime(checkupTx.getDateTimeServe()), place, ""));
                        checkup.add(new GenericModel(formatCheckupLab("HCT", checkupTx.getHct(), "43.5-53.7 %"), Util.formatDateTime(checkupTx.getDateTimeServe()), place, ""));
                        checkup.add(new GenericModel(formatCheckupLab("Lymphocytes", checkupTx.getLym(), "12-44 %"), "", "", ""));
                        checkup.add(new GenericModel(formatCheckupLab("Monocyte", checkupTx.getMono(), "0.0-11.2 %"), "", "", ""));
                        checkup.add(new GenericModel(formatCheckupLab("Eosinophils", checkupTx.getEos(), "0.0-9.5 %"), "", "", ""));
                        checkup.add(new GenericModel(formatCheckupLab("FBS", checkupTx.getFbs(), "70-99 mg/dL"), "", "", ""));
                        checkup.add(new GenericModel(formatCheckupLab("Cholesterol", checkupTx.getChlt(), "0-200 mg/dL"), "", "", ""));
                        checkup.add(new GenericModel(formatCheckupLab("Creatinine", checkupTx.getCreatinine(), "0.66-1.25 mg/dL"), "", "", ""));
                        checkup.add(new GenericModel(formatCheckupLab("BUN", checkupTx.getBun(), "9-20 mg/dL"), "", "", ""));
                        checkup.add(new GenericModel(formatCheckupLab("SGOT", checkupTx.getSgot(), "10-72 U/L"), "", "", ""));
                        checkup.add(new GenericModel(formatCheckupLab("SGPT", checkupTx.getSgpt(), "0-59 U/L"), "", "", ""));
                        checkup.add(new GenericModel(formatCheckupLab("Triglyceride", checkupTx.getTrig(), "0-149 mg/dL"), "", "", ""));
                        checkup.add(new GenericModel(formatCheckupLab("HDL", checkupTx.getHdl(), ">40 mg/dL"), "", "", ""));
                        checkup.add(new GenericModel(formatCheckupLab("Uric", checkupTx.getUric(), "3.5-8.5 mg/dL"), "", "", ""));
                        checkup.add(new GenericModel(formatCheckupLab("LDL", checkupTx.getLdl(), "0-130 mg/dL"), "", "", ""));
                        checkup.add(new GenericModel(formatCheckupLab("Sp.Gr", checkupTx.getSpgr(), "1.003-1.030"), "", "", ""));
                        checkup.add(new GenericModel(formatCheckupLab("ph", checkupTx.getPh(), "4.5-8.0"), "", "", ""));
                        checkup.add(new GenericModel(formatCheckupLab("Glucose", checkupTx.getSugar(), "Negative"), "", "", ""));
                        checkup.add(new GenericModel(formatCheckupLab("Pmn", checkupTx.getPmn(), "%"), "", "", ""));
                        checkup.add(new GenericModel(formatCheckupLab("Alk", checkupTx.getAlk(), "0-41 U/L"), "", "", ""));
                        checkup.add(new GenericModel(formatCheckupLab("Alm", checkupTx.getAlm(), "3.5-5.2 g/dL"), "", "", ""));
                    }
                    return checkup;
                }).forEach((checkup) -> {
                    if (!checkup.isEmpty()) {
                        checkups.add(checkup);
                    }
                });

                medData.setCheckups(checkups);
            }

            XrayApi xrayApi = new XrayApi(CLIENT);
            String queryXray = "{\"where\" : {\"patient\":\"" + PATIENT_RESOURCE_PREFIX + id + "\"}}";
            List<Xray> existXrayTx = xrayApi.xrayFind(queryXray);

            List<List<GenericModel>> xrays = new ArrayList<>();
            if (existXrayTx != null && !existXrayTx.isEmpty()) {
                Collections.sort(existXrayTx, (Xray xray1, Xray xray2) -> {
                    if (xray1.getDateTimeService() == null) {
                        return 1;
                    } else if (xray2.getDateTimeService() == null) {
                        return -1;
                    } else {
                        return xray2.getDateTimeService().compareTo(xray1.getDateTimeService());
                    }
                });

                existXrayTx.stream().map((xrayTx) -> {
                    List<GenericModel> xray = new ArrayList<>();

                    if (Util.isNotEmpty(xrayTx.getXrayName())) {
                        String place = lookupHcp(xrayTx.getHealthCareProvider());
                        xray.add(new GenericModel("<span class='font-weight-bold'>" + xrayTx.getXrayName() + " </span><span class='text-info'>" + xrayTx.getXrayResult() + "</span>", Util.formatDateTime(xrayTx.getDateTimeService()), place, ""));
                        xray.add(new GenericModel(xrayTx.getXrayImage(), Util.formatDateTime(xrayTx.getDateTimeService()), place, ""));
                    }
                    return xray;

                }).forEach((xray) -> {
                    if (!xray.isEmpty()) {
                        xrays.add(xray);
                    }
                });

                medData.setXrays(xrays);
            }

            VaccinationApi vaccineApi = new VaccinationApi(CLIENT);
            String queryVaccine = "{\"where\" : {\"patient\":\"" + PATIENT_RESOURCE_PREFIX + id + "\"}}";
            List<Vaccination> existVaccineTx = vaccineApi.vaccinationFind(queryVaccine);

            List<GenericModel> vaccines = new ArrayList<>();
            if (existVaccineTx != null && !existVaccineTx.isEmpty()) {
                Collections.sort(existVaccineTx, (Vaccination vaccine1, Vaccination vaccine2) -> {
                    if (vaccine1.getDateTimeServe() == null) {
                        return 1;
                    } else if (vaccine2.getDateTimeServe() == null) {
                        return -1;
                    } else {
                        return vaccine2.getDateTimeServe().compareTo(vaccine1.getDateTimeServe());
                    }
                });

                existVaccineTx.stream().forEach((vaccineTx) -> {
                    vaccines.add(new GenericModel("<span class='font-weight-bold'>" + vaccineTx.getVaccineName() + " </span><span class='text-info'>(" + vaccineTx.getVaccineType() + ")</span>", Util.formatDateTime(vaccineTx.getDateTimeServe()), lookupHcp(vaccineTx.getHealthCareProvider()), ""));
                });
            }
            medData.setVaccines(vaccines);

        } catch (ApiException e) {
            LOG.error("findMedicalData error " + e.getResponseBody(), e);
        }
        LOG.info("Finished!");
        return medData;
    }

    @Override
    public boolean installData(String type) {
        try {
            WalletApi walletApi = new WalletApi(CLIENT);
            walletApi.cardSetDefault(HCP_WALLET);

            SystemApi apiInstance = new SystemApi(CLIENT);
            PingResponse result = apiInstance.systemPing();
            LOG.info("Ping result = " + result);
        } catch (ApiException ex) {
            LOG.error("installData set card default error ", ex);
            return false;
        }

        String csvFile;
        String line;
        String cvsSplitBy = ",";

        if (null != type) {
            switch (type) {
                case "vaccine":
                    csvFile = "D:/vaccine.csv";
                    try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(csvFile), "UTF-8"))) {
                        while ((line = br.readLine()) != null) {
                            // use comma as separator
                            String[] vaccineArr = line.split(cvsSplitBy);

                            LOG.debug("Vaccine [name= " + vaccineArr[0] + " , type=" + vaccineArr[1]
                                    + " , datetimeServe=" + vaccineArr[2] + " , booster=" + vaccineArr[3]
                                    + " , assetId=" + vaccineArr[4] + " , patientId=" + vaccineArr[5] + " , hcpId=" + vaccineArr[6] + "]");

                            try {
                                Vaccination vaccine = new Vaccination();
                                vaccine.setVaccineName(vaccineArr[0]);
                                vaccine.setVaccineType(vaccineArr[1]);
                                vaccine.setDateTimeServe(DateTime.parse(vaccineArr[2]));
                                vaccine.setNumberOfBooster(vaccineArr[3]);
                                vaccine.setAssetId(vaccineArr[4]);
                                vaccine.setPatient(PATIENT_RESOURCE_PREFIX + vaccineArr[5]);
                                vaccine.setHealthCareProvider(HCP_RESOURCE_PREFIX + vaccineArr[6]);

                                VaccinationApi vaccineApi = new VaccinationApi(CLIENT);
                                Vaccination createdVaccine = vaccineApi.vaccinationCreate(vaccine);
                                LOG.info("vaccine created transaction id = " + createdVaccine.getAssetId());
                            } catch (ApiException e) {
                                LOG.error("installData vaccine error " + e.getResponseBody(), e);
                                return false;
                            }
                        }
                        LOG.info("InstallData vaccine Finish!!");
                    } catch (IOException e) {
                        LOG.error("installData read vaccine file error ", e);
                    }
                    break;
                case "checkup":
                    csvFile = "D:/checkup.csv";
                    try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(csvFile), "UTF-8"))) {

                        while ((line = br.readLine()) != null) {
                            // use comma as separator
                            String[] checkupArr = line.split(cvsSplitBy);

                            LOG.debug("Checkup [id= " + checkupArr[0] + " , dateTimeServe=" + checkupArr[1] + " , dateTimeUpdate=" + checkupArr[2]
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
                            try {
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
                                checkupHist.setPatient(PATIENT_RESOURCE_PREFIX + checkupArr[27]);
                                checkupHist.setHealthCareProvider(HCP_RESOURCE_PREFIX + checkupArr[28]);

                                CheckupHistoryApi checkupHistoryApi = new CheckupHistoryApi(CLIENT);
                                CheckupHistory createdCheckupHist = checkupHistoryApi.checkupHistoryCreate(checkupHist);
                                LOG.info("checkup created transaction id = " + createdCheckupHist.getCheckupHistoryId());
                            } catch (ApiException e) {
                                LOG.error("installData checkup error " + e.getResponseBody(), e);
                                return false;
                            }
                        }
                        LOG.info("InstallData checkup Finish!!");
                    } catch (IOException e) {
                        LOG.error("installData read checkup file error ", e);
                    }
                    break;
                case "calcium":
                    csvFile = "D:/calcium.csv";
                    try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(csvFile), "UTF-8"))) {

                        while ((line = br.readLine()) != null) {
                            // use comma as separator
                            String[] checkupArr = line.split(cvsSplitBy);

                            LOG.debug("Checkup [id= " + checkupArr[0] + " , dateTimeServe=" + checkupArr[1] + " , dateTimeUpdate=" + checkupArr[2]
                                    + " , calcium_score_result=" + Util.encodeImage(new File(checkupArr[3])) + " , calcium_score_image=" + Util.encodeImage(new File(checkupArr[4]))
                                    + " , assetId=" + checkupArr[5] + " , patientId=" + checkupArr[6] + " , hcpId=" + checkupArr[7]
                                    + "]");
                            try {
                                CheckupHistory checkupHist = new CheckupHistory();
                                checkupHist.setCheckupHistoryId(checkupArr[0]);
                                checkupHist.setDateTimeServe(checkupArr[1]);
                                checkupHist.setDateTimeUpdate(checkupArr[2]);

                                checkupHist.setCalciumScoreResult(Util.encodeImage(new File(checkupArr[3])));
                                checkupHist.setCalciumScoreImage(Util.encodeImage(new File(checkupArr[4])));
                                checkupHist.setAssetId(checkupArr[5]);
                                checkupHist.setPatient(PATIENT_RESOURCE_PREFIX + checkupArr[6]);
                                checkupHist.setHealthCareProvider(HCP_RESOURCE_PREFIX + checkupArr[7]);

                                CheckupHistoryApi checkupHistoryApi = new CheckupHistoryApi(CLIENT);
                                CheckupHistory createdCheckupHist = checkupHistoryApi.checkupHistoryCreate(checkupHist);
                                LOG.info("calcium created transaction id = " + createdCheckupHist.getCheckupHistoryId());
                            } catch (ApiException e) {
                                LOG.error("installData calcium error " + e.getResponseBody(), e);
                                return false;
                            }
                        }
                        LOG.info("InstallData calcium Finish!!");
                    } catch (IOException e) {
                        LOG.error("installData read calcium file error ", e);
                    }
                    break;
                case "visit":
                    csvFile = "D:/visit.csv";
                    try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(csvFile), "UTF-8"))) {
                        while ((line = br.readLine()) != null) {
                            // use comma as separator
                            String[] visitArr = line.split(cvsSplitBy);

                            LOG.debug("Visit [serviceId= " + visitArr[0] + " , datetimeService=" + visitArr[1]
                                    + " , datetimeUpdate=" + visitArr[1] + " , patientId=" + visitArr[2]
                                    + " , hcpId=" + visitArr[3] + "]");

                            try {
                                ServiceHistory serviceHist = new ServiceHistory();
                                serviceHist.setServiceId(visitArr[0]);
                                serviceHist.setAssetId("asset" + visitArr[0]);
                                serviceHist.setDateTimeService(DateTime.parse(visitArr[1]));
                                serviceHist.setDateTimeUpdate(DateTime.parse(visitArr[1]));
                                serviceHist.setPatient(PATIENT_RESOURCE_PREFIX + visitArr[2]);
                                serviceHist.setHealthCareProvider(HCP_RESOURCE_PREFIX + visitArr[3]);

                                ServiceHistoryApi serviceHistApi = new ServiceHistoryApi(CLIENT);
                                ServiceHistory createdServiceHist = serviceHistApi.serviceHistoryCreate(serviceHist);
                                LOG.info("visit created transaction id = " + createdServiceHist.getAssetId());
                            } catch (ApiException e) {
                                LOG.error("installData visit error " + e.getResponseBody(), e);
                                return false;
                            }
                        }
                        LOG.info("InstallData visit Finish!!");
                    } catch (IOException e) {
                        LOG.error("installData read visit file error ", e);
                    }
                    break;
                case "diagnosis":
                    csvFile = "D:/diagnosis.csv";
                    try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(csvFile), "UTF-8"))) {
                        while ((line = br.readLine()) != null) {
                            // use comma as separator
                            String[] diagArr = line.split(cvsSplitBy);

                            LOG.debug("DiagHistory [diagId= " + diagArr[0] + " , datetimeService=" + diagArr[1]
                                    + " , diagName=" + diagArr[2] + " , datetimeUpdate=" + diagArr[1] + " , visit=" + diagArr[3]
                                    + " , assetId=" + diagArr[4] + " , patient=" + diagArr[5] + " , hcpId=" + diagArr[6] + "]");

                            try {
                                DiagHistory diagHist = new DiagHistory();
                                diagHist.setDiagId(diagArr[0]);
                                diagHist.setDateTimeService(DateTime.parse(diagArr[1]));
                                diagHist.setDiagName(diagArr[2]);
                                diagHist.setDiagType("Dx");
                                diagHist.setDateTimeUpdate(DateTime.parse(diagArr[1]));
                                diagHist.setAssetId(diagArr[4]);

                                diagHist.setVisit(SERVICE_RESOURCE_PREFIX + diagArr[3]);
                                diagHist.setPatient(PATIENT_RESOURCE_PREFIX + diagArr[5]);
                                diagHist.setHealthCareProvider(HCP_RESOURCE_PREFIX + diagArr[6]);

                                DiagHistoryApi diagHistApi = new DiagHistoryApi(CLIENT);
                                DiagHistory createdDiagHist = diagHistApi.diagHistoryCreate(diagHist);
                                LOG.info("diag created transaction id = " + createdDiagHist.getAssetId());
                            } catch (ApiException e) {
                                LOG.error("installData diag error " + e.getResponseBody(), e);
                                return false;
                            }
                        }
                        LOG.info("InstallData diag Finish!!");
                    } catch (IOException e) {
                        LOG.error("installData read diag file error ", e);
                    }
                    break;
                default:
                    break;
            }
        }
        return true;
    }

    private static void initLookupHcp() {
        if (HCP_LOOKUP.isEmpty()) {
            try {
                HealthCareProviderApi healthcareProviderApi = new HealthCareProviderApi(CLIENT);
                healthcareProviderApi.healthCareProviderFind("").stream().forEach((hcp) -> {
                    LOG.debug("initLookupHcp : " + hcp.getHealthCareProviderId() + ", " + hcp.getHealthCareProviderName());

                    HCP_LOOKUP.put(hcp.getHealthCareProviderId(), hcp.getHealthCareProviderName());
                });
            } catch (ApiException ex) {
                LOG.error("lookup hcp error ", ex);
            }
        }
    }

    private String lookupHcp(String hcpId) {
        String thisHcpId = hcpId;
        if (!hcpId.isEmpty() && hcpId.startsWith("resource:") && hcpId.contains("#")) {
            thisHcpId = hcpId.substring(hcpId.indexOf("#") + 1);
        }
        LOG.debug("lookupHcp id = " + thisHcpId);

        return HCP_LOOKUP.get(thisHcpId);
    }

    private String formatCheckupLab(String labName, String labValue, String normalRange) {
        labValue = labValue != null ? labValue : "-";
        return "<span class='font-weight-bold'>" + labName + ": </span><span class='text-info font-weight-bold'>" + labValue + "</span> [" + normalRange + "]";
    }
}
