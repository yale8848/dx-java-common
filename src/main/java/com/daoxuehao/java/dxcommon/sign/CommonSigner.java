package com.daoxuehao.java.dxcommon.sign;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.daoxuehao.java.dxcommon.util.MD5;

import java.net.URL;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Yale
 * create at: 2022-06-07 16:52
 **/
public class CommonSigner implements ICommonSigner {

    private static final String SignKeyName = "dxSign";
    private static final String TimeStampKeyName = "dxTimeStamp";
    private static final String AppIdKeyName = "dxAppId";


    private String mAppId ="";
    private String mAppKey ="";
    private CommonSigner(String appId,String appKey){
        mAppId = appId;
        mAppKey = appKey;
    }
    public static ICommonSigner create(String appId,String appKey){
        return new CommonSigner(appId,appKey);
    }

    public static String getSignKeyName(){
        return SignKeyName;
    }

    public static String getTimeStampKeyName(){
        return TimeStampKeyName;
    }
    public static String getAppIdKeyName(){
        return AppIdKeyName;
    }

    private TreeMap getParamsMap(String url) throws Exception{
        URL u = new URL(url);
        String qs = u.getQuery();

        TreeMap<String, String> params = new TreeMap<>();
        if (qs!=null&&qs.length()>0){
            String[] ps = qs.split("&");
            for (String item:ps) {
                String[] i = item.split("=");
                if (i.length==1){
                    params.put(i[0],"");
                }else
                if (i.length==2){

                    String di = i[1];
                    try {
                        di =URLDecoder.decode(i[1],"utf-8");
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    params.put(i[0],di);
                }
            }
        }

        return params;
    }
    @Override
    public String getSign(String url) throws Exception {

        TreeMap params = getParamsMap(url);

        if (params.containsKey(SignKeyName)){
            throw new Exception("find "+SignKeyName);
        }
        if (!params.containsKey(AppIdKeyName)||!params.containsKey(TimeStampKeyName)){
            throw new Exception("not find "+AppIdKeyName+" or "+TimeStampKeyName);
        }

        return getSign(params);
    }

    @Override
    public String getSign(Map<String, String> params)  {

        boolean isTreeMap = params instanceof TreeMap;
        if (!isTreeMap){
            TreeMap<String,String> pa = new TreeMap<>();
            pa.putAll(params);
            params = pa;
        }
        StringBuilder sb = new StringBuilder();
        for(Map.Entry<String,String> et:params.entrySet()){
            sb.append(et.getKey()).append(et.getValue());
        }
        sb.append(mAppKey);
        return MD5.getMd5Str(sb.toString());
    }



    @Override
    public boolean checkSign(Map<String, String> params)  {

        if (!params.containsKey(SignKeyName)||!params.containsKey(AppIdKeyName)||!params.containsKey(TimeStampKeyName)){
            return false;
        }
        String appId=params.get(AppIdKeyName);
        if (!mAppId.equals(appId))return false;

        String sign = params.remove(SignKeyName);

        String vSign = getSign(params);

        return vSign.equals(sign);
    }

    @Override
    public boolean checkSign(String url) {

        try {
           Map<String,String> params = getParamsMap(url);
           return checkSign(params);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean checkSign(JSONObject jsonObject) {

        if (!jsonObject.containsKey(SignKeyName)||!jsonObject.containsKey(AppIdKeyName)||!jsonObject.containsKey(TimeStampKeyName)){
            return false;
        }

        TreeMap<String,String> pa = new TreeMap<>();
        for (Map.Entry<String, Object> en:jsonObject.entrySet()) {
            pa.put(en.getKey(),en.getValue().toString());
        }
        String sign = (String) jsonObject.remove(SignKeyName);
        String vSign = getSign(pa);
        return vSign.equals(sign);
    }



    @Override
    public String getSignUrl(String url) throws Exception {

        TreeMap<String,String> params = getParamsMap(url);


        params.remove(SignKeyName);
        params.remove(AppIdKeyName);
        params.remove(TimeStampKeyName);

        params.put(AppIdKeyName,mAppId);
        params.put(TimeStampKeyName,System.currentTimeMillis()+"");

        String sign = getSign(params);

        params.put(SignKeyName,sign);

        StringBuilder sb = new StringBuilder();
        URL u = new URL(url);
        sb.append(u.getProtocol()+"://"+u.getHost());
        int port = u.getPort();
        if (port!=-1){
            sb.append(":"+port);
        }
        sb.append(u.getPath()+"?");

        for(Map.Entry<String,String> entry : params.entrySet()){
            sb.append(entry.getKey()).append("=").append(entry.getValue());
            sb.append("&");
        }
        if (sb.charAt(sb.length()-1) == '&'){
            sb.deleteCharAt(sb.length()-1);
        }
        return sb.toString();
    }

    @Override
    public void addSignParams(Map<String, String> params) {


        params.put(AppIdKeyName,mAppId);
        params.put(TimeStampKeyName,System.currentTimeMillis()+"");

        boolean isTreeMap = params instanceof TreeMap;
        if (!isTreeMap){
            TreeMap<String,String> pa = new TreeMap<>();
            pa.putAll(params);
            params = pa;
        }
        StringBuilder sb = new StringBuilder();
        for(Map.Entry<String,String> et:params.entrySet()){
            sb.append(et.getKey()).append(et.getValue());
        }
        sb.append(mAppKey);

        params.put(SignKeyName,MD5.getMd5Str(sb.toString()));
    }

    @Override
    public void addSignParams(JSONObject jsonObject) {

        TreeMap<String,String> pa = new TreeMap<>();
        for (Map.Entry<String, Object> en:jsonObject.entrySet()) {
            pa.put(en.getKey(),en.getValue().toString());
        }
        if (!jsonObject.containsKey(AppIdKeyName)){
            jsonObject.put(AppIdKeyName,mAppId);
        }
        if (!jsonObject.containsKey(TimeStampKeyName)){
            jsonObject.put(TimeStampKeyName,System.currentTimeMillis()+"");
        }
        jsonObject.remove(SignKeyName);

        jsonObject.put(SignKeyName,getSign(pa));


    }
}
