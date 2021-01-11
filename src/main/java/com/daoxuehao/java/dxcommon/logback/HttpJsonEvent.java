package com.daoxuehao.java.dxcommon.logback;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

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


    private AtomicBoolean isQueueFinish =new AtomicBoolean(true);
    private AtomicBoolean isQueueLogFinish =new AtomicBoolean(true);

    private com.daoxuehao.java.dxcommon.logback.HttpEventParamsCallBack httpEventParamsCallBack;

    private LogFilterCallBack logFilterCallBack;

    private HashMap<String,TimeRecord> timeWindow = new HashMap<>();

    static {

        HttpJsonEvent.Self.start();
    }


    public HttpJsonEvent setLogFilterCallBack(LogFilterCallBack logFilterCallBack){
        this.logFilterCallBack = logFilterCallBack;
        return this;
    }

    public HttpJsonEvent setHttpEventParamsCallBack(com.daoxuehao.java.dxcommon.logback.HttpEventParamsCallBack callBack){
        httpEventParamsCallBack = callBack;
        return this;

    }

    public com.daoxuehao.java.dxcommon.logback.HttpEventParamsCallBack getHttpEventParamsCallBack(){
        return httpEventParamsCallBack;
    }

    private void httpPost(ConcurrentLinkedQueue queue,String path){
        JSONArray jsonArray = new JSONArray();
        for(int i = 0;i<batchNum;i++){
            String tmp = (String) queue.poll();
            if (tmp==null){
                break;
            }
            jsonArray.add(JSON.parseObject(tmp));
        }
        if (jsonArray.size()>0&&url!=null){

            JSONObject  jsonObject = new JSONObject();

            jsonObject.put("data",jsonArray);

            com.daoxuehao.java.dxcommon.logback.Http.postJson(url+path,jsonObject.toJSONString());
        }
    }

    public void start(){
        timer.schedule(new TimerTask() {
            @Override
            public void run() {

                if (!isQueueFinish.get()){
                    return;
                }

                threadPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        if (isQueueFinish.compareAndSet(true,false)){
                            httpPost(queue,"/hub/event");
                            isQueueFinish.set(true);
                        }
                    }
                });

            }
        },0,period);


        timerLog.schedule(new TimerTask() {
            @Override
            public void run() {
                if (!isQueueLogFinish.get()){
                    return;
                }

                threadPoolLog.execute(new Runnable() {
                    @Override
                    public void run() {
                        if (isQueueLogFinish.compareAndSet(true,false)){
                            httpPost(queueLog,"/hub/log");
                            isQueueLogFinish.set(true);
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

    private boolean ignore(JSONObject jsonObject){
        if (logFilterCallBack!=null&&logFilterCallBack.ignore(jsonObject))  return true;

        if (logFilterCallBack!=null){
            String  key = logFilterCallBack.logWindowFilter(timeWindow,jsonObject);

            if (key!=null&&key.length()>0&&timeWindow.containsKey(key)){

                TimeRecord t = timeWindow.get(key);
                if (t!=null&&t.getTimeWindow()>0){

                    Long n  = System.currentTimeMillis();
                    Long l = t.getLastTime();
                    if (l == 0){
                        t.setLastTime(n);
                        return false;
                    }
                    if (n-l<= t.getTimeWindow()) return true;
                    t.setLastTime(n);
                }
            }
        }
        return false;
    }

    public void addDataEvent(JSONObject jsonObject){


        if (ignore(jsonObject)) return;


        jsonObject.put("timestamp",TimeUtil.formatTimestamp(System.currentTimeMillis(),TimeUtil.timestampFormat,null));
        addData(jsonObject,queue);
    }

    public void addDataLog(JSONObject jsonObject){

        if (ignore(jsonObject)) return;
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
