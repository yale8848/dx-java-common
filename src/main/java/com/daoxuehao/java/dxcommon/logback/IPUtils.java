package com.daoxuehao.java.dxcommon.logback;


import javax.servlet.http.HttpServletRequest;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

public class IPUtils {



    public static String getIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (StringUtils.isNotEmpty(ip) && !"unKnown".equalsIgnoreCase(ip)) {
            // 多次反向代理后会有多个ip值，第一个ip才是真实ip
            int index = ip.indexOf(",");
            if (index != -1) {
                return ip.substring(0, index);
            } else {
                return ip;
            }
        }
        ip = request.getHeader("X-Real-IP");
        if (StringUtils.isNotEmpty(ip) && !"unKnown".equalsIgnoreCase(ip)) {
            return ip;
        }
        return request.getRemoteAddr();
    }

    /*
    * 127.xxx.xxx.xxx 属于"loopback" 地址，即只能你自己的本机可见，就是本机地址，比较常见的有127.0.0.1；
192.168.xxx.xxx 属于private 私有地址(site local address)，属于本地组织内部访问，只能在本地局域网可见。同样10.xxx.xxx.xxx、从172.16.xxx.xxx 到 172.31.xxx.xxx都是私有地址，也是属于组织内部访问；
169.254.xxx.xxx 属于连接本地地址（link local IP），在单独网段可用
从224.xxx.xxx.xxx 到 239.xxx.xxx.xxx 属于组播地址
比较特殊的255.255.255.255 属于广播地址
除此之外的地址就是点对点的可用的公开IPv4地址
    *
    * */
    public static String getLocalHostIpv4(){
        try{
            Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
            while (allNetInterfaces.hasMoreElements()){
                NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
                Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
                while (addresses.hasMoreElements()){
                    InetAddress ip = (InetAddress) addresses.nextElement();
                    if (ip != null
                            && ip instanceof Inet4Address
                            && !ip.isLoopbackAddress() //loopback地址即本机地址，IPv4的loopback范围是127.0.0.0 ~ 127.255.255.255
                            && ip.getHostAddress().indexOf(":")==-1){

                        String ad = ip.getHostAddress();

                        if (ad.startsWith("10.")||ad.startsWith("172.16.")||ad.startsWith("172.31.")||ad.startsWith("192.168.")){
                            return ad;
                        }

                        return "";
                    }
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return "";
    }
}
