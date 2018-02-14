/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.depa.hisbroker.model;

/**
 *
 * @author Prasert
 */
public class XrayModel {
    private String id;
    private String name;
    private String result;
    private String resultDateTime;
    private String pid;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getResultDateTime() {
        return resultDateTime;
    }

    public void setResultDateTime(String resultDateTime) {
        this.resultDateTime = resultDateTime;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    @Override
    public String toString() {
        return "XrayModel{" + "id=" + id + ", name=" + name + ", result=" + result + ", resultDateTime=" + resultDateTime + ", pid=" + pid + '}';
    }
    
    public String toCsv() {
        String cvsSplitBy = ",";
        StringBuilder stb = new StringBuilder();
        return stb.append(id).append(cvsSplitBy).append(pid).append(cvsSplitBy)
                .append(name).append(cvsSplitBy).append(result).append(cvsSplitBy)
                .append(resultDateTime)
                .toString();
    }
}
