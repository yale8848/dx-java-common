package com.daoxuehao.java.dxcommon.sign;

import junit.framework.TestCase;

/**
 * Created by yale on 2022/6/7.
 */
public class CommonSignerTest extends TestCase {

    public void testCreate() {
    }

    public void testGetSignKeyName() {
    }

    public void testGetSign() {
    }

    public void testTestGetSign() {
    }

    public void testGetSignUrl() {

      ICommonSigner commonSigner =   CommonSigner.create("a","b");

      try {
          commonSigner.getSignUrl("http://www.baidu.com");
          commonSigner.getSignUrl("http://www.baidu.com/get");
          commonSigner.getSignUrl("https://www.baidu.com/get?");
          commonSigner.getSignUrl("https://www.baidu.com/get?a");
          commonSigner.getSignUrl("https://www.baidu.com/get?a=");
          commonSigner.getSignUrl("https://www.baidu.com/get?a=b");
          commonSigner.getSignUrl("https://www.baidu.com/get?a=b&cc=bb");
          commonSigner.getSignUrl("https://www.baidu.com:8889/get?a=b&cc=bb");
      }catch (Exception e){

      }

    }
}