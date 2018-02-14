/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.depa.hisbroker.model;

import org.joda.time.LocalDateTime;

/**
 *
 * @author Prasert
 */
public class CheckupModel {
    private String id;
    private String hb;
    private String wbc;
    private String rbc;
    private String hct;
    private String lym;
    private String mono;
    private String eos;
    private String fbs;
    private String chlt;
    private String creatinine;
    private String bun;
    private String sgot;
    private String sgpt;
    private String trig;
    private String hdl;
    private String uric;
    private String ldl;
    private String spgr;
    private String ph;
    private String sugar;
    private String alk;
    private String alm;
    private String hba1c;
    private String pmn;
    private String uawbc;
    private String pressure;
    private String pulse;
    private String visitId;
    private String pid;
    private String checkupDateTime;
    private String conclusion;
    private String recommendation;

    public String getHb() {
        return hb;
    }

    public void setHb(String hb) {
        this.hb = hb;
    }

    public String getWbc() {
        return wbc;
    }

    public void setWbc(String wbc) {
        this.wbc = wbc;
    }

    public String getRbc() {
        return rbc;
    }

    public void setRbc(String rbc) {
        this.rbc = rbc;
    }

    public String getHct() {
        return hct;
    }

    public void setHct(String hct) {
        this.hct = hct;
    }

    public String getLym() {
        return lym;
    }

    public void setLym(String lym) {
        this.lym = lym;
    }

    public String getMono() {
        return mono;
    }

    public void setMono(String mono) {
        this.mono = mono;
    }

    public String getEos() {
        return eos;
    }

    public void setEos(String eos) {
        this.eos = eos;
    }

    public String getFbs() {
        return fbs;
    }

    public void setFbs(String fbs) {
        this.fbs = fbs;
    }

    public String getChlt() {
        return chlt;
    }

    public void setChlt(String chlt) {
        this.chlt = chlt;
    }

    public String getCreatinine() {
        return creatinine;
    }

    public void setCreatinine(String creatinine) {
        this.creatinine = creatinine;
    }

    public String getBun() {
        return bun;
    }

    public void setBun(String bun) {
        this.bun = bun;
    }

    public String getSgot() {
        return sgot;
    }

    public void setSgot(String sgot) {
        this.sgot = sgot;
    }

    public String getSgpt() {
        return sgpt;
    }

    public void setSgpt(String sgpt) {
        this.sgpt = sgpt;
    }

    public String getTrig() {
        return trig;
    }

    public void setTrig(String trig) {
        this.trig = trig;
    }

    public String getHdl() {
        return hdl;
    }

    public void setHdl(String hdl) {
        this.hdl = hdl;
    }

    public String getUric() {
        return uric;
    }

    public void setUric(String uric) {
        this.uric = uric;
    }

    public String getLdl() {
        return ldl;
    }

    public void setLdl(String ldl) {
        this.ldl = ldl;
    }

    public String getSpgr() {
        return spgr;
    }

    public void setSpgr(String spgr) {
        this.spgr = spgr;
    }

    public String getPh() {
        return ph;
    }

    public void setPh(String ph) {
        this.ph = ph;
    }

    public String getSugar() {
        return sugar;
    }

    public void setSugar(String sugar) {
        this.sugar = sugar;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAlk() {
        return alk;
    }

    public void setAlk(String alk) {
        this.alk = alk;
    }

    public String getHba1c() {
        return hba1c;
    }

    public void setHba1c(String hba1c) {
        this.hba1c = hba1c;
    }

    public String getPmn() {
        return pmn;
    }

    public void setPmn(String pmn) {
        this.pmn = pmn;
    }

    public String getUawbc() {
        return uawbc;
    }

    public void setUawbc(String uawbc) {
        this.uawbc = uawbc;
    }

    public String getPressure() {
        return pressure;
    }

    public void setPressure(String pressure) {
        this.pressure = pressure;
    }

    public String getPulse() {
        return pulse;
    }

    public void setPulse(String pulse) {
        this.pulse = pulse;
    }

    public String getVisitId() {
        return visitId;
    }

    public void setVisitId(String visitId) {
        this.visitId = visitId;
    }

    public String getCheckupDateTime() {
        return checkupDateTime;
    }

    public void setCheckupDateTime(String checkupDateTime) {
        this.checkupDateTime = checkupDateTime;
    }

    public String getAlm() {
        return alm;
    }

    public void setAlm(String alm) {
        this.alm = alm;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getConclusion() {
        return conclusion;
    }

    public void setConclusion(String conclusion) {
        this.conclusion = conclusion;
    }

    public String getRecommendation() {
        return recommendation;
    }

    public void setRecommendation(String recommendation) {
        this.recommendation = recommendation;
    }

    @Override
    public String toString() {
        return "CheckupModel{" + "id=" + id + ", hb=" + hb + ", wbc=" + wbc + ", rbc=" + rbc + ", hct=" + hct + ", lym=" + lym + ", mono=" + mono + ", eos=" + eos + ", fbs=" + fbs + ", chlt=" + chlt + ", creatinine=" + creatinine + ", bun=" + bun + ", sgot=" + sgot + ", sgpt=" + sgpt + ", trig=" + trig + ", hdl=" + hdl + ", uric=" + uric + ", ldl=" + ldl + ", spgr=" + spgr + ", ph=" + ph + ", sugar=" + sugar + ", alk=" + alk + ", alm=" + alm + ", hba1c=" + hba1c + ", pmn=" + pmn + ", uawbc=" + uawbc + ", pressure=" + pressure + ", pulse=" + pulse + ", visitId=" + visitId + ", pid=" + pid + ", checkupDateTime=" + checkupDateTime + ", conclusion=" + conclusion + ", recommendation=" + recommendation + '}';
    }

    
}
