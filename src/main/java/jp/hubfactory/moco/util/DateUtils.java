package jp.hubfactory.moco.util;

import java.util.Date;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class DateUtils {

    /**
     * 現在日時取得
     * @return
     */
    public static Date getNowDate(){
        return new Date(System.currentTimeMillis());
    }
}
