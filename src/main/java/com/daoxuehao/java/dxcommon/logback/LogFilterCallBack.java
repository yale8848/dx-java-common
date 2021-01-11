package com.daoxuehao.java.dxcommon.logback;

import com.alibaba.fastjson.JSONObject;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

/**
 * Created by yale on 2021/1/11.
 */
public interface LogFilterCallBack {

    String logWindowFilter( HashMap<String,Long> timeWindow ,JSONObject ret);
    boolean ignore(JSONObject ret);
}
