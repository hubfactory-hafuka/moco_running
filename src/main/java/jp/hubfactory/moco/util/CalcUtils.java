package jp.hubfactory.moco.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class CalcUtils {

    /** 軽いジョギングのMETS値 */
    private static final int METS_LEVEL_1 = 8;

    private static final int METS_LEVEL_2 = 9;

    private static final int METS_LEVEL_3 = 10;

    private static final int METS_LEVEL_4 = 11;

    private static final int METS_LEVEL_5 = 15;


    /**
     * 消費カロリー計算<br>
     * 消費カロリー(kcal) = METS x 運動時間(時間) x 体重(kg) x 1.05
     *
     * @param weight 体重
     * @param time 運動時間
     * @param avgTime 平均時間
     * @return
     */
    public static int calcCalories(Double weight, String time, String avgTime) {

        // 走った時間(秒)
        int timeSec = MocoDateUtils.convertTimeStrToSecond(time);
        // 走った時間を時間に変換
        double timeHour = timeSec / 3600d;
        timeHour = new BigDecimal(String.valueOf(timeHour)).setScale(3, RoundingMode.FLOOR).doubleValue();

        int mets = 0;

        String[] avgTimes = avgTime.split("\'");

        if (Integer.valueOf(avgTimes[0]).intValue() <= 4) {
            mets = METS_LEVEL_5;
        } else if (Integer.valueOf(avgTimes[0]).intValue() <= 5) {
            mets = METS_LEVEL_4;
        } else if (Integer.valueOf(avgTimes[0]).intValue() <= 6) {
            mets = METS_LEVEL_3;
        } else if (Integer.valueOf(avgTimes[0]).intValue() <= 7) {
            mets = METS_LEVEL_2;
        } else {
            mets = METS_LEVEL_1;
        }

        // 消費カロリー(kcal) = METS x 運動時間(時間) x 体重(kg) x 1.05
        int calories = Double.valueOf(Math.ceil(mets * timeHour * weight * 1.05)).intValue();

        return calories;
    }
}
