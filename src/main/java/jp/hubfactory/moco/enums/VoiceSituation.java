package jp.hubfactory.moco.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum VoiceSituation {

    /** スタート時 */
    START(1),
    /** 1キロ地点 */
    POINT_1KM(2),
    /** 3キロ地点 */
    POINT_3KM(3),
    /** 5キロ地点 */
    POINT_5KM(4),
    /** 10キロ地点以降 */
    POINT_10KM_AFTEDR(5),
    /** 中間地点（目標設定時） */
    WAY_POINT(6),
    /** 残り1km地点（目標設定時）*/
    LAST_POINT(7),
    /** ゴール時 */
    FINISH(8),
    /** 新記録 */
    NEW_RECORD(9),
    ;

    @Getter
    private Integer key;

}
