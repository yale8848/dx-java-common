package com.daoxuehao.java.dxcommon.logback;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Yale
 * create at: 2020-12-08 10:14
 **/
public class TimeUtil {

    protected static String timestampFormat = "yyyy-MM-dd HH:mm:ss.SSS";

    protected static String formatTimestamp(long var1,String timestampFormat,String timestampFormatTimezoneId) {
        if (timestampFormat != null && var1 >= 0L) {
            Date var3 = new Date(var1);
            DateFormat var4 = createDateFormat(timestampFormat);
            if (timestampFormatTimezoneId != null) {
                TimeZone var5 = TimeZone.getTimeZone(timestampFormatTimezoneId);
                var4.setTimeZone(var5);
            }

            return format(var3, var4);
        } else {
            return String.valueOf(var1);
        }
    }
    protected static String format(Date var1, DateFormat var2) {
        return var2.format(var1);
    }
    protected static  DateFormat createDateFormat(String var1) {
        return new SimpleDateFormat(var1);
    }
}
