package com.daoxuehao.java.dxcommon.logback;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Yale
 * create at: 2020-12-07 10:48
 **/
public enum HttpJsonEvent {
    Self;

    private int period = 5000;
    private int batchNum = 10;
    private String url = null;
    private String name = "";

    private boolean prettyPrint= false;

    private String profile="";

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    private static ConcurrentLinkedQueue queue = new ConcurrentLinkedQueue();
    private  ExecutorService threadPool = Executors.newSingleThreadExecutor();
    private Timer timer=new Timer();




    private static ConcurrentLinkedQueue queueLog = new ConcurrentLinkedQueue();
    private  ExecutorService threadPoolLog = Executors.newSingleThreadExecutor();
    private Timer timerLog=new Timer();

    private com.daoxuehao.java.dxcommon.logback.HttpEventParamsCallBack httpEventParamsCallBack;

    static {

        HttpJsonEvent.Self.start();
    }


    public HttpJsonEvent setHttpEventParamsCallBack(com.daoxuehao.java.dxcommon.logback.HttpEventParamsCallBack callBack){
        httpEventParamsCallBack = callBack;
        return this;

    }

    public com.daoxuehao.java.dxcommon.logback.HttpEventParamsCallBack getHttpEventParamsCallBack(){
        return httpEventParamsCallBack;
    }


    public void start(){
        timer.schedule(new TimerTask() {
            @Override
            public void run() {


                threadPool.execute(new Runnable() {
                    @Override
                    public void run() {

                        JSONArray jsonArray = new JSONArray();
                        for(int i = 0;i<batchNum;i++){
                            String tmp = (String) queue.poll();
                            if (tmp==null){
                                break;
                            }
                            jsonArray.add(JSON.parseObject(tmp));
                        }
                        if (jsonArray.size()>0&&url!=null){

                            com.daoxuehao.java.dxcommon.logback.Http.postJson(url+"/hub/event",jsonArray.toJSONString());
                        }
                    }
                });

            }
        },0,period);


        timerLog.schedule(new TimerTask() {
            @Override
            public void run() {


                threadPoolLog.execute(new Runnable() {
                    @Override
                    public void run() {

                        JSONArray jsonArray = new JSONArray();
                        for(int i = 0;i<batchNum;i++){
                            String tmp = (String) queueLog.poll();
                            if (tmp==null){
                                break;
                            }
                            jsonArray.add(JSON.parseObject(tmp));
                        }
                        if (jsonArray.size()>0&&url!=null){

                            com.daoxuehao.java.dxcommon.logback.Http.postJson(url+"/hub/log",jsonArray.toJSONString());
                        }
                    }
                });

            }
        },0,period);

    }


    public HttpJsonEvent setUrl(String url){

        if (url!=null && url.startsWith("http")){
            this.url = url;
        }
        return this;
    }
    public void stop(){
        queue.clear();
        threadPool.shutdown();
        timer.cancel();

        queueLog.clear();
        threadPoolLog.shutdown();
        timerLog.cancel();
    }


    public void addDataEvent(JSONObject jsonObject){

        jsonObject.put("timestamp",TimeUtil.formatTimestamp(System.currentTimeMillis(),TimeUtil.timestampFormat,null));
        addData(jsonObject,queue);
    }

    public void addDataLog(JSONObject jsonObject){
        addData(jsonObject,queueLog);

    }


    private void addData(JSONObject jsonObject,ConcurrentLinkedQueue queue){

        jsonObject.put("host", com.daoxuehao.java.dxcommon.logback.IPUtils.getLocalHostIpv4());
        jsonObject.put("name", name);

        jsonObject.put("profile", profile);

        queue.add(jsonObject.toJSONString());
    }

    public HttpJsonEvent setPeriod(int period) {

        if (period<1000){
            period = 1000;
        }
        this.period = period;
        return this;
    }
    public HttpJsonEvent setBathNum(int num){
        if (num<1){
            num = 1;
        }
        this.batchNum = num;

        return this;
    }

    public int getPeriod() {
        return period;
    }

    public int getBatchNum() {
        return batchNum;
    }


    public boolean isPrettyPrint() {
        return prettyPrint;
    }

    public void setPrettyPrint(boolean prettyPrint) {
        this.prettyPrint = prettyPrint;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
