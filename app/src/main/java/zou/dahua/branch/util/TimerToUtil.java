package zou.dahua.branch.util;

import android.content.Context;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import zou.dahua.branch.R;

/**
 * 时间工具类
 * Created by Deep on 2017/11/6 0006.
 */

public class TimerToUtil {

    public static SimpleDateFormat getFormat(String partten) {
        return new SimpleDateFormat(partten, Locale.CHINA);
    }

    /**
     * 计算月数
     *
     * @return
     */
    private static int calculationDaysOfMonth(int year, int month) {
        int day = 0;
        switch (month) {
            // 31天
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                day = 31;
                break;
            // 30天
            case 4:
            case 6:
            case 9:
            case 11:
                day = 30;
                break;
            // 计算2月天数
            case 2:
                day = year % 100 == 0 ? year % 400 == 0 ? 29 : 28
                        : year % 4 == 0 ? 29 : 28;
                break;
        }

        return day;
    }

    // 时间显示方式
    public static String getIMListTimeDisplay(String getDateString, Context context, boolean timeZone) {
        Date getDate = null;
        try {
            if (timeZone) {
                getDate = getFormat("yyyy-MM-dd'T'HH:mmZ").parse(getDateString);
            } else {
                //getDate = getFormat("yyyyMMdd'T'HH:mm:ss").parse(getDateString);
                getDate = getFormat("yyyy-MM-dd HH:mm").parse(getDateString);
            }
        } catch (ParseException e) {
            getDate = new Date();
        }

        final long getTime = getDate.getTime();

        final long currTime = System.currentTimeMillis();
        final Date formatSysDate = new Date(currTime);

        // 判断当前总天数
        final int sysMonth = formatSysDate.getMonth() + 1;
        final int sysYear = formatSysDate.getYear();
        long seconds = 0;
        if (currTime >= (getTime - 60 * 1000)) {
            // 计算服务器返回时间与当前时间差值
            seconds = (currTime - getTime) / 1000;

            final long minute = seconds / 60;
            final long hours = minute / 60;
            final long day = hours / 24;
            final long month = day / calculationDaysOfMonth(sysYear, sysMonth);
            final long year = month / 12;

            if (day > 0 && year == 0 && month == 0) {
                return day + "天前";
            } else if (year > 0 || month > 0 || day > 0) {
                final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
                return simpleDateFormat.format(getDate);
            } else if (hours > 0) {
                return hours + context.getString(R.string.str_hoursago);
            } else if (minute > 0) {
                return minute + context.getString(R.string.str_minsago);
            } else if (seconds > 0) {
                return "1" + context.getString(R.string.str_minsago);
                // return seconds + context.getString(R.string.str_secondago);
            } else {
                //          return "1" + context.getString(R.string.str_secondago);
                return "1" + context.getString(R.string.str_minsago); //都换成分钟前
            }
        } else {
            return "";
        }
    }

    // 时间显示方式
    public static String getIMListTimeDisplay(long getDateLong, Context context, boolean timeZone) {
        Date getDate = new Date(getDateLong);

        final long getTime = getDate.getTime();

        final long currTime = System.currentTimeMillis();
        final Date formatSysDate = new Date(currTime);

        // 判断当前总天数
        final int sysMonth = formatSysDate.getMonth() + 1;
        final int sysYear = formatSysDate.getYear();
        long seconds = 0;
        if (currTime >= (getTime - 60 * 1000)) {
            // 计算服务器返回时间与当前时间差值
            seconds = (currTime - getTime) / 1000;

            final long minute = seconds / 60;
            final long hours = minute / 60;
            final long day = hours / 24;
            final long month = day / calculationDaysOfMonth(sysYear, sysMonth);
            final long year = month / 12;

            if (day > 0 && year == 0 && month == 0) {
                return day + "天前";
            } else if (year > 0 || month > 0 || day > 0) {
                final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
                return simpleDateFormat.format(getDate);
            } else if (hours > 0) {
                return hours + context.getString(R.string.str_hoursago);
            } else if (minute > 0) {
                return minute + context.getString(R.string.str_minsago);
            } else if (seconds > 0) {
                return "1" + context.getString(R.string.str_minsago);
                // return seconds + context.getString(R.string.str_secondago);
            } else {
                //          return "1" + context.getString(R.string.str_secondago);
                return "1" + context.getString(R.string.str_minsago); //都换成分钟前
            }
        } else {
            return "";
        }
    }

