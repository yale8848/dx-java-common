package com.daoxuehao.java.dxcommon.spring;

import com.alibaba.fastjson.JSONObject;
import com.daoxuehao.java.dxcommon.logback.*;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.SortedMap;

@Configuration
public class WebConfInterceptor implements WebMvcConfigurer {
    private static  JSONObject getRes(HttpServletResponse arg1){
        JSONObject jsonObject = new JSONObject();
        try {
            ServletOutputStream so = arg1.getOutputStream();

            if (so instanceof  BodyCachingHttpServletResponseWrapper.ServletOutputStreamWrapper){


                BodyCachingHttpServletResponseWrapper.ServletOutputStreamWrapper bc = (BodyCachingHttpServletResponseWrapper.ServletOutputStreamWrapper) so;

                byte[] responseBody = bc.getOutputStream().toByteArray();


                String res = new String(responseBody,"UTF-8");


                return JSONObject.parseObject(res);


            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        return jsonObject;

    }
    private static  JSONObject getJsonParams(HttpServletRequest arg0){
        JSONObject jsonObject = new JSONObject();
        try {

            if (arg0 instanceof  BodyCachingHttpServletRequestWrapper){


                BodyCachingHttpServletRequestWrapper bsr = (BodyCachingHttpServletRequestWrapper) arg0;

                byte[] responseBody =  bsr.getBody();


                String res = new String(responseBody,"UTF-8");


                return JSONObject.parseObject(res);


            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        return jsonObject;

    }
    public static void postInterHandle(HttpServletRequest arg0, HttpServletResponse arg1){
        String method =arg0.getMethod().toLowerCase();

        String resCt = arg1.getHeader("Content-Type");
        String reqCt = arg0.getHeader("Content-Type");


        if (reqCt == null){
            reqCt = "";
        }
        if (resCt==null){
            return;
        }

        if (!method.equals("post")&&!method.equals("get"))
            return;


        if (reqCt.contains("application/octet-stream")||reqCt.contains("multipart/form-data")){
            return;
        }

        if (!resCt.contains("application/json")){
            return;
        }


        JSONObject jsonObject = new JSONObject();

        jsonObject.put("header", Http.getHeader(arg0));
        if(reqCt.contains("application/json")){

            jsonObject.put("params", getJsonParams(arg0));

        }else{
            jsonObject.put("params", Http.getParam(arg0));
        }
        jsonObject.put("res", getRes(arg1));

        jsonObject.put("event", arg0.getRequestURI());
        jsonObject.put("remoteIp", IPUtils.getIp(arg0));

        HttpEventParamsCallBack httpEventParamsCallBack = HttpJsonEvent.Self.getHttpEventParamsCallBack();


        if (httpEventParamsCallBack !=null){
            SortedMap<String,String> map = httpEventParamsCallBack.getAdditionParams(arg0);
            if (map!=null){
                for (SortedMap.Entry<String,String> entry :map.entrySet()) {
                    jsonObject.put(entry.getKey(),entry.getValue());
                }
            }

            String uid = httpEventParamsCallBack.getUserId(arg0);
            if (uid!=null){
                jsonObject.put("userId", uid);
            }

        }

        HttpJsonEvent.Self.addDataEvent(jsonObject);

    }

    public static void addInterceptor(InterceptorRegistry registry){
        HandlerInterceptor inter = new HandlerInterceptor() {

            @Override
            public boolean preHandle(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2) throws Exception {


                return true;
            }



            @Override
            public void postHandle(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, ModelAndView arg3)
                    throws Exception {

                postInterHandle(arg0,arg1);

            }

            @Override
            public void afterCompletion(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, Exception arg3)
                    throws Exception {
                // TODO Auto-generated method stub
            }
        };
        registry.addInterceptor(inter).addPathPatterns("/**");
    }
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        addInterceptor(registry);
    }
}