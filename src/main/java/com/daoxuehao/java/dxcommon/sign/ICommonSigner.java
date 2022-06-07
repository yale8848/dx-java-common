package com.daoxuehao.java.dxcommon.sign;

import java.util.Map;

/**
 * Created by yale on 2022/6/7.
 */
public interface ICommonSigner {

    String getSign(String url) throws Exception;
    String getSign(Map<String,String> params) throws Exception;
    String getSignUrl(String url) throws Exception;
}
