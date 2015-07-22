package jp.hubfactory.moco.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum RankingType {

    /** 全体 */
    ALL(1),
    /** ガール別 */
    GIRL(2);

    @Getter
    private Integer key;

    public static RankingType valueOf(Integer key) {
        for (RankingType type : RankingType.values()) {
            if (type.getKey().equals(key)) {
                return type;
            }
        }
        return ALL;
    }
}