    public static String getDateToString(long milSecond, String pattern) {
        Date date = new Date(milSecond);
        SimpleDateFormat format = new SimpleDateFormat(pattern, Locale.CHINA);
        return format.format(date);
    }

    public static boolean haveGoIn(String start, String end) {
        try {
            Date getDateStart = getFormat("HH:mm:ss").parse(start);
            Date getDateEnd = getFormat("HH:mm:ss").parse(end);

            Date formatSysDate = new Date(System.currentTimeMillis());

            Date getDateNow = getFormat("HH:mm:ss").parse(formatSysDate.getHours()
                    + ":" + formatSysDate.getMinutes() + ":" + formatSysDate.getSeconds());

            if (getDateStart.getTime() - 1000 * 60 * 30 < getDateNow.getTime()
                    && getDateNow.getTime() < getDateEnd.getTime() + 1000 * 60 * 30) {
                return true;
            } else {
                return false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean haveGoInLong(long start, long end) {

        if (start < System.currentTimeMillis()
                && System.currentTimeMillis() < end) {
            return true;
        } else {
            return false;
        }
    }

    public static String returnWeekString(long date) {
        SimpleDateFormat df;
        df = getFormat("yyyy-MM-dd");
        String time = df.format(date);
        String[] week = time.split("-");
        /*
         * 以2049年10月1日（100周年国庆）为例，用蔡勒（Zeller）公式进行计算，过程如下：
         * 蔡勒（Zeller）公式：w=y+[y/4]+[c/4]-2c+[26(m+1)/10]+d-1
         * =49+[49/4]+[20/4]-2×20+[26× (10+1)/10]+1-1
         * =49+[12.25]+5-40+[28.6] =49+12+5-40+28 =54 (除以7余5)
         * 即2049年10月1日（100周年国庆）是星期5。
         */
        int c = Integer.valueOf(week[0].substring(0, 1));
        int y = Integer.valueOf(week[0].substring(2, 3));
        int month = Integer.valueOf(week[1]);
        // int weekIndex=y+(y/4)+(c/4)-2*c+(26*(month+1)/10)+day-1;
        if (month > 3 && month <= 14) {
        } else {
            y = y - 1;
            month = month + 12;
        }
        int day = Integer.valueOf(week[2]);
        int weekIndex = y + (y / 4) + (c / 4) - 2 * c + (26 * (month + 1) / 10) + day - 1;
        if (weekIndex < 0) {
            weekIndex = weekIndex * (-1);
        } else {
            weekIndex = weekIndex % 7;
        }
        String weekDate = null;
        switch (weekIndex) {
            case 1:
                weekDate = "一";
                break;
            case 2:
                weekDate = "二";
                break;
            case 3:
                weekDate = "三";
                break;
            case 4:
                weekDate = "四";
                break;
            case 5:
                weekDate = "五";
                break;
            case 6:
                weekDate = "六";
                break;
            case 7:
                weekDate = "日";
                break;
            default:
                weekDate = "一";
                break;
        }
        return weekDate;
    }

    public static String returnTimeWeekString(long date) {
        String startTime = getFormat("MM月dd日").format(new Date(date));
        String mTime = " (" + returnWeekString(date) + ") ";
        String endTime = getFormat("HH:mm").format(new Date(date));
        return startTime + mTime + endTime;
    }

    public static String songDateToString(long milSecond) {
        Date date = new Date(milSecond);

        SimpleDateFormat format;
        if (milSecond < 1000 * 60 * 60) {
            format = new SimpleDateFormat("mm:ss", Locale.CHINA);
        } else {
            format = new SimpleDateFormat("HH:mm:ss", Locale.CHINA);
        }
        return format.format(date);
    }
}
