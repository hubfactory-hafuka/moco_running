package jp.hubfactory.moco.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum VoiceSituation {

    /** スタート時 */
    START(1,"スタート時"),
    /** 1キロ地点 */
    POINT_1KM(2, "1km地点通過"),
    /** 3キロ地点 */
    POINT_3KM(3, "3km地点通過"),
    /** 5キロ地点 */
    POINT_5KM(4, "5km地点通過"),
    /** 10キロ地点以降 */
    POINT_10KM_AFTEDR(5, "ポイント地点通過"),
    /** 中間地点（目標設定時） */
    WAY_POINT(6, "中間地点通過"),
    /** 残り1km地点（目標設定時）*/
    LAST_POINT(7, "ラスト1km"),
    /** ゴール時 */
    FINISH(8, "ゴール"),
    /** 新記録 */
    NEW_RECORD(9, "記録更新"),
    ;

    @Getter
    private Integer key;
    @Getter
    private String name;


    public static VoiceSituation valueOf(Integer situation) {
        for (VoiceSituation type : VoiceSituation.values()) {
            if (type.getKey().equals(situation)) {
                return type;
            }
        }
        throw new IllegalStateException("VoiceSituation error. situation=" + situation);
    }

}
