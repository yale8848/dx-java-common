package com.daoxuehao.java.dxcommon.logback;

import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TimeZone;

/**
 * Yale
 * create at: 2020-12-05 15:02
 **/

public class HttpJsonLogAppender extends UnsynchronizedAppenderBase<LoggingEvent> {


    protected String timestampFormat = "yyyy-MM-dd HH:mm:ss.SSS";
    protected String timestampFormatTimezoneId;
    protected String url;
    private boolean prettyPrint = true;
    private String name="";
    private String proxy;

    private String profile;
    private int period;
    private int batchNum;



    public JSONObject toJsonObj(Map var1)  {

        JSONObject jsonObject = new JSONObject();

        try {
            return (JSONObject) JSON.toJSON(var1);
        }catch (Exception e){
            e.printStackTrace();

            jsonObject.put("logName",var1.get("logName"));
            jsonObject.put("level",var1.get("level"));
            jsonObject.put("message",var1.get("message"));
            return  jsonObject ;

        }


    }

    public String toJsonString(Map var1)  {

        try {
            return JSON.toJSONString(var1,prettyPrint);
        }catch (Exception e){

            e.printStackTrace();

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("logName",var1.get("logName"));
            jsonObject.put("level",var1.get("level"));
            jsonObject.put("message",var1.get("message"));
            return jsonObject.toJSONString(prettyPrint);

        }


    }


    protected String formatTimestamp(long var1) {


        return TimeUtil.formatTimestamp(var1,timestampFormat,timestampFormatTimezoneId);
    }


    @Override
    public void start() {
        super.start();

        HttpJsonEvent.Self.setUrl(url);
        HttpJsonEvent.Self.setName(name);
        HttpJsonEvent.Self.setPeriod(period);
        HttpJsonEvent.Self.setBathNum(batchNum);
        HttpJsonEvent.Self.setProfile(profile);

    }
    @Override
    public void stop() {
        super.stop();

        HttpJsonEvent.Self.stop();
    }

    @Override
    protected void append(LoggingEvent e) {

        LinkedHashMap var2 = new LinkedHashMap();


        var2.put("level",e.getLevel().levelStr);
        var2.put("timestamp",formatTimestamp(e.getTimeStamp()));
        var2.put("logger",e.getLoggerName());
        var2.put("message",e.getMessage());
        //var2.put("raw-message",e.getFormattedMessage());
        //var2.put("context",e.getLoggerContextVO().getName());
        var2.put("thread",e.getThreadName());
        //System.out.println(toJsonString(var2));




        HttpJsonEvent.Self.addDataLog(toJsonObj(var2));

    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isPrettyPrint() {
        return this.prettyPrint;
    }

    public void setPrettyPrint(boolean var1) {
        this.prettyPrint = var1;
    }

    public String getTimestampFormat() {
        return timestampFormat;
    }

    public void setTimestampFormat(String timestampFormat) {
        this.timestampFormat = timestampFormat;
    }

    public String getTimestampFormatTimezoneId() {
        return timestampFormatTimezoneId;
    }

    public void setTimestampFormatTimezoneId(String timestampFormatTimezoneId) {
        this.timestampFormatTimezoneId = timestampFormatTimezoneId;
    }

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    public int getBatchNum() {
        return batchNum;
    }

    public void setBatchNum(int batchNum) {
        this.batchNum = batchNum;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }
}
