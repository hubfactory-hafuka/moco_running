package jp.hubfactory.moco.util;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import lombok.NoArgsConstructor;

import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang.time.FastDateFormat;

@NoArgsConstructor
public class MocoDateUtils {

    /** 日時フォーマット : yyyy/MM/dd */
    public static final String DATE_FORMAT_yyyyMMdd_SLASH = "yyyy/MM/dd";
    /** 日時フォーマット : HH:mm:ss */
    public static final String DATE_FORMAT_HHmmss = "HH:mm:ss";


    /** 日時フォーマット : yyyy-MM-dd HH:mm:ss */
    public static final String DATE_FORMAT_NORMAL = "yyyy-MM-dd HH:mm:ss";
    /** 日時フォーマット : yyyy/MM/dd HH:mm:ss */
    public static final String DATE_FORMAT_NORMAL_SLASH = "yyyy/MM/dd HH:mm:ss";
    /** 日時フォーマット : MM/dd HH:mm */
    public static final String DATE_FORMAT_MMDDHHmm = "MM/dd HH:mm";
    /** 日時フォーマット : HH:mm */
    public static final String DATE_FORMAT_HHmm = "HH:mm";
    /** 日時フォーマット : yyyyMMdd */
    public static final String DATE_FORMAT_yyyyMMdd = "yyyyMMdd";
    /** 日時フォーマット : yyyy-MM-dd */
    public static final String DATE_FORMAT_yyyyMMdd_HYPHEN = "yyyy-MM-dd";
    /** 日時フォーマット : MM */
    public static final String DATE_FORMAT_MM = "MM";
    /** 日時フォーマット : dd */
    public static final String DATE_FORMAT_DD = "dd";

    /**
     * 現在日時取得
     * @return
     */
    public static Date getNowDate(){
        return new Date(System.currentTimeMillis());
    }

    /**
     * String型に変換します。
     *
     * @param date 対象日付
     * @param format フォーマット
     * @return
     */
    public static String convertString(Date date, String format) {
        FastDateFormat sdf = FastDateFormat.getInstance(format);
        return sdf.format(date);
    }

    /**
     * Date型に変換します。
     *
     * @param date 対象日付
     * @param format フォーマット
     * @return
     */
    public static Date convertDate(String date, String format) {
        try {
            return DateUtils.parseDate(date, new String[] { format });
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public static String convertTimeString(Date date, String format) {

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        int second = cal.get(Calendar.SECOND);

        String timeStr = null;

        if (hour > 0) {
            timeStr = String.format("%02d:%02d:%02d", hour, minute, second);
        } else if (minute > 0) {
            timeStr = String.format("%01d:%02d", minute, second);
        } else if (second > 0) {
            timeStr = String.format("%02d", second);
        } else {
            timeStr = "";
        }
        return timeStr;
    }

    public static String convertAvgTimeString(Date date, String format) {

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        int minute = cal.get(Calendar.MINUTE);
        int second = cal.get(Calendar.SECOND);

        String timeStr = null;

        if (minute > 0) {
            timeStr = String.format("%01d'%02d\"", minute, second);
        } else if (second > 0) {
            timeStr = String.format("%01d\"", second);
        } else {
            timeStr = "";
        }
        return timeStr;
    }

    public static String convertTimeString(int minute, int second) {

        String timeStr = null;

        if (minute > 0) {
            timeStr = String.format("%01d'%02d\"", minute, second);
        } else if (second > 0) {
            timeStr = String.format("%01d\"", second);
        } else {
            timeStr = "";
        }
        return timeStr;

    }
}
