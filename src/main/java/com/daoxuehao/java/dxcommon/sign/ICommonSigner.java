package com.daoxuehao.java.dxcommon.sign;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.util.Map;

/**
 * Created by yale on 2022/6/7.
 */
public interface ICommonSigner {


    //add sign params in url
    String getSignUrl(String url) throws Exception;

    //add sign params in map
    void addSignParams(Map<String,String> params);

    //add sign params in map
    void addSignParams(JSONObject jsonStr );

    //check sign params in map
    boolean checkSign(Map<String,String> params);

    //check sign with url
    boolean checkSign(String url);


    //check sign params in JSON
    boolean checkSign(JSONObject params);

    //get sign with url
    String getSign(String url) throws Exception;

    //get sign in map
    String getSign(Map<String,String> params);




}
