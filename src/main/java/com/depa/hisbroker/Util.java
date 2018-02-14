/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.depa.hisbroker;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;

/**
 *
 * @author Prasert
 */
public class Util {
    private final static Logger LOG = Logger.getLogger(Util.class);
    
    public static boolean isNotEmpty(String param){
        return param != null && !param.isEmpty();
    }
    
    public static String encodeImage(File file) {
        String hexString = "";
        FileInputStream imageInFile = null;
        try {
            imageInFile = new FileInputStream(file);
            byte imageData[] = new byte[(int) file.length()];
            imageInFile.read(imageData);
            hexString = encodeImage(imageData);
        } catch (FileNotFoundException ex) {
            LOG.error("encodeImage error : ", ex);
        } catch (IOException ex) {
            LOG.error("encodeImage error : ", ex);
        } finally {
            try {
                if (imageInFile != null) {
                    imageInFile.close();
                }
            } catch (IOException ex) {
                LOG.error("encodeImage close file error : ", ex);
            }
        }
        return hexString;
    }
    
    public static String encodeImage(byte[] byteArray) {
        return Base64.encodeBase64String(byteArray);
    }

    public static byte[] decodeImage(String imageDataString) {
        return Base64.decodeBase64(imageDataString);
    }
    
    public static String formatDateTime(DateTime dateTime){
        return dateTime.toString("dd-MM-yyyy, hh:mm:ss");
    }
    
    public static String formatDateTime(String dateTime){
        return DateTime.parse(dateTime).toString("dd-MM-yyyy, hh:mm:ss");
    }
}
