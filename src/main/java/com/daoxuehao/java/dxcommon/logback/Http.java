package com.daoxuehao.java.dxcommon.logback;

import com.alibaba.fastjson.JSONObject;
import com.dxjy.dxdesklamp.common.UserEncryptCode;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.Map;

/**
 * Yale
 * create at: 2020-12-07 10:43
 **/
public class Http {

    public static boolean canLog(String method,String reqContentType) {
        if (method ==null || method == ""){
            return false;
        }

        if (reqContentType ==null || reqContentType == ""){
            return false;
        }

        method = method.toLowerCase();
        reqContentType = reqContentType.toLowerCase();

        if (method.equals("get")){
            return true;
        }

        if (method.equals("post")&&(reqContentType.contains("application/json") ||
                reqContentType.contains("application/x-www-form-urlencoded") ||
                reqContentType.contains("application/xml")
                )){

            return true;
        }


        return false;



    }

    public static JSONObject getHeader(HttpServletRequest request) {
        JSONObject header = new JSONObject();
        try {
            Enumeration<String> en = request.getHeaderNames();
            if (en!=null){
                while (en.hasMoreElements()){
                    String n = en.nextElement();
                    header.put(n,request.getHeader(n));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return header;
    }


    public static JSONObject getParam(HttpServletRequest request) {
        JSONObject param = new JSONObject();
        try {
            Map<String, String[]> params = request.getParameterMap();
            for (String key : params.keySet()) {
                String[] values = params.get(key);
                for (int i = 0; i < values.length; i++) {
                    String value = values[i];
                    param.put(key, value);
                }
            }

            Enumeration<String> en = request.getHeaderNames();
            JSONObject header = new JSONObject();
            if (en!=null){
                while (en.hasMoreElements()){
                    String n = en.nextElement();
                    header.put(n,request.getHeader(n));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return param;
    }

    public static void postJson(String url,final String event) {
        try {
            URL endpoint = new URL(url);
            final HttpURLConnection connection =(HttpURLConnection) endpoint.openConnection();;
    /*        if (proxy == null) {
                //connection = (HttpURLConnection) endpoint.openConnection();
            } else {
                // connection = (HttpURLConnection) endpoint.openConnection(proxy);
            }*/
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.addRequestProperty("Content-Type", "application/json");
            connection.connect();
            sendAndClose(event, connection.getOutputStream());
            connection.disconnect();
            final int responseCode = connection.getResponseCode();
            if (responseCode != 200) {
                final String message = readResponseBody(connection.getInputStream());
                System.out.println(message);
                //addError("Loggly post failed (HTTP " + responseCode + ").  Response body:\n" + message);
            }
        } catch (final Exception e) {
            //addError("IOException while attempting to communicate with Loggly", e);
            e.printStackTrace();
        }

    }


    private static void sendAndClose(final String event, final OutputStream output) throws IOException {
        try {
            output.write(event.getBytes("UTF-8"));
        } finally {
            output.close();
        }
    }
    public static byte[] toBytes(final InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int count;
        byte[] buf = new byte[512];

        while((count = is.read(buf, 0, buf.length)) != -1) {
            baos.write(buf, 0, count);
        }
        baos.flush();

        return baos.toByteArray();
    }
    public static String readResponseBody(final InputStream input) throws IOException {
        try {
            final byte[] bytes = toBytes(input);
            return new String(bytes, "UTF-8");
        } finally {
            input.close();
        }
    }
}
