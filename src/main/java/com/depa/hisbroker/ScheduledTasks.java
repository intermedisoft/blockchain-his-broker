/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.depa.hisbroker;

import com.depa.hisbroker.model.CheckupModel;
import com.depa.hisbroker.model.XrayModel;
import com.thoughtworks.xstream.XStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 *
 * @author Prasert
 */
@Component
public class ScheduledTasks {

    private static final Logger LOG = LoggerFactory.getLogger(ScheduledTasks.class);
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("HH:mm:ss");

    private final MedicalDataRepository medicalDataRepository;

    @Autowired
    private Environment env;

    @Autowired
    DataSyncDao dataDao;

    public ScheduledTasks(MedicalDataRepository medicalDataRepository) {
        this.medicalDataRepository = medicalDataRepository;
    }

    public void sync() {
        LOG.info("Start sync at {}", DATE_FORMAT.format(new Date()));
        LocalDate syncDate = LocalDate.now().minusDays(1);

        Iterable<DataSyncEntity> dataSyncs = dataDao.findAll();
        if (dataSyncs.iterator().hasNext()) {
            for (DataSyncEntity entity : dataSyncs) {
                LOG.info("entity = " + entity.toString());
                if (entity.getHisName().equals(env.getRequiredProperty("blockchain.his.wallet"))) {

                    if (entity.getLastSync().isBefore(syncDate)) {
                        LOG.info("Before Sync, last sync date = " + entity.getLastSync().toString());

                        getDataFromHis(entity.getLastSync(), syncDate);
                        /* update datetime*/
                        entity.setLastSync(syncDate);
                        entity.setDateTimeUpdate(LocalDateTime.now());
                        DataSyncEntity updatedEntity = dataDao.save(entity);
                        LOG.info("After Sync, last sync date = " + updatedEntity.getLastSync().toString());
                    } else {
                        LOG.warn("Not sync on last sync date = " + entity.getLastSync().toString() + ", current date = " + syncDate);
                    }
                }
            }
        } else {
            LOG.info("This is first time to sync");

            getDataFromHis(LocalDate.parse(env.getRequiredProperty("blockchain.his.startdate")), syncDate);
            /* create datetime*/
            DataSyncEntity entity = new DataSyncEntity(env.getRequiredProperty("blockchain.his.wallet"), syncDate, LocalDateTime.now());
            DataSyncEntity updatedEntity = dataDao.save(entity);
            LOG.info("First sync date = " + updatedEntity.getLastSync().toString());
        }
    }

    private void getDataFromHis(LocalDate beginDate, LocalDate endDate) {
        if (beginDate.isBefore(endDate)) {
            String csvPath = env.getRequiredProperty("blockchain.his.data.path");
            int days = Days.daysBetween(beginDate, endDate).getDays();
            for (int i = 1; i <= days; i++) {
                String csvFile = csvPath + "/checkup" + beginDate.plusDays(i).toString("yyyy-MM-dd") + ".csv";
                getFromFile(csvFile).stream().forEach((checkup) -> {
                    // create data on blockchain
                    medicalDataRepository.save(checkup);
                });
            }
        }
    }

    @Scheduled(fixedRate = 3600000)
    public void importData() {
        saveCheckupFromXml();
        saveXrayFromXml();
    }

    private void saveCheckupFromXml() {
        String filePath = env.getRequiredProperty("blockchain.his.data.path");
        String xmlFile = filePath + "/"+env.getRequiredProperty("blockchain.his.name")+"checkup" + LocalDate.now().toString("yyyy-MM-dd") + ".xml";
        File file = new File(xmlFile);
        if (file.exists()) {
            XStream xstream = new XStream();
            xstream.alias("CheckupModel", CheckupModel.class);
            List<CheckupModel> checkups = (List<CheckupModel>) xstream.fromXML(file);
            checkups.stream().forEach((checkup) -> {
                // create data on blockchain
                LOG.info(checkup.toString());
                medicalDataRepository.save(checkup);
            });
        } else {
            LOG.warn("not found file = " + file.getAbsolutePath());
        }
    }

