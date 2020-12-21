package com.daoxuehao.java.dxcommon.logback;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Yale
 * create at: 2020-12-07 14:27
 **/
public class BodyCachingHttpServletRequestWrapper extends HttpServletRequestWrapper {


    private byte[] body;
    private ServletInputStreamWrapper inputStreamWrapper;

    private HashMap<String,String[]> paramsMap = new HashMap<>();

    public BodyCachingHttpServletRequestWrapper(HttpServletRequest request) throws IOException {
        super(request);


        this.copyParams();

        this.body = IOUtils.toBytes(request.getInputStream());
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(this.body);
        this.inputStreamWrapper = new ServletInputStreamWrapper(byteArrayInputStream);
        resetInputStream();
    }

    private void resetInputStream() {
        this.inputStreamWrapper.setInputStream(new ByteArrayInputStream(this.body != null ? this.body : new byte[0]));
    }

    public byte[] getBody() {
        return body;
    }

    public void copyParams(){
        if (super.getParameterMap() == null){
            return;
        }
        Map<String,String[]> map =super.getParameterMap();
        for (String key : map.keySet()) {
            paramsMap.put(key, map.get(key));
        }
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        return this.inputStreamWrapper;
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(this.inputStreamWrapper));
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        return paramsMap;
    }

    @Data
    @AllArgsConstructor
    private static class ServletInputStreamWrapper extends ServletInputStream {

        private InputStream inputStream;

        @Override
        public boolean isFinished() {
            return true;
        }

        @Override
        public boolean isReady() {
            return false;
        }

        @Override
        public void setReadListener(ReadListener readListener) {

        }

        @Override
        public int read() throws IOException {
            return this.inputStream.read();
        }
    }
}
