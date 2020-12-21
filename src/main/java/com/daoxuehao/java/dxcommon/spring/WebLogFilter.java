package com.daoxuehao.java.dxcommon.spring;

import com.daoxuehao.java.dxcommon.logback.BodyCachingHttpServletRequestWrapper;
import com.daoxuehao.java.dxcommon.logback.BodyCachingHttpServletResponseWrapper;
import com.daoxuehao.java.dxcommon.logback.HttpJsonEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Yale
 * create at: 2020-12-07 14:39
 **/
@WebFilter(filterName = "WebLogFilter", urlPatterns = "/*")
public class WebLogFilter implements Filter,Ordered {


    @Value("${dxcommon.logback.name}")
    private String name;
    @Value("${dxcommon.logback.url}")
    private String url;

    @Value("${dxcommon.logback.period}")
    private int period;
    @Value("${dxcommon.logback.batchNum}")
    private int batchNum;

    @Value("${spring.profiles.active}")
    private String profile;

    private int order = Ordered.LOWEST_PRECEDENCE - 80000;


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

        HttpJsonEvent.Self.setUrl(url);
        HttpJsonEvent.Self.setName(name);
        HttpJsonEvent.Self.setPeriod(period);
        HttpJsonEvent.Self.setBathNum(batchNum);
        HttpJsonEvent.Self.setProfile(profile);

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {


        if (servletRequest instanceof  HttpServletRequest){

            BodyCachingHttpServletResponseWrapper responseWrapper =
                    new BodyCachingHttpServletResponseWrapper((HttpServletResponse) servletResponse);

            HttpServletRequest hs = (HttpServletRequest) servletRequest;

            String reqCt = hs.getHeader("Content-Type");

            if (reqCt!=null&&reqCt.contains("application/json")){
                BodyCachingHttpServletRequestWrapper requestWrapper =
                        new BodyCachingHttpServletRequestWrapper((HttpServletRequest) servletRequest);
                filterChain.doFilter(requestWrapper , responseWrapper);
            }else{
                filterChain.doFilter(servletRequest , responseWrapper);
            }

        }else{
            filterChain.doFilter(servletRequest , servletResponse);
        }



    }

    @Override
    public void destroy() {

    }



    @Override
    public int getOrder() {
        return order;
    }
}