    private void saveXrayFromXml() {
        String filePath = env.getRequiredProperty("blockchain.his.data.path");
        String xmlFile = filePath + "/"+env.getRequiredProperty("blockchain.his.name")+"xray" + LocalDate.now().toString("yyyy-MM-dd") + ".xml";
        File file = new File(xmlFile);
        if (file.exists()) {
            XStream xstream = new XStream();
            xstream.alias("XrayModel", XrayModel.class);
            List<XrayModel> xrays = (List<XrayModel>) xstream.fromXML(file);
            xrays.stream().forEach((xray) -> {
                LOG.info(xray.toString());
                medicalDataRepository.save(xray);
            });
        } else {
            LOG.warn("not found file = " + file.getAbsolutePath());
        }
    }

    private List<CheckupModel> getFromFile(String filename) {
        List<CheckupModel> checkups = new ArrayList();
        String line;
        String csvSplitBy = ",";
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filename), "UTF-8"))) {
            while ((line = br.readLine()) != null) {
                // use comma as separator
                String[] checkupArr = line.split(csvSplitBy);

                LOG.debug("Checkup [id= " + checkupArr[0] + " , visitId=" + checkupArr[1] + " , pid=" + checkupArr[2]
                        + " , pulse=" + checkupArr[3] + " , pressure=" + checkupArr[4]
                        + " , hb=" + checkupArr[5] + " , cbc_wbc=" + checkupArr[6]
                        + " , rbc=" + checkupArr[7] + " , hct=" + checkupArr[8] + " , lym=" + checkupArr[9]
                        + " , mono=" + checkupArr[10] + " , eos=" + checkupArr[11] + " , fbs=" + checkupArr[12]
                        + " , chlt=" + checkupArr[13] + " , creatinine=" + checkupArr[14] + " , bun=" + checkupArr[15]
                        + " , sgot=" + checkupArr[16] + " , sgpt=" + checkupArr[17] + " , trig=" + checkupArr[18]
                        + " , hdl=" + checkupArr[19] + " , uric=" + checkupArr[20] + " , ldl=" + checkupArr[21]
                        + " , spgr=" + checkupArr[22] + " , ph=" + checkupArr[23] + " , sugar=" + checkupArr[24]
                        + " , checkupDateTime=" + checkupArr[25]
                        + "]");

                CheckupModel checkup = new CheckupModel();
                checkup.setId(checkupArr[0]);

                checkup.setVisitId(checkupArr[1]);
                checkup.setPid(checkupArr[2]);

                checkup.setPulse(checkupArr[3]);
                checkup.setPressure(checkupArr[4]);
                checkup.setHb(checkupArr[5]);
                checkup.setWbc(checkupArr[6]);
                checkup.setRbc(checkupArr[7]);
                checkup.setHct(checkupArr[8]);
                checkup.setLym(checkupArr[9]);
                checkup.setMono(checkupArr[10]);
                checkup.setEos(checkupArr[11]);
                checkup.setFbs(checkupArr[12]);
                checkup.setChlt(checkupArr[13]);
                checkup.setCreatinine(checkupArr[14]);
                checkup.setBun(checkupArr[15]);
                checkup.setSgot(checkupArr[16]);
                checkup.setSgpt(checkupArr[17]);
                checkup.setTrig(checkupArr[18]);
                checkup.setHdl(checkupArr[19]);
                checkup.setUric(checkupArr[20]);
                checkup.setLdl(checkupArr[21]);
                checkup.setSpgr(checkupArr[22]);
                checkup.setPh(checkupArr[23]);
                checkup.setSugar(checkupArr[24]);
                checkup.setCheckupDateTime(checkupArr[25]);
                checkup.setAlk("");
                checkup.setAlm("");
                checkup.setPmn("");
                checkup.setUawbc("");

                checkups.add(checkup);
            }
            LOG.info("InstallData checkup Finish!!");
        } catch (IOException e) {
            LOG.error("installData read checkup file error ", e);
        }
        return checkups;
    }
}
