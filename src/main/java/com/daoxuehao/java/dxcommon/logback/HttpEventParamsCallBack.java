package com.daoxuehao.java.dxcommon.logback;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.SortedMap;

/**
 * Yale
 * create at: 2020-12-07 16:03
 **/
public interface HttpEventParamsCallBack {

    String getUserId(HttpServletRequest arg0);

    SortedMap<String,String> getAdditionParams(HttpServletRequest arg0);
}
