package com.jdjz.lrucachedemo.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

/**
 * Created by happen on 2018-07-19.
 */

public class JsonToStringUtil {
    public static String JsonToString(InputStream is) {
        InputStreamReader isr;
        String result = "";
        String line = "";
        try {
            isr = new InputStreamReader(is, "utf-8");
            BufferedReader br = new BufferedReader(isr);
            try {
                while ((line = br.readLine()) != null) {
                    result += line;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }
}
