package com.daoxuehao.java.dxcommon.sign;

import com.daoxuehao.java.dxcommon.util.MD5;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Yale
 * create at: 2022-06-07 16:52
 **/
public class CommonSigner implements ICommonSigner {

    private static final String SignKeyName = "dxSign";
    private static final String TimeStampKeyName = "dxTimeStamp";
    private static final String AppIdKeyName = "dxAppId";


    private String mSignKeyName = SignKeyName;
    private String mTimeStampKeyName = TimeStampKeyName;
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
                    params.put(i[0],i[1]);
                }
            }
        }
        params.remove(SignKeyName);
        params.remove(AppIdKeyName);
        params.remove(TimeStampKeyName);

        params.put(AppIdKeyName,mAppId);
        params.put(TimeStampKeyName,System.currentTimeMillis()+"");

        return params;
    }
    @Override
    public String getSign(String url) throws Exception {

        TreeMap params = getParamsMap(url);

        return getSign(params);
    }

    @Override
    public String getSign(Map<String, String> params) throws Exception {

        StringBuilder sb = new StringBuilder();
        for(Map.Entry<String,String> et:params.entrySet()){
            sb.append(et.getKey()).append(et.getValue());
        }
        sb.append(mAppKey);
        return MD5.getMd5Str(sb.toString());
    }

    @Override
    public String getSignUrl(String url) throws Exception {

        TreeMap<String,String> params = getParamsMap(url);

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
}
